package fr.ans.psc.pscload.mapper;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import fr.ans.psc.pscload.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class Loader {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(Loader.class);

    private final Map<String, Professionnel> psMap = new HashMap<>();

    private final Map<String, Structure> structureMap = new HashMap<>();

    public void loadFileToMap(File file) throws FileNotFoundException {
        log.info("loading {} into list of Ps", file.getName());
        psMap.clear();
        structureMap.clear();
        // ObjectRowProcessor converts the parsed values and gives you the resulting row.
        ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
            @Override
            public void rowProcessed(Object[] objects, ParsingContext parsingContext) {
                String[] items = Arrays.asList(objects).toArray(new String[objects.length]);
                Professionnel psRow = new Professionnel(items);
                Professionnel mappedPs = psMap.get(psRow.getNationalId());
                if (mappedPs != null) {
                    mapExPro(psRow, mappedPs);
                } else {
                    psMap.put(psRow.getNationalId(), psRow);
                }
                // get structure in map by its reference from row
                if (structureMap.get(items[28]) == null) {
                    Structure newStructure = new Structure(items);
                    structureMap.put(newStructure.getStructureId(), newStructure);
                }
            }
        };

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.getFormat().setDelimiter('|');
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setNullValue("");

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new BufferedReader(new FileReader(file)));
        log.info("loading complete!");
    }

    public Map<String, Professionnel> getPsMap() {
        return psMap;
    }

    public Map<String, Structure> getStructureMap() {
        return structureMap;
    }

    private void mapExPro(Professionnel psRow, Professionnel mappedPs) {
        ExerciceProfessionnel exProRow = psRow.getProfessions().get(0);
        mappedPs.getProfessions().stream()
                .filter(exo -> exo.getProfessionId().equals(exProRow.getProfessionId())).findAny()
                .ifPresentOrElse(exPro -> mapSituationNExpertise(exProRow, exPro), () -> mappedPs.getProfessions().add(exProRow));
    }

    private void mapSituationNExpertise(ExerciceProfessionnel exProRow, ExerciceProfessionnel mappedExPro) {
        SavoirFaire expertiseRow = exProRow.getExpertises().get(0);
        mappedExPro.getExpertises().stream()
                .filter(expertise -> expertise.getExpertiseId().equals(expertiseRow.getExpertiseId())).findAny()
                .ifPresentOrElse(expertise -> {}, () -> mappedExPro.getExpertises().add(expertiseRow));

        SituationExercice situationRow = exProRow.getWorkSituations().get(0);
        mappedExPro.getWorkSituations().stream()
                .filter(situation -> situation.getSituationId().equals(situationRow.getSituationId())).findAny()
                .ifPresentOrElse(situation -> mapStructureRef(situationRow, situation), () -> mappedExPro.getWorkSituations().add(situationRow));
    }

    private void mapStructureRef(SituationExercice situationRow, SituationExercice mappedSituation) {
        StructureRef structureRefRow = situationRow.getStructures().get(0);
        mappedSituation.getStructures().stream()
                .filter(structureRef -> structureRef.getStructureId().equals(structureRefRow.getStructureId()))
                .findAny().ifPresentOrElse(structureRef -> {}, () -> mappedSituation.getStructures().add(structureRefRow));
    }
}
