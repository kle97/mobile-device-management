package mobile.device.management.model.hub;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import mobile.device.management.model.JsonBaseConfig;

@Value
@EqualsAndHashCode(callSuper = true)
public class Stereotype extends JsonBaseConfig {

    @JsonProperty("appium:udid")
    String udid;

    @JsonProperty("appium:systemPort")
    String systemPort;

    @JsonProperty("appium:deviceName")
    String deviceName;

    String platformName;

    @JsonProperty("appium:platformVersion")
    String platformVersion;

    String automationName;
}
