package com.modakdev.product_catalog_module.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ServerIpConfig implements ApplicationListener<WebServerInitializedEvent> {

    private String serverIp;

    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }
        System.out.println("Server IP Address: " + serverIp);
    }

    public String getServerIp() {
        return serverIp;
    }
}
