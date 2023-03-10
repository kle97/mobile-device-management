package mobile.device.management.service;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.model.AndroidDevice;
import mobile.device.management.model.IOSDevice;
import mobile.device.management.util.FileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AppiumConfig {
    
    public static boolean createConfigFile(String configDirPath, AndroidDevice androidDevice) {
        try {
            Files.createDirectories(Paths.get(configDirPath));
            List<String> lines = new ArrayList<>();
            lines.add("server:");
            lines.add("  port: " + androidDevice.getAppiumPort());
            lines.add("  base-path: /wd/hub");
            lines.add("  allow-insecure:");
            lines.add("    - adb_shell");
            lines.add("  use-drivers:");
            lines.add("    - uiautomator2");
            lines.add("  default-capabilities:");
            lines.add("    appium:systemPort: " + androidDevice.getSystemPort());
            lines.add("    appium:deviceName: " + androidDevice.getDeviceName());
            lines.add("    appium:udid: " + androidDevice.getUdid());
            lines.add("    platformName: " + androidDevice.getPlatformName());
            lines.add("    platformVersion: " + androidDevice.getPlatformVersion());
            lines.add("    automationName: " + "UIAutomator2");
            FileManager.writeFile(lines, configDirPath + "/" + androidDevice.getUdid() + ".yml");
            return true;
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        
        return false;
    }

    public static boolean createConfigFile(String configDirPath, IOSDevice androidDevice) {
        try {
            Files.createDirectories(Paths.get(configDirPath));
            List<String> lines = new ArrayList<>();
            lines.add("server:");
            lines.add("  port: " + androidDevice.getAppiumPort());
            lines.add("  base-path: /wd/hub");
            lines.add("  use-drivers:");
            lines.add("    - xcuitest");
            lines.add("  default-capabilities:");
            lines.add("    appium:wdaLocalPort: " + androidDevice.getWdaLocalPort());
            lines.add("    appium:deviceName: " + androidDevice.getDeviceName());
            lines.add("    appium:udid: " + androidDevice.getUdid());
            lines.add("    platformName: " + androidDevice.getPlatformName());
            lines.add("    platformVersion: " + androidDevice.getPlatformVersion());
            lines.add("    automationName: " + "XCUITest");
            FileManager.writeFile(lines, configDirPath + "/" + androidDevice.getUdid() + ".yml");
            return true;
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        return false;
    }
}
