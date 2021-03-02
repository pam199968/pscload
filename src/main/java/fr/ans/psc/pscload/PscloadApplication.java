package fr.ans.psc.pscload;

import fr.ans.psc.pscload.component.JsonFormatter;
import fr.ans.psc.pscload.service.PscRestApi;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PscloadApplication {

	@Autowired
	private final PscRestApi pscRestApi;

	@Autowired
	private final JsonFormatter jsonFormatter;

	@Value("${ps.api.base.url}")
	private String apiBaseUrl;

	public PscloadApplication(PscRestApi pscRestApi, JsonFormatter jsonFormatter) {
		this.pscRestApi = pscRestApi;
		this.jsonFormatter = jsonFormatter;
	}

	@RabbitListener(queues = "${queue.name}")
	public void listen(String message) {
		pscRestApi.put(apiBaseUrl, jsonFormatter.psFromMessage(message));
	}

	public static void main(String[] args) {
		SpringApplication.run(PscloadApplication.class, args);
	}

}
