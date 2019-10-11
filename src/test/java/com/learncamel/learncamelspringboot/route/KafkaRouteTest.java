package com.learncamel.learncamelspringboot.route;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertNotNull;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class KafkaRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private ConsumerTemplate consumerTemplate;

    @Test
    public void kafkaRoute_success() {
        final InputStream resource = this.getClass().getResourceAsStream("kafkaInput.json");
        String input = new Scanner(resource, StandardCharsets.UTF_8).useDelimiter("\n").next();
        String response = (String) producerTemplate.requestBody("kafka:inputItemTopic?brokers=localhost:9092", input);
        assertNotNull(response);
    }

    @Test
    public void kafkaRoute_invalidSku() {
        final InputStream resource = this.getClass().getResourceAsStream("kafkaErrorInput.json");
        String input = new Scanner(resource, StandardCharsets.UTF_8).useDelimiter("\n").next();
        producerTemplate.requestBody("kafka:inputItemTopic?brokers=localhost:9092", input);
        String response = (String) consumerTemplate.receiveBody("kafka:errorTopic?brokers=localhost:9092");
        System.out.println("Response is: " + response);
        assertNotNull(response);
    }
}

