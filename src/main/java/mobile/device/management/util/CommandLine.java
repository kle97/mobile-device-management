package mobile.device.management.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CommandLine {
    
    private static final List<Process> backgroundProcesses = new ArrayList<>();
    private static final List<BufferedReader> backgroundBufferedReader = new ArrayList<>();

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
            
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (bufferedReader.ready() && (line = bufferedReader.readLine()) != null) {
                commandLineResults.add(line);
            }
            backgroundBufferedReader.add(bufferedReader);
        } catch (IOException | InterruptedException e) {
            log.debug(e.getMessage());
        }
        return commandLineResults;
    }
    
    public static void closeBackgroundProcesses() {
        // for (BufferedReader bufferedReader: backgroundBufferedReader) {
        //     try {
        //         bufferedReader.close();
        //     } catch (IOException e) {
        //         log.debug(e.getMessage());
        //     }
        // }
        
        for (Process process: backgroundProcesses) {
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
    }
}
