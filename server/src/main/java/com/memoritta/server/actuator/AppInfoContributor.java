package com.memoritta.server.actuator;

import com.memoritta.server.config.ServerConfig;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class AppInfoContributor implements InfoContributor {

    private final ServerConfig serverConfig;

    public AppInfoContributor(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("app", serverConfig.getVersion());
    }
}