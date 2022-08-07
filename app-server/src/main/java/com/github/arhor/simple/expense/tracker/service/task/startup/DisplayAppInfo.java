package com.github.arhor.simple.expense.tracker.service.task.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DisplayAppInfo implements StartupTask {

    private final Environment env;

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    public void execute() {
        val appName = getAppName();
        val protocol = getProtocol();
        val serverPort = getServerPort();
        val contextPath = getContextPath();
        val hostAddress = getHostAddress();
        val javaVersion = getJavaVersion();
        log.info(
            """
                            
                --------------------------------------------------------------------------------
                    Application `{}` is running! Access URLs:
                    - Local:     {}://localhost:{}{}
                    - External:  {}://{}:{}{}
                    - java ver.: {}
                --------------------------------------------------------------------------------""",
            appName,
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            javaVersion
        );
    }

    private String getAppName() {
        return env.getProperty("spring.application.name");
    }

    private String getServerPort() {
        return env.getProperty("server.port");
    }

    private String getContextPath() {
        val contextPath = env.getProperty("server.servlet.context-path");
        return ((contextPath == null) || contextPath.isBlank())
            ? "/"
            : contextPath;
    }

    private String getProtocol() {
        val sslKeyStore = env.getProperty("server.ssl.key-store");
        return (sslKeyStore == null)
            ? "http"
            : "https";
    }

    private String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            val defaultHost = "localhost";
            log.warn("The host name could not be determined, using `{}` as fallback", defaultHost);
            return defaultHost;
        }
    }

    private String getJavaVersion() {
        return env.getProperty("java.version");
    }
}
