package org.example.savethewas.metrics;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

// 요청 스레드 기준 할당량 + JVM 전체 GC 횟수/시간을 구간 단위로 측정
public class RunMetrics {
    private static final com.sun.management.ThreadMXBean THREADS =
            (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();

    public record Result(long allocatedMb, long gcCount, long gcTimeMs) {
    }

    private final long startAllocated = THREADS.getThreadAllocatedBytes(Thread.currentThread().getId());
    private final long startGcCount = gcCount();
    private final long startGcTime = gcTime();

    public static RunMetrics start() {
        return new RunMetrics();
    }

    public Result finish() {
        long allocated = THREADS.getThreadAllocatedBytes(Thread.currentThread().getId()) - startAllocated;
        return new Result(allocated >> 20, gcCount() - startGcCount, gcTime() - startGcTime);
    }

    private static long gcCount() {
        return ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(GarbageCollectorMXBean::getCollectionCount).sum();
    }

    private static long gcTime() {
        return ManagementFactory.getGarbageCollectorMXBeans().stream()
                .mapToLong(GarbageCollectorMXBean::getCollectionTime).sum();
    }
}
