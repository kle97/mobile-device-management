package mobile.device.management.model.hub;

import lombok.Value;

@Value
public class NodeStatus {
    NodeStatusValue value;

    @Value
    public static class NodeStatusValue {
        boolean ready;
        String message;
        Node node;
    }
}
