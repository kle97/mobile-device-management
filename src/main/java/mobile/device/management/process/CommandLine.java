package mobile.device.management.process;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.util.OSChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CommandLine {

    public static List<String> run(String... commands) {
        List<String> commandLineResults = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);

        String command = String.join(" ", commands);
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
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroy();
                log.debug("Process timeout after {} seconds. Process is forcibly terminated!", 10);
                return commandLineResults;
            }

            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                commandLineResults.add(line);
            }
        } catch (IOException | InterruptedException e) {
            log.debug(e.getMessage());
        }
        return commandLineResults;
    }
}
