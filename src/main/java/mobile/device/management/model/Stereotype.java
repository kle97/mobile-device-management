package mobile.device.management.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

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
