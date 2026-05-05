package se.sundsvall.party.integration.citizen;

import generated.client.citizen.PersonGuidBatch;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.strip;

@Component
public class CitizenIntegration {

	private final CitizenClient client;

	public CitizenIntegration(final CitizenClient client) {
		this.client = client;
	}

	public Optional<String> getPersonId(final String municipalityId, final String personNumber) {
		return Optional.ofNullable(removeQuotationMarks(client.getPersonId(municipalityId, personNumber)));
	}

	public Optional<String> getPersonalNumber(final String municipalityId, final String personId) {
		return Optional.ofNullable(removeQuotationMarks(client.getPersonalNumber(municipalityId, personId)));
	}

	public List<PersonGuidBatch> getPersonIdsBatch(final String municipalityId, final List<String> personNumbers) {
		return client.getPersonIdsBatch(municipalityId, personNumbers);
	}

	public List<PersonGuidBatch> getPersonalNumbersBatch(final String municipalityId, final List<String> personIds) {
		return client.getPersonalNumbersBatch(municipalityId, personIds);
	}

	private static String removeQuotationMarks(final String string) {
		return strip(string, "\"");
	}
}
