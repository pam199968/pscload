package fr.ans.psc.pscload;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.ObjectRowProcessor;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import fr.ans.psc.pscload.model.factory.AttributeFactory;
import fr.ans.psc.pscload.model.object.Attribute;
import fr.ans.psc.pscload.model.object.Professionnel;
import fr.ans.psc.pscload.model.object.response.PsListResponse;
import fr.ans.psc.pscload.service.PscRestApi;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class PscloadApplicationTests {

	@Test
	@Disabled
	void postMessageTest() {
		String url = "http://localhost:8000/api/ps";
		String message = "0|496112012|0496112012|CHOLOT|Florence'Renée'Georgette|20/05/1964|||||||MME|60|C||MESFIOUI RAJA|Florence|||S|||||||||||||||||||||||||||||";
		//String message = "3|190000042/021721|3190000042/021721|PROS|JEAN LOUIS''||||||0555926000||M||||PROS|JEAN LOUIS|||S||||||||F190000042||||||||||||||0555926000||0555926080|||||";
		PscRestApi pscRestApi = new PscRestApi();
		pscRestApi.putPs(url, message);
	}

	@Test
	@Disabled
	void restServiceTest() {
		String url = "http://localhost:8000/api/ps";
		PscRestApi pscRestApi = new PscRestApi();
		PsListResponse psListResponse = pscRestApi.getPsAsResponse(url);
		System.out.println("fae");
	}

	@Test
	@Disabled
	void contextLoads() throws UnsupportedEncodingException {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setDelimiter('|');

		// creates a CSV parser
		CsvParser parser = new CsvParser(settings);

		long startTime = System.currentTimeMillis();
		// parses all rows into records in one go.
		List<Record> allRowsRecords = parser.parseAllRecords(
				new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader()
						.getResourceAsStream("Extraction_PS_PSC.txt")), "UTF-8"));
		long stopTime = System.currentTimeMillis();
		System.out.println(stopTime-startTime);

	}

	@Test
	@Disabled
	void rowProcessorTest() throws UnsupportedEncodingException {
		// BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
		BeanListProcessor<Professionnel> rowProcessor = new BeanListProcessor<>(Professionnel.class);

		com.univocity.parsers.csv.CsvParserSettings parserSettings = new com.univocity.parsers.csv.CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.getFormat().setDelimiter('|');
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		long startTime = System.currentTimeMillis();
		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader()
				.getResourceAsStream("Extraction_PS_PSC.txt")), "UTF-8"));
		System.out.println(System.currentTimeMillis()-startTime);
		// The BeanListProcessor provides a list of objects extracted from the input.
		List<Professionnel> beans = rowProcessor.getBeans();

		System.out.println("walalala");

		Attribute circle = (Attribute) AttributeFactory.getAttribute("az");

	}

	@Test
	@Disabled
	void myProcessorTest() throws IOException {

		List<Professionnel> psList = new ArrayList<>();
		// ObjectRowProcessor converts the parsed values and gives you the resulting row.
		ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
			@Override
			public void rowProcessed(Object[] objects, ParsingContext parsingContext) {
				Professionnel ps = new Professionnel((String[]) objects);
				psList.add(ps);
			}

		};

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.getFormat().setDelimiter('|');
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		long startTime = System.currentTimeMillis();
		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader()
				.getResourceAsStream("Extraction_PS_PSC.txt")), "UTF-8"));
		System.out.println(System.currentTimeMillis()-startTime);


		Kryo kryo = new Kryo();
		kryo.register(ArrayList.class);
		kryo.register(Professionnel.class);
		kryo.register(Attribute.class);

		Output output = new Output(new FileOutputStream("no-fly.ser"));
		kryo.writeObjectOrNull(output, psList, ArrayList.class);
		output.close();

		System.out.println(System.currentTimeMillis()-startTime);

	}

	@Test
	@Disabled
	public void deserializeTest() throws IOException {
		long startTime = System.currentTimeMillis();

		Kryo kryo = new Kryo();
		kryo.register(ArrayList.class, 9);
		kryo.register(Professionnel.class, 10);
		kryo.register(Attribute.class, 11);

		Input input = new Input(new FileInputStream("no-fly.ser"));
		List<Professionnel> object2 = kryo.readObjectOrNull(input, ArrayList.class);
		input.close();

		System.out.println(System.currentTimeMillis()-startTime);
	}

}
