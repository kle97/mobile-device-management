package mobile.device.management.service;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.model.Device;
import mobile.device.management.model.IOSDevice;
import mobile.device.management.util.CommandLine;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class XctraceService {
    private static final String IOS_PLATFORM_NAME = "iOS";
    private static final String DEFAULT_AUTOMATION_NAME = "XCUITest";

    public static List<Device> getConnectedDevices() {
        List<Device> deviceList = new ArrayList<>();
        List<String> resultLines = CommandLine.run("xcrun xctrace list devices");
        for (String line: resultLines) {
            if (line.contains("== Simulators ==")) {
                break;
            } else if(isDeviceConnected(line)) {
                String udid = getUdid(line);
                String deviceName = getDeviceName(line);
                String platformVersion = getPlatformVersion(line);
                log.info(line);

                IOSDevice device = new IOSDevice(udid, deviceName, IOS_PLATFORM_NAME,
                                                 platformVersion, DEFAULT_AUTOMATION_NAME);
                deviceList.add(device);
            }
        }
        return deviceList;
    }

    public static boolean isDeviceConnected(String line) {
        return line.matches("[\\w\\s.-]+ \\(\\d+.\\d+.?\\d*\\) \\([A-Za-z0-9-]+\\)");
    }

    public static String getUdid(String line) {
        return line.substring(line.lastIndexOf("(") + 1, line.lastIndexOf(")"));
    }

    public static String getDeviceName(String line) {
        return line.substring(0, line.indexOf(" ("));
    }

    public static String getPlatformVersion(String line) {
        return line.substring(line.indexOf("(") + 1, line.indexOf(")"));
    }
}