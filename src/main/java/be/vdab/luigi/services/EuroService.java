package be.vdab.luigi.services;

import be.vdab.luigi.exceptions.KoersClientException;
import be.vdab.luigi.restclients.KoersClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class EuroService {
    private final KoersClient[] koersClients;

    public EuroService(KoersClient[] koersClients) {
        this.koersClients = koersClients;
    }

    public BigDecimal naarDollar(BigDecimal euro) {
        Exception laatste = null;
        for (var client : koersClients) {
            try {
                return euro.multiply(client.getDollarKoers()).setScale(2, RoundingMode.HALF_UP);
            } catch (KoersClientException e) {
                laatste = e;
            }
        }
        throw new KoersClientException("Kan dollar koers nergens lezen.", laatste);
    }
}
