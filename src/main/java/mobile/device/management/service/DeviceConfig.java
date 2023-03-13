package mobile.device.management.service;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mobile.device.management.model.AndroidDevice;
import mobile.device.management.model.AppConfig;
import mobile.device.management.model.Device;
import mobile.device.management.util.FileManager;
import mobile.device.management.util.JacksonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class DeviceConfig {

    private static final ObjectReader objectReader = JacksonMapper.readerFor(DeviceConfig.class);
    private final AppConfig appConfig;
    private final Device device;
    private final int appiumPort;
    private final int nodePort;
    private final ObjectNode defaultCapabilities;
    
    public DeviceConfig(AppConfig appConfig, Device device) {
        this.appConfig = appConfig;
        this.device = device;
        this.appiumPort = PortManager.getNextAppiumPort();
        this.nodePort = PortManager.getNextNodeServerPort();
        this.defaultCapabilities = this.getDefaultCapabilities(device);
    }

    private ObjectNode getDefaultCapabilities(Device device) {
        ObjectNode node = (ObjectNode) objectReader.createObjectNode();
        node.put("appium:udid", device.getUdid());
        node.put("appium:deviceName", device.getDeviceName());
        node.put("platformName", device.getPlatformName());
        node.put("appium:platformVersion", device.getPlatformVersion());
        node.put("appium:automationName", device.getAutomationName());
        if (device instanceof AndroidDevice) {
            node.put("appium:systemPort", PortManager.getNextSystemPort());
        } else {
            node.put("appium:wdaLocalPort", PortManager.getNextWdaLocalPort());
        }
        return node;
    }

    public Path createAppiumConfigFile() {
        try {
            Files.createDirectories(Paths.get(this.appConfig.getConfigDirectory()));

            ObjectNode parentNode = (ObjectNode) objectReader.createObjectNode();
            ObjectNode node = (ObjectNode) objectReader.createObjectNode();
            parentNode.set("server", node);
            node.put("address", this.appConfig.getHostIpAddress());
            node.put("port", this.appiumPort);
            node.put("base-path", "/wd/hub");
            ArrayNode driverArray = (ArrayNode) objectReader.createArrayNode();
            node.set("use-drivers", driverArray.add(this.device.getAutomationName().toLowerCase()));
            node.set("default-capabilities", this.defaultCapabilities);

            String filePath = this.appConfig.getConfigDirectory() + "/" + this.device.getUdid() + ".json";
            return FileManager.writeFile(List.of(parentNode.toPrettyString()), filePath);
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    public Path createNodeConfigFile() {
        try {
            Files.createDirectories(Paths.get(this.appConfig.getConfigDirectory()));
            List<String> lines = new ArrayList<>();
            lines.add("[server]");
            lines.add("port = " + this.nodePort);
            lines.add("");
            lines.add("[logging]");
            lines.add("tracing = " + appConfig.isNodeTracing());
            lines.add("");
            lines.add("[node]");
            lines.add("hub = " + String.format("'http://%s:%d'", appConfig.getHubIpAddress(), appConfig.getHubPort()));
            lines.add("detect-drivers = false");
            lines.add("max-sessions = 1");
            lines.add("register-cycle = 10");
            lines.add("session-timeout = 300");
            lines.add("");
            lines.add("[relay]");
            lines.add("url = " + String.format("'http://%s:%d/wd/hub'", appConfig.getHostIpAddress(), this.appiumPort));
            lines.add("status-endpoint = '/status'");
            lines.add("[[relay.configs]]");
            lines.add("max-sessions = 1");
            lines.add("stereotype = '''");
            lines.add(this.defaultCapabilities.toPrettyString());
            lines.add("'''");
            return FileManager.writeFile(lines, appConfig.getConfigDirectory() + "/" + device.getUdid() + ".toml");
        } catch (IOException | SecurityException e) {
            log.debug(e.getMessage());
        }
        return null;
    }
}
