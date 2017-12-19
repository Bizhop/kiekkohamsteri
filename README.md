# kiekkohamsteri
Sovellus kiekkojen hamstraamiseen

## Tietokannan luonti
Käynnistä tietokanta dockerilla: docker-compose up

## Java sovellus
Tee oma versio asetustiedostosta src/main/resources/application.properties.template (ilman .template päätettä)
Oletusarvot kehitykseen ovat:
* port 8181
* db hamsteri
* user hamsteri
* password hamsteri

Käynnistä java-sovellus: mvn spring-boot:run

