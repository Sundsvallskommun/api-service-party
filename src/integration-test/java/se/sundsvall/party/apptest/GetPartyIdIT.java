package se.sundsvall.party.apptest;

import static java.util.Map.entry;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.party.Application;

/**
 * Get partyId by legalId tests.
 */
@WireMockAppTestSuite(files = "classpath:/GetPartyIdIT/", classes = Application.class)
class GetPartyIdIT extends AbstractAppTest {

	private final static Entry<String, List<String>> EXPECTED_OK_CONTENT_TYPE_HEADER = entry(CONTENT_TYPE, List.of(TEXT_PLAIN_VALUE));
	private final static Entry<String, List<String>> EXPECTED_ERROR_JSON_CONTENT_TYPE_HEADER = entry(CONTENT_TYPE, List.of(APPLICATION_PROBLEM_JSON_VALUE));

	@Test
	void test01_getPartyIdPrivate() throws Exception {
		setupCall()
			.withServicePath("/2281/PRIVATE/197706010123/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(EXPECTED_OK_CONTENT_TYPE_HEADER.getKey(), EXPECTED_OK_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("fbeac2e0-6a30-411f-b083-4a53578cb6d4")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getPartyIdEnterpriseForAktieBolag() throws Exception {
		setupCall()
			.withServicePath("/2281/ENTERPRISE/5566778899/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(EXPECTED_OK_CONTENT_TYPE_HEADER.getKey(), EXPECTED_OK_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("51633ca1-a533-4e71-af82-18a1ea646573")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getPartyIdEnterpriseForEnskildFirma() throws Exception {
		setupCall()
			.withServicePath("/2281/ENTERPRISE/197706010123/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(EXPECTED_OK_CONTENT_TYPE_HEADER.getKey(), EXPECTED_OK_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("2dfe8910-d760-4fea-97ee-3ef39b60d425")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_getPartyIdPrivateNotFound() throws Exception {
		setupCall()
			.withServicePath("/2281/PRIVATE/197806010123/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(EXPECTED_ERROR_JSON_CONTENT_TYPE_HEADER.getKey(), EXPECTED_ERROR_JSON_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_getPartyIdEnterpriseNotFound() throws Exception {
		setupCall()
			.withServicePath("/2281/ENTERPRISE/5566778890/partyId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponseHeader(EXPECTED_ERROR_JSON_CONTENT_TYPE_HEADER.getKey(), EXPECTED_ERROR_JSON_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}
}
