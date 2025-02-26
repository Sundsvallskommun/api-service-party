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

		final var legalId = "2201011234"; // LegalId without century

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
}
