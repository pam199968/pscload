package fr.ans.psc.pscload;

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

	@Value("${ps.put.url}")
	private String psPutUrl;

	public PscloadApplication(PscRestApi pscRestApi) {
		this.pscRestApi = pscRestApi;
	}

	@RabbitListener(queues = "${queue.name}")
	public void listen(String in) {
		pscRestApi.putPs(psPutUrl, in);
	}

	public static void main(String[] args) {
		SpringApplication.run(PscloadApplication.class, args);
	}

}
