package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.alert.MailProcessor;
import com.learncamel.learncamelspringboot.domain.Item;
import com.learncamel.learncamelspringboot.exception.DataException;
import com.learncamel.learncamelspringboot.process.BuildSqlProcessor;
import com.learncamel.learncamelspringboot.process.SuccessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class SimpleCamelRoute extends RouteBuilder {

    @Autowired
    Environment environment;

    @Autowired
    DataSource dataSource;

    @Autowired
    BuildSqlProcessor buildSqlProcessor;


    @Autowired
    SuccessProcessor successProcessor;

    @Autowired
    MailProcessor mailProcessor;

    @Override
    public void configure() {
        log.info("Starting the Camel Route");

        DataFormat bindy = new BindyCsvDataFormat(Item.class);

        // note- this is what was done in the tutorial but as I understand it the dead letter channel is supposed to be
        // for messages that can't be delivered, not just for any old type of error.
        errorHandler(deadLetterChannel("log:errorInRoute?level=ERROR&showProperties=true"));
        onException(PSQLException.class).log(LoggingLevel.ERROR, "PSQLException in the route ${body}")
                 .maximumRedeliveries(3).redeliveryDelay(1000).retryAttemptedLogLevel(LoggingLevel.ERROR);
        onException(DataException.class).log(LoggingLevel.ERROR, "DataException in the route ${body}")
             .choice()
                .when((header("env").isNotEqualTo("mock")))
                    .log("DataException: non-mock env flow and the body is ${body}")
                     // dear lord the mail processor is slow, in reality I would definitely move this to a background thread.
                     .process(mailProcessor)
                .otherwise()
                    .log("DataException: mock env flow and the body is ${body}")
             .end()        ;

        //Using 2 different ways of getting environment variables for demonstration purposes only
        from("{{startRoute}}").routeId("mainRoute")
                .log("Timer Invoked and the body is ${body} " + environment.getProperty("message"))
                .choice()
                    .when((header("env").isNotEqualTo("mock")))
                        .log("PollEnrich: non-mock env flow and the body is ${body}")
                        .pollEnrich("{{fromRoute}}")
                    .otherwise()
                        .log("PollEnrich: mock env flow and the body is ${body}")
                .end()
                .to("{{toRoute1}}")
                .log("unmarshalling and the body is ${body}")
                .unmarshal(bindy)
                .log("The unmarshalled object is ${body}")
                .split(body())
                    .log("Record is ${body}")
                    .process(buildSqlProcessor)
                    .to("{{toRoute2}}")
                .end()
            .process(successProcessor)
            .to("{{toRoute3}}");
        log.info("Ending the Camel Route");
    }
}
