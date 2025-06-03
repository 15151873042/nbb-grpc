package io.github.nbb.core.listener;

import io.github.nbb.core.env.EnvUtil;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author 胡鹏
 */
public class StartingApplicationListener implements SpringApplicationRunListener {

    private final SpringApplication application;

    private final String[] args;


    public StartingApplicationListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {

        this.injectEnvironment(environment);
    }

    private void injectEnvironment(ConfigurableEnvironment environment) {
        EnvUtil.setEnvironment(environment);
    }
}
