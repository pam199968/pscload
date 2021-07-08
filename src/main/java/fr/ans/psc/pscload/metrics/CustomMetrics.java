package fr.ans.psc.pscload.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Pscload metrics.
 */
@Component
public class CustomMetrics {

    private final Map<CustomMetric, AtomicInteger> appGauges = new HashMap<>();

    /**
     * The enum Custom metric.
     */
    public enum CustomMetric {
        PS_LOAD_SIZE,
        STRUCTURE_LOAD_SIZE,
        STAGE,
        PS_LOAD_PROGRESSION,
        STRUCTURE_LOAD_PROGRESSION,
        PS_DELETE_SIZE,
        PS_DELETE_PROGRESSION,
        PS_CREATE_SIZE,
        PS_CREATE_PROGRESSION,
        PS_EDIT_SIZE,
        PS_EDIT_PROGRESSION,
        STRUCTURE_DELETE_SIZE,
        STRUCTURE_DELETE_PROGRESSION,
        STRUCTURE_CREATE_SIZE,
        STRUCTURE_CREATE_PROGRESSION,
        STRUCTURE_EDIT_SIZE,
        STRUCTURE_EDIT_PROGRESSION,
    }

    /**
     * Instantiates a new Custom metrics.
     *
     * @param meterRegistry the meter registry
     */
    public CustomMetrics(MeterRegistry meterRegistry) {
        appGauges.put(CustomMetric.PS_LOAD_SIZE, meterRegistry.gauge("ps.load.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_LOAD_SIZE, meterRegistry.gauge("structure.load.size", new AtomicInteger(0)));

        appGauges.put(CustomMetric.STAGE, meterRegistry.gauge("pscload.stage", new AtomicInteger(0)));

        appGauges.put(CustomMetric.PS_LOAD_PROGRESSION, meterRegistry.gauge("ps.load.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_LOAD_PROGRESSION ,meterRegistry.gauge("structure.load.progression", new AtomicInteger(0)));

        appGauges.put(CustomMetric.PS_DELETE_SIZE ,meterRegistry.gauge("ps.delete.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_DELETE_PROGRESSION ,meterRegistry.gauge("ps.delete.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_CREATE_SIZE ,meterRegistry.gauge("ps.create.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_CREATE_PROGRESSION ,meterRegistry.gauge("ps.create.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_EDIT_SIZE ,meterRegistry.gauge("ps.edit.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.PS_EDIT_PROGRESSION ,meterRegistry.gauge("ps.edit.progression", new AtomicInteger(0)));

        appGauges.put(CustomMetric.STRUCTURE_DELETE_SIZE ,meterRegistry.gauge("structure.delete.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_DELETE_PROGRESSION ,meterRegistry.gauge("structure.delete.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_CREATE_SIZE ,meterRegistry.gauge("structure.create.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_CREATE_PROGRESSION ,meterRegistry.gauge("structure.create.progression", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_EDIT_SIZE ,meterRegistry.gauge("structure.edit.size", new AtomicInteger(0)));
        appGauges.put(CustomMetric.STRUCTURE_EDIT_PROGRESSION ,meterRegistry.gauge("structure.edit.progression", new AtomicInteger(0)));
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
