package se.sundsvall.party.integration.citizenmapping;

import static se.sundsvall.party.integration.citizenmapping.configuration.CitizenMappingConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import se.sundsvall.party.integration.citizenmapping.configuration.CitizenMappingConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.citizenmapping.url}", configuration = CitizenMappingConfiguration.class)
public interface CitizenMappingClient {

	/**
	 * Send personId for getting associated personalnumber.
	 * 
	 * @param personId the person id that personal number shall be returned for
	 * @return string containing personalnumber for sent in person id
	 * @throws org.zalando.problem.ThrowableProblem when called service responds with error code
	 */
	@GetMapping(path = "/citizenmapping/{personId}/personalnumber", produces = MediaType.TEXT_PLAIN_VALUE)
	String getPersonalNumber(@PathVariable(name = "personId") String personId);
}
