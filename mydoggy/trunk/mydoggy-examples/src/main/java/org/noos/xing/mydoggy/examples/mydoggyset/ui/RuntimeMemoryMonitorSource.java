package org.noos.xing.mydoggy.examples.mydoggyset.ui;

/**
 * @author Angelo De Caro
 */
public class RuntimeMemoryMonitorSource implements MonitorSource {

    private Runtime r = Runtime.getRuntime();

    public float getTotal() {
        return r.totalMemory();
    }

    public float getUsed() {
        return r.totalMemory() - r.freeMemory();
    }

}
