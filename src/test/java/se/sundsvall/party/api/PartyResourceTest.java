package se.sundsvall.party.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.party.Application;
import se.sundsvall.party.api.model.PartyLegalIdResponse;
import se.sundsvall.party.service.PartyService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static se.sundsvall.party.api.model.PartyType.ENTERPRISE;
import static se.sundsvall.party.api.model.PartyType.PRIVATE;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class PartyResourceTest {

	@MockitoBean
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

	@Test
	void getPartyIdsByPersonNumbers() {

		// Arrange
		final var municipalityId = "2281";
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personId1 = "81471222-5798-11e9-ae24-57fa13b361e1";
		final var personId2 = "81471222-5798-11e9-ae24-57fa13b361e2";
		final var personNumbers = List.of(personNumber1, personNumber2);
		final var expectedResult = Map.of(personNumber1, personId1, personNumber2, personId2);

		when(serviceMock.getPartyIds(municipalityId, personNumbers)).thenReturn(expectedResult);

		// Act
		webTestClient.post().uri("/{municipalityId}/PRIVATE/partyIds", municipalityId)
			.contentType(APPLICATION_JSON)
			.bodyValue(personNumbers)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$." + personNumber1).isEqualTo(personId1)
			.jsonPath("$." + personNumber2).isEqualTo(personId2);

		// Assert
		verify(serviceMock).getPartyIds(municipalityId, personNumbers);
	}

	@Test
	void getLegalIdsByPartyIds() {

		// Arrange
		final var municipalityId = "2281";
		final var personId1 = "81471222-5798-11e9-ae24-57fa13b361e1";
		final var personId2 = "81471222-5798-11e9-ae24-57fa13b361e2";
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personIds = Set.of(personId1, personId2);
		final var expectedResult = Map.of(personId1, personNumber1, personId2, personNumber2);

		when(serviceMock.getLegalIds(municipalityId, personIds)).thenReturn(expectedResult);

		// Act
		webTestClient.post().uri("/{municipalityId}/PRIVATE/legalIds", municipalityId)
			.contentType(APPLICATION_JSON)
			.bodyValue(personIds)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$." + personId1).isEqualTo(personNumber1)
			.jsonPath("$." + personId2).isEqualTo(personNumber2);

		// Assert
		verify(serviceMock).getLegalIds(municipalityId, personIds);
	}

	@Test
	void getLegalIdsByPartyIdsWithoutType() {

		// Arrange
		final var municipalityId = "2281";
		final var partyId1 = "81471222-5798-11e9-ae24-57fa13b361e1";
		final var partyId2 = "81471222-5798-11e9-ae24-57fa13b361e2";
		final var partyId3 = "81471222-5798-11e9-ae24-57fa13b361e3";
		final var partyIds = Set.of(partyId1, partyId2, partyId3);
		final var expectedResult = new PartyLegalIdResponse(
			Map.of(partyId1, "199001011234"),
			Map.of(partyId2, "5565125584"),
			List.of(partyId3));

		when(serviceMock.getLegalIdsByPartyIds(municipalityId, partyIds)).thenReturn(expectedResult);

		// Act
		webTestClient.post().uri("/{municipalityId}/legalIds", municipalityId)
			.contentType(APPLICATION_JSON)
			.bodyValue(partyIds)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.personalNumbers." + partyId1).isEqualTo("199001011234")
			.jsonPath("$.organizationNumbers." + partyId2).isEqualTo("5565125584")
			.jsonPath("$.notFound[0]").isEqualTo(partyId3);

		// Assert
		verify(serviceMock).getLegalIdsByPartyIds(municipalityId, partyIds);
	}

	@Test
	void getLegalIdByPartyIdWithoutType() {

		// Arrange
		final var municipalityId = "2281";
		final var partyId = "81471222-5798-11e9-ae24-57fa13b361e1";
		final var legalId = "197706010123";

		when(serviceMock.getLegalIdByPartyId(municipalityId, partyId)).thenReturn(legalId);

		// Act
		webTestClient.get().uri("/{municipalityId}/partyId/{partyId}/legalId", municipalityId, partyId)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(TEXT_PLAIN)
			.expectBody(String.class).isEqualTo(legalId);

		// Assert
		verify(serviceMock).getLegalIdByPartyId(municipalityId, partyId);
	}
}
