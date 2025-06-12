package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.Company;
import com.example.baoNgoCv.model.Industry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustryRepository extends JpaRepository<Industry, Long> {


    Industry findByName(String name);
}
