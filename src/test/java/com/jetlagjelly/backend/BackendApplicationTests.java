package com.jetlagjelly.backend;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
		Endpoints.getMeetingConstraints("bmclean2@oswego.edu randomperson@oswego.edu", 120, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY);
		Endpoints.intersectSubmit();
	}





}
