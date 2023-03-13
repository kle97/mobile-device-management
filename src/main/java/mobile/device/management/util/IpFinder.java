package mobile.device.management.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class IpFinder {
    
    public static String getHostIpAddress() {
        String ipAddress = "0.0.0.0";
        try (Socket socket = new Socket();) {
            socket.connect(new InetSocketAddress("google.com", 80));
            ipAddress = String.valueOf(socket.getLocalAddress()).replaceFirst("/", "");
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        return ipAddress;
    }
}
