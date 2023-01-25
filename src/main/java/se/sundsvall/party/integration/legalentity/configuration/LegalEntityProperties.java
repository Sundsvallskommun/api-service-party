package se.sundsvall.party.integration.legalentity.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.legalentity")
public record LegalEntityProperties(int connectTimeout, int readTimeout) {
}
