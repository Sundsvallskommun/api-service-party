package se.sundsvall.party.service;

import static org.apache.commons.lang3.StringUtils.wrap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.party.api.model.PartyType.ENTERPRISE;
import static se.sundsvall.party.api.model.PartyType.PRIVATE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.party.integration.citizen.CitizenClient;
import se.sundsvall.party.integration.legalentity.LegalEntityClient;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

	@Mock
	private CitizenClient citizenClientMock;

	@Mock
	private LegalEntityClient legalEntityClientMock;

	@InjectMocks
	private PartyService service;

	@Test
	void testGetLegalIdForEnterpriseType() {
		final var municipalityId = "municipalityId";
		final var partyId = "partyId";
		final var legalId = "legalId";

		when(legalEntityClientMock.getOrganizationNumber(partyId)).thenReturn(wrap(legalId, "\""));

		assertThat(service.getLegalId(municipalityId, ENTERPRISE, partyId)).isEqualTo(legalId);
		verify(legalEntityClientMock).getOrganizationNumber(partyId);
	}

	@Test
	void testGetLegalIdForPrivateType() {
		final var municipalityId = "municipalityId";
		final var partyId = "partyId";
		final var legalId = "legalId";

		when(citizenClientMock.getPersonalNumber(partyId)).thenReturn(wrap(legalId, "\""));

		assertThat(service.getLegalId(municipalityId, PRIVATE, partyId)).isEqualTo(legalId);
		verify(citizenClientMock).getPersonalNumber(partyId);
	}

	@Test
	void testGetLegalIdForNullType() {
		final var municipalityId = "municipalityId";
		final var legalId = "legalId";

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getLegalId(municipalityId, null, legalId));
		assertThat(exception).hasMessage("Not Found: No legalId found!");

		verifyNoInteractions(citizenClientMock, citizenClientMock, legalEntityClientMock);
	}

	@Test
	void testGetPartyIdForEnterpriseTypeForLegalIdWithoutCentury() {
		final var municipalityId = "municipalityId";
		final var legalId = "2201011234"; // LegalId without century
		final var partyId = "partyId";

		when(legalEntityClientMock.getOrganizationId(legalId)).thenReturn(wrap(partyId, "\""));

		assertThat(service.getPartyId(municipalityId, ENTERPRISE, legalId)).isEqualTo(partyId);
		verify(legalEntityClientMock).getOrganizationId(legalId);
	}

	@Test
	void testGetPartyIdForEnterpriseTypeForLegalIdWithCentury() {
		final var municipalityId = "municipalityId";
		final var legalId = "202201011234"; // LegalId with century
		final var partyId = "partyId";

		when(legalEntityClientMock.getOrganizationId(legalId)).thenReturn(wrap(partyId, "\""));

		assertThat(service.getPartyId(municipalityId, ENTERPRISE, legalId)).isEqualTo(partyId);
		verify(legalEntityClientMock).getOrganizationId(legalId);
	}

	@Test
	void testGetPartyIdForPrivateType() {
		final var municipalityId = "municipalityId";
		final var legalId = "202201011234"; // LegalId with century
		final var partyId = "partyId";

		when(citizenClientMock.getPersonId(legalId)).thenReturn(wrap(partyId, "\""));

		assertThat(service.getPartyId(municipalityId, PRIVATE, legalId)).isEqualTo(partyId);
		verify(citizenClientMock).getPersonId(legalId);
	}

	@Test
	void testGetPartyIdForNullType() {
		final var municipalityId = "municipalityId";
		final var partyId = "partyId";

		final var exception = assertThrows(ThrowableProblem.class, () -> service.getPartyId(municipalityId, null, partyId));

		assertThat(exception).hasMessage("Not Found: No partyId found!");
		verifyNoInteractions(citizenClientMock, citizenClientMock, legalEntityClientMock);
	}
}
