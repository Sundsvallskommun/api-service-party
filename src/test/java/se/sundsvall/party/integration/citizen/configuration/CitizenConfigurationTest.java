package se.sundsvall.party.integration.citizen.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration.CLIENT_REGISTRATION_ID;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;

import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;
import se.sundsvall.party.Application;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class CitizenConfigurationTest {

	@Mock
	private ClientRegistrationRepository clientRegistrationRepositoryMock;

	@Mock
	private ClientRegistration clientRegistrationMock;

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Mock
	private FeignBuilderCustomizer feignBuilderCustomizerMock;

	@Autowired
	private CitizenProperties properties;

	@Test
	void testFeignBuilderCustomizer() {
		final var configuration = new CitizenConfiguration();

		when(clientRegistrationRepositoryMock.findByRegistrationId(any())).thenReturn(clientRegistrationMock);
		when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(feignBuilderCustomizerMock);

		try (MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			var customizer = configuration.feignBuilderCustomizer(properties, clientRegistrationRepositoryMock);

			ArgumentCaptor<ProblemErrorDecoder> errorDecoderCaptor = ArgumentCaptor.forClass(ProblemErrorDecoder.class);

			verify(feignMultiCustomizerSpy).withErrorDecoder(errorDecoderCaptor.capture());
			verify(clientRegistrationRepositoryMock).findByRegistrationId(CLIENT_REGISTRATION_ID);
			verify(feignMultiCustomizerSpy).withRetryableOAuth2InterceptorForClientRegistration(same(clientRegistrationMock));
			verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(5, 30);
			verify(feignMultiCustomizerSpy).composeCustomizersToOne();

			assertThat(errorDecoderCaptor.getValue())
				.hasFieldOrPropertyWithValue("integrationName", CLIENT_REGISTRATION_ID)
				.hasFieldOrPropertyWithValue("bypassResponseCodes", List.of(NOT_FOUND.value()));
			assertThat(customizer).isSameAs(feignBuilderCustomizerMock);
		}
	}

	@Test
	void testProperties() {
		assertThat(properties.connectTimeout()).isEqualTo(5);
		assertThat(properties.readTimeout()).isEqualTo(30);
	}
}
