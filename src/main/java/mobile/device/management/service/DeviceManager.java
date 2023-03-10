package mobile.device.management.service;

import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import mobile.device.management.model.AndroidDevice;
import mobile.device.management.model.AppConfig;
import mobile.device.management.model.hub.HubStatus;
import mobile.device.management.model.hub.Node;
import mobile.device.management.util.JacksonMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DeviceManager {

    private final ObjectReader jsonReader = JacksonMapper.readerFor(DeviceManager.class);
    private final AppConfig appConfig;

    private static int nextAppiumPort = 4723;
    private static int nextSystemPort = 8200;
    private static int nextWdaLocalPort = 8100;
    private static int nextNodeServerPort = 5555;
    
    public DeviceManager(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void scheduleCheckDevices() {
        ScheduledTask.schedulePeriodicTask(this::checkDevices, 0, 5, TimeUnit.SECONDS,
                                           10, TimeUnit.SECONDS);
    }

    public void scheduleGetHubStatus() {
        ScheduledTask.schedulePeriodicTask(this::getHubStatus, 0, 5, TimeUnit.SECONDS,
                                           10, TimeUnit.SECONDS);
    }

    public void checkDevices() {
        List<String> resultLines = CommandLine.run("adb devices -l");
        for (String line: resultLines) {
            if (line.contains("transport_id:") && line.contains("  device ")) {
                String transportId = this.getTransportId(line);
                if (transportId.isBlank()) {
                    continue;
                }
                String udid = this.getUdid(line);
                String deviceName = this.getDeviceName(line);
                String platformName = "Android";
                String platformVersion = this.getPlatformVersion(transportId);
                int appiumPort = this.getNextAppiumPort();
                int nodePort = this.getNextNodeServerPort();
                int systemPort = this.getNextSystemPort();
                log.info("Udid: {}, deviceName: {}, platformName: {}, platformVersion: {}, transportId: {}",
                         udid, deviceName, platformName, platformVersion, transportId);

                AndroidDevice device = new AndroidDevice(udid, transportId, deviceName, platformName, platformVersion, 
                                                         appiumPort, nodePort, systemPort);
                AppiumConfig.createConfigFile(this.appConfig.getConfigDir(), device);
            }
        }
    }

    private String getUdid(String line) {
        return line.split("\\s+")[0];
    }

    private String getTransportId(String line) {
        String transportId = line.substring(line.lastIndexOf(":") + 1);
        if (!transportId.matches("\\d+")) {
            transportId = "";
        }
        return transportId;
    }

    private String getDeviceName(String line) {
        String deviceName = "";
        try {
            deviceName = line.substring(line.lastIndexOf(" product:") + 9, line.indexOf(" model:"));
        } catch (IndexOutOfBoundsException e) {
            log.debug(e.getMessage());
        }
        return deviceName;
    }

    private String getPlatformVersion(String transportId) {
        List<String> resultLines = CommandLine.run("adb -t " + transportId + " shell getprop ro.build.version.release");
        if (resultLines.size() > 0) {
            return resultLines.get(0);
        } else {
            return "";
        }
    }

    public void getHubStatus() {
        String statusAddress = String.format("http://%s:%d/status", appConfig.getHubAddress(), appConfig.getHubPort());
        List<String> resultLines = CommandLine.run("curl " + statusAddress);
        String jsonString = String.join("\n", resultLines);
        int jsonStartIndex = jsonString.indexOf("{\n");
        jsonStartIndex = Math.max(jsonStartIndex, 0);
        jsonString = jsonString.substring(jsonStartIndex);
        try {
            HubStatus hubStatus = this.jsonReader.readValue(jsonString, HubStatus.class);
            for (Node node: hubStatus.getValue().getNodes()) {
                log.info(node.getId());
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }

    }

    private int getNextAppiumPort() {
        while (isPortOccupied(nextAppiumPort)) {
            nextAppiumPort++;
        }
        int returnPort = nextAppiumPort;
        nextAppiumPort++;
        return returnPort;
    }

    private int getNextSystemPort() {
        while (isPortOccupied(nextSystemPort)) {
            nextSystemPort++;
        }
        int returnPort = nextSystemPort;
        nextSystemPort++;
        return returnPort;
    }

    private int getNextWdaLocalPort() {
        while (isPortOccupied(nextWdaLocalPort)) {
            nextWdaLocalPort++;
        }
        int returnPort = nextWdaLocalPort;
        nextWdaLocalPort++;
        return returnPort;
    }

    private int getNextNodeServerPort() {
        while (isPortOccupied(nextNodeServerPort)) {
            nextNodeServerPort++;
        }
        int returnPort = nextNodeServerPort;
        nextNodeServerPort++;
        return returnPort;
    }
    
    private boolean isPortOccupied(int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException ex) {
            log.trace("Port {} is occupied!", port);
            return true;
        }
        return false;
    }
}
