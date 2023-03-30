package mobile.device.management.util;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.service.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class CommandLine {

    private static final List<Process> backgroundProcesses = new ArrayList<>();
    private static final List<ExecutorService> backgroundExecutorServices = new ArrayList<>();

    public static List<String> run(String... commands) {
        String command = String.join(" ", commands);
        return run(command, 10);
    }

    public static List<String> run(String command, int timeoutInSecond) {
        List<String> commandLineResults = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);

        if (OSChecker.isWindows()) {
            processBuilder.command("cmd", "/c", command);
        } else if (OSChecker.isLinux() || OSChecker.isMacOS()) {
            processBuilder.command("sh", "-c", command);
        } else { // Unrecognized operating system
            return commandLineResults;
        }
        try {
            Process process = processBuilder.start();
            log.info("Running command: {}", command);
            if (!process.waitFor(timeoutInSecond, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.getInputStream().close();
                process.getOutputStream().close();
                process.getErrorStream().close();
                log.debug("Process timeout after {} seconds. Process is forcibly terminated!", timeoutInSecond);
                return commandLineResults;
            }

            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (bufferedReader.ready() && (line = bufferedReader.readLine()) != null) {
                commandLineResults.add(line);
            }
            bufferedReader.close();
        } catch (IOException | InterruptedException e) {
            log.debug(e.getMessage());
        }
        return commandLineResults;
    }

    public static List<String> runAndWait(String... commands) {
        String command = String.join(" ", commands);
        return runAndWait(command, 10);
    }

    public static List<String> runAndWait(String command, int timeoutInSecond) {
        List<String> commandLineResults = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);

        if (OSChecker.isWindows()) {
            processBuilder.command("cmd", "/c", command);
        } else if (OSChecker.isLinux() || OSChecker.isMacOS()) {
            processBuilder.command("sh", "-c", command);
        } else { // Unrecognized operating system
            return commandLineResults;
        }
        try {
            Process process = processBuilder.start();
            backgroundProcesses.add(process);
            log.info("Running and waiting {} seconds for command: {}", timeoutInSecond, command);
            process.waitFor(timeoutInSecond, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            log.debug(e.getMessage());
        }
        return commandLineResults;
    }

    public static void runInBackground(String command, Consumer<String> consumer, int timeoutInSecond) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);

        if (OSChecker.isWindows()) {
            processBuilder.command("cmd", "/c", command);
        } else if (OSChecker.isLinux() || OSChecker.isMacOS()) {
            processBuilder.command("sh", "-c", command);
        } else { // Unrecognized operating system
            return;
        }

        try {
            Process process = processBuilder.start();
            backgroundProcesses.add(process);
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), consumer);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            backgroundExecutorServices.add(executorService);
            executorService.execute(streamGobbler);
            log.info("Running and waiting {} seconds for command: {}", timeoutInSecond, command);
            try {
                process.waitFor(timeoutInSecond, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.debug(e.getMessage());
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public static void closeBackgroundProcesses() {
        for (Process process: backgroundProcesses) {
            destroyProcess(process);
        }
        
        for (ExecutorService service: backgroundExecutorServices) {
            destroyExecutorService(service);
        }
    }
    
    private static void destroyProcess(Process process) {
        process.descendants().forEach(ProcessHandle::destroy);
        try {
            process.destroyForcibly();
            process.getInputStream().close();
            process.getOutputStream().close();
            process.getErrorStream().close();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }
    
    private static void destroyExecutorService(ExecutorService service) {
        service.shutdown();
        try {
            if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
        log.debug("Background service is shutdown!");
    }
}