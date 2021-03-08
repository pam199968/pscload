package fr.ans.psc.pscload.model.mapper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.ObjectRowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import fr.ans.psc.pscload.model.object.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProfessionnelMapper {

    public Map<String, Professionnel> getPsHashMap(String fileName) {
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
        parser.parse(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader()
                .getResourceAsStream(fileName)), StandardCharsets.UTF_8));

        return psMap;
    }

    public void serialisePsHashMapToFile(Object psList, String fileName) throws FileNotFoundException {
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
    }

    public Map<String, Professionnel> deserialiseFileToPsHashMap(String fileName) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        kryo.register(HashMap.class, 9);
        kryo.register(ArrayList.class, 10);
        kryo.register(Professionnel.class, 11);
        kryo.register(ExerciceProfessionnel.class, 12);
        kryo.register(SavoirFaire.class, 13);
        kryo.register(SituationExercice.class, 14);
        kryo.register(Structure.class, 15);

        Input input = new Input(new FileInputStream(fileName));
        Map<String, Professionnel> psList = kryo.readObjectOrNull(input, HashMap.class);
        input.close();

        return psList;
    }

}
