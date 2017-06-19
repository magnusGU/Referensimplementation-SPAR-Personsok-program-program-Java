package se.statenspersonadressregister.referensimplementation;

import java.util.Objects;

public class KlientCertifikatInformation {
    final private String klientCertifikatSokvag;
    final private char[] klientCertifikatLosenord;
    final private String cacertSokvag;
    final private char[] cacertLosenord;

    /**
     * Innehåller information om klientcertifikatet
     *
     * @param klientCertifikatSokvag Sökväg till klientcertifikatet, till exempel /opt/spar/personsok/klientcertifikat.p12
     * @param klientCertifikatLosenord Lösenord till klientcertifikatet
     * @param cacertSokvag Sökväg till cacert
     * @param cacertLosenord Lösenord till cacert
     */
    KlientCertifikatInformation(String klientCertifikatSokvag, String klientCertifikatLosenord, String cacertSokvag, String cacertLosenord) {
        Objects.requireNonNull(klientCertifikatSokvag);
        Objects.requireNonNull(klientCertifikatLosenord);
        Objects.requireNonNull(cacertSokvag);
        Objects.requireNonNull(cacertLosenord);

        this.klientCertifikatSokvag = klientCertifikatSokvag;
        this.klientCertifikatLosenord = klientCertifikatLosenord.toCharArray();
        this.cacertSokvag = cacertSokvag;
        this.cacertLosenord = cacertLosenord.toCharArray();
    }


    /**
     * Innehåller information om klientcertifikatet, använder Javas standard cacert
     *
     * @param klientCertifikatSokvag Se {@link KlientCertifikatInformation#KlientCertifikatInformation(String, String, String, String) KlientCertifikatInformation}
     * @param klientCertifikatLosenord Se {@link KlientCertifikatInformation#KlientCertifikatInformation(String, String, String, String) KlientCertifikatInformation}
     */
    KlientCertifikatInformation(final String klientCertifikatSokvag, final String klientCertifikatLosenord) {
        this(klientCertifikatSokvag, klientCertifikatLosenord, System.getProperty("java.home") + "/lib/security/cacerts", "changeit");
    }

    public String getKlientCertifikatSokvag() {
        return klientCertifikatSokvag;
    }

    public char[] getKlientCertifikatLosenord() {
        return klientCertifikatLosenord;
    }

    public String getCacertSokvag() {
        return cacertSokvag;
    }

    public char[] getCacertLosenord() {
        return cacertLosenord;
    }
}
