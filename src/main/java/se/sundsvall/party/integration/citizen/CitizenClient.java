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
	 * Method for retrieving personId associated with the provided personal identity number.
	 *
	 * @param  personalNumber                       the personal identity number.
	 * @return                                      string containing personId for sent in personal identity number.
	 * @throws org.zalando.problem.ThrowableProblem when called service responds with error code.
	 */
	@GetMapping(path = "/{personalNumber}/guid", produces = TEXT_PLAIN_VALUE)
	String getPersonId(@PathVariable("personalNumber") String personalNumber);

	/**
	 * Method for retrieving personal identity number associated with the provided personId.
	 *
	 * @param  personId                             the personId.
	 * @return                                      string containing personal identity number for sent in personId.
	 * @throws org.zalando.problem.ThrowableProblem when called service responds with error code.
	 */
	@GetMapping(path = "/{personId}/personnumber", produces = TEXT_PLAIN_VALUE)
	String getPersonalNumber(@PathVariable("personId") String personId);
}
