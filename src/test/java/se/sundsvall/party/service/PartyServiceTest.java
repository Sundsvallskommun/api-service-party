package se.sundsvall.party.service;

import generated.client.citizen.PersonGuidBatch;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.party.integration.citizen.CitizenIntegration;
import se.sundsvall.party.integration.legalentity.LegalEntityIntegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
	private CitizenIntegration citizenIntegrationMock;

	@Mock
	private LegalEntityIntegration legalEntityIntegrationMock;

	@InjectMocks
	private PartyService service;

	@Test
	void testGetLegalIdForEnterpriseType() {

		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.of(LEGAL_ID));

		assertThat(service.getLegalId(MUNICIPALITY_ID, ENTERPRISE, PARTY_ID)).isEqualTo(LEGAL_ID);
		verify(legalEntityIntegrationMock).getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID);
	}

	@Test
	void testGetLegalIdForPrivateType() {

		when(citizenIntegrationMock.getPersonalNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.of(LEGAL_ID));

		assertThat(service.getLegalId(MUNICIPALITY_ID, PRIVATE, PARTY_ID)).isEqualTo(LEGAL_ID);
		verify(citizenIntegrationMock).getPersonalNumber(MUNICIPALITY_ID, PARTY_ID);
	}

	@Test
	void testGetLegalIdForEnterpriseTypeNotFound() {

		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getLegalId(MUNICIPALITY_ID, ENTERPRISE, PARTY_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("No legalId found for partyId");
	}

	@Test
	void testGetLegalIdForPrivateTypeNotFound() {

		when(citizenIntegrationMock.getPersonalNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getLegalId(MUNICIPALITY_ID, PRIVATE, PARTY_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("No legalId found for partyId");
	}

	@Test
	void testGetPartyIdForEnterpriseType() {

		final var legalId = "5566778899";

		when(legalEntityIntegrationMock.getOrganizationId(MUNICIPALITY_ID, legalId)).thenReturn(Optional.of(PARTY_ID));

		assertThat(service.getPartyId(MUNICIPALITY_ID, ENTERPRISE, legalId)).isEqualTo(PARTY_ID);
		verify(legalEntityIntegrationMock).getOrganizationId(MUNICIPALITY_ID, legalId);
	}

	@Test
	void testGetPartyIdForEnterpriseTypeWithPersonalNumber() {

		final var legalId = "197706010123";

		when(legalEntityIntegrationMock.getOrganizationId(MUNICIPALITY_ID, legalId)).thenReturn(Optional.of(PARTY_ID));

		assertThat(service.getPartyId(MUNICIPALITY_ID, ENTERPRISE, legalId)).isEqualTo(PARTY_ID);
		verify(legalEntityIntegrationMock).getOrganizationId(MUNICIPALITY_ID, legalId);
	}

	@Test
	void testGetPartyIdForPrivateType() {

		final var legalId = "197706010123";

		when(citizenIntegrationMock.getPersonId(MUNICIPALITY_ID, legalId)).thenReturn(Optional.of(PARTY_ID));

		assertThat(service.getPartyId(MUNICIPALITY_ID, PRIVATE, legalId)).isEqualTo(PARTY_ID);
		verify(citizenIntegrationMock).getPersonId(MUNICIPALITY_ID, legalId);
	}

	@Test
	void testGetPartyIdForEnterpriseTypeNotFound() {

		final var legalId = "5566778899";

		when(legalEntityIntegrationMock.getOrganizationId(MUNICIPALITY_ID, legalId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getPartyId(MUNICIPALITY_ID, ENTERPRISE, legalId))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("No partyId found for legalId");
	}

	@Test
	void testGetPartyIdForPrivateTypeNotFound() {

		final var legalId = "197706010123";

		when(citizenIntegrationMock.getPersonId(MUNICIPALITY_ID, legalId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getPartyId(MUNICIPALITY_ID, PRIVATE, legalId))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("No partyId found for legalId");
	}

	@Test
	void testGetPartyIdForEnterpriseTypeWithInvalidLegalId() {

		assertThatThrownBy(() -> service.getPartyId(MUNICIPALITY_ID, ENTERPRISE, "invalid"))
			.isInstanceOf(ConstraintViolationProblem.class);

		verifyNoInteractions(citizenIntegrationMock, legalEntityIntegrationMock);
	}

	@Test
	void testGetPartyIdForPrivateTypeWithInvalidLegalId() {

		assertThatThrownBy(() -> service.getPartyId(MUNICIPALITY_ID, PRIVATE, "invalid"))
			.isInstanceOf(ConstraintViolationProblem.class);

		verifyNoInteractions(citizenIntegrationMock, legalEntityIntegrationMock);
	}

	@Test
	void testGetPartyIds() {
		// Arrange
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumbers = List.of(personNumber1, personNumber2);

		when(citizenIntegrationMock.getPersonIdsBatch(MUNICIPALITY_ID, personNumbers)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(personNumber2).personId(personId2).success(true)));

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).hasSize(2)
			.containsEntry(personNumber1, personId1.toString())
			.containsEntry(personNumber2, personId2.toString());
		verify(citizenIntegrationMock).getPersonIdsBatch(MUNICIPALITY_ID, personNumbers);
	}

	@Test
	void testGetPartyIdsFiltersUnsuccessfulResults() {
		// Arrange
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personNumbers = List.of(personNumber1, personNumber2);

		when(citizenIntegrationMock.getPersonIdsBatch(MUNICIPALITY_ID, personNumbers)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(personNumber2).success(false).errorMessage("Not found")));

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personNumber1, personId1.toString())
			.doesNotContainKey(personNumber2);
		verify(citizenIntegrationMock).getPersonIdsBatch(MUNICIPALITY_ID, personNumbers);
	}

	@Test
	void testGetPartyIdsFiltersNullPersonNumber() {
		// Arrange
		final var personNumber1 = "199001011234";
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumbers = List.of(personNumber1, "199002021234");

		when(citizenIntegrationMock.getPersonIdsBatch(MUNICIPALITY_ID, personNumbers)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(null).personId(personId2).success(true)));

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personNumber1, personId1.toString());
	}

	@Test
	void testGetPartyIdsFiltersNullPersonId() {
		// Arrange
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personNumbers = List.of(personNumber1, personNumber2);

		when(citizenIntegrationMock.getPersonIdsBatch(MUNICIPALITY_ID, personNumbers)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(personNumber2).personId(null).success(true)));

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personNumber1, personId1.toString());
	}

	@Test
	void testGetPartyIdsWithEmptyList() {
		// Arrange
		final List<String> personNumbers = List.of();

		when(citizenIntegrationMock.getPersonIdsBatch(MUNICIPALITY_ID, personNumbers)).thenReturn(List.of());

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).isEmpty();
		verify(citizenIntegrationMock).getPersonIdsBatch(MUNICIPALITY_ID, personNumbers);
	}

	@Test
	void testGetPartyIdsWithDuplicates() {
		// Arrange
		final var personNumber1 = "199001011234";
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personNumbers = List.of(personNumber1, personNumber1, personNumber1); // Same person 3 times
		final var distinctPersonNumbers = List.of(personNumber1); // Expected distinct list

		when(citizenIntegrationMock.getPersonIdsBatch(MUNICIPALITY_ID, distinctPersonNumbers)).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true)));

		// Act
		final var result = service.getPartyIds(MUNICIPALITY_ID, personNumbers);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personNumber1, personId1.toString());
		verify(citizenIntegrationMock).getPersonIdsBatch(MUNICIPALITY_ID, distinctPersonNumbers); // Verify only distinct list was sent
	}

	@Test
	void testGetLegalIds() {
		// Arrange
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personIds = Set.of(personId1.toString(), personId2.toString());

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(personNumber2).personId(personId2).success(true)));

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).hasSize(2)
			.containsEntry(personId1.toString(), personNumber1)
			.containsEntry(personId2.toString(), personNumber2);
	}

	@Test
	void testGetLegalIdsFiltersUnsuccessfulResults() {
		// Arrange
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumber1 = "199001011234";
		final var personIds = Set.of(personId1.toString(), personId2.toString());

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personId(personId2).success(false).errorMessage("Not found")));

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personId1.toString(), personNumber1)
			.doesNotContainKey(personId2.toString());
	}

	@Test
	void testGetLegalIdsFiltersNullPersonNumber() {
		// Arrange
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumber1 = "199001011234";
		final var personIds = Set.of(personId1.toString(), personId2.toString());

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(null).personId(personId2).success(true)));

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personId1.toString(), personNumber1);
	}

	@Test
	void testGetLegalIdsFiltersNullPersonId() {
		// Arrange
		final var personId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var personIds = Set.of(personId1.toString(), "81471222-5798-11e9-ae24-57fa13b361e2");

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of(
			new PersonGuidBatch().personNumber(personNumber1).personId(personId1).success(true),
			new PersonGuidBatch().personNumber(personNumber2).personId(null).success(true)));

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).hasSize(1)
			.containsEntry(personId1.toString(), personNumber1);
	}

	@Test
	void testGetLegalIdsWithEmptySet() {
		// Arrange
		final Set<String> personIds = Set.of();

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of());

		// Act
		final var result = service.getLegalIds(MUNICIPALITY_ID, personIds);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void testGetLegalIdsByPartyIdsAllFoundInCitizen() {
		// Arrange
		final var partyId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var partyId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumber1 = "199001011234";
		final var personNumber2 = "199002021234";
		final var partyIds = Set.of(partyId1.toString(), partyId2.toString());

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of(
			new PersonGuidBatch().personId(partyId1).personNumber(personNumber1).success(true),
			new PersonGuidBatch().personId(partyId2).personNumber(personNumber2).success(true)));

		// Act
		final var result = service.getLegalIdsByPartyIds(MUNICIPALITY_ID, partyIds);

		// Assert
		assertThat(result.personalNumbers()).hasSize(2)
			.containsEntry(partyId1.toString(), personNumber1)
			.containsEntry(partyId2.toString(), personNumber2);
		assertThat(result.organizationNumbers()).isEmpty();
		assertThat(result.notFound()).isEmpty();
		verifyNoInteractions(legalEntityIntegrationMock);
	}

	@Test
	void testGetLegalIdsByPartyIdsAllFoundInLegalEntity() {
		// Arrange
		final var partyId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var partyId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var orgNumber1 = "5565125584";
		final var orgNumber2 = "5565125585";
		final var partyIds = Set.of(partyId1.toString(), partyId2.toString());

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of(
			new PersonGuidBatch().personId(partyId1).success(false),
			new PersonGuidBatch().personId(partyId2).success(false)));
		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, partyId1.toString())).thenReturn(Optional.of(orgNumber1));
		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, partyId2.toString())).thenReturn(Optional.of(orgNumber2));

		// Act
		final var result = service.getLegalIdsByPartyIds(MUNICIPALITY_ID, partyIds);

		// Assert
		assertThat(result.personalNumbers()).isEmpty();
		assertThat(result.organizationNumbers()).hasSize(2)
			.containsEntry(partyId1.toString(), orgNumber1)
			.containsEntry(partyId2.toString(), orgNumber2);
		assertThat(result.notFound()).isEmpty();
	}

	@Test
	void testGetLegalIdsByPartyIdsMixedResults() {
		// Arrange
		final var partyId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var partyId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var partyId3 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e3");
		final var personNumber1 = "199001011234";
		final var orgNumber2 = "5565125584";
		final var partyIds = Set.of(partyId1.toString(), partyId2.toString(), partyId3.toString());

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of(
			new PersonGuidBatch().personId(partyId1).personNumber(personNumber1).success(true),
			new PersonGuidBatch().personId(partyId2).success(false),
			new PersonGuidBatch().personId(partyId3).success(false)));
		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, partyId2.toString())).thenReturn(Optional.of(orgNumber2));
		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, partyId3.toString())).thenReturn(Optional.empty());

		// Act
		final var result = service.getLegalIdsByPartyIds(MUNICIPALITY_ID, partyIds);

		// Assert
		assertThat(result.personalNumbers()).hasSize(1)
			.containsEntry(partyId1.toString(), personNumber1);
		assertThat(result.organizationNumbers()).hasSize(1)
			.containsEntry(partyId2.toString(), orgNumber2);
		assertThat(result.notFound()).containsExactly(partyId3.toString());
	}

	@Test
	void testGetLegalIdsByPartyIdsEmptyInput() {
		// Arrange
		final Set<String> partyIds = Set.of();

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of());

		// Act
		final var result = service.getLegalIdsByPartyIds(MUNICIPALITY_ID, partyIds);

		// Assert
		assertThat(result.personalNumbers()).isEmpty();
		assertThat(result.organizationNumbers()).isEmpty();
		assertThat(result.notFound()).isEmpty();
		verifyNoInteractions(legalEntityIntegrationMock);
	}

	@Test
	void testGetLegalIdsByPartyIdsLegalEntityOnlyCalledForCitizenMisses() {
		// Arrange
		final var partyId1 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e1");
		final var partyId2 = UUID.fromString("81471222-5798-11e9-ae24-57fa13b361e2");
		final var personNumber1 = "199001011234";
		final var partyIds = Set.of(partyId1.toString(), partyId2.toString());

		when(citizenIntegrationMock.getPersonalNumbersBatch(any(), any())).thenReturn(List.of(
			new PersonGuidBatch().personId(partyId1).personNumber(personNumber1).success(true),
			new PersonGuidBatch().personId(partyId2).success(false)));
		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, partyId2.toString())).thenReturn(Optional.empty());

		// Act
		service.getLegalIdsByPartyIds(MUNICIPALITY_ID, partyIds);

		// Assert
		verify(legalEntityIntegrationMock).getOrganizationNumber(MUNICIPALITY_ID, partyId2.toString());
		verify(legalEntityIntegrationMock, never()).getOrganizationNumber(MUNICIPALITY_ID, partyId1.toString());
	}

	@Test
	void testGetLegalIdByPartyIdFoundAsPrivate() {

		when(citizenIntegrationMock.getPersonalNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.of(LEGAL_ID));

		assertThat(service.getLegalIdByPartyId(MUNICIPALITY_ID, PARTY_ID)).isEqualTo(LEGAL_ID);

		verify(citizenIntegrationMock).getPersonalNumber(MUNICIPALITY_ID, PARTY_ID);
		verifyNoInteractions(legalEntityIntegrationMock);
	}

	@Test
	void testGetLegalIdByPartyIdFoundAsEnterprise() {

		when(citizenIntegrationMock.getPersonalNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.empty());
		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.of(LEGAL_ID));

		assertThat(service.getLegalIdByPartyId(MUNICIPALITY_ID, PARTY_ID)).isEqualTo(LEGAL_ID);

		verify(citizenIntegrationMock).getPersonalNumber(MUNICIPALITY_ID, PARTY_ID);
		verify(legalEntityIntegrationMock).getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID);
	}

	@Test
	void testGetLegalIdByPartyIdNotFound() {

		when(citizenIntegrationMock.getPersonalNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.empty());
		when(legalEntityIntegrationMock.getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getLegalIdByPartyId(MUNICIPALITY_ID, PARTY_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasMessageContaining("was not found as PRIVATE or ENTERPRISE");

		verify(citizenIntegrationMock).getPersonalNumber(MUNICIPALITY_ID, PARTY_ID);
		verify(legalEntityIntegrationMock).getOrganizationNumber(MUNICIPALITY_ID, PARTY_ID);
	}
}
