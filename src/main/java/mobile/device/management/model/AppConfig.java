package mobile.device.management.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mobile.device.management.util.IpFinder;

@Getter
@NoArgsConstructor
@Setter(onMethod = @__(@JsonSetter(nulls = Nulls.SKIP)))
public class AppConfig {

    @JsonProperty("host.address")
    String hostIpAddress = IpFinder.getHostIpAddress();

    @JsonProperty("hub.address")
    String hubIpAddress = hostIpAddress;

    @JsonProperty("hub.port")
    int hubPort = 4444;

    @JsonProperty("selenium.server-path")
    String seleniumServerPath = "libs/selenium-server.jar";

    @JsonProperty("config.dir")
    String configDirectory = "config";

    @JsonProperty("log.dir")
    String logDirectory = "log";

    @JsonProperty("node.tracing")
    boolean nodeTracing = false;

    @JsonProperty("node.session-timeout")
    long nodeSessionTimeout = 300;

    @JsonProperty("appium.base-path")
    String basePath = "/wd/hub";

    @JsonProperty("appium.keep-alive-timeout")
    int keepAliveTimeout = 600;

    @JsonProperty("appium.allow-insecure")
    String allowInsecure = "";
}