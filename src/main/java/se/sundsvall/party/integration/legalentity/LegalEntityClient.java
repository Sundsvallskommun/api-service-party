package se.sundsvall.party.integration.legalentity;

import static se.sundsvall.party.integration.legalentity.configuration.LegalEntityConfiguration.CLIENT_REGISTRATION_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import se.sundsvall.party.integration.legalentity.configuration.LegalEntityConfiguration;

@FeignClient(name = CLIENT_REGISTRATION_ID, url = "${integration.legalentity.url}", configuration = LegalEntityConfiguration.class)
public interface LegalEntityClient {

	/**
	 * Method for retrieving organizationId associated to sent in organizationNumber.
	 * 
	 * @param organizationNumber for the organization to retrieve organizationId for
	 * @return a string containing organizationId for sent in organizationNumber
	 * @throws org.zalando.problem.ThrowableProblem
	 */
	@GetMapping(path = "/{organizationNumber}/guid", produces = MediaType.TEXT_PLAIN_VALUE)
	String getOrganizationId(@PathVariable(name = "organizationNumber") String organizationNumber);

	/**
	 * Method for retrieving organizationNumber associated to sent in organizationId.
	 * 
	 * @param organizationId for the organization to retrieve organizationNumber for
	 * @return a string containing organizationNumber for sent in organizationId
	 * @throws org.zalando.problem.ThrowableProblem
	 */
	@GetMapping(path = "/{legalEntityId}/organizationnumber", produces = MediaType.TEXT_PLAIN_VALUE)
	String getOrganizationNumber(@PathVariable(name = "legalEntityId") String legalEntityId);
}
