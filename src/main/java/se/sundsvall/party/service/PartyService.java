package se.sundsvall.party.service;

import generated.client.citizen.PersonGuidBatch;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.common.validators.annotation.impl.ValidOrganizationNumberConstraintValidator;
import se.sundsvall.dept44.common.validators.annotation.impl.ValidPersonalNumberConstraintValidator;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.problem.violations.Violation;
import se.sundsvall.party.api.model.PartyLegalIdResponse;
import se.sundsvall.party.api.model.PartyType;
import se.sundsvall.party.integration.citizen.CitizenIntegration;
import se.sundsvall.party.integration.legalentity.LegalEntityIntegration;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PartyService {

	private static final ExecutorService LEGAL_ENTITY_EXECUTOR = Executors.newFixedThreadPool(10);
	private static final ValidOrganizationNumberConstraintValidator ENTERPRISE_VALIDATOR = new ValidOrganizationNumberConstraintValidator();
	private static final ValidPersonalNumberConstraintValidator PRIVATE_VALIDATOR = new ValidPersonalNumberConstraintValidator();
	private static final String ENTERPRISE_VALIDATION_ERROR_MESSAGE = ENTERPRISE_VALIDATOR.getMessage() + " or " + PRIVATE_VALIDATOR.getMessage().substring(PRIVATE_VALIDATOR.getMessage().indexOf("^"));

	private final CitizenIntegration citizenIntegration;
	private final LegalEntityIntegration legalEntityIntegration;

	public PartyService(final CitizenIntegration citizenIntegration, final LegalEntityIntegration legalEntityIntegration) {
		this.citizenIntegration = citizenIntegration;
		this.legalEntityIntegration = legalEntityIntegration;
	}

	public String getLegalId(final String municipalityId, final PartyType type, final String partyId) {
		return switch (type) {
			case ENTERPRISE -> legalEntityIntegration.getOrganizationNumber(municipalityId, partyId)
				.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No legalId found for partyId '%s' of type %s".formatted(partyId, type)));
			case PRIVATE -> citizenIntegration.getPersonalNumber(municipalityId, partyId)
				.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No legalId found for partyId '%s' of type %s".formatted(partyId, type)));
		};
	}

	public String getPartyId(final String municipalityId, final PartyType type, final String legalId) {
		return switch (type) {
			case ENTERPRISE -> {
				validateEnterpriseLegalId(legalId);
				yield legalEntityIntegration.getOrganizationId(municipalityId, legalId)
					.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No partyId found for legalId '%s' of type %s".formatted(legalId, type)));
			}
			case PRIVATE -> {
				validatePrivateLegalId(legalId);
				yield citizenIntegration.getPersonId(municipalityId, legalId)
					.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No partyId found for legalId '%s' of type %s".formatted(legalId, type)));
			}
		};
	}

	public String getLegalIdByPartyId(final String municipalityId, final String partyId) {
		var personalNumber = citizenIntegration.getPersonalNumber(municipalityId, partyId);
		if (personalNumber.isPresent()) {
			return personalNumber.get();
		}

		var organizationNumber = legalEntityIntegration.getOrganizationNumber(municipalityId, partyId);
		if (organizationNumber.isPresent()) {
			return organizationNumber.get();
		}

		throw Problem.valueOf(NOT_FOUND, "PartyId '%s' was not found as PRIVATE or ENTERPRISE".formatted(partyId));
	}

	public Map<String, String> getPartyIds(final String municipalityId, final List<String> personNumbers) {
		final var distinctPersonNumbers = personNumbers.stream()
			.distinct()
			.toList();

		return citizenIntegration.getPersonIdsBatch(municipalityId, distinctPersonNumbers).stream()
			.filter(result -> Boolean.TRUE.equals(result.getSuccess()) && result.getPersonNumber() != null && result.getPersonId() != null)
			.collect(Collectors.toMap(
				PersonGuidBatch::getPersonNumber,
				result -> result.getPersonId().toString()));
	}

	public PartyLegalIdResponse getLegalIdsByPartyIds(final String municipalityId, final Set<String> partyIds) {
		final var personalNumbers = getLegalIds(municipalityId, partyIds);

		final var citizenMisses = partyIds.stream()
			.filter(id -> !personalNumbers.containsKey(id))
			.toList();

		final var organizationNumbers = new ConcurrentHashMap<String, String>();
		final var futures = citizenMisses.stream()
			.map(partyId -> CompletableFuture.runAsync(() -> legalEntityIntegration.getOrganizationNumber(municipalityId, partyId)
				.ifPresent(orgNumber -> organizationNumbers.put(partyId, orgNumber)), LEGAL_ENTITY_EXECUTOR))
			.toList();

		CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

		final var notFound = citizenMisses.stream()
			.filter(id -> !organizationNumbers.containsKey(id))
			.toList();

		return new PartyLegalIdResponse(personalNumbers, Map.copyOf(organizationNumbers), notFound);
	}

	private void validateEnterpriseLegalId(final String legalId) {
		if (!ENTERPRISE_VALIDATOR.isValid(legalId) && !PRIVATE_VALIDATOR.isValid(legalId)) {
			throw new ConstraintViolationProblem(BAD_REQUEST, List.of(new Violation("getPartyIdByLegalId.legalId", ENTERPRISE_VALIDATION_ERROR_MESSAGE)));
		}
	}

	private void validatePrivateLegalId(final String legalId) {
		if (!PRIVATE_VALIDATOR.isValid(legalId)) {
			throw new ConstraintViolationProblem(BAD_REQUEST, List.of(new Violation("getPartyIdByLegalId.legalId", PRIVATE_VALIDATOR.getMessage())));
		}
	}

	public Map<String, String> getLegalIds(final String municipalityId, final Set<String> personIds) {
		return citizenIntegration.getPersonalNumbersBatch(municipalityId, List.copyOf(personIds)).stream()
			.filter(result -> Boolean.TRUE.equals(result.getSuccess()) && result.getPersonNumber() != null && result.getPersonId() != null)
			.collect(Collectors.toMap(
				result -> result.getPersonId().toString(),
				PersonGuidBatch::getPersonNumber));
	}
}
