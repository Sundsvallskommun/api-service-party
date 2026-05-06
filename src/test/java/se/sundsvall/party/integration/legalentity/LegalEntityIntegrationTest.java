package se.sundsvall.party.integration.legalentity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import se.sundsvall.dept44.exception.ClientProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegalEntityIntegrationTest {

	@Mock
	private LegalEntityClient clientMock;

	@InjectMocks
	private LegalEntityIntegration integration;

	@Test
	void getOrganizationId() {
		when(clientMock.getOrganizationId("2281", "5565125584")).thenReturn("\"some-uuid\"");

		final var result = integration.getOrganizationId("2281", "5565125584");

		assertThat(result).contains("some-uuid");
	}

	@Test
	void getOrganizationIdReturnsNull() {
		when(clientMock.getOrganizationId("2281", "5565125584")).thenReturn(null);

		final var result = integration.getOrganizationId("2281", "5565125584");

		assertThat(result).isEmpty();
	}

	@Test
	void getOrganizationIdNotFound() {
		when(clientMock.getOrganizationId("2281", "5565125584")).thenThrow(new ClientProblem(HttpStatus.NOT_FOUND, "Not found"));

		final var result = integration.getOrganizationId("2281", "5565125584");

		assertThat(result).isEmpty();
	}

	@Test
	void getOrganizationIdNon404ClientProblemPropagates() {
		when(clientMock.getOrganizationId("2281", "5565125584")).thenThrow(new ClientProblem(HttpStatus.FORBIDDEN, "Forbidden"));

		assertThatThrownBy(() -> integration.getOrganizationId("2281", "5565125584"))
			.isInstanceOf(ClientProblem.class);
	}

	@Test
	void getOrganizationNumber() {
		when(clientMock.getOrganizationNumber("2281", "some-uuid")).thenReturn("\"5565125584\"");

		final var result = integration.getOrganizationNumber("2281", "some-uuid");

		assertThat(result).contains("5565125584");
	}

	@Test
	void getOrganizationNumberReturnsNull() {
		when(clientMock.getOrganizationNumber("2281", "some-uuid")).thenReturn(null);

		final var result = integration.getOrganizationNumber("2281", "some-uuid");

		assertThat(result).isEmpty();
	}

	@Test
	void getOrganizationNumberNotFound() {
		when(clientMock.getOrganizationNumber("2281", "some-uuid")).thenThrow(new ClientProblem(HttpStatus.NOT_FOUND, "Not found"));

		final var result = integration.getOrganizationNumber("2281", "some-uuid");

		assertThat(result).isEmpty();
	}

	@Test
	void getOrganizationNumberNon404ClientProblemPropagates() {
		when(clientMock.getOrganizationNumber("2281", "some-uuid")).thenThrow(new ClientProblem(HttpStatus.FORBIDDEN, "Forbidden"));

		assertThatThrownBy(() -> integration.getOrganizationNumber("2281", "some-uuid"))
			.isInstanceOf(ClientProblem.class);
	}
}
