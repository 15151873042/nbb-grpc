package io.github.nbb.core.env;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author 胡鹏
 */
public class EnvUtil {

    private static ConfigurableEnvironment environment;

    private static int port = -1;


    public static void setEnvironment(ConfigurableEnvironment environment) {
        EnvUtil.environment = environment;
    }

    public static int getPort() {
        if (port == -1) {
            port = getProperty("server.port", Integer.class, 8848);
        }
        return port;
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return environment.getProperty(key, targetType, defaultValue);
    }
}
