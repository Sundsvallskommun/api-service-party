package se.sundsvall.party.integration.citizenmapping;

import static se.sundsvall.party.integration.citizenmapping.configuration.CitizenMappingConfiguration.CLIENT_REGISTRATION_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.party.integration.citizenmapping.configuration.CitizenMappingConfiguration;

@FeignClient(name = CLIENT_REGISTRATION_ID, url = "${integration.citizenmapping.url}", configuration = CitizenMappingConfiguration.class)
@CircuitBreaker(name = CLIENT_REGISTRATION_ID)
public interface CitizenMappingClient {

	/**
	 * Send personId for getting associated personalnumber.
	 * 
	 * @param personId
	 * @return a String with personalnumber
	 */
	@GetMapping(path = "/citizenmapping/{personId}/personalnumber", produces = MediaType.TEXT_PLAIN_VALUE)
	String getPersonalNumber(@PathVariable(name = "personId") String personId);
}
