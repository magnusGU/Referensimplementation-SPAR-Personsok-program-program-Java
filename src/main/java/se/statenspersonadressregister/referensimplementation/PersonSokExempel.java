package se.statenspersonadressregister.referensimplementation;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import se.statenspersonadressregister.personsok.version20160213.AviseringsPostTYPE;
import se.statenspersonadressregister.personsok.version20160213.FastighetTYPE;
import se.statenspersonadressregister.personsok.version20160213.FolkbokforingsadressTYPE;
import se.statenspersonadressregister.personsok.version20160213.FonetiskSokningTYPE;
import se.statenspersonadressregister.personsok.version20160213.IdentifieringsInformationTYPE;
import se.statenspersonadressregister.personsok.version20160213.OverstigerMaxAntalSvarsposterTYPE;
import se.statenspersonadressregister.personsok.version20160213.PersonIdTYPE;
import se.statenspersonadressregister.personsok.version20160213.PersondetaljerTYPE;
import se.statenspersonadressregister.personsok.version20160213.PersonsokningFragaTYPE;
import se.statenspersonadressregister.personsok.version20160213.RelationTYPE;
import se.statenspersonadressregister.personsok.version20160213.SPARPersonsokningFraga;
import se.statenspersonadressregister.personsok.version20160213.SPARPersonsokningSvar;
import se.statenspersonadressregister.personsok.version20160213.SarskildPostadressTYPE;
import se.statenspersonadressregister.personsok.version20160213.UndantagTYPE;
import se.statenspersonadressregister.personsok.version20160213.UtlandsadressTYPE;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.awt.event.*;
import javax.swing.*;

import static se.statenspersonadressregister.referensimplementation.PersonSokExempelLogTexter.*;

/**
 * Referensimplementation till SPAR Personsök program-program, version 20160213.
 * Använder Java-klasser genererade av JAXB från xsd-filer. För att generera klasser kör <i>mvn install</i>.
 *
 * För information om detaljer och betydelser i fråga och svar, se gränssnittsmanualen på SPAR:s hemsida.
 *
 * @see <a href="https://www.statenspersonadressregister.se/">https://www.statenspersonadressregister.se/</a>
 */
