package sch.frog.jaeger.config;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigHolder {

    private ConfigHolder(){
        // do nothing.
    }
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigHolder.class);

    private static final Properties props = new Properties();

    static {
        String confFile = System.getProperty("confFile");
        if(confFile == null || confFile.trim().equals("")){
            logger.warn("no assign config file from env. load config file from collector.properties");
            confFile = "collector.properties";
        }
        logger.info("load config.");
        try (
                InputStream inputStream = new FileInputStream(new File(confFile))
                ){
            props.load(inputStream);
            logger.info("load config success.");
        } catch (IOException e) {
            logger.warn("load config error, msg : {}", e.getMessage(), e);
        }
    }

    /**
     * 获取配置, 环境变量中的优先级高于配置文件
     */
    public static String getProperty(String key, String defaultValue){
        String val = System.getProperty(key);
        if(val == null){
            return props.getProperty(key, defaultValue);
        }else{
            return val;
        }
    }

    /**
     * 获取配置, 环境变量中的配置高于配置文件
     */
    public static String getProperty(String key){
        String val = System.getProperty(key);
        if(val == null){
            return props.getProperty(key);
        }else{
            return val;
        }
    }
}
