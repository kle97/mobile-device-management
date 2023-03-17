package mobile.device.management.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
public class PortManager {

    private static int nextAppiumPort = 4723;
    private static int nextSystemPort = 8200;
    private static int nextWdaLocalPort = 8100;
    private static int nextNodeServerPort = 5555;

    public static int getNextAppiumPort() {
        while (isPortOccupied(nextAppiumPort)) {
            nextAppiumPort++;
        }
        int returnPort = nextAppiumPort;
        nextAppiumPort++;
        return returnPort;
    }

    public static int getNextSystemPort() {
        while (isPortOccupied(nextSystemPort)) {
            nextSystemPort++;
        }
        int returnPort = nextSystemPort;
        nextSystemPort++;
        return returnPort;
    }

    public static int getNextWdaLocalPort() {
        while (isPortOccupied(nextWdaLocalPort)) {
            nextWdaLocalPort++;
        }
        int returnPort = nextWdaLocalPort;
        nextWdaLocalPort++;
        return returnPort;
    }

    public static int getNextNodeServerPort() {
        while (isPortOccupied(nextNodeServerPort)) {
            nextNodeServerPort++;
        }
        int returnPort = nextNodeServerPort;
        nextNodeServerPort++;
        return returnPort;
    }

    public static boolean isPortOccupied(int port) {
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
    
    public static void rollbackPorts(int appiumPort, int nodeServerPort, int systemPort, int wdaLocalPort) {
        nextAppiumPort = appiumPort;
        nextNodeServerPort = nodeServerPort;
        if (systemPort > 0) {
            nextSystemPort = systemPort;
        }
        if (wdaLocalPort > 0) {
            nextWdaLocalPort = wdaLocalPort;
        }
    }
}
