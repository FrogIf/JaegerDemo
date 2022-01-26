package sch.frog.jaeger.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import sch.frog.jaeger.model.TestData;
import sch.frog.jaeger.repository.TestDataRepostory;
import sch.frog.jaeger.service.ITestDataService;

@Service
public class TestDataService implements ITestDataService {

    @Autowired
    private TestDataRepostory testDataRepostory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<TestData> queryList() {
        return testDataRepostory.findAll();
    }

    @Override
    public void save(TestData testData) {
        testDataRepostory.save(testData);
    }

    @Override
    public List<Map<String, Object>> queryListByJdbc() {
        return jdbcTemplate.queryForList("select * from test_data");
    }
    
}
