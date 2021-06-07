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

    private Map<String, Professionnel> psMap = new HashMap<>();

    private Map<String, Structure> structureMap = new HashMap<>();

    public Map<String, Professionnel> getPsMap() {
        return psMap;
    }

    public Map<String, Structure> getStructureMap() {
        return structureMap;
    }

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
                    ExerciceProfessionnel exPro = psRow.getProfessions().get(0);
                    ExerciceProfessionnel mappedExPro =
                            mappedPs.getProfessions().stream().filter(exo -> exo.getProfessionId().equals(exPro.getProfessionId())).findAny().orElse(null);
                    if (mappedExPro != null) {
                        SavoirFaire expertise = exPro.getExpertises().get(0);
                        SavoirFaire mappedExpertise =
                                mappedExPro.getExpertises().stream().filter(expert -> expert.getExpertiseId().equals(expertise.getExpertiseId())).findAny().orElse(null);
                        if (mappedExpertise == null) {
                            mappedExPro.getExpertises().add(expertise);
                        }
                        SituationExercice situation = exPro.getWorkSituations().get(0);
                        SituationExercice mappedSituation =
                                mappedExPro.getWorkSituations().stream().filter(situ -> situ.getSituationId().equals(situation.getSituationId())).findAny().orElse(null);
                        if (mappedSituation == null) {
                            mappedExPro.getWorkSituations().add(situation);
                        }
                    } else {
                        mappedPs.getProfessions().add(exPro);
                    }
                } else {
                    psMap.put(psRow.getNationalId(), psRow);
                }

                if (structureMap.get(items[28]) == null) {
                    Structure newStructure = new Structure(Arrays.asList(objects).toArray(new String[objects.length]));
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
}
