package mobile.device.management.util;

import java.util.Locale;

public class OSChecker {
    private static OSType detectedOS = null;

    public static OSType getOS() {
        if (detectedOS == null) {
            String operatingSystem = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if (operatingSystem.contains("win")) {
                detectedOS = OSType.WINDOWS;
            } else if (operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix")) {
                detectedOS = OSType.LINUX;
            } else if (operatingSystem.contains("mac")) {
                detectedOS = OSType.MACOS;
            } else {
                detectedOS = OSType.UNRECOGNIZED;
            }
        }
        return detectedOS;
    }

    public boolean isWindows() {
        return getOS().equals(OSType.WINDOWS);
    }

    public boolean isLinux() {
        return getOS().equals(OSType.LINUX);
    }

    public boolean isMacOS() {
        return getOS().equals(OSType.MACOS);
    }
}
