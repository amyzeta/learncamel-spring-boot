package com.learncamel.learncamelspringboot.route;

public class ActiveMqRouteTest extends AbstractMessagingRouteTest {
    public ActiveMqRouteTest() {
        super("activemqRoute.from", "activemqRoute.errorTo");
    }
}