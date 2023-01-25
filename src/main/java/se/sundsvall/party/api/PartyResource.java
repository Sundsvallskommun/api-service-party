package se.sundsvall.party.api;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.dept44.common.validators.annotation.impl.ValidOrganizationNumberConstraintValidator;
import se.sundsvall.dept44.common.validators.annotation.impl.ValidPersonalNumberConstraintValidator;
import se.sundsvall.party.api.model.PartyType;
import se.sundsvall.party.service.PartyService;

@RestController
@Validated
@Tag(name = "Party", description = "Party operations")
public class PartyResource {

	private static final Logger LOG = LoggerFactory.getLogger(PartyResource.class);
	private static final ValidOrganizationNumberConstraintValidator ENTERPRISE_VALIDATOR = new ValidOrganizationNumberConstraintValidator();
	private static final ValidPersonalNumberConstraintValidator PRIVATE_VALIDATOR = new ValidPersonalNumberConstraintValidator();
	private static final String ENTERPRISE_VALIDATION_ERROR_MESSAGE = ENTERPRISE_VALIDATOR.getMessage() + " or " + PRIVATE_VALIDATOR.getMessage().substring(PRIVATE_VALIDATOR.getMessage().indexOf("^"));
	
	@Autowired
	private PartyService service;
	
	@GetMapping(path = "/{type}/{legalId}/partyId", produces = { TEXT_PLAIN_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Get partys unique identifier by legal-ID")
	@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class)))
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<String> getPartyIdByLegalId(
		@Parameter(name = "type", description = "Party type", required = true) @PathVariable(name = "type") PartyType type,
		@Parameter(name = "legalId", description = "Legal-ID", required = true, example = "5565125584") @PathVariable(name = "legalId") String legalId) {
		LOG.debug("Received getPartyIdByLegalId request for type: '{}', legalId: '{}'", type, legalId);
		
		switch (type) {
			case ENTERPRISE -> validateEnterpriseLegalId(legalId);
			case PRIVATE -> validatePrivateLegalId(legalId);
			default -> throw Problem.valueOf(BAD_REQUEST, "getUuidByLegalId.type is unknown");
		}

		return ok().contentType(TEXT_PLAIN).body(service.getPartyId(type, legalId));
	}

	@GetMapping(path = "/{type}/{partyId}/legalId", produces = { TEXT_PLAIN_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Get partys legal-ID by unique identifier")
	@ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class)))
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<String> getLegalIdByPartyId(
		@Parameter(name = "type", description = "Party type", required = true) @PathVariable(name = "type") PartyType type,
		@Parameter(name = "partyId", description = "Universally unique identifier for the party", required = true, example = "81471222-5798-11e9-ae24-57fa13b361e1") @ValidUuid @PathVariable(name = "partyId") String partyId) {
		LOG.debug("Received getLegalIdByPartyId request for type: '{}', partyId: '{}'", type, partyId);

		return ok().contentType(TEXT_PLAIN).body(service.getLegalId(type, partyId));
	}
	
	private void validateEnterpriseLegalId(String legalId) {
		if (!ENTERPRISE_VALIDATOR.isValid(legalId) && !PRIVATE_VALIDATOR.isValid(legalId)) { // Check if it is a valid organization number for an enterprise company or an individual company
			throw new ConstraintViolationProblem(BAD_REQUEST, List.of(new Violation("getPartyIdByLegalId.legalId", ENTERPRISE_VALIDATION_ERROR_MESSAGE)));
		}
	}

	private void validatePrivateLegalId(String legalId) {
		if (!PRIVATE_VALIDATOR.isValid(legalId)) {
			throw new ConstraintViolationProblem(BAD_REQUEST, List.of(new Violation("getPartyIdByLegalId.legalId", PRIVATE_VALIDATOR.getMessage())));
		}
	}
}