@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class PersonSokExempel {
    private final static Logger log = Logger.getLogger(PersonSokExempel.class);

    private static JTextField t;
    private final PersonSokInstallningar personSokInstallngar;

    /**
     * Skapar en instans av PersonSokExempel och kör demonstrationen
     */
    public static void main(String[] args) {

	t = new JTextField("YYYYMMDDXXXX",1);
        sattLog4JKonfiguration("/log4j.demonstration.properties");
        PersonSokInstallningar personSokInstallningar = new PersonSokInstallningar(args);
        new PersonSokExempel(personSokInstallningar).demonstration();
    }

    protected PersonSokExempel() {
        this(new PersonSokInstallningar());
    }

    public PersonSokExempel(PersonSokInstallningar installningar) {
        this.personSokInstallngar = installningar;
    }

    /**
     * Demonstration av referensimplementationen.
     */
    private void demonstration() {
        try {
            log.info("Startar demonstration av Personsök program-program, version 20160213");

            KlientCertifikatInformation certifikatInformation = createKlientCertifikatInformation();
            SOAPKommunikation soapKommunikation = createSoapKommunikation(certifikatInformation);
            IdentifieringsInformationTYPE identifieringsInformation = createIdentifieringsInformation();

            log.debug("Sökning på personid 197910312391");
            SPARPersonsokningSvar svar = soapKommunikation.skickaPersonsokningFraga(
                    createSPARPersonsokningFraga(identifieringsInformation, createPersonsokningFragaPersonId("197910312391")));
            logSPARPersonsokningSvar(svar);


        } catch (SOAPKommunikationException e) {
            log.error("Något gick fel", e);
        }
    }

    protected KlientCertifikatInformation createKlientCertifikatInformation() {
        String certifikatSokvag = personSokInstallngar.getCertifikatSokvag();
        if (certifikatSokvag == null || certifikatSokvag.isEmpty()) {
            return null;
        } else {
            String caSokvag = personSokInstallngar.getCaSokvag();
            String certifikatLosenord = personSokInstallngar.getCertifikatLosenord();
            if (caSokvag == null || caSokvag.isEmpty()) {
                return new KlientCertifikatInformation(
                        certifikatSokvag,
                        certifikatLosenord);
            } else {
                String caLosenord = personSokInstallngar.getCaLosenord();
                return new KlientCertifikatInformation(
                        certifikatSokvag,
                        certifikatLosenord,
                        caSokvag,
                        caLosenord);
            }
        }
    }

    protected SOAPKommunikation createSoapKommunikation(KlientCertifikatInformation certifikatInformation)
            throws SOAPKommunikationException {
        return new SOAPKommunikation(personSokInstallngar.getUrl(), certifikatInformation);
    }

    /**
     * Skapar och returnerar en SPARPersonsokningFraga, huvudelementet i en fråga till personsök i SPAR
     */
    protected SPARPersonsokningFraga createSPARPersonsokningFraga(IdentifieringsInformationTYPE identifieringsInformation, PersonsokningFragaTYPE personsokningFraga) {
        SPARPersonsokningFraga sparPersonsokningFraga = new SPARPersonsokningFraga();
        sparPersonsokningFraga.setIdentifieringsInformation(identifieringsInformation);
        sparPersonsokningFraga.setPersonsokningFraga(personsokningFraga);
        return sparPersonsokningFraga;
    }

    /**
     * Skapar och returnerar IdentifieringsInformation för giltig kund och uppdrag i kundtestmiljön
     */
    protected IdentifieringsInformationTYPE createIdentifieringsInformation() {
        IdentifieringsInformationTYPE identifieringsInformation = new IdentifieringsInformationTYPE();
        identifieringsInformation.setKundNrLeveransMottagare(personSokInstallngar.getKundNrLeveransMottagare());
        identifieringsInformation.setKundNrSlutkund(personSokInstallngar.getKundNrSlutkund());
        identifieringsInformation.setUppdragsId(personSokInstallngar.getUppdragsId());
        identifieringsInformation.setSlutAnvandarId(personSokInstallngar.getSlutAnvandarId());
        identifieringsInformation.setTidsstampel(new XMLGregorianCalendarImpl(new GregorianCalendar()));
        identifieringsInformation.setOrgNrSlutkund(personSokInstallngar.getOrgNrSlutkund());
        return identifieringsInformation;
    }

    /**
     * Skapar och returnerar en fråga på personnummer-/samordningsnummer
     */
    protected PersonsokningFragaTYPE createPersonsokningFragaPersonId(String personId) {
        PersonsokningFragaTYPE personsokningFraga = new PersonsokningFragaTYPE();
        PersonIdTYPE personIdTYPE = new PersonIdTYPE();
        personIdTYPE.setFysiskPersonId(personId);
        personsokningFraga.setPersonId(personIdTYPE);
        return personsokningFraga;
    }
    /**
     * Loggar ett personsökningarsvar
     */
    protected void logSPARPersonsokningSvar(SPARPersonsokningSvar svar) {
        StringBuilder sb = new StringBuilder(SPARPERSONSOKNING_SVAR);
        sb.append(MELLANSLAG + svar.getPersonsokningSvarsPost().size() + SOKTRAFFAR +
                svar.getUndantag().size() + ANTALUNDANTAG + RADBRYT);

        maybeAppend(sb, svar.getOverstigerMaxAntalSvarsposter(), overstiger ->
                OVERSTIGER_MAX_ANTAL_TRAFFAR + ((OverstigerMaxAntalSvarsposterTYPE) overstiger).getAntalPoster() +
                        TRAFFAR_AV_MAX + ((OverstigerMaxAntalSvarsposterTYPE) overstiger).getMaxAntalSvarsPoster() + RADBRYT);

        svar.getUndantag().forEach(undantag -> logUndantag(sb, undantag));
        svar.getPersonsokningSvarsPost().forEach(aviseringsPost -> logAviseringsPost(sb, aviseringsPost));
	System.out.println(sb.toString());
        log.debug(sb.toString());
    }

    /**
     * Lägger till loggning om undantag till StringBuilder
     */
    protected void logUndantag(StringBuilder sb, UndantagTYPE undantag) {
        sb.append(UNDANTAG + RADBRYT);
        maybeAppend(sb, undantag.getKod(), kod -> KOD + kod + RADBRYT);
        maybeAppend(sb, undantag.getBeskrivning(), beskrivning -> BESKRIVNING + beskrivning + RADBRYT);
    }

    /**
     * Lägger till loggningstext om aviseringsport till StringBuilder
     */
    protected void logAviseringsPost(StringBuilder sb, AviseringsPostTYPE aviseringsPost) {
        sb.append(AVISERINGSPOST + RADBRYT);
        maybeAppend(sb, aviseringsPost.getPersonId(), id -> PERSON_ID + ((PersonIdTYPE) id).getFysiskPersonId() + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSenasteAndringSPAR(), senast -> SENASTE_ANDRING_SPAR + senast + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSekretessmarkering(), sek -> SEKRETESSMARKERING1 + sek + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSekretessAndringsdatum(), datum -> SEKRETESS_ANDRINGSDATUM + datum + RADBRYT);
        maybeAppend(sb, aviseringsPost.getBeskattningsar(), ar -> BESKATTNINGSAR + ar + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSummeradInkomst(), inkomst -> SUMMERAD_INKOMST + inkomst + RADBRYT);
        aviseringsPost.getFastighet().forEach(fastighet -> logFastighet(sb, fastighet));
        aviseringsPost.getPersondetaljer().forEach(persondetalj -> logPersonDetalj(sb, persondetalj));
        aviseringsPost.getRelation().forEach(relation -> logRelation(sb, relation));

        Optional.ofNullable(aviseringsPost.getAdresser())
                .ifPresent(adresser -> {
                    adresser.getFolkbokforingsadress().forEach((fba) -> logFolkbokforingsAdress(sb, fba));
                    adresser.getSarskildPostadress().forEach((spa) -> logSarskildPostadress(sb, spa));
                    adresser.getUtlandsadress().forEach((ua) -> logUtlandsadress(sb, ua));
                });
    }

    /**
     * Lägger till loggningstext om fastighet till StringBuilder
     */
    protected void logFastighet(StringBuilder sb, FastighetTYPE fastighet) {
        sb.append(FASTIGHET + RADBRYT);
        maybeAppend(sb, fastighet.getFastighetsKod(), kod -> FASTIGHETSKOD + kod + RADBRYT);
        maybeAppend(sb, fastighet.getFastighetKommunKod(), kommun -> KOMMUN + kommun + RADBRYT);
        maybeAppend(sb, fastighet.getFastighetLanKod(), lan -> LAN + lan + RADBRYT);
        maybeAppend(sb, fastighet.getTaxeringsar(), ar -> TAXERINGSAR + ar + RADBRYT);
        maybeAppend(sb, fastighet.getTaxeringsvarde(), ar -> TAXERINGSVARDE + ar + RADBRYT);
        maybeAppend(sb, fastighet.getAndelstalTaljare(), taljare -> ANDELSTAL_TALJARE + taljare + RADBRYT);
        maybeAppend(sb, fastighet.getAndelstalNamnare(), namnare -> ANDELSTAL_NAMNARE + namnare + RADBRYT);
    }

    /**
     * Lägger till loggningstext om utlandsadresser till StringBuilder
     */
    protected void logUtlandsadress(StringBuilder sb, UtlandsadressTYPE ua) {
        sb.append(UTLANDSADRESS + RADBRYT);
        maybeAppend(sb, ua.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, ua.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, ua.getCareOf(), co -> CARE_OF + co + RADBRYT);
        maybeAppend(sb, ua.getUtdelningsadress1(), ua1 -> UTDELNINGSADRESS_1 + ua1 + RADBRYT);
        maybeAppend(sb, ua.getUtdelningsadress2(), ua2 -> UTDELNINGSADRESS_2 + ua2 + RADBRYT);
        maybeAppend(sb, ua.getUtdelningsadress3(), ua3 -> UTDELNINGSADRESS_3 + ua3 + RADBRYT);
        maybeAppend(sb, ua.getLand(), land -> LAND + land + RADBRYT);
    }

    /**
     * Lägger till loggningstext om särskild postadress till StringBuilder
     */
    protected void logSarskildPostadress(StringBuilder sb, SarskildPostadressTYPE spa) {
        sb.append(SARSKILD_POSTADRESS + RADBRYT);
        maybeAppend(sb, spa.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, spa.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, spa.getCareOf(), co -> CARE_OF + co + RADBRYT);
        maybeAppend(sb, spa.getUtdelningsadress1(), ua1 -> UTDELNINGSADRESS_1 + ua1 + RADBRYT);
        maybeAppend(sb, spa.getUtdelningsadress2(), ua2 -> UTDELNINGSADRESS_2 + ua2 + RADBRYT);
        maybeAppend(sb, spa.getPostNr(), postnr -> POSTNUMMER + postnr + RADBRYT);
        maybeAppend(sb, spa.getPostort(), postort -> POSTORT + postort + RADBRYT);

    }

    /**
     * Lägger till loggningstext om folkbokföringsadress till StringBuilder
     */
    protected void logFolkbokforingsAdress(StringBuilder sb, FolkbokforingsadressTYPE fba) {
        sb.append(FOLKBOKFÖRINGSADRESS + RADBRYT);
        maybeAppend(sb, fba.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, fba.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, fba.getCareOf(), co -> CARE_OF + co + RADBRYT);
        maybeAppend(sb, fba.getUtdelningsadress1(), ua1 -> UTDELNINGSADRESS_1 + ua1 + RADBRYT);
        maybeAppend(sb, fba.getUtdelningsadress2(), ua2 -> UTDELNINGSADRESS_2 + ua2 + RADBRYT);
        maybeAppend(sb, fba.getPostNr(), postnr -> POSTNUMMER + postnr + RADBRYT);
        maybeAppend(sb, fba.getPostort(), postort -> POSTORT + postort + RADBRYT);

        maybeAppend(sb, fba.getFolkbokforingsdatum(), datum -> "    Folkbokföringsdatum = " + datum + RADBRYT);
        maybeAppend(sb, fba.getFolkbokfordForsamlingKod(), kod -> FOLKBOKFORDSFÖRSAMLINGKOD + kod + RADBRYT);
        maybeAppend(sb, fba.getDistriktKod(), kod -> DISTRIKTKOD + kod + RADBRYT);
        maybeAppend(sb, fba.getFolkbokfordKommunKod(), kod -> KOMMUNKOD + kod + RADBRYT);
        maybeAppend(sb, fba.getFolkbokfordLanKod(), kod -> LANKOD + kod + RADBRYT);
    }

    /**
     * Lägger till loggningstext om relation till StringBuilder
     */
    protected void logRelation(StringBuilder sb, RelationTYPE relation) {
        sb.append(RELATION + RADBRYT);
        maybeAppend(sb, relation.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, relation.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);
        maybeAppend(sb, relation.getRelationstyp(), typ -> RELATIONSTYP + typ + RADBRYT);

        maybeAppend(sb, relation.getPersonId(), personid -> PERSON_ID + personid + RADBRYT);
        maybeAppend(sb, relation.getFodelsetid(), fodelsetid -> FÖDELSETID + fodelsetid + RADBRYT);

        maybeAppend(sb, relation.getAvregistreringsdatum(), datum -> AVREGISTRERINGSDATUM + datum + RADBRYT);
        maybeAppend(sb, relation.getAvregistreringsorsakKod(), datum -> AVREGISTRERINGSORSAK_KOD + datum + RADBRYT);

        maybeAppend(sb, relation.getFornamn(), namn -> FÖRNAMN + namn + RADBRYT);
        maybeAppend(sb, relation.getMellannamn(), namn -> MELLANNAMN + namn + RADBRYT);
        maybeAppend(sb, relation.getEfternamn(), namn -> EFTERNAMN + namn + RADBRYT);
    }

    /**
     * Lägger till loggning om persondetalj till StringBuilder
     */
    protected void logPersonDetalj(StringBuilder sb, PersondetaljerTYPE persondetalj) {
        sb.append(PERSONDETALJ + RADBRYT);
        maybeAppend(sb, persondetalj.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, persondetalj.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);
        maybeAppend(sb, persondetalj.getAvregistreringsdatum(), datum -> AVREGISTRERINGSDATUM + datum + RADBRYT);
        maybeAppend(sb, persondetalj.getAvregistreringsorsakKod(), kod -> AVREGISTRERINGSORSAK_KOD + kod + RADBRYT);

        maybeAppend(sb, persondetalj.getFornamn(), namn -> FÖRNAMN + namn + RADBRYT);
        maybeAppend(sb, persondetalj.getMellannamn(), namn -> MELLANNAMN + namn + RADBRYT);
        maybeAppend(sb, persondetalj.getEfternamn(), namn -> EFTERNAMN + namn + RADBRYT);
        maybeAppend(sb, persondetalj.getAviseringsnamn(), namn -> AVISERINGSNAMN + namn + RADBRYT);
        maybeAppend(sb, persondetalj.getTilltalsnamn(), tilltal -> TILLTALSNAMN + tilltal + RADBRYT);

        maybeAppend(sb, persondetalj.getFodelselanKod(), kod -> FÖDELSELAN_KOD + kod + RADBRYT);
        maybeAppend(sb, persondetalj.getFodelseforsamling(), forsamling -> FÖDELSEFORSAMLING + forsamling + RADBRYT);
        maybeAppend(sb, persondetalj.getFodelsetid(), tid -> FÖDELSETID + tid + RADBRYT);

        maybeAppend(sb, persondetalj.getHanvisningsPersonNrByttFran(), hanvisning -> HÄNVISNINGS_PERSON_NR_BYTT_FRÅN + hanvisning + RADBRYT);
        maybeAppend(sb, persondetalj.getHanvisningsPersonNrByttTill(), hanvisning -> HÄNVISNINGS_PERSON_NR_BYTT_TILL + hanvisning + RADBRYT);
        maybeAppend(sb, persondetalj.getSekretessmarkering(), sekretess -> SEKRETESSMARKERING2 + sekretess + RADBRYT);
        maybeAppend(sb, persondetalj.getKon(), kon -> KÖN + kon + RADBRYT);
    }

    /**
     * Hjälpfunktion för att göra koden mer lättläst, om objektet objekt inte är null så mappas objektet om till en sträng som appendas på StringBuilder.
     * @param sb Stringbuilder där det eventuellt läggs till text
     * @param objekt Om det här värden inte är null mappas det om
     * @param funktion Används för att mappa om objektet om det inte är null
     */
    protected void maybeAppend(StringBuilder sb, Object objekt, Function<? super Object, ? extends String> funktion) {
        Optional.ofNullable(objekt).map(funktion).ifPresent(sb::append);
    }

    /**
     * Laddar in log4j-konfigurationsfil i runtime
     * Används vid tester för att undvika onödig loggning, vilket sker om man lägger en log4j.properties under resources.
     */
    protected static void sattLog4JKonfiguration(String log4jKonfiguration) {
        try {
            Properties properties = new Properties();
            InputStream configStream = PersonSokExempel.class.getResourceAsStream(log4jKonfiguration);
            properties.load(configStream);
            configStream.close();

            properties.setProperty("log4j.appender.FILE.file", log4jKonfiguration);
            LogManager.resetConfiguration();
            PropertyConfigurator.configure(properties);
        } catch (IOException e) {
            System.out.println("Kunde ej ladda log4j-konfigurationsfil");
        }
    }
}
