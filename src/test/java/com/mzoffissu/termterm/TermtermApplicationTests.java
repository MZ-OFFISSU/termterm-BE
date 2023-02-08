package com.mzoffissu.termterm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class TermtermApplicationTests {

	@Value("${server.host}")
	private String host;

	@Test
	@DisplayName("LocalDateTime.now()")
	public void f() throws Exception{
	    //given
		System.out.println(host);

	    //when

	    //then
	}


}
