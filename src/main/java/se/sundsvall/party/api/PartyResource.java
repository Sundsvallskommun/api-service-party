package se.sundsvall.party.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.party.api.model.PartyLegalIdResponse;
import se.sundsvall.party.api.model.PartyType;
import se.sundsvall.party.service.PartyService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@Tag(name = "Party", description = "Party operations")
@ApiResponses(value = {
	@ApiResponse(responseCode = "200", description = "Successful Operation", useReturnTypeSchema = true),
	@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class PartyResource {

	private final PartyService service;

	PartyResource(final PartyService service) {
		this.service = service;
	}

	@GetMapping(path = "/{municipalityId}/{type}/{legalId}/partyId", produces = TEXT_PLAIN_VALUE)
	@Operation(summary = "Get partys unique identifier by legal-ID")
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<String> getPartyIdByLegalId(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "type", description = "Party type", required = true) @PathVariable final PartyType type,
		@Parameter(name = "legalId", description = "Legal-ID", required = true, example = "5565125584") @PathVariable final String legalId) {

		return ok().contentType(TEXT_PLAIN).body(service.getPartyId(municipalityId, type, legalId));
	}

	@GetMapping(path = "/{municipalityId}/{type}/{partyId}/legalId", produces = TEXT_PLAIN_VALUE)
	@Operation(summary = "Get partys legal-ID by unique identifier")
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<String> getLegalIdByPartyId(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "type", description = "Party type", required = true) @PathVariable final PartyType type,
		@Parameter(name = "partyId", description = "Universally unique identifier for the party", required = true, example = "81471222-5798-11e9-ae24-57fa13b361e1") @ValidUuid @PathVariable final String partyId) {

		return ok().contentType(TEXT_PLAIN).body(service.getLegalId(municipalityId, type, partyId));
	}

	@GetMapping(path = "/{municipalityId}/partyId/{partyId}/legalId", produces = TEXT_PLAIN_VALUE)
	@Operation(summary = "Get partys legal-ID by unique identifier without specifying party type")
	@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<String> getLegalIdByPartyIdWithoutType(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "partyId", description = "Universally unique identifier for the party", required = true, example = "81471222-5798-11e9-ae24-57fa13b361e1") @ValidUuid @PathVariable final String partyId) {

		return ok().contentType(TEXT_PLAIN).body(service.getLegalIdByPartyId(municipalityId, partyId));
	}

	@PostMapping(path = "/{municipalityId}/PRIVATE/partyIds", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get party identifiers by personal identity numbers in batch")
	ResponseEntity<Map<String, String>> getPartyIdsByPersonNumbers(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@RequestBody final List<String> personNumbers) {

		return ok(service.getPartyIds(municipalityId, personNumbers));
	}

	@PostMapping(path = "/{municipalityId}/PRIVATE/legalIds", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get personal identity numbers by party identifiers in batch")
	ResponseEntity<Map<String, String>> getLegalIdsByPartyIds(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@RequestBody final Set<@ValidUuid String> personIds) {

		return ok(service.getLegalIds(municipalityId, personIds));
	}

	@PostMapping(path = "/{municipalityId}/legalIds", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get legal IDs by party identifiers in batch, resolving both private and enterprise parties")
	ResponseEntity<PartyLegalIdResponse> getLegalIdsByPartyIdsWithoutType(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@RequestBody final Set<@ValidUuid String> partyIds) {

		return ok(service.getLegalIdsByPartyIds(municipalityId, partyIds));
	}
}
