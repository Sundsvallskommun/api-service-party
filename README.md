# Party

## Leverantör

Sundsvalls kommun

## Beskrivning
Party är en fasad-tjänst som tillhandahåller funktionalitet för att översätta partyId till person- eller organisationsnummer och vice versa.

## Tekniska detaljer

### Integrationer
Tjänsten integrerar mot:

* Mikrotjänst Citizen
* Mikrotjänst CitizenMapping
* Mikrotjänst LegalEntity

### Starta tjänsten

|Konfigurationsnyckel|Beskrivning|
|---|---|
|`integration.citizen.url`|Endpoint address for citizen service|
|`spring.security.oauth2.client.registration.citizen.client-id`|Client-ID to use when authenticating to citizen service|
|`spring.security.oauth2.client.registration.citizen.client-secret`|Client secret to use when authenticating to citizen service|
|`spring.security.oauth2.client.provider.citizen.token-uri`|Endpoint address for renewal of citizen token|

|`integration.citizenmapping.url`|Endpoint address for citizenmapping service|
|`spring.security.oauth2.client.registration.citizenmapping.client-id`|Client-ID to use when authenticating to citizenmapping service|
|`spring.security.oauth2.client.registration.citizenmapping.client-secret`|Client secret to use when authenticating to citizenmapping service|
|`spring.security.oauth2.client.provider.citizenmapping.token-uri`|Endpoint address for renewal of citizenmapping token|

|`integration.legalentity.url`|Endpoint address for legalentity service|
|`spring.security.oauth2.client.registration.legalentity.client-id`|Client-ID to use when authenticating to legalentity service|
|`spring.security.oauth2.client.registration.legalentity.client-secret`|Client secret to use when authenticating to legalentity service|
|`spring.security.oauth2.client.provider.legalentity.token-uri`|Endpoint address for renewal of legalentity token|

### Paketera och starta tjänsten
Applikationen kan paketeras genom:

```
./mvnw package
```
Kommandot skapar filen `api-service-party-<version>.jar` i katalogen `target`. Tjänsten kan nu köras genom kommandot `java -jar target/api-service-party-<version>.jar`.

### Bygga och starta med Docker
Exekvera följande kommando för att bygga en Docker-image:

```
docker build -f src/main/docker/Dockerfile -t api.sundsvall.se/ms-party:latest .
```

Exekvera följande kommando för att starta samma Docker-image i en container:

```
docker run -i --rm -p8080:8080 api.sundsvall.se/ms-party

```

#### Kör applikationen lokalt

Exekvera följande kommando för att bygga och starta en container i sandbox mode:  

```
docker-compose -f src/main/docker/docker-compose-sandbox.yaml build && docker-compose -f src/main/docker/docker-compose-sandbox.yaml up
```


## 
Copyright (c) 2022 Sundsvalls kommun