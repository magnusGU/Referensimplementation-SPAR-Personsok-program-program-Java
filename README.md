Klassen PersonSokExempel har en main funktion. Har testat följande nedanför

mvn clean install exec:java -Dexec.mainClass="se.statenspersonadressregister.referensimpelementation.PersonSokExempel"




# Referensimplementation SPAR Personsök program-program

Denna källkod är en referensimplementation av SPAR Personsök program-program version _20160213_.

Referensimplementationen är skriven för _Java 8_ och använder _Maven_ för projekthantering. 
_JAXB2_, som följer med i Java, används för att skapa Javakod från XML-schemafiler (.xsd). 
Utöver det används två externa bibliotek, _Log4j 2_ för att hantera loggning och _JUnit 4_ för att hantera testning av koden.

För mer detaljer om verksamhetsbegrepp inom SPAR, och även andra tjänster inom SPAR se gränssnittsmanualen som är tillgänglig på 
[SPARs hemsida](https://www.statenspersonadressregister.se).

## Användning
Kör `mvn clean install` i samma bibliotek som _pom.xml_ för att hämta hem beroenden och från xsd-filer skapa Java-klasser. 
Med detta kommando körs även testerna, vilket gör att anrop kommer skickas till kundtestmiljön.

_PersonSokExempel_ innehåller en demonstration som gör fyra olika sökningar och loggar utförligt ut resultatet.

_KlientCertifikatInformationTest_ har fyra tester som kör mot kundtestmiljön. Dessa verifierar att inget går fel. 

### Kundtest
Vi rekommenderar att det klientcertifikat som är tänkt att användas i produktion även används vid tester mot kundtestmiljön, 
detta för att i ett tidigt skede verifiera att certifikatet är korrekt.

### Klientcertifikat
För att på ett enkelt sätt använda rätt klientcertifikat vid anslutningen kan _KlientCertifikatInformation_ användas. 
Denna vill ha klientcertifikatet i _PKCS12-format (.p12)_. Det går också att använda Javas _keytool_ för att lägga till 
ett klientcertifikat, men detta alternativ avråder vi från.

### Rootcertifikat
_KlientCertifikatInformation_ kan även hantera rootcertifikat för att verifiera SPAR:s identitet. Denna vill ha 
rootcertifikatet i _PKCS12-format (.p12)_. Vi rekommenderar att verifiering av rootcertifikatet görs även om en annan lösning används.

### Produktion
Om koden används för att integrera mot produktionsmiljön krävs ett giltigt klientcertifikat, det inkluderade 
testcertifikatet fungerar endast i kundtestmiljön. Även indentifieringsinformation behöver vara giltig, 
se _KundNrLeveransMottagare_, _KundNrSlutkund_ och _UppdragsId_. För mer information kontakta SPAR:s kundtjänst.

### Docker

Medföljande _Dockerfile_ kan användas för att bygga en Dockerimage. Denna kan användas för att starta en container som kör 
_PersonSokExempel_, som gör ett antal sökningar mot kundtest.
  
För mer information om Docker, se [https://www.docker.com/](https://www.docker.com/).
