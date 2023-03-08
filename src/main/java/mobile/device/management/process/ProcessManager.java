package mobile.device.management.process;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProcessManager {

    public static void main(String[] args) {
        new ProcessManager().schedulePeriodicTask();
        // String str1 = "List of devices attached";
        // String str2 = "emulator-5580          device product:sdk_phone_x86_64 model:Android_SDK_built_for_x86_64 device:generic_x86_64 transport_id:1";
        // String str3 = "emulator-5582          offline product:sdk_phone_x86_64 model:Android_SDK_built_for_x86_64 device:generic_x86_64 transport_id:2";
        // String str4 = "\n";
        // List<String> list = new ArrayList<>();
        // list.add(str1);
        // list.add(str2);
        // list.add(str3);
        // list.add(str4);
        // for (String str: list) {
        //     if (str.contains("transport_id") && str.contains("  device ")) {
        //         log.info("UDID: " + str.split("\\s+")[0]);
        //         log.info("Transport id: " + str.substring(str.lastIndexOf(":") + 1));
        //         log.info("Product name: " + str.substring(str.lastIndexOf(" product:") + 9, str.indexOf(" model:")));
        //     }
        // }
    }

    public void schedulePeriodicTask() {
        ScheduledTask.schedulePeriodicTask(this::checkDevices, 0, 5, TimeUnit.SECONDS,
                                           15, TimeUnit.SECONDS);
    }

    public void checkDevices() {
        List<String> list = CommandLine.run("adb devices -l");
        for (String str: list) {
            log.info(str);
        }
    }
}
