package be.vdab.luigi.controllers;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.KoersClientException;
import be.vdab.luigi.forms.VanTotPrijsForm;
import be.vdab.luigi.services.EuroService;
import be.vdab.luigi.services.PizzaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
@RequestMapping("pizzas")
public class PizzaController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EuroService euroService;
    private final PizzaService pizzaService;

    public PizzaController(EuroService euroService, PizzaService pizzaService) {
        this.euroService = euroService;
        this.pizzaService = pizzaService;
    }

    @GetMapping
    public ModelAndView findAll() {
        return new ModelAndView("pizzas", "allePizzas", pizzaService.findAll());
    }

    @GetMapping("{id}")
    public ModelAndView findById(@PathVariable long id) {
        var modelAndView = new ModelAndView("pizza");
        pizzaService.findById(id).ifPresent(pizza -> {
            modelAndView.addObject("pizza", pizza);
            try {
                modelAndView.addObject("inDollar", euroService.naarDollar(pizza.getPrijs()));
            } catch (KoersClientException e) {
                logger.error("Kan dollar koers niet lezen", e);
            }
        });
        return modelAndView;
    }

    @GetMapping("prijzen")
    public ModelAndView prijzen() {
        return new ModelAndView("pizzasperprijs", "prijzen", pizzaService.findUniekePrijzen());
    }

    @GetMapping("prijzen/{prijs}")
    public ModelAndView findByPrijs(@PathVariable BigDecimal prijs) {
        return new ModelAndView("pizzasperprijs", "pizzas", pizzaService.findByPrijs(prijs))
                .addObject("prijzen", pizzaService.findUniekePrijzen());
    }

    @GetMapping("aantalpizzasperprijs")
    public ModelAndView findAantalPizzasPerPrijs() {
        return new ModelAndView("aantalpizzasperprijs",
                "aantalPizzasPerPrijs", pizzaService.findAantalPizzasPerPrijs());
    }

    @GetMapping("vantotprijs/form")
    public ModelAndView vanTotPrijsForm() {
        return new ModelAndView("vantotprijs")
                .addObject(new VanTotPrijsForm(null, null));
    }

    @GetMapping("vantotprijs")
    public ModelAndView findByPrijsBetween(@Valid VanTotPrijsForm form, Errors errors) {
        var modelAndView = new ModelAndView("vantotprijs");
        if (errors.hasErrors()) {
            return modelAndView;
        }

        return modelAndView.addObject("pizzas", pizzaService.findByPrijsBetween(form.van(), form.tot()));
    }

    @GetMapping("toevoegen/form")
    public ModelAndView toevoegenForm() {
        return new ModelAndView("toevoegen")
                .addObject(new Pizza(0, "", null, false));
    }

    @PostMapping
    public String toevoegen(@Valid Pizza pizza, Errors errors, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "toevoegen";
        }
        redirect.addAttribute("idNieuwePizza", pizzaService.create(pizza));
        return "redirect:/pizzas";
    }
}
