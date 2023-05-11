package se.sundsvall.party.integration.citizen;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.citizen.url}", configuration = CitizenConfiguration.class)
public interface CitizenClient {

	/**
	 * Method for retrieving personId associated to sent in personNumber
	 * 
	 * @param personNumber the person number that personId shall be returned for
	 * @return string containing personId for sent in personNumber
	 * @throws org.zalando.problem.ThrowableProblem when called service responds with error code
	 */
	@GetMapping(path = "/person/{personnumber}/guid", produces = TEXT_PLAIN_VALUE)
	String getPersonId(@PathVariable("personnumber") String personNumber);
}
