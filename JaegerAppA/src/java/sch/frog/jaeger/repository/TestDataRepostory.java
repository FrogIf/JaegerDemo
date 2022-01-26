package sch.frog.jaeger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sch.frog.jaeger.model.TestData;

public interface TestDataRepostory extends JpaRepository<TestData, Integer>{
    
}
