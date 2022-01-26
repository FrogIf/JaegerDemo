package sch.frog.jaeger.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sch.frog.jaeger.model.TestData;
import sch.frog.jaeger.service.ITestDataService;

@RestController
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    @Autowired
    private RestTemplate restTemplate;

    @Value("${frog.app-b.address}")
    private String appBAddress;

    @Autowired
    private ITestDataService testDataService;

    @RequestMapping("/hello")
    public List<TestData> hello(){
        String url = appBAddress + "/hello";
        ResponseEntity<String> res = restTemplate.getForEntity(url, String.class);
        logger.info("call app b , url : {}, response : {}", url, res);
        return testDataService.queryList();
    }

    @RequestMapping("/jdbc")
    public List<Map<String, Object>> jdbc(){
        return testDataService.queryListByJdbc();
    }

    @RequestMapping("/haha")
    public String haha(){
        TestData testData = new TestData();
        testData.setName(UUID.randomUUID().toString());
        testDataService.save(testData);
        return "success";
    }

}
