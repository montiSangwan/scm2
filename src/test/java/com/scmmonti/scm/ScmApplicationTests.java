package com.scmmonti.scm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scm.service.EmailService;

@SpringBootTest
class ScmApplicationTests {

	@Autowired
	private EmailService emailService;

	@Test
	void contextLoads() {
	}

	@Test
	void sendEmailTest() {
		emailService.sendEmail("montisangwan1706@gmail.com",
		 "this is testing", "this is scm project");
	}

}
