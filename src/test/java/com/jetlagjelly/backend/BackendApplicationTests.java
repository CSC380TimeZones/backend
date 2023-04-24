package com.jetlagjelly.backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() throws GeneralSecurityException, IOException {
		Endpoints.getMeetingConstraints("bmclean2@oswego.edu randomperson@oswego.edu", 120, 1682308800000L, 1682715600000L);
	}

	@Test
	void setTimezone() {
		Endpoints.setTimezone("bmclean2@oswego.edu", "America/New_York");
	}

}
