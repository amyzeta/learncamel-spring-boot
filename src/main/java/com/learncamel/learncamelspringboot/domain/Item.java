package com.learncamel.learncamelspringboot.domain;

import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.math.BigDecimal;

@Data
@CsvRecord(separator = ",", skipFirstLine = true)
public class Item {

    @DataField(pos = 1)
    private String transactionType;

    @DataField(pos = 2)
    private String sku;

    @DataField(pos = 3)
    private String itemDescription;

    @DataField(pos = 4, precision = 2)
    private BigDecimal price;

}

