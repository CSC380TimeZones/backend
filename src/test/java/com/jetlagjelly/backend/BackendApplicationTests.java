package com.jetlagjelly.backend;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  @Test
  void addNewUser() {

    List<String> sca = new ArrayList<>();
    sca.add("create");
    List<String> cida = new ArrayList<>();
    cida.add("Phases of the Moon");
    List<List<Boolean>> dyya = new ArrayList<>();
    List<Boolean> dya = new ArrayList<>();
    dya.add(0, true);
    dya.add(1, false);
    dya.add(2, false);
    dya.add(3, false);
    dya.add(4, false);
    dya.add(5, false);
    dya.add(6, false);
    List<Boolean> dyaa = new ArrayList<>();
    dyaa.add(0, false);
    dyaa.add(1, false);
    dyaa.add(2, false);
    dyaa.add(3, false);
    dyaa.add(4, true);
    dyaa.add(5, false);
    dyaa.add(6, false);
    dyya.add(dya);
    dyya.add(dyaa);

    List<List<Boolean>> sda = new ArrayList<>();
    List<Boolean> sd = new ArrayList<>();
    sd.add(0, false);
    sd.add(1, true);
    sd.add(2, false);
    sd.add(3, false);
    sd.add(4, false);
    sd.add(5, false);
    sd.add(6, false);
    sda.add(sd);
    List<Double> sta = new ArrayList<>();
    sta.add(9.50);
    sta.add(14.00);
    List<Double> ena = new ArrayList<>();
    ena.add(13.00);
    ena.add(18.00);
    List<Double> ss = new ArrayList<>();
    ss.add(4.00);
    List<Double> se = new ArrayList<>();
    se.add(10.00);

    Endpoints.addNewUser("JetLagn@gmail.com", -5, cida, sta, ena, dyya, ss, se,
        sda);
  }

  @Test
  void currentUser() {
    System.out.println(
        Endpoints.currentUser("bmclean2@oswego.edu").calendar_id);
    System.out.println(Endpoints.currentUser("bmclean2@oswego.edu").days);
  }
}
