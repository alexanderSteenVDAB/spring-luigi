package be.vdab.luigi.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record VanTotPrijsForm(@NotNull @PositiveOrZero BigDecimal van, @NotNull @PositiveOrZero BigDecimal tot) {
}
