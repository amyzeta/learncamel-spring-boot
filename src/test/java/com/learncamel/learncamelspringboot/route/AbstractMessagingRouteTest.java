package com.learncamel.learncamelspringboot.route;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertNotNull;

// don't have a damn clue what these tests are supposed to achieve
// don't know why Kafka gets an item out of the consumerTemplate and ActiveMq gets null
// don't know why a test that checks for null or a lack thereof is something to celebrate. Sigh.
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public abstract class AbstractMessagingRouteTest {

    protected final String from;
    protected final String errorTo;

    AbstractMessagingRouteTest(final String from, final String errorTo) {
        this.from = from;
        this.errorTo = errorTo;
    }

    @Autowired
    protected ProducerTemplate producerTemplate;

    @Autowired
    protected ConsumerTemplate consumerTemplate;

    @Autowired
    protected Environment environment;


    @Test
    public void success() {
        final InputStream resource = this.getClass().getResourceAsStream("input.json");
        String input = new Scanner(resource, StandardCharsets.UTF_8).useDelimiter("\n").next();
        assertNotNull(producerTemplate.requestBody(environment.getProperty(from), input));
    }

}
