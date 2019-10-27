package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.process.MailProcessor;
import com.learncamel.learncamelspringboot.domain.Item;
import com.learncamel.learncamelspringboot.exception.DataException;
import com.learncamel.learncamelspringboot.process.BuildSqlProcessor;
import com.learncamel.learncamelspringboot.process.ValidateDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.gson.GsonDataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
public abstract class AbstractMessagingRoute extends RouteBuilder {

    private final String from;
    private final String errorTo;

    protected AbstractMessagingRoute(final String from, final String errorTo) {
        this.from = from;
        this.errorTo = errorTo;
    }

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
                .to(errorTo);

        from(from)
                .log("Read message: ${body}")
                .unmarshal(itemFormat)
                .log("Unmarshalled message is ${body}")
                .process(validateDataProcessor)
                .process(buildSqlProcessor)
                .to("{{toRoute}}")
                .to("{{selectNode}}")
                .log("Read item from DB is ${body}");
        log.info("Ending the Camel Route");
    }

}
