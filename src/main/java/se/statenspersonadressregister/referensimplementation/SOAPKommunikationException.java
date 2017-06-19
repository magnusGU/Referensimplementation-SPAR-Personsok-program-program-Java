package se.statenspersonadressregister.referensimplementation;

/**
 * När något gått fel i SOAPKommunikation
 */
public class SOAPKommunikationException extends Exception {
    public SOAPKommunikationException(String meddelande, Exception orsak) {
        super(meddelande, orsak);
    }
}
