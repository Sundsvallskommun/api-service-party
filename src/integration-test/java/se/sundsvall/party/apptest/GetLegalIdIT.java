package se.sundsvall.party.apptest;

import static java.util.Map.entry;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.util.List;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.party.Application;

/**
 * Get legalId by partyId tests.
 */
@WireMockAppTestSuite(files = "classpath:/GetLegalIdIT/", classes = Application.class)
class GetLegalIdIT extends AbstractAppTest {

	private final static Entry<String, List<String>> EXPECTED_SUCCESS_CONTENT_TYPE_HEADER = entry(CONTENT_TYPE, List.of(TEXT_PLAIN_VALUE));

	@Test
	void test01_getLegalIdPrivate() throws Exception {
		setupCall()
			.withServicePath("/PRIVATE/fbeac2e0-6a30-411f-b083-4a53578cb6d4/legalId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(EXPECTED_SUCCESS_CONTENT_TYPE_HEADER.getKey(), EXPECTED_SUCCESS_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("197706010123")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getLegalIdEnterprise() throws Exception {
		setupCall()
			.withServicePath("/ENTERPRISE/51633ca1-a533-4e71-af82-18a1ea646573/legalId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(EXPECTED_SUCCESS_CONTENT_TYPE_HEADER.getKey(), EXPECTED_SUCCESS_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("5566778899")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getLegalIdPrivateNotFound() throws Exception {
		setupCall()
			.withServicePath("/PRIVATE/fbeac2e0-6a30-411f-b083-4a53578cb6d4/legalId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_getLegalIdEnterpriseNotFound() throws Exception {
		setupCall()
			.withServicePath("/ENTERPRISE/51633ca1-a533-4e71-af82-18a1ea646573/legalId")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(HttpStatus.NOT_FOUND)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

}
