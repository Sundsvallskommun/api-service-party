package se.sundsvall.party.integration.citizen.configuration;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;
import se.sundsvall.party.Application;
import se.sundsvall.party.integration.citizen.CitizenClient;
import se.sundsvall.party.integration.legalentity.LegalEntityClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration.CLIENT_ID;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class CitizenConfigurationTest {

	@MockitoBean
	private CitizenClient citizenClient;

	@MockitoBean
	private LegalEntityClient legalEntityClient;

	@Autowired
	private CitizenProperties properties;

	@Test
	void testFeignBuilderCustomizer() {
		final var configuration = new CitizenConfiguration();
		final var clientRegistrationRepositoryMock = mock(ClientRegistrationRepository.class);
		final var clientRegistrationMock = mock(ClientRegistration.class);
		final var feignMultiCustomizerSpy = spy(FeignMultiCustomizer.class);
		final var feignBuilderCustomizerMock = mock(FeignBuilderCustomizer.class);

		when(clientRegistrationRepositoryMock.findByRegistrationId(any())).thenReturn(clientRegistrationMock);
		when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(feignBuilderCustomizerMock);

		try (MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			var customizer = configuration.feignBuilderCustomizer(properties, clientRegistrationRepositoryMock);

			ArgumentCaptor<ProblemErrorDecoder> errorDecoderCaptor = ArgumentCaptor.forClass(ProblemErrorDecoder.class);

			verify(feignMultiCustomizerSpy).withErrorDecoder(errorDecoderCaptor.capture());
			verify(clientRegistrationRepositoryMock).findByRegistrationId(CLIENT_ID);
			verify(feignMultiCustomizerSpy).withRetryableOAuth2InterceptorForClientRegistration(same(clientRegistrationMock));
			verify(feignMultiCustomizerSpy).withRequestTimeoutsInSeconds(5, 30);
			verify(feignMultiCustomizerSpy).composeCustomizersToOne();

			assertThat(errorDecoderCaptor.getValue())
				.hasFieldOrPropertyWithValue("integrationName", CLIENT_ID)
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
