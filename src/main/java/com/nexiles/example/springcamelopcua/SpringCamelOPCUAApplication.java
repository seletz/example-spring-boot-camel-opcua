/*
 * Copyright (c) 2019 nexiles GmbH.
 */

package com.nexiles.example.springcamelopcua;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class SpringCamelOPCUAApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringCamelOPCUAApplication.class, args);
        CamelSpringBootApplicationController controller = context.getBean(CamelSpringBootApplicationController.class);
        controller.run();
    }

}

@Data
@Configuration
@ConfigurationProperties(prefix = "nexiles")
class Config {

    /**
     * OPC endpoint
     */
    String endpoint = "tcp://127.0.0.1:12686/example?allowedSecurityPolicies=None&node=RAW(ns=2;s=HelloWorld/ScalarTypes/Boolean)";

}

@Slf4j
@Component
class OPCUARouteBuilder extends RouteBuilder {

    private final Config config;

    OPCUARouteBuilder(Config config) {
        this.config = config;
        log.debug("Config: {}", config);
    }

    /**
     * <b>Called on initialization to build the routes using the fluent builder syntax.</b>
     * <p/>
     * This is a central method for RouteBuilder implementations to implement
     * the routes using the Java fluent builder syntax.
     *
     */
    @Override
    public void configure() {
        from("milo-client:" + config.getEndpoint()).routeId("Test Route")
                .process(exchange -> {
                    String routeId = exchange.getFromRouteId();
                    DataValue data = exchange.getIn().getBody(DataValue.class);
                    log.info("Route '{}': Status: {}, Value: {}",
                            routeId,
                            data.getStatusCode().toString(), data.getValue().getValue());
                });

    }
}
