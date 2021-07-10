package fr.ans.psc.pscload.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Pscload metrics.
 */
@Component
public class CustomMetrics {

    private final Map<CustomMetric, AtomicInteger> appGauges = new EnumMap<>(CustomMetric.class);

    /**
     * The enum Custom metric.
     */
    public enum CustomMetric {
        STAGE,
        PS_UPLOAD_SIZE,
        PS_UPLOAD_PROGRESSION,
        STRUCTURE_UPLOAD_SIZE,
        STRUCTURE_UPLOAD_PROGRESSION,
        PS_DELETE_SIZE,
        PS_DELETE_PROGRESSION,
        PS_CREATE_SIZE,
        PS_CREATE_PROGRESSION,
        PS_UPDATE_SIZE,
        PS_UPDATE_PROGRESSION,
        STRUCTURE_DELETE_SIZE,
        STRUCTURE_DELETE_PROGRESSION,
        STRUCTURE_CREATE_SIZE,
        STRUCTURE_CREATE_PROGRESSION,
        STRUCTURE_UPDATE_SIZE,
        STRUCTURE_UPDATE_PROGRESSION,
    }

    /**
     * Instantiates a new Custom metrics.
     *
     * @param meterRegistry the meter registry
     */
    public CustomMetrics(MeterRegistry meterRegistry) {
        appGauges.put(CustomMetric.STAGE, meterRegistry.gauge("pscload.stage", new AtomicInteger(0)));

        appGauges.put(CustomMetric.PS_UPLOAD_SIZE, meterRegistry.gauge("ps.upload.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_UPLOAD_PROGRESSION, meterRegistry.gauge("ps.upload.progression", new AtomicInteger(0)));

        appGauges.put(CustomMetric.STRUCTURE_UPLOAD_SIZE, meterRegistry.gauge("structure.upload.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_UPLOAD_PROGRESSION,meterRegistry.gauge("structure.upload.progression", new AtomicInteger(0)));

        appGauges.put(CustomMetric.PS_DELETE_SIZE ,meterRegistry.gauge("ps.delete.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_DELETE_PROGRESSION ,meterRegistry.gauge("ps.delete.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_CREATE_SIZE ,meterRegistry.gauge("ps.create.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_CREATE_PROGRESSION ,meterRegistry.gauge("ps.create.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_UPDATE_SIZE,meterRegistry.gauge("ps.edit.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_UPDATE_PROGRESSION,meterRegistry.gauge("ps.edit.progression", new AtomicInteger(0)));

        appGauges.put(CustomMetric.STRUCTURE_DELETE_SIZE ,meterRegistry.gauge("structure.delete.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_DELETE_PROGRESSION ,meterRegistry.gauge("structure.delete.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_CREATE_SIZE ,meterRegistry.gauge("structure.create.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_CREATE_PROGRESSION ,meterRegistry.gauge("structure.create.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_UPDATE_SIZE,meterRegistry.gauge("structure.edit.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_UPDATE_PROGRESSION,meterRegistry.gauge("structure.edit.progression", new AtomicInteger(0)));
    }

    /**
     * Gets app gauges.
     *
     * @return the app gauges
     */
    public Map<CustomMetric, AtomicInteger> getAppGauges() {
        return appGauges;
    }

}
