package com.eresearch.dblp.consumer.it;


import com.eresearch.dblp.consumer.EresearchDblpConsumerApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = EresearchDblpConsumerApplication.class,
        properties = {"application.properties"}
)

@RunWith(SpringRunner.class)
public class SpecificationIT {


    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
    }


    //TODO
    @Test
    public void dblp_consumption_works_as_expected() {
        Assert.assertEquals(1, 1);
    }

    //TODO
    @Test
    public void dblp_consumption_works_as_expected_async_queue() {
        Assert.assertEquals(1, 1);
    }

}
