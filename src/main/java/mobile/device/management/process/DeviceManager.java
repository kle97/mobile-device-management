package mobile.device.management.process;

import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import mobile.device.management.model.AndroidDevice;
import mobile.device.management.model.HubStatus;
import mobile.device.management.model.Node;
import mobile.device.management.util.JacksonMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DeviceManager {

    private final ObjectReader jsonReader = JacksonMapper.readerFor(DeviceManager.class);

    public static void main(String[] args) {
        new DeviceManager().scheduleCheckDevices();
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

                AndroidDevice device = new AndroidDevice(udid, transportId, deviceName, platformName, platformVersion);
                AppiumConfig.createConfigFile(device);
                log.info("UDID: " + udid);
                log.info("Transport id: " + transportId);
                log.info("Device name: " + deviceName);
                log.info("Platform version: " + platformVersion);
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
        List<String> resultLines = CommandLine.run("curl http://localhost:4444/status");
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
}
