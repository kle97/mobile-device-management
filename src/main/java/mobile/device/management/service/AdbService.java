package mobile.device.management.service;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.model.AndroidDevice;
import mobile.device.management.model.Device;
import mobile.device.management.util.CommandLine;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AdbService {

    private static final String ANDROID_PLATFORM_NAME = "Android";
    private static final String DEFAULT_AUTOMATION_NAME = "UIAutomator2";

    public static List<Device> getConnectedDevices() {
        List<Device> deviceList = new ArrayList<>();
        List<String> resultLines = CommandLine.run("adb devices -l");
        for (String line: resultLines) {
            if (isDeviceConnected(line)) {
                String transportId = getTransportId(line);
                if (transportId.isBlank()) {
                    continue;
                }
                String udid = getUdid(line);
                String deviceName = getDeviceName(line);
                String platformVersion = getPlatformVersion(transportId);

                AndroidDevice device = new AndroidDevice(udid, transportId, deviceName, ANDROID_PLATFORM_NAME, 
                                                         platformVersion, DEFAULT_AUTOMATION_NAME);
                deviceList.add(device);
            }
        }
        
        return deviceList;
    }

    public static boolean isDeviceConnected(String line) {
        return line.contains("transport_id:") && line.contains("  device ");
    }

    public static String getUdid(String line) {
        return line.split("\\s+")[0];
    }

    public static String getTransportId(String line) {
        String transportId = line.substring(line.lastIndexOf(":") + 1);
        if (!transportId.matches("\\d+")) {
            transportId = "";
        }
        return transportId;
    }

    public static String getDeviceName(String line) {
        String deviceName = "";
        try {
            deviceName = line.substring(line.lastIndexOf(" product:") + 9, line.indexOf(" model:"));
        } catch (IndexOutOfBoundsException e) {
            log.debug(e.getMessage());
        }
        return deviceName;
    }

    public static String getPlatformVersion(String transportId) {
        List<String> resultLines = CommandLine.run("adb -t " + transportId + " shell getprop ro.build.version.release");
        if (resultLines.size() > 0) {
            return resultLines.get(0);
        } else {
            return "";
        }
    }
}
