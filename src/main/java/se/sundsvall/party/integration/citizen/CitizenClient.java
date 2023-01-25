package se.sundsvall.party.integration.citizen;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration.CLIENT_REGISTRATION_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration;

@FeignClient(name = CLIENT_REGISTRATION_ID, url = "${integration.citizen.url}", configuration = CitizenConfiguration.class)
@CircuitBreaker(name = CLIENT_REGISTRATION_ID)
public interface CitizenClient {

	/**
	 * Method for retrieving personId associated to sent in personNumber
	 * 
	 * @param personNumber that personId shall be returned for
	 * @return a string containing personId for sent in personNumber
	 * @throws org.zalando.problem.ThrowableProblem
	 */
	@GetMapping(path = "/person/{personnumber}/guid", produces = TEXT_PLAIN_VALUE)
	String getPersonId(@PathVariable("personnumber") String personNumber);
}
