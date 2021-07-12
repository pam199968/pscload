package fr.ans.psc.pscload.component;

import fr.ans.psc.pscload.service.PscRestApi;
import fr.ans.psc.pscload.service.RestUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@RabbitListener(queues = "${queue.name}")
@Component
public class Receiver {

    @Autowired
    private final PscRestApi pscRestApi;

    @Autowired
    private final JsonFormatter jsonFormatter;

    private final RestUtils restUtils = new RestUtils();

    private final CountDownLatch latch = new CountDownLatch(1);

    public Receiver(PscRestApi pscRestApi, JsonFormatter jsonFormatter) {
        this.pscRestApi = pscRestApi;
        this.jsonFormatter = jsonFormatter;
    }

    @RabbitHandler
    public void receiveMessage(String message) {
        restUtils.put(pscRestApi.getPsUrl(), jsonFormatter.psFromMessage(message));
        restUtils.put(pscRestApi.getStructureUrl(), jsonFormatter.structureFromMessage(message));
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
