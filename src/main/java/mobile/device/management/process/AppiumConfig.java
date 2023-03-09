package mobile.device.management.process;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.model.AndroidDevice;
import mobile.device.management.util.FileWriter;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AppiumConfig {

    private static int nextPort = 4723;
    private static int nextSystemPort = 8200;

    public static void createConfigFile(AndroidDevice androidDevice) {
        String directoryPath = "appiumconfig";
        File directory = new File(directoryPath);
        if (directory.exists() || directory.mkdirs()) {
            List<String> lines = new ArrayList<>();
            lines.add("server:");
            lines.add("  port: " + getNextPort());
            lines.add("  base-path: /wd/hub");
            lines.add("  allow-insecure:");
            lines.add("    - adb_shell");
            lines.add("  use-drivers:");
            lines.add("    - uiautomator2");
            lines.add("  default-capabilities:");
            lines.add("    appium:systemPort: " + getNextSystemPort());
            lines.add("    appium:deviceName: " + androidDevice.getDeviceName());
            lines.add("    appium:udid: " + androidDevice.getUdid());
            lines.add("    platformName: " + androidDevice.getPlatformName());
            lines.add("    platformVersion: " + androidDevice.getPlatformVersion());
            lines.add("    automationName: " + "UIAutomator2");
            FileWriter.writeFile(lines, directoryPath + "/" + androidDevice.getUdid() + ".yml");
        }
    }

    private static int getNextPort() {
        while (isPortOccupied(nextPort)) {
            nextPort++;
        }
        int returnPort = nextPort;
        nextPort++;
        return returnPort;
    }

    private static int getNextSystemPort() {
        while (isPortOccupied(nextSystemPort)) {
            nextSystemPort++;
        }
        int returnPort = nextSystemPort;
        nextSystemPort++;
        return returnPort;
    }

    private static boolean isPortOccupied(int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException ex) {
            log.trace("Port {} is occupied!", port);
            return true;
        }
        return false;
    }
}
