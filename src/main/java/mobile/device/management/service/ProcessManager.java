package mobile.device.management.service;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.util.CommandLine;
import mobile.device.management.util.OSChecker;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProcessManager {

    public static void killProcess(int port) {
        for (String pid: getPIDs(port)) {
            if (OSChecker.isWindows()) {
                CommandLine.run("taskkill /F /PID " + pid).forEach(log::info);
            } else if (OSChecker.isLinux() || OSChecker.isMacOS()) {
                CommandLine.run("kill -9 " + pid).forEach(log::info);
            }
        }
    }

    public static List<String> getPIDs(int port) {
        List<String> pids = new ArrayList<>();
        if (OSChecker.isWindows()) {
            List<String> resultLines = CommandLine.run("netstat -ano | findstr :" + port);
            for (String line: resultLines) {
                log.info(line);
                if (line.contains("LISTENING")) {
                    pids.add(line.substring(line.lastIndexOf(" ")));
                }
            }
        } else if (OSChecker.isLinux() || OSChecker.isMacOS()) {
            List<String> resultLines = CommandLine.run("lsof -t -i :" + port);
            for (String line: resultLines) {
                log.info(line);
                if (line.matches("\\d+")) {
                    pids.add(line);
                }
            }
        }
        return pids;
    }
}