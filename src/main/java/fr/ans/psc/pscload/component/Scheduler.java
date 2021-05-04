package fr.ans.psc.pscload.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * The type Scheduler.
 */
@Component
public class Scheduler {

    @Autowired
    private final Loader loader;

    @Value("${enable.scheduler:true}")
    private boolean enabled;

    @Value("${extract.download.url}")
    private String extractDownloadUrl;

    public Scheduler(Loader loader) {
        this.loader = loader;
    }

    /**
     * Download and parse.
     */
    @Scheduled(fixedRateString = "${schedule.rate.ms}")
    public void run() throws GeneralSecurityException, IOException {
        if (enabled) {
            loader.downloadAndParse(extractDownloadUrl);
        }
    }
}
