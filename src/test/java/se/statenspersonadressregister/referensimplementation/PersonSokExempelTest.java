package se.statenspersonadressregister.referensimplementation;

import org.junit.Before;
import org.junit.Test;
import se.statenspersonadressregister.personsok.version20160213.IdentifieringsInformationTYPE;
import se.statenspersonadressregister.personsok.version20160213.PersonsokningFragaTYPE;
import se.statenspersonadressregister.personsok.version20160213.SPARPersonsokningFraga;
import se.statenspersonadressregister.personsok.version20160213.SPARPersonsokningSvar;

import static org.junit.Assert.*;

public class PersonSokExempelTest {

    private PersonSokExempel personSokExempel;
    private SOAPKommunikation soapKommunikation;
    private IdentifieringsInformationTYPE identifieringsInformation;

    @Before
    public void setUp() throws Exception {
        personSokExempel = new PersonSokExempel();
        KlientCertifikatInformation certifikatInformation = personSokExempel.createKlientCertifikatInformation();
        soapKommunikation = personSokExempel.createSoapKommunikation(certifikatInformation);
        identifieringsInformation = personSokExempel.createIdentifieringsInformation();
    }

    @Test
    public void sokningGiltigtPersonId() throws SOAPKommunikationException {
        PersonsokningFragaTYPE personSokningType = personSokExempel.createPersonsokningFragaPersonId("197910312391");
        SPARPersonsokningFraga sparPersonsokningFraga = personSokExempel.createSPARPersonsokningFraga(identifieringsInformation, personSokningType);
        SPARPersonsokningSvar svar = soapKommunikation.skickaPersonsokningFraga(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(0, svar.getUndantag().size());
        assertEquals(1, svar.getPersonsokningSvarsPost().size());
        assertEquals("Jerry Felipe", svar.getPersonsokningSvarsPost().get(0).getPersondetaljer().get(0).getFornamn());
        assertEquals("Efternamn3663", svar.getPersonsokningSvarsPost().get(0).getPersondetaljer().get(0).getEfternamn());
    }

    @Test
    public void sokningFelaktigtPersonId() throws SOAPKommunikationException {
        PersonsokningFragaTYPE personSokningType = personSokExempel.createPersonsokningFragaPersonId("000000000000");
        SPARPersonsokningFraga sparPersonsokningFraga = personSokExempel.createSPARPersonsokningFraga(identifieringsInformation, personSokningType);
        SPARPersonsokningSvar svar = soapKommunikation.skickaPersonsokningFraga(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(2, svar.getUndantag().size());
        assertEquals(0, svar.getPersonsokningSvarsPost().size());
    }

    @Test
    public void sokningFonetisk() throws SOAPKommunikationException {
        PersonsokningFragaTYPE personSokningType = personSokExempel.createPersonsokningFragaFonetiskNamnSok("mikael");
        SPARPersonsokningFraga sparPersonsokningFraga = personSokExempel.createSPARPersonsokningFraga(identifieringsInformation, personSokningType);
        SPARPersonsokningSvar svar = soapKommunikation.skickaPersonsokningFraga(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(0, svar.getUndantag().size());
        assertTrue(svar.getPersonsokningSvarsPost().size() > 1);
    }

    @Test
    public void sokningFonetiskNollTraffar() throws SOAPKommunikationException {
        PersonsokningFragaTYPE personSokningType = personSokExempel.createPersonsokningFragaFonetiskNamnSok("dethärnamnetfinnsinteispar");
        SPARPersonsokningFraga sparPersonsokningFraga = personSokExempel.createSPARPersonsokningFraga(identifieringsInformation, personSokningType);
        SPARPersonsokningSvar svar = soapKommunikation.skickaPersonsokningFraga(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(0, svar.getUndantag().size());
        assertEquals(0, svar.getPersonsokningSvarsPost().size());
    }

    @Test
    public void sokningFonetiskForMangaTraffar() throws SOAPKommunikationException {
        PersonsokningFragaTYPE personSokningType = personSokExempel.createPersonsokningFragaFonetiskNamnSok("an*");
        SPARPersonsokningFraga sparPersonsokningFraga = personSokExempel.createSPARPersonsokningFraga(identifieringsInformation, personSokningType);
        SPARPersonsokningSvar svar = soapKommunikation.skickaPersonsokningFraga(sparPersonsokningFraga);

        assertNotNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(0, svar.getUndantag().size());
        assertEquals(0, svar.getPersonsokningSvarsPost().size());
        assertTrue(svar.getOverstigerMaxAntalSvarsposter().getAntalPoster() > 100);
        assertEquals(100, svar.getOverstigerMaxAntalSvarsposter().getMaxAntalSvarsPoster());
    }
}