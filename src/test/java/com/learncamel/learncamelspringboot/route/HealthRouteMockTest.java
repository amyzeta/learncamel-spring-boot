package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.process.HealthCheckProcessor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
public class HealthRouteMockTest {


    @Autowired
    Environment environment;

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    HealthCheckProcessor healthCheckProcessor;


    @Test
    public void healthRouteTest() {
        String input = "{\"status\":\"UP\",\"details\":{\"db\":{\"status\":\"UP\",\"details\":{\"database\":\"PostgreSQL\",\"hello\":1}},\"mail\":{\"status\":\"DOWN\",\"details\":{\"location\":\"smtp.gmail.com:587\"}}}}";
        String response = (String) producerTemplate.requestBodyAndHeader(environment.getProperty("healthRoute"), input, "env", environment.getProperty("spring.profiles.active"));
        assertEquals("mail component in the route is down", response);
    }

}
