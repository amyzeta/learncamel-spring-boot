package com.learncamel.learncamelspringboot.route;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Warning: tests expect DB to contain specific data when they start :(
 * (but also, they don't verify anything that will fail when that data is not what it expects :( )
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SimpleCamelRouteTest {

    final Logger log = LoggerFactory.getLogger(SimpleCamelRouteTest.class);

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    Environment environment;

    @Before
    public void startCleanup() throws IOException {
        FileUtils.cleanDirectory(new File("data/input"));
        FileUtils.deleteDirectory(new File("data/output"));
    }

    @Test
    public void moveFile_add() throws InterruptedException, IOException {
        final String message = "type,sku#,itemdescription,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,500";
        final String fileName = "fileTest.txt";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"), message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);
        File outFile = new File("data/output/" + fileName);
        assertTrue(outFile.exists());

        String output = new String(Files.readAllBytes(Paths.get("data/output/success.txt")));
        assertEquals("Data updated successfully", output);
    }

    @Test
    public void moveFile_add_throwsException() throws InterruptedException {
        final String message = "type,sku#,itemdescription,price\n" +
                "ADD,,Samsung TV,500";
        final String fileName = "fileTest.txt";


        log.info("sending now");
        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"), message, Exchange.FILE_NAME, fileName);

        // It takes about 6 seconds to send an email, and we won't move file to the error folder until that happens
        Thread.sleep(10000);
        log.info("starting verifications");
        File outFile = new File("data/output/" + fileName);
        assertTrue(outFile.exists());

        assertFalse(new File("data/output/success.txt").exists());
        assertTrue(new File("data/input/error/" + fileName).exists());
    }

    @Test
    public void moveFile_update() throws InterruptedException, IOException {
        final String message = "type,sku#,itemdescription,price\n" +
                "UPDATE,100,Samsung TV,600";
        final String fileName = "fileTest.txt";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"), message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);
        File outFile = new File("data/output/" + fileName);
        assertTrue(outFile.exists());

        String output = new String(Files.readAllBytes(Paths.get("data/output/success.txt")));
        assertEquals("Data updated successfully", output);
    }
    @Test
    public void moveFile_delete() throws InterruptedException, IOException {
        final String message = "type,sku#,itemdescription,price\n" +
                "DELETE,100,Samsung TV,600";
        final String fileName = "fileTest.txt";

        producerTemplate.sendBodyAndHeader(environment.getProperty("fromRoute"), message, Exchange.FILE_NAME, fileName);

        Thread.sleep(3000);
        File outFile = new File("data/output/" + fileName);
        assertTrue(outFile.exists());

        String output = new String(Files.readAllBytes(Paths.get("data/output/success.txt")));
        assertEquals("Data updated successfully", output);
    }
}
