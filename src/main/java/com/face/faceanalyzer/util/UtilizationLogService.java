package com.face.faceanalyzer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UtilizationLogService {

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 15)
    public void scheduleUtilizationChecks() {
        Runtime runtime = Runtime.getRuntime();

        long totalHeapSize = runtime.totalMemory();
        long maxHeapSize = runtime.maxMemory();
        long freeHeapSize = runtime.freeMemory();

        log.info("=== Heap Memory Information ===");
        log.info("Total Heap Size: {} MB", totalHeapSize / (1024 * 1024));
        log.info("Max Heap Size: {} MB", maxHeapSize / (1024 * 1024));
        log.info("Free Heap Size: {} MB", freeHeapSize / (1024 * 1024));

        // Disk Space Information
        File diskPartition = new File("/");
        long totalDiskSpace = diskPartition.getTotalSpace();
        long freeDiskSpace = diskPartition.getFreeSpace();
        long usableDiskSpace = diskPartition.getUsableSpace();

        log.info("\n=== Disk Space Information ===");
        log.info("Total Disk Size: {} MB", totalDiskSpace / (1024 * 1024));
        log.info("Free Disk Space: {} MB", freeDiskSpace / (1024 * 1024));
        log.info("Usable Disk Space: {} MB", usableDiskSpace / (1024 * 1024));
    }
}
