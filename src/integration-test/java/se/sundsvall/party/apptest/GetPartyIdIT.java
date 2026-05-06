package se.sundsvall.party.apptest;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.party.Application;

/**
 * Get partyId by legalId tests.
 */
@WireMockAppTestSuite(files = "classpath:/GetPartyIdIT/", classes = Application.class)
class GetPartyIdIT extends AbstractAppTest {

	private static final String RESPONSE = "response.json";

	@Test
	void test01_getPartyIdPrivate() {
		setupCall()
			.withServicePath("/2281/PRIVATE/197706010123/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(TEXT_PLAIN_VALUE))
			.withExpectedResponse("fbeac2e0-6a30-411f-b083-4a53578cb6d4")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getPartyIdEnterpriseForAktieBolag() {
		setupCall()
			.withServicePath("/2281/ENTERPRISE/5566778899/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(TEXT_PLAIN_VALUE))
			.withExpectedResponse("51633ca1-a533-4e71-af82-18a1ea646573")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getPartyIdEnterpriseForEnskildFirma() {
		setupCall()
			.withServicePath("/2281/ENTERPRISE/197706010123/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(TEXT_PLAIN_VALUE))
			.withExpectedResponse("2dfe8910-d760-4fea-97ee-3ef39b60d425")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_getPartyIdPrivateNotFound() {
		setupCall()
			.withServicePath("/2281/PRIVATE/197806010123/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_getPartyIdEnterpriseNotFound() {
		setupCall()
			.withServicePath("/2281/ENTERPRISE/5566778890/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE))
			.withExpectedResponse(RESPONSE)
			.sendRequestAndVerifyResponse();
	}
}
