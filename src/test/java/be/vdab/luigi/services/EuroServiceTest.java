package be.vdab.luigi.services;

import be.vdab.luigi.restclients.KoersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EuroServiceTest {
    private EuroService euroService;
    @Mock
    private KoersClient koersClient;

    @BeforeEach
    void beforeEach() {
        euroService = new EuroService(new KoersClient[]{koersClient});
    }

    @Test
    void naarDollar() {
        when(koersClient.getDollarKoers()).thenReturn(BigDecimal.valueOf(1.1565));
        assertThat(euroService.naarDollar(BigDecimal.valueOf(3))).isEqualByComparingTo("3.47");
        verify(koersClient).getDollarKoers();
    }

}