package se.sundsvall.party.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static se.sundsvall.party.api.model.PartyType.ENTERPRISE;
import static se.sundsvall.party.api.model.PartyType.PRIVATE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.party.Application;
import se.sundsvall.party.service.PartyService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class PartyResourceTest {

	@MockBean
	private PartyService serviceMock;
	
	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getPartyIdByEnterpriseLegalId() {
		var type = ENTERPRISE.name();
		var legalId = "5566123456";
		var partyId = "81471222-5798-11e9-ae24-57fa13b361e1";
		
		when(serviceMock.getPartyId(ENTERPRISE, legalId)).thenReturn(partyId);
		
		webTestClient.get().uri("/{type}/{legalId}/partyId", type, legalId)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(TEXT_PLAIN)
		.expectBody(String.class).isEqualTo(partyId);
		
		verify(serviceMock).getPartyId(ENTERPRISE, legalId);
	}
	
	@Test
	void getPartyIdByPrivateLegalId() {
		var type = PRIVATE.name();
		var legalId = "200001011234";
		var partyId = "81471222-5798-11e9-ae24-57fa13b361e2";
		
		when(serviceMock.getPartyId(PRIVATE, legalId)).thenReturn(partyId);
		
		webTestClient.get().uri("/{type}/{legalId}/partyId", type, legalId)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(TEXT_PLAIN)
		.expectBody(String.class).isEqualTo(partyId);
		
		verify(serviceMock).getPartyId(PRIVATE, legalId);
	}

	@Test
	void getLegalIdByPartyId() {
		var type = PRIVATE.name();
		var partyId = "81471222-5798-11e9-ae24-57fa13b361e1";
		var legalId = "200001011234";
		
		when(serviceMock.getLegalId(PRIVATE, partyId)).thenReturn(legalId);
		
		webTestClient.get().uri("/{type}/{partyId}/legalId", type, partyId)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(TEXT_PLAIN)
		.expectBody(String.class).isEqualTo(legalId);

		verify(serviceMock).getLegalId(PRIVATE, partyId);
	}
	
}
