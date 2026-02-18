package se.sundsvall.party.service;

import generated.client.citizen.PersonGuidBatch;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.party.api.model.PartyType;
import se.sundsvall.party.integration.citizen.CitizenClient;
import se.sundsvall.party.integration.legalentity.LegalEntityClient;

import static org.apache.commons.lang3.StringUtils.strip;
import static org.zalando.problem.Status.NOT_FOUND;

@Service
public class PartyService {

	private final CitizenClient citizenClient;
	private final LegalEntityClient legalEntityClient;

	public PartyService(final CitizenClient citizenClient, final LegalEntityClient legalEntityClient) {
		this.citizenClient = citizenClient;
		this.legalEntityClient = legalEntityClient;
	}

	private static String removeQutationMarks(final String string) {
		return strip(string, "\"");
	}

	public String getLegalId(final String municipalityId, final PartyType type, final String partyId) {
		return Optional.ofNullable(type)
			.map(partyType -> switch (partyType)
			{
				case ENTERPRISE -> removeQutationMarks(legalEntityClient.getOrganizationNumber(municipalityId, partyId));
				case PRIVATE -> removeQutationMarks(citizenClient.getPersonalNumber(municipalityId, partyId));
			})
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No legalId found!"));
	}

	public String getPartyId(final String municipalityId, final PartyType type, final String legalId) {
		return Optional.ofNullable(type)
			.map(partyType -> switch (partyType)
			{
				case ENTERPRISE -> removeQutationMarks(legalEntityClient.getOrganizationId(municipalityId, legalId));
				case PRIVATE -> removeQutationMarks(citizenClient.getPersonId(municipalityId, legalId));
			})
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No partyId found!"));
	}

	public Map<String, String> getPartyIds(final String municipalityId, final List<String> personNumbers) {
		final var distinctPersonNumbers = personNumbers.stream()
			.distinct()
			.toList();

		return citizenClient.getPersonIdsBatch(municipalityId, distinctPersonNumbers).stream()
			.filter(result -> Boolean.TRUE.equals(result.getSuccess()) && result.getPersonNumber() != null && result.getPersonId() != null)
			.collect(Collectors.toMap(
				PersonGuidBatch::getPersonNumber,
				result -> result.getPersonId().toString()));
	}

	public Map<String, String> getLegalIds(final String municipalityId, final List<String> personIds) {
		final var distinctPersonIds = personIds.stream()
			.distinct()
			.toList();

		return citizenClient.getPersonalNumbersBatch(municipalityId, distinctPersonIds).stream()
			.filter(result -> Boolean.TRUE.equals(result.getSuccess()) && result.getPersonNumber() != null && result.getPersonId() != null)
			.collect(Collectors.toMap(
				result -> result.getPersonId().toString(),
				PersonGuidBatch::getPersonNumber));
	}
}
