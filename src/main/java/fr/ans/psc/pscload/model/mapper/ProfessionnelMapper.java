package fr.ans.psc.pscload.model.mapper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import fr.ans.psc.pscload.model.object.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfessionnelMapper {

    /**
     * The logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ProfessionnelMapper.class);

    ProfessionnelMapper() {}

    public static Map<String, Professionnel> getPsMapFromFile(File file) throws FileNotFoundException {
        log.info("loading {} into list of Ps", file.getName());
        Map<String, Professionnel> psMap = new HashMap<>();
        // ObjectRowProcessor converts the parsed values and gives you the resulting row.
        ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
            @Override
            public void rowProcessed(Object[] objects, ParsingContext parsingContext) {
                Professionnel newPs = new Professionnel(Arrays.asList(objects).toArray(new String[objects.length]));
                Professionnel mappedPs = psMap.get(newPs.getNationalId());
                if (mappedPs != null) {
                    ExerciceProfessionnel exPro = newPs.getProfessions().get(0);
                    ExerciceProfessionnel mappedExPro =
                            mappedPs.getProfessions().stream().filter(exo -> exo.getKey().equals(exPro.getKey())).findAny().orElse(null);
                    if (mappedExPro != null) {
                        SavoirFaire expertise = exPro.getExpertises().get(0);
                        SavoirFaire mappedExpertise =
                                mappedExPro.getExpertises().stream().filter(expert -> expert.getKey().equals(expertise.getKey())).findAny().orElse(null);
                        if (mappedExpertise == null) {
                            mappedExPro.getExpertises().add(expertise);
                        }
                        SituationExercice situation = exPro.getWorkSituations().get(0);
                        SituationExercice mappedSituation =
                                mappedExPro.getWorkSituations().stream().filter(situ -> situ.getKey().equals(situation.getKey())).findAny().orElse(null);
                        if (mappedSituation == null) {
                            mappedExPro.getWorkSituations().add(situation);
                        }
                    } else {
                        mappedPs.getProfessions().add(exPro);
                    }
                } else {
                    psMap.put(newPs.getNationalId(), newPs);
                }
            }
        };

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.getFormat().setLineSeparator("\n");
        parserSettings.getFormat().setDelimiter('|');
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new FileReader(file));
        log.info("loading complete!");
        return psMap;
    }

    public static void serialisePsMapToFile(Object psList, String fileName) throws FileNotFoundException {
        log.info("serializing list of Ps to {}", fileName);
        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, 9);
        kryo.register(ArrayList.class, 10);
        kryo.register(Professionnel.class, 11);
        kryo.register(ExerciceProfessionnel.class, 12);
        kryo.register(SavoirFaire.class, 13);
        kryo.register(SituationExercice.class, 14);
        kryo.register(Structure.class, 15);

        Output output = new Output(new FileOutputStream(fileName));
        kryo.writeObjectOrNull(output, psList, HashMap.class);
        output.close();
        log.info("serialization complete!");
    }

    public static Map<String, Professionnel> deserialiseFileToPsMap(File file) throws FileNotFoundException {
        log.info("deserializing {} to list of Ps", file.getName());
        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, 9);
        kryo.register(ArrayList.class, 10);
        kryo.register(Professionnel.class, 11);
        kryo.register(ExerciceProfessionnel.class, 12);
        kryo.register(SavoirFaire.class, 13);
        kryo.register(SituationExercice.class, 14);
        kryo.register(Structure.class, 15);

        Input input = new Input(new FileInputStream(file));
        Map<String, Professionnel> psList = kryo.readObjectOrNull(input, HashMap.class);
        input.close();

        log.info("deserialization complete!");
        return psList;
    }

}
