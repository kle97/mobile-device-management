package mobile.device.management.service;

import mobile.device.management.model.Device;
import mobile.device.management.util.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class XctraceService {
    private static final String IOS_PLATFORM_NAME = "iOS";
    private static final String DEFAULT_AUTOMATION_NAME = "XCUITest";

    public static List<Device> getConnectedDevices() {
        List<Device> deviceList = new ArrayList<>();
        List<String> resultLines = CommandLine.run("xcrun xctrace list devices");
        
        return deviceList;
    }
}
