package se.sundsvall.party.service;

import static org.apache.commons.lang3.StringUtils.wrap;
import static org.assertj.core.api.Assertions.assertThat;
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

import se.sundsvall.party.integration.citizen.CitizenClient;
import se.sundsvall.party.integration.citizenmapping.CitizenMappingClient;
import se.sundsvall.party.integration.legalentity.LegalEntityClient;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

	@Mock
	private CitizenMappingClient citizenMappingClientMock;

	@Mock
	private CitizenClient citizenClientMock;

	@Mock
	private LegalEntityClient legalEntityClientMock;

	@InjectMocks
	private PartyService service;
	
	@Test
	void testGetLegalIdForEnterpriseType() {
		var partyId = "partyId";
		var legalId = "legalId";
		
		when(legalEntityClientMock.getOrganizationNumber(partyId)).thenReturn(wrap(legalId, "\""));
		
		assertThat(service.getLegalId(ENTERPRISE, partyId)).isEqualTo(legalId);
		
		verify(legalEntityClientMock).getOrganizationNumber(partyId);
	}

	@Test
	void testGetLegalIdForPrivateType() {
		var partyId = "partyId";
		var legalId = "legalId";
		
		when(citizenMappingClientMock.getPersonalNumber(partyId)).thenReturn(wrap(legalId, "\""));
		
		assertThat(service.getLegalId(PRIVATE, partyId)).isEqualTo(legalId);
		
		verify(citizenMappingClientMock).getPersonalNumber(partyId);
	}

	@Test
	void testGetLegalIdForNullType() {
		var legalId = "legalId";
		
		assertThat(service.getLegalId(null, legalId)).isNull();
		
		verifyNoInteractions(citizenMappingClientMock, citizenClientMock, legalEntityClientMock);
	}

	@Test
	void testGetPartyIdForEnterpriseTypeForLegalIdWithoutCentury() {
		var legalId = "2201011234"; // LegalId without century
		var partyId = "partyId";
		
		when(legalEntityClientMock.getOrganizationId(legalId)).thenReturn(wrap(partyId, "\""));
		
		assertThat(service.getPartyId(ENTERPRISE, legalId)).isEqualTo(partyId);
		
		verify(legalEntityClientMock).getOrganizationId(legalId);
	}

	@Test
	void testGetPartyIdForEnterpriseTypeForLegalIdWithCentury() {
		var legalId = "202201011234"; // LegalId with century
		var partyId = "partyId";

		when(legalEntityClientMock.getOrganizationId(legalId)).thenReturn(wrap(partyId, "\""));

		assertThat(service.getPartyId(ENTERPRISE, legalId)).isEqualTo(partyId);

		verify(legalEntityClientMock).getOrganizationId(legalId);
	}

	@Test
	void testGetPartyIdForPrivateType() {
		var legalId = "202201011234"; // LegalId with century
		var partyId = "partyId";
		
		when(citizenClientMock.getPersonId(legalId)).thenReturn(wrap(partyId, "\""));
		
		assertThat(service.getPartyId(PRIVATE, legalId)).isEqualTo(partyId);
		
		verify(citizenClientMock).getPersonId(legalId);
	}

	@Test
	void testGetPartyIdForNullType() {
		var partyId = "partyId";
		
		assertThat(service.getPartyId(null, partyId)).isNull();
		
		verifyNoInteractions(citizenMappingClientMock, citizenClientMock, legalEntityClientMock);
	}
}
