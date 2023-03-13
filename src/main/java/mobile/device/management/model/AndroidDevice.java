package mobile.device.management.model;

import lombok.Value;

@Value
public class AndroidDevice implements Device {
    String udid;
    String transportId;
    String deviceName;
    String platformName;
    String platformVersion;
    String automationName;
}
