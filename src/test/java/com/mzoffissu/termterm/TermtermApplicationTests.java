package com.mzoffissu.termterm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class TermtermApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("LocalDateTime.now()")
	public void f() throws Exception{
	    //given
		System.out.println(LocalDateTime.now());

	    //when

	    //then
	}


}
