package be.vdab.luigi.services;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.repositories.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PizzaServiceTest {
    @Mock
    private PizzaRepository repository;
    private PizzaService service;

    @BeforeEach
    void beforeEach() {
        service = new PizzaService(repository);
    }

    @Test
    void create() {
        var pizza = new Pizza(0, "test", BigDecimal.TEN, false);
        when(repository.create(pizza)).thenReturn(1L);
        assertThat(service.create(pizza)).isEqualTo(1L);
        verify(repository).create(pizza);
    }
}