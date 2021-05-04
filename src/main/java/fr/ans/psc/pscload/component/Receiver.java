package fr.ans.psc.pscload.component;

import fr.ans.psc.pscload.service.PscRestApi;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@RabbitListener(queues = "${queue.name}")
@Component
public class Receiver {

    // fancy way to receive messages, not using it for now

    @Autowired
    private final PscRestApi pscRestApi;

    @Autowired
    private final JsonFormatter jsonFormatter;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Value("${ps.api.base.url}")
    private String apiBaseUrl;

    public Receiver(PscRestApi pscRestApi, JsonFormatter jsonFormatter) {
        this.pscRestApi = pscRestApi;
        this.jsonFormatter = jsonFormatter;
    }

    @RabbitHandler
    public void receiveMessage(String message) {
        pscRestApi.put(apiBaseUrl, jsonFormatter.psFromMessage(message));
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}
