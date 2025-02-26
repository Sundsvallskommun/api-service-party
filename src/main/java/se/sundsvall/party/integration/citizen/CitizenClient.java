package se.sundsvall.party.integration.citizen;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration.CLIENT_ID;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration;

@FeignClient(name = CLIENT_ID, url = "${integration.citizen.url}", configuration = CitizenConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface CitizenClient {

	/**
	 * Method for retrieving personId associated with the provided personal identity number.
	 *
	 * @param  municipalityId                       the municipalityId.
	 * @param  personNumber                         the personal identity number.
	 * @return                                      string containing personId for sent in personal identity number.
	 * @throws org.zalando.problem.ThrowableProblem when called service responds with error code.
	 */
	@GetMapping(path = "/{municipalityId}/{personNumber}/guid", produces = TEXT_PLAIN_VALUE)
	String getPersonId(@PathVariable("municipalityId") String municipalityId, @PathVariable("personNumber") String personNumber);

	/**
	 * Method for retrieving personal identity number associated with the provided personId.
	 *
	 * @param  municipalityId                       the municipalityId.
	 * @param  personId                             the personId.
	 * @return                                      string containing personal identity number for sent in personId.
	 * @throws org.zalando.problem.ThrowableProblem when called service responds with error code.
	 */
	@GetMapping(path = "/{municipalityId}/{personId}/personnumber", produces = TEXT_PLAIN_VALUE)
	String getPersonalNumber(@PathVariable("municipalityId") String municipalityId, @PathVariable("personId") String personId);
}
