package com.tihor.ocr.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.tihor.ocr.domain.Model;
public class TestClass {
	UserDetailsRepository edsUserDetailRepository;

	@Test public void persistedMovieShouldBeRetrievableFromGraphDb() {
	     Model user1 = new Model();
	     user1.setRollNo("123abc");
	     user1.setName("TestUser1");
	     user1.setAge(34);
	     user1 = edsUserDetailRepository.save(user1);

	     Model user11 = edsUserDetailRepository.findOne(12L);
	     System.out.println(user11);
	     assertEquals(user1.age,user11.age);  
	}	

	
	
	public static void main(String[] args) {
		ApplicationContext  ctx=new ClassPathXmlApplicationContext("EDSspringConfig.xml");
		UserDetailsRepository m=ctx.getBean(UserDetailsRepository.class);
	}    
}
