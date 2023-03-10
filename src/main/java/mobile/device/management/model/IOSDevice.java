package mobile.device.management.model;

import lombok.Value;

@Value
public class IOSDevice {
    String udid;
    String transportId;
    String deviceName;
    String platformName;
    String platformVersion;
    int appiumPort;
    int nodePort;
    int wdaLocalPort;
}
