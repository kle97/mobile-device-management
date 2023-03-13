package mobile.device.management.service;

import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import mobile.device.management.model.AppConfig;
import mobile.device.management.model.Device;
import mobile.device.management.model.hub.HubStatus;
import mobile.device.management.model.hub.Node;
import mobile.device.management.util.CommandLine;
import mobile.device.management.util.JacksonMapper;
import mobile.device.management.util.OSChecker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class DeviceManager {

    private final ObjectReader jsonReader = JacksonMapper.readerFor(DeviceManager.class);
    private final AppConfig appConfig;
    
    private final Map<String, DeviceConfig> registeredDevices = new HashMap<>();
    
    public DeviceManager(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void scheduleCheckDevices() {
        TaskScheduler.schedulePeriodicTask(this::checkDevices, 0, 30, TimeUnit.SECONDS,
                                           180, TimeUnit.SECONDS);
    }

    public void checkDevices() {
        List<Device> currentConnectedDevices = AdbService.getConnectedDevices();
        if (OSChecker.isMacOS()) {
            currentConnectedDevices.addAll(XctraceService.getConnectedDevices());
        }
        for (Device device: currentConnectedDevices) {
            if (!this.registeredDevices.containsKey(device.getUdid())) {
                DeviceConfig deviceConfig = new DeviceConfig(this.appConfig, device);
                Path appiumConfigFile = deviceConfig.createAppiumConfigFile();
                Path nodeConfigFile = deviceConfig.createNodeConfigFile();
                if (this.startAppiumServer(appiumConfigFile) && this.registerNode(nodeConfigFile)) {
                    this.registeredDevices.put(device.getUdid(), deviceConfig);
                }
            }
        }
        
        if (currentConnectedDevices.size() < registeredDevices.size()) {
            Set<String> currentDeviceUdids = currentConnectedDevices.stream().map(Device::getUdid).collect(Collectors.toSet());
            for (Map.Entry<String, DeviceConfig> entry: registeredDevices.entrySet()) {
                if (!currentDeviceUdids.contains(entry.getKey())) {
                    ProcessManager.killProcess(entry.getValue().getAppiumPort());
                    ProcessManager.killProcess(entry.getValue().getNodePort());
                }
            }
        }
    }
    
    public boolean startAppiumServer(Path appiumConfigFile) {
        boolean serverStarted = false;
        List<String> resultLines = CommandLine.runAndWait("appium --config " + appiumConfigFile.toAbsolutePath(), 15);
        for (String line: resultLines) {
            log.info(line);
            if (line.contains("Appium REST http interface listener started")) {
                serverStarted = true;
            }
        }
        return serverStarted;
    }
    
    public boolean registerNode(Path nodeConfigFile) {
        boolean nodeRegistered = false;
        List<String> resultLines = CommandLine.runAndWait("java -jar " + appConfig.getSeleniumServerPath() + " node --config " 
                                                                  + nodeConfigFile.toAbsolutePath(), 15);
        for (String line: resultLines) {
            log.info(line);
            if (line.contains("Node has been added")) {
                nodeRegistered = true;
            }
        }
        return nodeRegistered;
    }

    public void closeAppiumServers() {
        for (DeviceConfig deviceConfig: this.registeredDevices.values()) {
            ProcessManager.killProcess(deviceConfig.getAppiumPort());
        }
    }

    public void unregisteredNodes() {
        for (DeviceConfig deviceConfig: this.registeredDevices.values()) {
            ProcessManager.killProcess(deviceConfig.getNodePort());
        }
    }

    public void getHubStatus() {
        String statusAddress = String.format("http://%s:%d/status", this.appConfig.getHubIpAddress(), this.appConfig.getHubPort());
        List<String> resultLines = CommandLine.run("curl " + statusAddress);
        String jsonString = String.join("\n", resultLines);
        int jsonStartIndex = jsonString.indexOf("{\n");
        jsonStartIndex = Math.max(jsonStartIndex, 0);
        jsonString = jsonString.substring(jsonStartIndex);
        try {
            HubStatus hubStatus = jsonReader.readValue(jsonString, HubStatus.class);
            for (Node node: hubStatus.getValue().getNodes()) {
                log.info(node.getId());
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }
}
