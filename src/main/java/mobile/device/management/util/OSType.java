package mobile.device.management.util;

public enum OSType {
    WINDOWS("Windows"),
    MACOS("MacOS"),
    LINUX("Linux"),
    UNRECOGNIZED("Unrecognized");

    private final String OSName;

    private OSType(String OSName) {
        this.OSName = OSName;
    }

    public String toString() {
        return this.OSName;
    }
}
