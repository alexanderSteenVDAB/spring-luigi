package be.vdab.luigi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalTime;

@Controller
class IndexController {
    @GetMapping("/")
    public ModelAndView index() {
        var morgenOfMiddag = LocalTime.now().getHour() < 12 ? "morgen" : "middag";
        return new ModelAndView("index", "moment", morgenOfMiddag);
    }
}
