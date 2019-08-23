package com.learncamel.learncamelspringboot.process;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HealthCheckProcessor implements org.apache.camel.Processor {

    @Override
    public void process(final Exchange exchange) throws Exception {
        final String healthCheckResult = exchange.getIn().getBody(String.class);
        log.info("Health Check String of the APP is: " + healthCheckResult);

        Map<String, Object> map = new ObjectMapper().readValue(healthCheckResult, new TypeReference<Map<String, Object>>(){});
        log.info("map read is: " + map);

        String exceptionMessages = ((Map<String, Object>)map.get("details")).entrySet().stream()
                .filter(e -> "DOWN".equals(((Map<String, Object>)e.getValue()).get("status")))
                .map(e -> e.getKey() + " component in the route is down")
                .collect(Collectors.joining("\n"));
        if (exceptionMessages.length() != 0) {
            log.info("Exception message is: " + exceptionMessages);
            exchange.getIn().setHeader("error", true);
            exchange.getIn().setBody(exceptionMessages);
            exchange.setProperty(Exchange.EXCEPTION_CAUGHT, exceptionMessages);
        }
    }
}
