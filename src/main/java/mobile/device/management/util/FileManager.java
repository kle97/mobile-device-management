package mobile.device.management.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class FileManager {
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    private static final JavaPropsMapper propsMapper = (JavaPropsMapper) new JavaPropsMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static Path writeFile(List<String> lines, String filePath) {
        try {
            Path path = Paths.get(filePath);
            BufferedWriter bufferedWriter = Files.newBufferedWriter(path, ENCODING);
            for (String line: lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
            log.info("File created/modified at: {}", path.toAbsolutePath());
            return path;
        } catch (IOException | InvalidPathException e) {
            log.debug(e.getMessage());
        }
        return null;
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
        return readPropertiesFileAs(filePath, clazz, false);
    }
    
    public static <T> T readPropertiesFileAs(String filePath, Class<T> clazz, boolean withoutPathSeparator) {
        T object = null;
        try {
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream(filePath);
            properties.load(inputStream);
            inputStream.close();
            for (String systemPropertiesKey: System.getProperties().stringPropertyNames()) {
                String systemPropertiesValue = System.getProperty(systemPropertiesKey);
                if (properties.containsKey(systemPropertiesKey)) {
                    properties.setProperty(systemPropertiesKey, systemPropertiesValue);
                }
            }
            
            if (withoutPathSeparator) {
                JavaPropsSchema schema = JavaPropsSchema.emptySchema().withoutPathSeparator();
                object = propsMapper.readPropertiesAs(properties, schema, clazz);
            } else {
                object = propsMapper.readPropertiesAs(properties, clazz);
            }
        } catch (IOException | NullPointerException e) {
            log.debug(e.getMessage());
        }
        return object;
    }
}
