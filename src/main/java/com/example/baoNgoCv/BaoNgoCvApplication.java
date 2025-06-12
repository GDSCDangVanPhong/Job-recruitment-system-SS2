package com.example.baoNgoCv;

import com.example.baoNgoCv.dao.CompanyRepository;
import com.example.baoNgoCv.dao.UserRepository;
import com.example.baoNgoCv.model.Company;
import com.example.baoNgoCv.model.User;
import com.example.baoNgoCv.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class BaoNgoCvApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(BaoNgoCvApplication.class, args);

	}

}

