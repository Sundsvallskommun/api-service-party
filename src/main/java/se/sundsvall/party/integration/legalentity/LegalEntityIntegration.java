package se.sundsvall.party.integration.legalentity;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.exception.ClientProblem;

import static org.apache.commons.lang3.StringUtils.strip;

@Component
public class LegalEntityIntegration {

	private final LegalEntityClient client;

	public LegalEntityIntegration(final LegalEntityClient client) {
		this.client = client;
	}

	public Optional<String> getOrganizationId(final String municipalityId, final String organizationNumber) {
		try {
			return Optional.ofNullable(removeQuotationMarks(client.getOrganizationId(municipalityId, organizationNumber)));
		} catch (final ClientProblem e) {
			if (e.getStatus() == HttpStatus.NOT_FOUND) {
				return Optional.empty();
			}
			throw e;
		}
	}

	public Optional<String> getOrganizationNumber(final String municipalityId, final String legalEntityId) {
		try {
			return Optional.ofNullable(removeQuotationMarks(client.getOrganizationNumber(municipalityId, legalEntityId)));
		} catch (final ClientProblem e) {
			if (e.getStatus() == HttpStatus.NOT_FOUND) {
				return Optional.empty();
			}
			throw e;
		}
	}

	private static String removeQuotationMarks(final String string) {
		return strip(string, "\"");
	}
}
