package se.statenspersonadressregister.referensimplementation;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class KlientCertifikatInformationTest {
    private KlientCertifikatInformation testKlientCertifikatInformation1;

    @Before
    public void initiera() {
        testKlientCertifikatInformation1 = new KlientCertifikatInformation("/sokvag/till/certifikat", "h3mligt", "/sokvag/till/cacert", "1234");
    }

    @Test
    public void getKlientCertifikatSokvag() {
        final String facit = "/sokvag/till/certifikat";
        assertEquals(facit, testKlientCertifikatInformation1.getKlientCertifikatSokvag());
    }

    @Test
    public void getKlientCertifikatLosenord() {
        final char facit[] = new char[] { 'h', '3', 'm', 'l', 'i', 'g', 't' };
        assertArrayEquals(facit, testKlientCertifikatInformation1.getKlientCertifikatLosenord());
    }

    @Test
    public void getCacertSokvag() {
        final String facit = "/sokvag/till/cacert";
        assertEquals(facit, testKlientCertifikatInformation1.getCacertSokvag());
    }

    @Test
    public void getCacertLosenord() {
        final char facit[] = new char[] { '1', '2', '3', '4'};
        assertArrayEquals(facit, testKlientCertifikatInformation1.getCacertLosenord());
    }

}