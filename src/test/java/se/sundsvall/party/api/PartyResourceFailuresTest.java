package se.sundsvall.party.api;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
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

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class PartyResourceFailuresTest {

	private static final String ENTERPRISE_VALIDATION_MESSAGE = "must match the regular expression ^([1235789][\\d][2-9]\\d{7})$ or ^(19|20)[0-9]{10}$";
	private static final String PRIVATE_VALIDATION_MESSAGE = "must match the regular expression ^(19|20)[0-9]{10}$";
	private static final String UUID_VALIDATION_MESSAGE = "not a valid UUID";

	@MockBean
	private PartyService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void validateWrongFormatOnLegalIdForEnterprise() {
		var type = ENTERPRISE.name();
		var legalId = "1234";
		
		webTestClient.get().uri("/{type}/{legalId}/partyId", type, legalId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody()
			.jsonPath("$.title").isEqualTo("Constraint Violation")
			.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
			.jsonPath("$.violations[0].field").isEqualTo("getPartyIdByLegalId.legalId")
			.jsonPath("$.violations[0].message").isEqualTo(ENTERPRISE_VALIDATION_MESSAGE);

		verifyNoInteractions(serviceMock);
	}

	@Test
	void validateWrongFormatOnLegalIdForPrivate() {
		var type = PRIVATE.name();
		var legalId = "1234";
		
		webTestClient.get().uri("/{type}/{legalId}/partyId", type, legalId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody()
			.jsonPath("$.title").isEqualTo("Constraint Violation")
			.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
			.jsonPath("$.violations[0].field").isEqualTo("getPartyIdByLegalId.legalId")
			.jsonPath("$.violations[0].message").isEqualTo(PRIVATE_VALIDATION_MESSAGE);

		verifyNoInteractions(serviceMock);
	}
	
	@Test
	void validateWrongFormatOnPartyId() {
		var type = ENTERPRISE.name();
		var partyId = "81471222-5798-11e9-ae24-57fa13b361ex";
		
		webTestClient.get().uri("/{type}/{partyId}/legalId", type, partyId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody()
			.jsonPath("$.title").isEqualTo("Constraint Violation")
			.jsonPath("$.status").isEqualTo(BAD_REQUEST.value())
			.jsonPath("$.violations[0].field").isEqualTo("getLegalIdByPartyId.partyId")
			.jsonPath("$.violations[0].message").isEqualTo(UUID_VALIDATION_MESSAGE);

		verifyNoInteractions(serviceMock);
	}
}
