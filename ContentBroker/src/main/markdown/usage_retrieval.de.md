# Retrieval

Als Retrieval wird der Prozess des Zurückladens von Objekten auf die lokale Arbeitsstation eines Nutzers bezeichnet.

Am Ende des Retrieval Vorganges erhält der Nutzer einen Einlieferunsbeleg mit dem technischen Identifier des Objektes.
Objekte können über diesen Identifier recherchiert und deren Retrieval über die DAWeb angestoßen werden. 

## Manuelles Retrieval

### Voraussetzungen

* Der Contractor hat einen Rechner mit Internetverbindung.
* Der Contractor ist mit seinen Zugangsdaten in der DAWeb Oberfläche eingeloggt.
* Der Contractor hat zu einem früheren Zeitpunkt ein SIP eingespielt. Er hat den Identifier notiert.

### Schritte

1\. In der DAWeb Maske "Eingelieferte Objekte (AIP)" gibt es die Möglichkeit, die aufgelisteten Objekte per Filter einzugrenzen.

![](retrieval1.png)

2\. Das so ausgewählte Objekt wird dann gelistet. Per Klick auf den "Anfordern" Button des Objektes kann dann das Retrieval angestoßen werden.

![](retrieval3.png)

3\. Das System signalisiert mit folgender Meldung, dass das Retrieval erfolgreich angestoßen wurde:

![](retrieval2.png)

4\. Die Verarbeitung wird dann vom System vorgenommen. Das System informiert anschließend den Nutzer per Email, sobald das DIP zur Retrieval bereitsteht.

```
Betreff: [System] Retrieval Report für 2-20150415425545

Inhalt: 

Ihr angefordertes Objekt mit dem Namen "2-20150415425545" wurde unter 
Ihrem Outgoing Ordner unter TEST/outgoing/ abgelegt und steht jetzt zum Retrieval bereit!
``` 

5\. Es taucht ein Icon "Entnahme" auf. Sobald dieses Icon angezeigt wird steht das Paket zur Entname im Verzeichnis des Contractors zu Entnahme bereit. 

![](retrieval4.png)

Der Speicherplatz des Objektes kann ebenfalls über die DAWeb Maske "Angeforderte Objekte (DIP)" angesehen werden.

![](retrieval5.png)

**Alternativ** ist es möglich, das Paket per FileShare Client (z.B. FileZilla) aus dem ihm zugeteilten Nutzungsbereich
auf dem Knoten, dort im "outgoing"-Verzeichnis, herunterzuladen.

Um Ressourcen auf dem Nutzungsbereich zu schonen, wird das Paket **24 Stunden**, nachdem es gedownloadet wurde, automatisch vom System entfernt.

## Links

* [DIP-Spezifikation](specification_dip.md)


## User Reporting via DA-Web

Although ContentBroker sends Emails on significant actions (like "archived" or "user error") you are able to call interface of DA-Web for state calls, please refer to the information provided in this folder.




