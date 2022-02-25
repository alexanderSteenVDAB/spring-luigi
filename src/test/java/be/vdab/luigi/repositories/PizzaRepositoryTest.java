package be.vdab.luigi.repositories;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.PizzaNietGevondenException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@JdbcTest
@Import(PizzaRepository.class)
@Sql("/insertPizzas.sql")
class PizzaRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String PIZZAS = "pizzas";
    private final PizzaRepository repository;


    PizzaRepositoryTest(PizzaRepository repository) {
        this.repository = repository;
    }

    @Test
    void findAantal() {
        assertThat(repository.findAantal()).isEqualTo(countRowsInTable(PIZZAS));
    }

    @Test
    void findAllGeeftAllePizzasGesorteerdOpId() {
        assertThat(repository.findAll())
                .hasSize(countRowsInTable(PIZZAS))
                .extracting(Pizza::getId)
                .isSorted();
    }

    @Test
    void create() {
        var id = repository.create(new Pizza(0, "test2", BigDecimal.TEN, false));
        assertThat(id).isPositive();
        assertThat(countRowsInTableWhere(PIZZAS, "id = " + id)).isOne();
    }

    private long idVanTestPizza() {
        return jdbcTemplate.queryForObject("select id from pizzas where naam = 'Test'", Long.class);
    }

    private long idVanTest2Pizza() {
        return jdbcTemplate.queryForObject("select id from pizzas where naam = 'Test2'", Long.class);
    }

    @Test
    void delete() {
        var id = idVanTestPizza();
        repository.delete(id);
        assertThat(countRowsInTableWhere(PIZZAS, "id = " + id)).isZero();
    }

    @Test
    void findById() {
        assertThat(repository.findById(idVanTestPizza()))
                .hasValueSatisfying(pizza -> assertThat(pizza.getNaam()).isEqualTo("test"));
    }

    @Test
    void findByOnbestaandeIdVindtGeenPizza() {
        assertThat(repository.findById(-1)).isEmpty();
    }

    @Test
    void update() {
        var id = idVanTestPizza();
        var pizza = new Pizza(id, "test", BigDecimal.TEN, false);
        repository.update(pizza);
        assertThat(countRowsInTableWhere(PIZZAS, "prijs=10 and id=" + id)).isOne();
    }

    @Test
    void updateOnbestaandePizzaGeeftEenFout() {
        assertThatExceptionOfType(PizzaNietGevondenException.class).isThrownBy(
                () -> repository.update(new Pizza(-1, "test", BigDecimal.TEN, false)));
    }

    @Test
    void findByPrijsBetween() {
        var van = BigDecimal.ONE;
        var tot = BigDecimal.TEN;
        assertThat(repository.findByPrijsBetween(van, tot))
                .hasSize(countRowsInTableWhere(PIZZAS, "prijs between 1 and 10"))
                .extracting(Pizza::getPrijs)
                .allSatisfy(prijs -> assertThat(prijs).isBetween(van ,tot))
                .isSorted();
    }

    @Test
    void findUniekePrijsenGeeftPrijzenOplopend() {
        assertThat(repository.findUniekePrijzen())
                .hasSize(jdbcTemplate.queryForObject("select count(distinct prijs) from pizzas", Integer.class))
                .doesNotHaveDuplicates()
                .isSorted();
    }

    @Test
    void findByPrijs() {
        assertThat(repository.findByPrijs(BigDecimal.TEN))
                .hasSize(countRowsInTableWhere(PIZZAS, "prijs = 10"))
                .allSatisfy(pizza -> assertThat(pizza.getPrijs()).isEqualByComparingTo(BigDecimal.TEN))
                .extracting(Pizza::getNaam)
                .isSortedAccordingTo(String::compareToIgnoreCase);
    }

    @Test
    void findByIds() {
        long id1 = idVanTestPizza();
        long id2 = idVanTest2Pizza();
        assertThat(repository.findByIds(Set.of(id1, id2)))
                .extracting(Pizza::getId)
                .containsOnly(id1, id2)
                .isSorted();
    }

    @Test
    void findByIdsGeeftLegeVerzamelingPizzasBijLegeVerzamelingIds() {
        assertThat(repository.findByIds(Set.of())).isEmpty();
    }

    @Test
    void findByIdsGeeftLegeVerzamelingPizzasBijOnbestaandeIds() {
        assertThat(repository.findByIds(Set.of(-1L))).isEmpty();
    }

    @Test
    void findAantalPizzasPerPrijs() {
        var aantalPizzasPerPrijs = repository.findAantalPizzasPerPrijs();
        assertThat(aantalPizzasPerPrijs)
                .hasSize(jdbcTemplate.queryForObject("select count(distinct prijs) from pizzas", Integer.class));
        var rij1 = aantalPizzasPerPrijs.get(0);
        assertThat(rij1.aantal()).isEqualTo(countRowsInTableWhere(PIZZAS, "prijs =" + rij1.prijs()));
    }
}