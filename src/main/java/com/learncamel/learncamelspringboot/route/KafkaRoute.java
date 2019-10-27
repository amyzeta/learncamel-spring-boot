package com.learncamel.learncamelspringboot.route;

import org.springframework.stereotype.Component;

@Component
public class KafkaRoute extends AbstractMessagingRoute {

    public KafkaRoute() {
        super("{{kafkaRoute.from}}", "{{kafkaRoute.errorTo}}");
    }
}
