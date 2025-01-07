package se.sundsvall.party.service;

import static org.apache.commons.lang3.StringUtils.strip;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.party.api.model.PartyType;
import se.sundsvall.party.integration.citizen.CitizenClient;
import se.sundsvall.party.integration.legalentity.LegalEntityClient;

@Service
public class PartyService {

	private final CitizenClient citizenClient;
	private final LegalEntityClient legalEntityClient;

	public PartyService(CitizenClient citizenClient, LegalEntityClient legalEntityClient) {
		this.citizenClient = citizenClient;
		this.legalEntityClient = legalEntityClient;
	}

	public String getLegalId(final String municipalityId, final PartyType type, final String partyId) {
		return Optional.ofNullable(type)
			.map(partyType -> switch (partyType)
			{
				case ENTERPRISE -> removeQutationMarks(legalEntityClient.getOrganizationNumber(partyId));
				case PRIVATE -> removeQutationMarks(citizenClient.getPersonalNumber(partyId));
			})
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No legalId found!"));
	}

	public String getPartyId(final String municipalityId, final PartyType type, final String legalId) {
		return Optional.ofNullable(type)
			.map(partyType -> switch (partyType)
			{
				case ENTERPRISE -> removeQutationMarks(legalEntityClient.getOrganizationId(legalId));
				case PRIVATE -> removeQutationMarks(citizenClient.getPersonId(legalId));
			})
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No partyId found!"));
	}

	private static String removeQutationMarks(final String string) {
		return strip(string, "\"");
	}
}
