package com.taxisim.config.Manager;

import com.taxisim.logging.Logger;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private static final Properties props = new Properties();
    private static final Logger log = Logger.getInstance();

    private ConfigManager() throws Exception {

        try (InputStream input = ConfigManager.class
                .getResourceAsStream("/com/taxisim/config/resources/dbconfig.properties")) {

            if (input == null) {
                throw new Exception("dbconfig.properties not found under /com/taxisim/config/resources/");
            }
            props.load(input);
            log.info("ConfigManager loaded successfully from /com/taxisim/config/resources/");
        } catch (Exception e) {
            log.error("Failed to load dbconfig.properties: " + e.getMessage());
            throw e;
        }
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            try {
                instance = new ConfigManager();
            } catch (Exception e) {
                throw new RuntimeException("Could not create ConfigManager instance: " + e.getMessage(), e);
            }
        }
        return instance;
    }

    public String get(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            log.warn("Missing config key: " + key);
        }
        return value;
    }
}
