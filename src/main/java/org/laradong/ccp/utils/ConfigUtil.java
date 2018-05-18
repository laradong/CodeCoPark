package org.laradong.ccp.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ConfigUtil {
    public static final String PROPERTIES_PROP_FILE = "application.properties";
    public static final ConfigUtil INSTANCE = new ConfigUtil();
    private static final Logger logger = LoggerFactory.getLogger(ConstantUtil.LOG_SYSTEM);
    private static final String FILE_ENCODING = "utf-8";
    private static final PropertiesConfiguration prop = new PropertiesConfiguration();

    private ConfigUtil() {
        // 从环境变量取配置地址
        String filePath = System.getProperty(PROPERTIES_PROP_FILE);
        if (StringUtils.isBlank(filePath)) {
            logger.error("Load config failed, filePath is null!");
            return;
        }

        InputStream fin = null;
        try {
            fin = new FileInputStream(filePath);
            if (fin == null || fin.available() <= 0) {
                throw new RuntimeException(filePath + " is not found!");
            }
            prop.load(fin, FILE_ENCODING);
            prop.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (ConfigurationException ex) {
            logger.error("Load config failed, file={}, ConfigurationException={}.", filePath, ex.getLocalizedMessage(), ex);
        } catch (IOException ex) {
            logger.error("Load config failed, file={}, IOException={}.", filePath, ex.getLocalizedMessage(), ex);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException ex) {
                    logger.error("Close config failed, file={}, IOException={}.", filePath, ex.getLocalizedMessage(), ex);
                }
            }
        }
        prop.setReloadingStrategy(new FileChangedReloadingStrategy());
    }

    public static String getString(String key) {
        return prop.getString(key);
    }

    public static String[] getStringArray(String key) {
        return prop.getStringArray(key);
    }

    public static int getInt(String key) {
        return prop.getInt(key);
    }

    public static boolean getBoolean(String key) {
        return prop.getBoolean(key);
    }

    public static long getLong(String key) {
        return prop.getLong(key);
    }
}
