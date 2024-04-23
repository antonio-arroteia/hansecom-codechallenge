package com.hanse.codechallenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest
@EnableWebMvc
@ActiveProfiles("test")
class CodechallengeApplicationTests {

	@Test
	void contextLoads() {
	}

}
