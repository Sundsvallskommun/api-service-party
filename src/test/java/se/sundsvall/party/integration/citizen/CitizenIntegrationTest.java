package se.sundsvall.party.integration.citizen;

import generated.client.citizen.PersonGuidBatch;
import java.util.List;
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
class CitizenIntegrationTest {

	@Mock
	private CitizenClient clientMock;

	@InjectMocks
	private CitizenIntegration integration;

	@Test
	void getPersonId() {
		when(clientMock.getPersonId("2281", "199001011234")).thenReturn("\"some-uuid\"");

		final var result = integration.getPersonId("2281", "199001011234");

		assertThat(result).contains("some-uuid");
	}

	@Test
	void getPersonIdReturnsNull() {
		when(clientMock.getPersonId("2281", "199001011234")).thenReturn(null);

		final var result = integration.getPersonId("2281", "199001011234");

		assertThat(result).isEmpty();
	}

	@Test
	void getPersonIdNotFound() {
		when(clientMock.getPersonId("2281", "199001011234")).thenThrow(new ClientProblem(HttpStatus.NOT_FOUND, "Not found"));

		final var result = integration.getPersonId("2281", "199001011234");

		assertThat(result).isEmpty();
	}

	@Test
	void getPersonIdNon404ClientProblemPropagates() {
		when(clientMock.getPersonId("2281", "199001011234")).thenThrow(new ClientProblem(HttpStatus.FORBIDDEN, "Forbidden"));

		assertThatThrownBy(() -> integration.getPersonId("2281", "199001011234"))
			.isInstanceOf(ClientProblem.class);
	}

	@Test
	void getPersonalNumber() {
		when(clientMock.getPersonalNumber("2281", "some-uuid")).thenReturn("\"199001011234\"");

		final var result = integration.getPersonalNumber("2281", "some-uuid");

		assertThat(result).contains("199001011234");
	}

	@Test
	void getPersonalNumberReturnsNull() {
		when(clientMock.getPersonalNumber("2281", "some-uuid")).thenReturn(null);

		final var result = integration.getPersonalNumber("2281", "some-uuid");

		assertThat(result).isEmpty();
	}

	@Test
	void getPersonalNumberNotFound() {
		when(clientMock.getPersonalNumber("2281", "some-uuid")).thenThrow(new ClientProblem(HttpStatus.NOT_FOUND, "Not found"));

		final var result = integration.getPersonalNumber("2281", "some-uuid");

		assertThat(result).isEmpty();
	}

	@Test
	void getPersonalNumberNon404ClientProblemPropagates() {
		when(clientMock.getPersonalNumber("2281", "some-uuid")).thenThrow(new ClientProblem(HttpStatus.FORBIDDEN, "Forbidden"));

		assertThatThrownBy(() -> integration.getPersonalNumber("2281", "some-uuid"))
			.isInstanceOf(ClientProblem.class);
	}

	@Test
	void getPersonIdsBatch() {
		final var personNumbers = List.of("199001011234");
		final var batch = List.of(new PersonGuidBatch().personNumber("199001011234").success(true));
		when(clientMock.getPersonIdsBatch("2281", personNumbers)).thenReturn(batch);

		final var result = integration.getPersonIdsBatch("2281", personNumbers);

		assertThat(result).isEqualTo(batch);
	}

	@Test
	void getPersonalNumbersBatch() {
		final var personIds = List.of("some-uuid");
		final var batch = List.of(new PersonGuidBatch().personNumber("199001011234").success(true));
		when(clientMock.getPersonalNumbersBatch("2281", personIds)).thenReturn(batch);

		final var result = integration.getPersonalNumbersBatch("2281", personIds);

		assertThat(result).isEqualTo(batch);
	}
}
