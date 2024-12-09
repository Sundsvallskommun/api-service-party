package se.sundsvall.party.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static se.sundsvall.party.api.model.PartyType.ENTERPRISE;
import static se.sundsvall.party.api.model.PartyType.PRIVATE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.party.Application;
import se.sundsvall.party.service.PartyService;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class PartyResourceTest {

	@MockBean
	private PartyService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getPartyIdByEnterpriseLegalId() {

		// Arrange
		final var municipalityId = "2281";
		final var type = ENTERPRISE.name();
		final var legalId = "5566123456";
		final var partyId = "81471222-5798-11e9-ae24-57fa13b361e1";

		when(serviceMock.getPartyId(municipalityId, ENTERPRISE, legalId)).thenReturn(partyId);

		// Act
		webTestClient.get().uri("/{municipalityId}/{type}/{legalId}/partyId", municipalityId, type, legalId)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(TEXT_PLAIN)
			.expectBody(String.class).isEqualTo(partyId);

		// Assert
		verify(serviceMock).getPartyId(municipalityId, ENTERPRISE, legalId);
	}

	@Test
	void getPartyIdByPrivateLegalId() {

		// Arrange
		final var municipalityId = "2281";
		final var type = PRIVATE.name();
		final var legalId = "200001011234";
		final var partyId = "81471222-5798-11e9-ae24-57fa13b361e2";

		when(serviceMock.getPartyId(municipalityId, PRIVATE, legalId)).thenReturn(partyId);

		// Act
		webTestClient.get().uri("/{municipalityId}/{type}/{legalId}/partyId", municipalityId, type, legalId)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(TEXT_PLAIN)
			.expectBody(String.class).isEqualTo(partyId);

		// Assert
		verify(serviceMock).getPartyId(municipalityId, PRIVATE, legalId);
	}

	@Test
	void getLegalIdByPartyId() {

		// Arrange
		final var municipalityId = "2281";
		final var type = PRIVATE.name();
		final var partyId = "81471222-5798-11e9-ae24-57fa13b361e1";
		final var legalId = "200001011234";

		when(serviceMock.getLegalId(municipalityId, PRIVATE, partyId)).thenReturn(legalId);

		// Act
		webTestClient.get().uri("/{municipalityId}/{type}/{partyId}/legalId", municipalityId, type, partyId)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(TEXT_PLAIN)
			.expectBody(String.class).isEqualTo(legalId);

		// Assert
		verify(serviceMock).getLegalId(municipalityId, PRIVATE, partyId);
	}
}
