package com.learncamel.learncamelspringboot.route;

import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertNotNull;

public class KafkaRouteTest extends AbstractMessagingRouteTest {
    public KafkaRouteTest() {
        super("kafkaRoute.from", "kafkaRoute.errorTo");
    }

    @Test
    public void invalidSku() {
        final InputStream resource = this.getClass().getResourceAsStream("errorInput.json");
        String input = new Scanner(resource, StandardCharsets.UTF_8).useDelimiter("\n").next();
        Object result = producerTemplate.requestBody(environment.getProperty(from), input);
        String response = (String) consumerTemplate.receiveBody(environment.getProperty(errorTo));
        System.out.println("Response is: " + response);
        assertNotNull(response);
    }
}

