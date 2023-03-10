package mobile.device.management;

import lombok.extern.slf4j.Slf4j;
import mobile.device.management.constant.AppConstant;
import mobile.device.management.model.AppConfig;
import mobile.device.management.service.DeviceManager;
import mobile.device.management.service.IpFinder;
import mobile.device.management.util.FileManager;

@Slf4j
public class Application {
    public static void main(String[] args) {
        AppConfig appConfig = FileManager.readPropertiesFileAs(AppConstant.PROPERTIES_PATH, AppConfig.class);
        if (appConfig == null) {
            String hubAddress = IpFinder.getHostIpAddress();
            appConfig = new AppConfig("config", hubAddress, 4444);
        }
        new DeviceManager(appConfig).scheduleCheckDevices();
    }
}
