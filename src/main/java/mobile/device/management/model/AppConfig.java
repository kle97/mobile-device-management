package mobile.device.management.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import mobile.device.management.util.IpFinder;

@Value
public class AppConfig {
    
    @JsonProperty("host.address")
    String hostIpAddress = IpFinder.getHostIpAddress();
    
    @JsonProperty("hub.address")
    String hubIpAddress = hostIpAddress;

    @JsonProperty("hub.port")
    int hubPort = 4444;
    
    @JsonProperty("selenium.server.path")
    String seleniumServerPath = "libs/selenium-server.jar";

    @JsonProperty("config.dir")
    String configDirectory = "config";
    
    @JsonProperty("node.tracing")
    boolean nodeTracing = false;
}
