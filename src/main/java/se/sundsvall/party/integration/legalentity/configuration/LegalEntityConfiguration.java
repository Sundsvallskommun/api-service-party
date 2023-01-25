package se.sundsvall.party.integration.legalentity.configuration;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
public class LegalEntityConfiguration {

	public static final String CLIENT_REGISTRATION_ID = "legalentity";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(LegalEntityProperties legalEntityProperties, ClientRegistrationRepository clientRegistrationRepository) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_REGISTRATION_ID, List.of(NOT_FOUND.value())))
			.withRetryableOAuth2InterceptorForClientRegistration(clientRegistrationRepository.findByRegistrationId(CLIENT_REGISTRATION_ID))
			.withRequestTimeoutsInSeconds(legalEntityProperties.connectTimeout(), legalEntityProperties.readTimeout())
			.composeCustomizersToOne();
	}
}
