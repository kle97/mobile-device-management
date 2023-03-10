package mobile.device.management.model.hub;

import lombok.Value;

@Value
public class Node {
    String id;
    String uri;
    int maxSessions;
    OsInfo osInfo;
    String heartbeatPeriod;
    String availability;
    String version;
    Slot[] slots;

    @Value
    public static class OsInfo {
        String arch;
        String name;
        String version;
    }
}
