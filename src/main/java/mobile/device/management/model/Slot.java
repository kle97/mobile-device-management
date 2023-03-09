package mobile.device.management.model;

import lombok.Value;

@Value
public class Slot {
    Id id;
    String lastStarted;
    Session session;
    Stereotype stereotype;

    @Value
    public static class Id {
        String hostId;
        String id;
    }
}
