package fr.ans.psc.pscload;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import fr.ans.psc.pscload.component.JsonFormatter;
import fr.ans.psc.pscload.model.mapper.ProfessionnelMapper;
import fr.ans.psc.pscload.model.object.Professionnel;
import fr.ans.psc.pscload.service.PscRestApi;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SpringBootTest
class PscloadApplicationTests {

	@Test
	@Disabled
	void postMessageTest() {
		String url = "http://localhost:8000/api/ps";
		//String message = "0|496112012|0496112012|CHOLOT|Florence'Renée'Georgette|20/05/1964|||||||MME|60|C||MESFIOUI RAJA|Florence|||S|||||||||||||||||||||||||||||";
		String message = "3|190000042/021721|3190000042/021721|PROS|JEAN LOUIS''||||||0555926000||M||||PROS|JEAN LOUIS|||S||||||||F190000042||||||||||||||0555926000||0555926080|||||";
		PscRestApi pscRestApi = new PscRestApi();
		JsonFormatter jsonFormatter = new JsonFormatter();

		String jsonPs = jsonFormatter.nakedPsFromMessage(message);
		pscRestApi.put(url, jsonPs);
	}

	@Test
	@Disabled
	void restServiceTest() {
		String url = "http://localhost:8000/api/ps";
		PscRestApi pscRestApi = new PscRestApi();
		pscRestApi.delete(url + '/' + URLEncoder.encode("49278795704225/20005332", StandardCharsets.UTF_8));
//		PsListResponse psListResponse = pscRestApi.getPsList(url);
		System.out.println("fae");
	}

	@Test
	@Disabled
	void loadDbFromFileTest() throws FileNotFoundException {
		String url = "http://localhost:8000/api/ps";
		long startTime = System.currentTimeMillis();

		File file = new File("src/test/resources/download/Extraction_PSC_20001.txt");

		Map<String, Professionnel> original = ProfessionnelMapper.getPsMapFromFile(file);
		System.out.println(System.currentTimeMillis()-startTime);

		PscRestApi pscRestApi = new PscRestApi();
		JsonFormatter jsonFormatter = new JsonFormatter();

		for (Professionnel ps : original.values()) {
			pscRestApi.post(url, jsonFormatter.jsonFromObject(ps));
		}

		System.out.println(System.currentTimeMillis()-startTime);
	}

	@Test
	@Disabled
	void deserializeAndDiff() throws FileNotFoundException {
		String url = "http://localhost:8000/api/ps";
		long startTime = System.currentTimeMillis();

		File ogFile = new File("src/test/resources/download/Extraction_PSC_20001.txt");
		File newFile = new File("src/test/resources/download/Extraction_PSC_20002.txt");

		Map<String, Professionnel> original = ProfessionnelMapper.getPsMapFromFile(ogFile);
		System.out.println(System.currentTimeMillis()-startTime);

		Map<String, Professionnel> revised = ProfessionnelMapper.getPsMapFromFile(newFile);
		System.out.println(System.currentTimeMillis()-startTime);

		MapDifference<String, Professionnel> diff = Maps.difference(original, revised);

		PscRestApi pscRestApi = new PscRestApi();
		JsonFormatter jsonFormatter = new JsonFormatter();

		diff.entriesOnlyOnLeft().forEach((k, v) ->
				pscRestApi.delete(url + '/' + URLEncoder.encode(v.getNationalId(), StandardCharsets.UTF_8)));
		diff.entriesOnlyOnRight().forEach((k, v) ->
				pscRestApi.post(url, jsonFormatter.jsonFromObject(v)));
		diff.entriesDiffering().forEach((k, v) ->
				pscRestApi.diffUpdatePs(v.leftValue(), v.rightValue()));

		System.out.println(System.currentTimeMillis()-startTime);
	}

	@Test
	@Disabled
	void firstParseAndSerializeAndDeserialize() throws FileNotFoundException {
		long startTime = System.currentTimeMillis();

		File extFile = new File("src/test/resources/Extraction_PSC.txt");

		//build simple lists of the lines of the two testfiles
		Map<String, Professionnel> original = ProfessionnelMapper.getPsMapFromFile(extFile);
		System.out.println(System.currentTimeMillis()-startTime);
		ProfessionnelMapper.serialisePsMapToFile(original, "src/test/resources/Extraction_PSC.ser");
		File serFile = new File("src/test/resources/Extraction_PSC.ser");
		System.out.println(System.currentTimeMillis()-startTime);

		original.clear();
		Map<String, Professionnel> des = ProfessionnelMapper.deserialiseFileToPsMap(serFile);
		System.out.println(System.currentTimeMillis()-startTime);
	}

}
