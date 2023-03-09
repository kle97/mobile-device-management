package mobile.device.management.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JacksonMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static ObjectReader readerFor(Class<?> clazz) {
        return objectMapper.readerFor(clazz);
    }

    public static ObjectWriter writerFor(Class<?> clazz) {
        return objectMapper.writerFor(clazz);
    }
}
