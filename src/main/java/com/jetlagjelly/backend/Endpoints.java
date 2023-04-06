package com.jetlagjelly.backend;

import com.jetlagjelly.backend.models.MeetingContraint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Endpoints {

    @RequestMapping(method = RequestMethod.GET, value = "/email")
    public static MeetingContraint getMeetingConstraints(@RequestParam(value = "email", defaultValue = "No email found!") String email, @RequestParam(value = "mtngLength", defaultValue = "60") int mtngLength, @RequestParam(value = "daysInAdv", defaultValue = "7") int daysInAdv) {

        MeetingContraint mc = new MeetingContraint();
        mc.setEmail(email);
        mc.setMtngLength(mtngLength);
        mc.setDaysInAdv(daysInAdv);

        return mc;
    }
}
