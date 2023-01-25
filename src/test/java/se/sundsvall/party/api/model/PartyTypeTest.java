package se.sundsvall.party.api.model;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.party.api.model.PartyType.ENTERPRISE;
import static se.sundsvall.party.api.model.PartyType.PRIVATE;

import org.junit.jupiter.api.Test;

class PartyTypeTest {

	@Test
	void enums() {
		assertThat(PartyType.values()).containsExactlyInAnyOrder(ENTERPRISE, PRIVATE);
	}

	@Test
	void enumValues() {
		assertThat(PRIVATE).hasToString("PRIVATE");
		assertThat(ENTERPRISE).hasToString("ENTERPRISE");
	}
}
