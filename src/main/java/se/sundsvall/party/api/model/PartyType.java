package se.sundsvall.party.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Party type model", enumAsRef = true)
public enum PartyType {
	ENTERPRISE,
	PRIVATE
}
