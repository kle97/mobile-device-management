package mobile.device.management.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class StreamGobbler implements Runnable {
    
    private final InputStream inputStream;
    private final Consumer<String> consumer;
    
    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream))) {
            bufferedReader.lines().forEach(this.consumer);
        } catch (IOException e) {
            this.consumer.accept(e.getMessage());
        }
    }
}
