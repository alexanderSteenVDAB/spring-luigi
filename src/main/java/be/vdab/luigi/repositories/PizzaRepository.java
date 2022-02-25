package be.vdab.luigi.repositories;

import be.vdab.luigi.domain.Pizza;
import be.vdab.luigi.exceptions.PizzaNietGevondenException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class PizzaRepository {
    private final JdbcTemplate template;
    private final SimpleJdbcInsert insert;
    private final RowMapper<Pizza> pizzaMapper = (result, rowNum) ->
            new Pizza(result.getLong("id"), result.getString("naam"),
                    result.getBigDecimal("prijs"), result.getBoolean("pikant"));
    private final RowMapper<BigDecimal> prijsMapper = (result, rowNum) -> result.getBigDecimal("prijs");

    public PizzaRepository(JdbcTemplate template) {
        this.template = template;
        insert = new SimpleJdbcInsert(template)
                .withTableName("pizzas")
                .usingGeneratedKeyColumns("id");
    }

    public long findAantal() {
        var sql = """
                select count(*)
                from pizzas
                """;
        return template.queryForObject(sql, Long.class);
    }

    public void delete(long id) {
        var sql = """
                delete from pizzas
                where id = ?
                """;
        template.update(sql, id);
    }

    public void update(Pizza pizza) {
        var sql = """
                update pizzas
                set naam = ?, prijs = ?, pikant = ?
                where id = ?
                """;
        if (template.update(sql, pizza.getNaam(), pizza.getPrijs(), pizza.isPikant(), pizza.getId()) == 0) {
            throw new PizzaNietGevondenException();
        }
    }

    public long create(Pizza pizza) {
        return insert.executeAndReturnKey(
                Map.of("naam", pizza.getNaam(),
                        "prijs", pizza.getPrijs(),
                        "pikant", pizza.isPikant()))
                .longValue();
    }

    public List<Pizza> findAll() {
        var sql = """
                select id, naam, prijs, pikant
                from pizzas
                order by id
                """;
        return template.query(sql, pizzaMapper);
    }

    public List<Pizza> findByPrijsBetween(BigDecimal van, BigDecimal tot) {
        var sql = """
                select id, naam, prijs, pikant
                from pizzas
                where prijs between ? and ?
                order by prijs
                """;
        return template.query(sql, pizzaMapper, van, tot);
    }

    public Optional<Pizza> findById(long id) {
        try {
            var sql = """
                    select id, naam, prijs, pikant
                    from pizzas
                    where id = ?
                    """;
            return Optional.ofNullable(template.queryForObject(sql, pizzaMapper, id));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<BigDecimal> findUniekePrijzen() {
        var sql = """
                select distinct prijs
                from pizzas
                order by prijs
                """;
        return template.query(sql, prijsMapper);
    }

    public List<Pizza> findByPrijs(BigDecimal prijs) {
        var sql = """
                select id, naam, prijs, pikant
                from pizzas
                where prijs = ?
                order by naam
                """;
        return template.query(sql, pizzaMapper, prijs);
    }

    public List<Pizza> findByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        var sql = """
                select id, naam, prijs, pikant
                from pizzas
                where id in (
                """ + "?,".repeat(ids.size() - 1)
                + "?) order by id";
        return template.query(sql, pizzaMapper, ids.toArray());
    }

}
