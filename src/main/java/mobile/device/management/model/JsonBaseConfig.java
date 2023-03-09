package mobile.device.management.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class JsonBaseConfig {
    
    protected Map<String, Object> otherArguments = new HashMap<>();
    
    @JsonAnySetter
    protected void setOtherArguments(String argument, Object value) {
        this.otherArguments.put(argument, value);
    }
    
    public boolean hasConfig(String argument) {
        return this.otherArguments.containsKey(argument);
    }

    public Object getAsObject(String argument) {
        return this.otherArguments.get(argument);
    }

    public String getAsString(String argument) {
        Object value = getAsObject(argument);
        if (value instanceof String) {
            return (String) value;
        }
        return null;
    }
    
    public Boolean getAsBoolean(String argument) {
        String value = getAsString(argument);
        if (value.equals("true") || value.equals("false")) {
            return Boolean.valueOf(value);
        }
        return null;
    }

    public Integer getAsInteger(String argument) {
        String value = getAsString(argument);
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException | NullPointerException ex) {
            log.info(ex.getMessage());
            return null;
        }
    }

    public Double getAsDouble(String argument) {
        String value = getAsString(argument);
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException | NullPointerException ex) {
            log.info(ex.getMessage());
            return null;
        }
    }
    
    public String[] getAsStringArray(String argument) {
        Object value = getAsObject(argument);
        if (value instanceof String[]) {
            return (String[]) value;
        }
        return null;
    }

    public List<String> getAsListOfString(String argument) {
        Object value = getAsObject(argument);
        if (value instanceof List<?>) {
            List<String> list = new ArrayList<>();
            for (Object element: (List<?>) value) {
                if (element instanceof String) {
                    list.add((String) element);
                }
            }
            return list;
        }
        return null;
    }
}
