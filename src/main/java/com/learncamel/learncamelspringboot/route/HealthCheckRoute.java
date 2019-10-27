package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.process.MailProcessor;
import com.learncamel.learncamelspringboot.process.HealthCheckProcessor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HealthCheckRoute extends RouteBuilder {

    @Autowired
    HealthCheckProcessor healthCheckProcessor;

    @Autowired
    MailProcessor mailProcessor;

    @Override
    public void configure() throws Exception {
        final Predicate isNotMock = header("env").isNotEqualTo("mock");
        from("{{healthRoute}}").routeId("healthRoute")
                .choice()
                    .when(isNotMock)
                        .pollEnrich("http://localhost:8080/actuator/health")
                    .end()
                .process(healthCheckProcessor)
                .choice()
                    .when(header("error").isEqualTo(true))
                    .choice()
                        .when(isNotMock)
                            .process(mailProcessor)
                        .end()
                .end();
    }
}
