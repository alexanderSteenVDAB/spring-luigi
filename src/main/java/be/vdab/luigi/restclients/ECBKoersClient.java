package be.vdab.luigi.restclients;

import be.vdab.luigi.exceptions.KoersClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;

@Component
@Qualifier("ECB")
class ECBKoersClient implements KoersClient {
    private final URL url;
    private final XMLInputFactory factory = XMLInputFactory.newInstance();

    public ECBKoersClient() {
        try {
            url = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
        } catch (MalformedURLException e) {
            throw new KoersClientException("ECB URL is verkeerd.", e);
        }
    }

    @Override
    public BigDecimal getDollarKoers() {
        try (var stream = url.openStream()) {
            for (var reader = factory.createXMLStreamReader(stream);
                 reader.hasNext();) {
                reader.next();
                if (reader.isStartElement()) {
                    if ("USD".equals(reader.getAttributeValue(0))) {
                        return new BigDecimal(reader.getAttributeValue(1));
                    }
                }
            }
            throw new KoersClientException("XML kan ECB bevat geen USD.");
        } catch (IOException | XMLStreamException | NumberFormatException e) {
            throw new KoersClientException("Kan koers niet lezen via ECB.", e);
        }
    }
}
