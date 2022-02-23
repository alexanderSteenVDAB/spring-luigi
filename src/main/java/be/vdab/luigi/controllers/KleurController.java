package be.vdab.luigi.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller @RequestMapping("kleuren")
class KleurController {
    private static final int EEN_JAAR_IN_SECONDEN = 31_536_000;

    @GetMapping
    public ModelAndView toonPagina(@CookieValue Optional<String> kleur) {
        var modelAndView = new ModelAndView("kleuren");
        kleur.ifPresent(hetKleur -> modelAndView.addObject("kleur", hetKleur));
        return modelAndView;
    }

    @GetMapping("{kleur}")
    public String kiesKleur(@PathVariable String kleur, HttpServletResponse response) {
        var cookie = new Cookie("kleur", kleur);
        cookie.setMaxAge(EEN_JAAR_IN_SECONDEN);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "kleuren";
    }
}
