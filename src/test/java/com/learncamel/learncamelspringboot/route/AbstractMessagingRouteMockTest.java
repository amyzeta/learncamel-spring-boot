package com.learncamel.learncamelspringboot.route;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
public abstract class AbstractMessagingRouteMockTest {

    private final String from;

    AbstractMessagingRouteMockTest(final String from) {
        this.from = from;
    }

    @Autowired
    private ProducerTemplate producerTemplate;


    @Autowired
    private Environment environment;

    @Test
    public void unmarshalTest() {
        final InputStream resource = this.getClass().getResourceAsStream("input.json");
        String input = new Scanner(resource, StandardCharsets.UTF_8).useDelimiter("\n").next();
        String output = (String) producerTemplate.requestBodyAndHeader(environment.getProperty(from), input, "env", "mock");
        assertEquals("insert into items (sku, item_description, price) values ('100', 'SamsungTV', 500.00)", output);
    }

    @Test(expected = CamelExecutionException.class)
    public void unmarshalTest_error() {
        final InputStream resource = this.getClass().getResourceAsStream("errorInput.json");
        String input = new Scanner(resource, StandardCharsets.UTF_8).useDelimiter("\n").next();
        producerTemplate.requestBodyAndHeader(environment.getProperty(from), input, "env", "mock");
    }

}
