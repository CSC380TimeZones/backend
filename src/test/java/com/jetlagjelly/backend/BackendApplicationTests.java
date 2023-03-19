package com.jetlagjelly.backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.jetlagjelly.backend.Endpoints;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
		assertEquals("Hello World!", Endpoints.helloWorld("World"));
	}

}
