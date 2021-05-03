package fr.ans.psc.pscload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PscloadApplication {

	public static void main(String[] args) {
		new SpringApplication(PscloadApplication.class).run(args);
	}

}
