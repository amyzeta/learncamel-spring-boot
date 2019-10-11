package com.learncamel.learncamelspringboot.process;

import com.learncamel.learncamelspringboot.domain.Item;
import com.learncamel.learncamelspringboot.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

@Component
@Slf4j
public class BuildSqlProcessor implements org.apache.camel.Processor {
    @Override
    public void process(final Exchange exchange) throws Exception {
        Item item = (Item)exchange.getIn().getBody();
        log.info("Item in Processor is: " + item);

        final String query = getQuery(item);
        log.info("Query is " + query);
        exchange.getIn().setBody(query);
        exchange.getIn().setHeader("skuId", item.getSku());
    }

    private static String getQuery(final Item item) {
        final String sku = item.getSku();
        final String itemDescription = item.getItemDescription();
        final BigDecimal price = item.getPrice();

        switch (item.getTransactionType()) {
            case "ADD":
                return String.format("insert into items (sku, item_description, price) values ('%s', '%s', %s)", sku, itemDescription, price);
            case "UPDATE":
                return String.format("update items set price = %s where sku = '%s'", price, sku);
            case "DELETE":
                return String.format("delete from items where sku = '%s'", sku);
            default:
                throw new UnsupportedOperationException();
        }
    }
}
