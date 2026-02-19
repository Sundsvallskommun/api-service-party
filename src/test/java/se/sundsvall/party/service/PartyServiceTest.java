package se.sundsvall.party.service;

import generated.client.citizen.PersonGuidBatch;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.party.integration.citizen.CitizenClient;
import se.sundsvall.party.integration.legalentity.LegalEntityClient;

import static org.apache.commons.lang3.StringUtils.wrap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.party.api.model.PartyType.ENTERPRISE;
import static se.sundsvall.party.api.model.PartyType.PRIVATE;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

	private static final String PARTY_ID = "partyId";
	private static final String LEGAL_ID = "legalId";
	private static final String MUNICIPALITY_ID = "municipalityId";

	@Mock
	private CitizenClient citizenClientMock;

	@Mock
	private LegalEntityClient legalEntityClientMock;

	@InjectMocks
	private PartyService service;

	@Test
	void testGetLegalIdForEnterpriseType() {

		when(legalEntityClientMock.getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(wrap(LEGAL_ID, "\""));

		assertThat(service.getLegalId(MUNICIPALITY_ID, ENTERPRISE, PARTY_ID)).isEqualTo(LEGAL_ID);
		verify(legalEntityClientMock).getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID);
	}

	@Test
	void testGetLegalIdForPrivateType() {

		when(citizenClientMock.getPersonalNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(wrap(LEGAL_ID, "\""));

		assertThat(service.getLegalId(MUNICIPALITY_ID, PRIVATE, PARTY_ID)).isEqualTo(LEGAL_ID);
		verify(citizenClientMock).getPersonalNumber(MUNICIPALITY_ID, PARTY_ID);
	}

	@Test
	void testGetLegalIdForNullType() {

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getLegalId(MUNICIPALITY_ID, null, LEGAL_ID));
		assertThat(exception).hasMessage("Not Found: No legalId found!");

		verifyNoInteractions(citizenClientMock, citizenClientMock, legalEntityClientMock);
	}

	@Test
	void testGetPartyIdForEnterpriseTypeForLegalIdWithoutCentury() {

		final var legalId = "2201011234"; // LegalId without a century

		when(legalEntityClientMock.getOrganizationId(MUNICIPALITY_ID, legalId)).thenReturn(wrap(PARTY_ID, "\""));

		assertThat(service.getPartyId(MUNICIPALITY_ID, ENTERPRISE, legalId)).isEqualTo(PARTY_ID);
		verify(legalEntityClientMock).getOrganizationId(MUNICIPALITY_ID, legalId);
	}

	@Test
	void testGetPartyIdForEnterpriseTypeForLegalIdWithCentury() {

		final var legalId = "202201011234"; // LegalId with century

		when(legalEntityClientMock.getOrganizationId(MUNICIPALITY_ID, legalId)).thenReturn(wrap(PARTY_ID, "\""));

		assertThat(service.getPartyId(MUNICIPALITY_ID, ENTERPRISE, legalId)).isEqualTo(PARTY_ID);
		verify(legalEntityClientMock).getOrganizationId(MUNICIPALITY_ID, legalId);
	}

	@Test
	void testGetPartyIdForPrivateType() {

		final var legalId = "202201011234"; // LegalId with century

		when(citizenClientMock.getPersonId(MUNICIPALITY_ID, legalId)).thenReturn(wrap(PARTY_ID, "\""));

		assertThat(service.getPartyId(MUNICIPALITY_ID, PRIVATE, legalId)).isEqualTo(PARTY_ID);
		verify(citizenClientMock).getPersonId(MUNICIPALITY_ID, legalId);
	}

	@Test
	void testGetPartyIdForNullType() {

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getPartyId(MUNICIPALITY_ID, null, PARTY_ID));

		assertThat(exception).hasMessage("Not Found: No partyId found!");
		verifyNoInteractions(citizenClientMock, citizenClientMock, legalEntityClientMock);
	}

	@Test
	void testGetPartyIds() {
		// Arrange
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumbers = List.of(personNumber1, personNumber2);

		when(citizenClientMock.getPersonIdsBatch(MUNICIPALITY_ID, personNumbers)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(personNumber2).personId(personId2).success(true)));

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).hasSize(2)
			.containsEntry(personNumber1, personId1.toString())
			.containsEntry(personNumber2, personId2.toString());
		verify(citizenClientMock).getPersonIdsBatch(MUNICIPALITY_ID, personNumbers);
	}

	@Test
	void testGetPartyIdsFiltersUnsuccessfulResults() {
		// Arrange
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personNumbers = List.of(personNumber1, personNumber2);

		when(citizenClientMock.getPersonIdsBatch(MUNICIPALITY_ID, personNumbers)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(personNumber2).success(false).errorMessage("Not found")));

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personNumber1, personId1.toString())
			.doesNotContainKey(personNumber2);
		verify(citizenClientMock).getPersonIdsBatch(MUNICIPALITY_ID, personNumbers);
	}

	@Test
	void testGetPartyIdsWithEmptyList() {
		// Arrange
		final List<String> personNumbers = List.of();

		when(citizenClientMock.getPersonIdsBatch(MUNICIPALITY_ID, personNumbers)).thenReturn(List.of());

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).isEmpty();
		verify(citizenClientMock).getPersonIdsBatch(MUNICIPALITY_ID, personNumbers);
	}

	@Test
	void testGetPartyIdsWithDuplicates() {
		// Arrange
		final var personNumber1 = "199001011234";
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personNumbers = List.of(personNumber1, personNumber1, personNumber1); // Same person 3 times
		final var distinctPersonNumbers = List.of(personNumber1); // Expected distinct list

		when(citizenClientMock.getPersonIdsBatch(MUNICIPALITY_ID, distinctPersonNumbers)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true)));

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personNumber1, personId1.toString());
		verify(citizenClientMock).getPersonIdsBatch(MUNICIPALITY_ID, distinctPersonNumbers); // Verify only distinct list was sent
	}

	@Test
	void testGetLegalIds() {
		// Arrange
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personIds = List.of(personId1.toString(), personId2.toString());

		when(citizenClientMock.getPersonalNumbersBatch(MUNICIPALITY_ID, personIds)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(personNumber2).personId(personId2).success(true)));

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).hasSize(2)
			.containsEntry(personId1.toString(), personNumber1)
			.containsEntry(personId2.toString(), personNumber2);
		verify(citizenClientMock).getPersonalNumbersBatch(MUNICIPALITY_ID, personIds);
	}

	@Test
	void testGetLegalIdsFiltersUnsuccessfulResults() {
		// Arrange
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumber1 = "199001011234";
		final var personIds = List.of(personId1.toString(), personId2.toString());

		when(citizenClientMock.getPersonalNumbersBatch(MUNICIPALITY_ID, personIds)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personId(personId2).success(false).errorMessage("Not found")));

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personId1.toString(), personNumber1)
			.doesNotContainKey(personId2.toString());
		verify(citizenClientMock).getPersonalNumbersBatch(MUNICIPALITY_ID, personIds);
	}

	@Test
	void testGetLegalIdsWithEmptyList() {
		// Arrange
		final List<String> personIds = List.of();

		when(citizenClientMock.getPersonalNumbersBatch(MUNICIPALITY_ID, personIds)).thenReturn(List.of());

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).isEmpty();
		verify(citizenClientMock).getPersonalNumbersBatch(MUNICIPALITY_ID, personIds);
	}

	@Test
	void testGetLegalIdsWithDuplicates() {
		// Arrange
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personNumber1 = "199001011234";
		final var personIds = List.of(personId1.toString(), personId1.toString(), personId1.toString()); // Same UUID 3 times
		final var distinctPersonIds = List.of(personId1.toString()); // Expected distinct list

		when(citizenClientMock.getPersonalNumbersBatch(MUNICIPALITY_ID, distinctPersonIds)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true)));

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personId1.toString(), personNumber1);
		verify(citizenClientMock).getPersonalNumbersBatch(MUNICIPALITY_ID, distinctPersonIds); // Verify only distinct list was sent
	}
}
