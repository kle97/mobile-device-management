package mobile.device.management.model;

public interface Device {

    String getUdid();

    String getTransportId();

    String getDeviceName();

    String getPlatformName();

    String getPlatformVersion();

    String getAutomationName();
}
