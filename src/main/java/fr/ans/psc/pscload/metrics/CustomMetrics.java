package fr.ans.psc.pscload.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Pscload metrics.
 */
@Configuration
public class CustomMetrics {

    private Map<String, AtomicInteger> appGauges;

    /**
     * Instantiates a new Custom metrics.
     *
     * @param meterRegistry the meter registr
     */
    public CustomMetrics(MeterRegistry meterRegistry) {
        AtomicInteger psMapSize = meterRegistry.gauge("ps_map_size", new AtomicInteger(0));
        appGauges.put("ps_map_size", psMapSize);
    }

    /**
     * Gets app gauges.
     *
     * @return the app gauges
     */
    public Map<String, AtomicInteger> getAppGauges() {
        return appGauges;
    }

    /**
     * Sets app gauges.
     *
     * @param appGauges the app gauges
     */
    public void setAppGauges(Map<String, AtomicInteger> appGauges) {
        this.appGauges = appGauges;
    }
}
