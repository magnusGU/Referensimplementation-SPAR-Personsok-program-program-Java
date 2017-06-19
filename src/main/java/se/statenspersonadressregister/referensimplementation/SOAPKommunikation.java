package se.statenspersonadressregister.referensimplementation;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import se.statenspersonadressregister.personsok.version20160213.SPARPersonsokningFraga;
import se.statenspersonadressregister.personsok.version20160213.SPARPersonsokningSvar;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Objects;

public class SOAPKommunikation {
    private final static Logger log = Logger.getLogger(SOAPKommunikation.class);

    private final String url;

    /**
     * Exempel på kommunikation mot SOAP-tjänster
     * @param url Adress till den SOAP-tjänst du vill ansluta till
     * @param certifikatInformation Uppgifter till klientcertifikat och cacert, om den är nonnull så laddas klientcertfikikat in till Default {@link javax.net.ssl.SSLSocketFactory SSLSocketFactory}
     */
    public SOAPKommunikation(String url, KlientCertifikatInformation certifikatInformation) throws SOAPKommunikationException {
        Objects.requireNonNull(url);
        this.url = url;

        if (certifikatInformation != null) {
            laddaCertifikatOchKeystore(certifikatInformation);
        }
    }

    public SPARPersonsokningSvar skickaPersonsokningFraga(SPARPersonsokningFraga sparPersonsokningFraga) throws SOAPKommunikationException {
        try {
            log.trace("Skickar fråga till " + url);
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapMessage = createSOAPMessage(sparPersonsokningFraga);
            logSOAPResponse("Kommer skicka SOAP-meddelande: ", soapMessage);
            SOAPMessage soapResponse = soapConnection.call(soapMessage, url);

            log.trace("Tog emot svar från " + url);
            logSOAPResponse("Tog emot SOAP-meddelande: ", soapResponse);
            return createPersonsokningSvar(soapResponse);
        } catch (SOAPException e) {
            throw new SOAPKommunikationException("Kunde inte skicka fråga", e);
        }
    }

    /**
     * Laddar klientcertifikat till default {@link javax.net.ssl.SSLSocketFactory SSLSocketFactory}, gäller vid samtliga anrop som använder HTTPS
     **/
    protected void laddaCertifikatOchKeystore(KlientCertifikatInformation certifikatInformation) throws SOAPKommunikationException {
        try {
            log.debug("Laddar klientcertifikat");

            // Ladda klientcertifikat till en KeyStore
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            char[] klientCertifikatLosenord = certifikatInformation.getKlientCertifikatLosenord();
            if(klientCertifikatLosenord.length==0) {
                klientCertifikatLosenord = null;
            }
            clientStore.load(new FileInputStream(certifikatInformation.getKlientCertifikatSokvag()), klientCertifikatLosenord);

            // Skapa KeyManagerFactory, lägg till KeyStore med klientcertifikatet
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, klientCertifikatLosenord);

            // Ladda KeyStore med rootcertifikat
            KeyStore trustStore = KeyStore.getInstance("JKS");
            char[] cacertLosenord = certifikatInformation.getCacertLosenord();
            trustStore.load(new FileInputStream(certifikatInformation.getCacertSokvag()), cacertLosenord.length == 0 ? null : cacertLosenord);

            // Skapa TrustManagerFactory, lägg till KeyStore med rootcertifikat
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Skapa SSLContext, initera med KeyManagerFactory och TrustManagerFactory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

            // Sätt default SSLSocketFactory att använda ovan skapat SSLContext
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException e) {
            throw new SOAPKommunikationException("Kunde ej ladda klientcertifikat", e);
        }
    }

    /**
     * Omvandla svar från SOAP-tjänsten till SPARPersonsokningSvar
     */
    protected SPARPersonsokningSvar createPersonsokningSvar(SOAPMessage soapResponse) throws SOAPKommunikationException {
        try {
            Document soapDocument = soapResponse.getSOAPBody().extractContentAsDocument();
            Unmarshaller unmarshaller = JAXBContext.newInstance(SPARPersonsokningSvar.class).createUnmarshaller();
            return (SPARPersonsokningSvar) unmarshaller.unmarshal(soapDocument);
        } catch (SOAPException | JAXBException e) {
            throw new SOAPKommunikationException("Kunde inte omvandla SOAP till SPARPersonsokningSvar", e);
        }
    }

    /**
     * Omvandla SPARPersonsokningFraga till ett meddelande att skicka till SOAP-tjänsten
     */
    protected SOAPMessage createSOAPMessage(SPARPersonsokningFraga sparPersonsokningFraga) throws SOAPKommunikationException {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Marshaller marshaller = JAXBContext.newInstance(SPARPersonsokningFraga.class).createMarshaller();
            marshaller.marshal(sparPersonsokningFraga, document);

            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            soapMessage.getSOAPBody().addDocument(document);

            return soapMessage;
        } catch (ParserConfigurationException | JAXBException | SOAPException e) {
            throw new SOAPKommunikationException("Kunde ej omvandla SPARPersonsokningFraga till SOAP", e);
        }
    }

    /**
     * Logga XML-innehållet i ett SOAPMessage
     */
    protected void logSOAPResponse(String preLogTest, SOAPMessage soapResponse) {
        try {
            if (log.isTraceEnabled()) {
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                Source sourceContent = soapResponse.getSOAPPart().getContent();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                StreamResult result = new StreamResult(byteArrayOutputStream);
                transformer.transform(sourceContent, result);

                log.trace(preLogTest + byteArrayOutputStream.toString());
            }
        } catch (SOAPException | TransformerException e) {
            log.warn("Kunde inte logga SOAPsvar", e);
        }
    }
}
