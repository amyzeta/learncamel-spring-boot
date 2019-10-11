package com.learncamel.learncamelspringboot.process;

import com.learncamel.learncamelspringboot.domain.Item;
import com.learncamel.learncamelspringboot.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@Slf4j
public class ValidateDataProcessor implements Processor {
    @Override
    public void process(final Exchange exchange) throws Exception {
        Item item = (Item) exchange.getIn().getBody();
        log.info("Item in ValidateDataProcessor: " + item);
        if (ObjectUtils.isEmpty(item.getSku())) {
            throw new DataException("Sku is null for " + item.getItemDescription());
        }
    }
}
