package be.vdab.luigi.controllers;

import be.vdab.luigi.domain.Pizza;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

@Controller
public class PizzaController {
    private final Pizza[] allePizzas = {new Pizza(1, "Prosciutto", BigDecimal.valueOf(4), true),
            new Pizza(2, "Margherita", BigDecimal.valueOf(5), false),
            new Pizza(3, "Calzone", BigDecimal.valueOf(4), false)};

    @GetMapping("/pizzas")
    public ModelAndView findAll() {
        return new ModelAndView("pizzas", "allePizzas", allePizzas);
    }

    private Optional<Pizza> findByIdHelper(long id) {
        return Arrays.stream(allePizzas).filter(pizza -> pizza.getId()==id).findFirst();
    }

    @GetMapping("/pizzas/{id}")
    public ModelAndView findById(@PathVariable long id) {
        var modelAndView = new ModelAndView("pizza");
        findByIdHelper(id).ifPresent(gevondenPizza -> modelAndView.addObject("pizza", gevondenPizza));
        return modelAndView;
    }
}
