package se.sundsvall.party.service;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.strip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.sundsvall.party.api.model.PartyType;
import se.sundsvall.party.integration.citizen.CitizenClient;
import se.sundsvall.party.integration.citizenmapping.CitizenMappingClient;
import se.sundsvall.party.integration.legalentity.LegalEntityClient;

@Service
public class PartyService {
	@Autowired
	private CitizenMappingClient citizenMappingClient;

	@Autowired
	private CitizenClient citizenClient;

	@Autowired
	private LegalEntityClient legalEntityClient;

	public String getLegalId(PartyType type, String partyId) {
		if (isNull(type)) return null;
		return switch (type) {
			case ENTERPRISE -> removeQutationMarks(legalEntityClient.getOrganizationNumber(partyId));
			case PRIVATE -> removeQutationMarks(citizenMappingClient.getPersonalNumber(partyId));
		};
	}

	public String getPartyId(PartyType type, String legalId) {
		if (isNull(type)) return null;
		return switch (type) {
			case ENTERPRISE -> removeQutationMarks(legalEntityClient.getOrganizationId(legalId));
			case PRIVATE -> removeQutationMarks(citizenClient.getPersonId(legalId));
		};
	}

	private static String removeQutationMarks(String string) {
		return strip(string, "\"");
	}
}
