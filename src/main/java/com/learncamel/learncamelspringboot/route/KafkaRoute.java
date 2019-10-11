package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.alert.MailProcessor;
import com.learncamel.learncamelspringboot.domain.Item;
import com.learncamel.learncamelspringboot.exception.DataException;
import com.learncamel.learncamelspringboot.process.BuildSqlProcessor;
import com.learncamel.learncamelspringboot.process.SuccessProcessor;
import com.learncamel.learncamelspringboot.process.ValidateDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class KafkaRoute extends RouteBuilder {

    @Autowired
    Environment environment;

    @Autowired
    DataSource dataSource;


    @Autowired
    MailProcessor mailProcessor;

    @Autowired
    ValidateDataProcessor validateDataProcessor;

    @Autowired
    BuildSqlProcessor buildSqlProcessor;

    @Override
    public void configure() {
        log.info("Starting the Camel Route");
        GsonDataFormat itemFormat = new GsonDataFormat(Item.class);
        onException(PSQLException.class).log(LoggingLevel.ERROR, "PSQLException in the route ${body}")
                .maximumRedeliveries(3).redeliveryDelay(3000).retryAttemptedLogLevel(LoggingLevel.ERROR);
        onException(DataException.class, RuntimeException.class).log(LoggingLevel.ERROR, "RuntimeException in the route ${body}")
                .choice()
                    .when((header("env").isNotEqualTo("mock")))
                        .process(mailProcessor)
                    .end()
                .to("{{kafkaErrorRoute}}");

        from("{{kafkaFromRoute}}")
                .log("Read message from Kafka: ${body}")
                .unmarshal(itemFormat)
                .log("Unmarshalled message is ${body}")
                .process(validateDataProcessor)
                .process(buildSqlProcessor)
                .to("{{kafkaToRoute}}")
                .to("{{kafkaSelectNode}}")
                .log("Read item from DB is ${body}");
        log.info("Ending the Camel Route");
    }
}
