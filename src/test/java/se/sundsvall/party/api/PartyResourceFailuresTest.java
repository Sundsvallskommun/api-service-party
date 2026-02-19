package se.sundsvall.party.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.party.Application;
import se.sundsvall.party.service.PartyService;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static se.sundsvall.party.api.model.PartyType.ENTERPRISE;
import static se.sundsvall.party.api.model.PartyType.PRIVATE;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class PartyResourceFailuresTest {

	private static final String ENTERPRISE_VALIDATION_MESSAGE = "must match the regular expression ^([1235789][\\d][2-9]\\d{7})$ or ^(19|20)[0-9]{10}$";
	private static final String PRIVATE_VALIDATION_MESSAGE = "must match the regular expression ^(19|20)[0-9]{10}$";
	private static final String UUID_VALIDATION_MESSAGE = "not a valid UUID";
	private static final String MUNICIPALITY_ID_VALIDATION_MESSAGE = "not a valid municipality ID";

	@MockitoBean
	private PartyService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getPartyIdByLegalIdBadMunicipalityIdForEnterprise() {

		// Arrange
		final var municipalityId = "000";
		final var type = ENTERPRISE.name();
		final var legalId = "5566123456";

		// Act
		final var response = webTestClient.get().uri("/{municipalityId}/{type}/{legalId}/partyId", municipalityId, type, legalId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getPartyIdByLegalId.municipalityId", MUNICIPALITY_ID_VALIDATION_MESSAGE));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getPartyIdByLegalIdBadMunicipalitylIdForPrivate() {

		// Arrange
		final var municipalityId = "000";
		final var type = PRIVATE.name();
		final var legalId = "5566123456";

		// Act
		final var response = webTestClient.get().uri("/{municipalityId}/{type}/{legalId}/partyId", municipalityId, type, legalId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getPartyIdByLegalId.municipalityId", MUNICIPALITY_ID_VALIDATION_MESSAGE));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getPartyIdByLegalIdBadFormatOnLegalIdForEnterprise() {

		// Arrange
		final var municipalityId = "2281";
		final var type = ENTERPRISE.name();
		final var legalId = "1234";

		// Act
		final var response = webTestClient.get().uri("/{municipalityId}/{type}/{legalId}/partyId", municipalityId, type, legalId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getPartyIdByLegalId.legalId", ENTERPRISE_VALIDATION_MESSAGE));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getPartyIdByLegalIdBadFormatOnLegalIdForPrivate() {

		// Arrange
		final var municipalityId = "2281";
		final var type = PRIVATE.name();
		final var legalId = "1234";

		// Act
		final var response = webTestClient.get().uri("/{municipalityId}/{type}/{legalId}/partyId", municipalityId, type, legalId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getPartyIdByLegalId.legalId", PRIVATE_VALIDATION_MESSAGE));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getLegalIdByPartyIdBadFormatOnPartyId() {

		// Arrange
		final var municipalityId = "2281";
		final var type = ENTERPRISE.name();
		final var partyId = "81471222-5798-11e9-ae24-57fa13b361ex";

		// Act
		final var response = webTestClient.get().uri("/{municipalityId}/{type}/{partyId}/legalId", municipalityId, type, partyId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getLegalIdByPartyId.partyId", UUID_VALIDATION_MESSAGE));

		verifyNoInteractions(serviceMock);
	}

	@Test
	void getLegalIdByPartyIdBadMunicipalityId() {

		// Arrange
		final var municipalityId = "000";
		final var type = ENTERPRISE.name();
		final var partyId = randomUUID().toString();

		// Act
		final var response = webTestClient.get().uri("/{municipalityId}/{type}/{partyId}/legalId", municipalityId, type, partyId)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getLegalIdByPartyId.municipalityId", MUNICIPALITY_ID_VALIDATION_MESSAGE));

		verifyNoInteractions(serviceMock);
	}
}
