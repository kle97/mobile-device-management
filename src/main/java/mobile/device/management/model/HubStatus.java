package mobile.device.management.model;

import lombok.Value;

@Value
public class HubStatus {

    HubStatusValue value;

    @Value
    public static class HubStatusValue {
        boolean ready;
        String message;
        Node[] nodes;
    }
}
