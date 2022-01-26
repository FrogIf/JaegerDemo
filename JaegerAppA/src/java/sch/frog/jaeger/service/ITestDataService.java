package sch.frog.jaeger.service;

import java.util.List;
import java.util.Map;

import sch.frog.jaeger.model.TestData;

public interface ITestDataService {

    List<TestData> queryList();

    void save(TestData testData);

    List<Map<String, Object>> queryListByJdbc();
    
}
