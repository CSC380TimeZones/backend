package com.jetlagjelly.backend;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

  @Test
  void contextLoads() throws GeneralSecurityException, IOException {
    Endpoints.getMeetingConstraints(
        "bmclean2@oswego.edu JetLagJellyFan@gmail.com", 120, 1682913600000L,
        1683345540000L);
  }

  @Test
  void setTimezone() {
    Endpoints.setTimezone("bmclean2@oswego.edu", "+5");
  }

  @Test
  void addTimeRange() {
    Endpoints.addTimeRange("bmclean2@oswego.edu", "preferred", 15, 18,
        Arrays.asList(false, true, true, true, true, true, false));
  }

}
