package com.learncamel.learncamelspringboot.process;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class SuccessProcessor implements org.apache.camel.Processor {
    @Override
    public void process(final Exchange exchange) throws Exception {
        exchange.getIn().setBody("Data updated successfully");
    }
}
