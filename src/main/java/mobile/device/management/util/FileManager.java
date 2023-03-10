package mobile.device.management.util;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class FileManager {
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    private static final JavaPropsMapper propsMapper = new JavaPropsMapper();

    public static void writeFile(List<String> lines, String filePath) {
        Path path = Paths.get(filePath);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, ENCODING)) {
            for (String line: lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
    }

    public static List<String> readFile(String fileName) {
        Path path = Paths.get(fileName);
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, ENCODING)){
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        return lines;
    }
    
    public static <T> T readPropertiesFileAs(String filePath, Class<T> clazz) {
        T object = null;
        try {
            Properties properties = new Properties();
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath);
            properties.load(inputStream);
            for (String systemPropertiesKey: System.getProperties().stringPropertyNames()) {
                String systemPropertiesValue = System.getProperty(systemPropertiesKey);
                if (properties.containsKey(systemPropertiesKey)) {
                    properties.setProperty(systemPropertiesKey, systemPropertiesValue);
                }
            }
            object = propsMapper.readPropertiesAs(properties, clazz);
        } catch (IOException | NullPointerException e) {
            log.debug(e.getMessage());
        }
        return object;
    }
}
