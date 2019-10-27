package com.learncamel.learncamelspringboot.route;

import com.learncamel.learncamelspringboot.process.MailProcessor;
import com.learncamel.learncamelspringboot.process.BuildSqlProcessor;
import com.learncamel.learncamelspringboot.process.ValidateDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ActiveMQRoute extends AbstractMessagingRoute {

    public ActiveMQRoute() {
        super("{{activemqRoute.from}}", "{{activemqRoute.errorTo}}");
    }
}
