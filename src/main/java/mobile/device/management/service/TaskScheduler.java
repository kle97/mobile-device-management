package mobile.device.management.service;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TaskScheduler {

    public static void schedulePeriodicTask(Runnable command, long initialDelay, long period, TimeUnit timeUnit) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, timeUnit);
        log.info("Service is scheduled to run every {} {} with {} {} delay!",
                 period, getTimeUnitName(timeUnit), initialDelay, getTimeUnitName(timeUnit));
    }

    public static void schedulePeriodicTask(Runnable command, long initialDelay, long period, long duration) {
        schedulePeriodicTask(command, initialDelay, period, duration, TimeUnit.MILLISECONDS);
    }

    public static void schedulePeriodicTask(Runnable command, long initialDelay, long period, long duration, TimeUnit timeUnit) {
        schedulePeriodicTask(command, initialDelay, period, timeUnit, duration, timeUnit);
    }

    public static void schedulePeriodicTask(Runnable command, long initialDelay, long period,
                                            TimeUnit timeUnit, long duration, TimeUnit durationTimeUnit) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, timeUnit);
        scheduledExecutorService.schedule(() -> stopScheduleService(scheduledExecutorService), duration, durationTimeUnit);
        log.info("Service is scheduled to run every {} {} with {} {} delay!",
                 period, getTimeUnitName(timeUnit), initialDelay, getTimeUnitName(timeUnit));
        log.info("Scheduler will stop after {} {}!", duration, getTimeUnitName(durationTimeUnit));
    }

    private static void stopScheduleService(ScheduledExecutorService scheduledExecutorService) {
        scheduledExecutorService.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutorService.shutdownNow();
        }
        log.debug("Scheduled service is shutdown!");
    }

    private static String getTimeUnitName(TimeUnit timeUnit) {
        return timeUnit.name().toLowerCase().replaceFirst("s$", "(s)");
    }
}
