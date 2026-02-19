package se.sundsvall.party.integration.citizen;

import generated.client.citizen.PersonGuidBatch;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static se.sundsvall.party.integration.citizen.configuration.CitizenConfiguration.CLIENT_ID;

@FeignClient(name = CLIENT_ID, url = "${integration.citizen.url}", configuration = CitizenConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface CitizenClient {

	/**
	 * Method for retrieving personId associated with the provided personal identity number.
	 *
	 * @param  municipalityId   the municipalityId.
	 * @param  personNumber     the personal identity number.
	 * @return                  string containing personId for sent in personal identity number.
	 * @throws ThrowableProblem when called service responds with error code.
	 */
	@GetMapping(path = "/{municipalityId}/{personNumber}/guid", produces = TEXT_PLAIN_VALUE)
	String getPersonId(@PathVariable String municipalityId, @PathVariable String personNumber);

	/**
	 * Method for retrieving personal identity number associated with the provided personId.
	 *
	 * @param  municipalityId   the municipalityId.
	 * @param  personId         the personId.
	 * @return                  string containing personal identity number for sent in personId.
	 * @throws ThrowableProblem when called service responds with error code.
	 */
	@GetMapping(path = "/{municipalityId}/{personId}/personnumber", produces = TEXT_PLAIN_VALUE)
	String getPersonalNumber(@PathVariable String municipalityId, @PathVariable String personId);

	/**
	 * Method for retrieving personIds associated with the provided personal identity numbers in batch.
	 *
	 * @param  municipalityId   the municipalityId.
	 * @param  personNumbers    list of personal identity numbers.
	 * @return                  list of PersonGuidBatch containing personId for each personal identity number.
	 * @throws ThrowableProblem when called service responds with error code.
	 */
	@PostMapping(path = "/{municipalityId}/guid/batch", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	List<PersonGuidBatch> getPersonIdsBatch(@PathVariable String municipalityId, @RequestBody List<String> personNumbers);

	/**
	 * Method for retrieving personal identity numbers associated with the provided personIds in batch.
	 *
	 * @param  municipalityId   the municipalityId.
	 * @param  personIds        list of personIds (UUIDs).
	 * @return                  list of PersonGuidBatch containing a personal identity number for each personId.
	 * @throws ThrowableProblem when called service responds with error code.
	 */
	@PostMapping(path = "/{municipalityId}/personnumbers/batch", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	List<PersonGuidBatch> getPersonalNumbersBatch(@PathVariable String municipalityId, @RequestBody List<String> personIds);
}
