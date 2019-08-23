package com.learncamel.learncamelspringboot.route;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
public class SimpleCamelRouteMockTest {


    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    Environment environment;

    @EndpointInject(uri = "mock:output1")
    private MockEndpoint mockEndpoint1;

    @EndpointInject(uri = "mock:output2")
    private MockEndpoint mockEndpoint2;

    @EndpointInject(uri = "mock:output3")
    private MockEndpoint mockEndpoint3;


    @Test
    public void moveFile() throws InterruptedException {
        final String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,500";
        mockEndpoint1.expectedBodiesReceived(message);

        mockEndpoint2.expectedBodiesReceived(
                "insert into items (sku, item_description, price) values ('100', 'Samsung TV', 500.00)",
                "insert into items (sku, item_description, price) values ('101', 'LG TV', 500.00)"
        );

        mockEndpoint3.expectedBodiesReceived("Data updated successfully");

        producerTemplate.sendBodyAndHeader(environment.getProperty("startRoute"), message, "env",
                environment.getProperty("spring.profiles.active"));

        mockEndpoint1.assertIsSatisfied();
        mockEndpoint2.assertIsSatisfied();
        mockEndpoint3.assertIsSatisfied();
    }
}
