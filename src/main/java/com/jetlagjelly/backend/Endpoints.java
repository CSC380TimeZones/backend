package com.jetlagjelly.backend;

import com.jetlagjelly.backend.models.MeetingContraint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;

@RestController
public class Endpoints {

    @RequestMapping(method = RequestMethod.GET, value = "/email")
    public static MeetingContraint getMeetingConstraints(@RequestParam(value = "email", defaultValue = "No email found!") String email, @RequestParam(value = "mtngLength", defaultValue = "60") int mtngLength, @RequestParam(value = "startDay", defaultValue = "DayOfWeek.MONDAY") DayOfWeek startDay, @RequestParam(value = "endDay", defaultValue = "DayOfWeek.FRIDAY") DayOfWeek endDay) {

        MeetingContraint mc = new MeetingContraint();
        mc.setEmail(email);
        mc.setMtngLength(mtngLength);
        mc.setStartDay(startDay);
        mc.setEndDay(endDay);

        return mc;
    }
}
