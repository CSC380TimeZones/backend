package com.jetlagjelly.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

	@Test
	void addTimeRange() {
		Endpoints.addTimeRange("bmclean2@oswego.edu", 1500, 1800, 1);
	}

	@Test
	void addNewUser() {

		List<String> sca = new ArrayList<>();
		sca.add("create");
		List<String> cida = new ArrayList<>();
		cida.add("Phases of the Moon");
		List<Integer> dya = new ArrayList<>();
		dya.add(1);
		dya.add(5);
		List<Integer> sd = new ArrayList<>();
		sd.add(2);
		List<Integer> sta = new ArrayList<>();
		sta.add(300);
		sta.add(1200);
		List<Integer> ena = new ArrayList<>();
		ena.add(400);
		ena.add(2000);
		List<Integer> ss = new ArrayList<>();
		ss.add(200);
		List<Integer> se = new ArrayList<>();
		se.add(800);

		Endpoints.addNewUser("JetLagJellyFan@gmail.com",
				"MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
				"IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk", 3600, sca, "Bearer",
				"America/New_York", cida, sta, ena, dya, ss,
				se, sd);
	}

}