package se.statenspersonadressregister.referensimplementation;

import com.google.gson.Gson;
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
        System.out.println("************TESTSET************");
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
	Gson gson = new Gson();
	gson.toJson(svar, System.out);
	System.out.println("**********Does this work*************");
	System.out.println(svar.getPersonsokningSvarsPost().get(0).getAdresser());
	System.out.println(svar.getPersonsokningSvarsPost().get(0).getPersondetaljer().toString());
        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(0, svar.getUndantag().size());
        assertEquals(1, svar.getPersonsokningSvarsPost().size());
        assertEquals("Jerry Felipe", svar.getPersonsokningSvarsPost().get(0).getPersondetaljer().get(0).getFornamn());
        assertEquals("Efternamn3663", svar.getPersonsokningSvarsPost().get(0).getPersondetaljer().get(0).getEfternamn());
    }

}
