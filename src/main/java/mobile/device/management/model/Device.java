package mobile.device.management.model;

public interface Device {

    String getUdid();

    String getDeviceName();

    String getPlatformName();

    String getPlatformVersion();

    String getAutomationName();
}