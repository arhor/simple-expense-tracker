package com.github.arhor.simple.expense.tracker.config;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class CustomWebServerFactoryCustomizer implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    private static final boolean IS_DIRECT_BUFFER = false;
    private static final int BYTE_BUFFER_SIZE = 1024;
    private static final String WEB_SOCKET_DEPLOYMENT_INFO = "io.undertow.websockets.jsr.WebSocketDeploymentInfo";

    @Override
    public void customize(final UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo ->
            deploymentInfo.addServletContextAttribute(
                WEB_SOCKET_DEPLOYMENT_INFO,
                new WebSocketDeploymentInfo()
                    .setBuffers(
                        new DefaultByteBufferPool(
                            IS_DIRECT_BUFFER,
                            BYTE_BUFFER_SIZE
                        )
                    )
            )
        );
    }
}
