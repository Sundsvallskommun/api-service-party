package se.sundsvall.party.api.model;

import java.util.List;
import java.util.Map;

public record PartyLegalIdResponse(
	Map<String, String> personalNumbers,
	Map<String, String> organizationNumbers,
	List<String> notFound) {
}
