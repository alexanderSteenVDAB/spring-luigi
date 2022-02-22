package be.vdab.luigi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@Controller
@RequestMapping("os")
public class OSController {
    private static final String[] OSS = {"Windows", "Macintosh", "Android", "Linux"};

    @GetMapping
    public ModelAndView os(@RequestHeader("User-Agent") String userAgent) {
        var modelAndView = new ModelAndView("os");
        Arrays.stream(OSS).filter(userAgent::contains)
                .findFirst()
                .ifPresent(gevondenOs -> modelAndView.addObject("os", gevondenOs));
        return modelAndView;
    }
}
