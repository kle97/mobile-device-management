package mobile.device.management.model.hub;

import lombok.Value;

@Value
public class Session {

    Capabilities capabilities;
    String sessionId;
    String start;
    Stereotype stereotype;
    String uri;
}
