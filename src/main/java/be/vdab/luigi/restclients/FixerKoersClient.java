package be.vdab.luigi.restclients;

import be.vdab.luigi.exceptions.KoersClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

@Component
@Qualifier("Fixer")
class FixerKoersClient implements KoersClient {
    private static final Pattern PATTERN = Pattern.compile("^.*\"USD\": *(\\d+\\.?\\d*).*$");

    private final URL url;

    public FixerKoersClient(@Value("${fixerKoersURL}") URL url) {
        this.url = url;
    }

    @Override
    public BigDecimal getDollarKoers() {
        try (var stream = url.openStream()) {
            var matcher = PATTERN.matcher(new String(stream.readAllBytes()));
            if (!matcher.matches()) {
                throw new KoersClientException("Fixer data ongeldig");
            }
            return new BigDecimal(matcher.group(1));
        } catch (IOException e) {
            throw new KoersClientException("Kan koers niet lezen via Fixer.", e);
        }
    }
}
