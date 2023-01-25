package se.sundsvall.party.integration.citizenmapping.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.citizenmapping")
public record CitizenMappingProperties(int connectTimeout, int readTimeout) {
}
