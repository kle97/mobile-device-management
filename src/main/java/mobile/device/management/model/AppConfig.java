package mobile.device.management.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Value;

@Value
public class AppConfig {
    
    @JsonProperty("config.dir")
    String configDir;
    
    @JsonProperty("hub.address")
    String hubAddress;

    @JsonProperty("hub.port")
    int hubPort;
    
    
}
