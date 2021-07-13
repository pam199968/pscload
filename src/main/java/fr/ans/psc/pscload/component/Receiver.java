package fr.ans.psc.pscload.component;

import fr.ans.psc.pscload.service.PscRestApi;
import fr.ans.psc.pscload.service.task.Update;
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

    private final CountDownLatch latch = new CountDownLatch(1);

    public Receiver(PscRestApi pscRestApi, JsonFormatter jsonFormatter) {
        this.pscRestApi = pscRestApi;
        this.jsonFormatter = jsonFormatter;
    }

    @RabbitHandler
    public void receiveMessage(String message) {
        new Update(pscRestApi.getPsUrl(), jsonFormatter.psFromMessage(message)).call();
        new Update(pscRestApi.getStructureUrl(), jsonFormatter.structureFromMessage(message)).call();
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
