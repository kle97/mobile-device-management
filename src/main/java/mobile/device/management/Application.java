package mobile.device.management;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.constant.AppConstant;
import mobile.device.management.model.AppConfig;
import mobile.device.management.service.DeviceManager;
import mobile.device.management.util.CommandLine;
import mobile.device.management.util.FileManager;

@Slf4j
public class Application {
    public static void main(String[] args) {
        AppConfig appConfig = FileManager.readPropertiesFileAs(AppConstant.PROPERTIES_PATH, AppConfig.class, true);
        appConfig = appConfig != null ? appConfig : new AppConfig();
        
        DeviceManager deviceManager = new DeviceManager(appConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(deviceManager::closeAppiumServers));
        Runtime.getRuntime().addShutdownHook(new Thread(deviceManager::unregisteredNodes));
        Runtime.getRuntime().addShutdownHook(new Thread(CommandLine::closeBackgroundProcesses));
        deviceManager.scheduleCheckDevices();
    }
}
