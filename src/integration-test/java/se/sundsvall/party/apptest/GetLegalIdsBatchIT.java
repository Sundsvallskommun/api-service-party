package se.sundsvall.party.apptest;

import static java.util.Map.entry;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Map.Entry;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.party.Application;

/**
 * Get legalIds by partyIds in batch tests.
 */
@WireMockAppTestSuite(files = "classpath:/GetLegalIdsBatchIT/", classes = Application.class)
class GetLegalIdsBatchIT extends AbstractAppTest {

	private static final Entry<String, List<String>> EXPECTED_JSON_CONTENT_TYPE_HEADER = entry(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE));

	@Test
	void test01_getLegalIdsBatch() {
		setupCall()
			.withServicePath("/2281/PRIVATE/legalIds")
			.withHttpMethod(POST)
			.withRequest("request.json")
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(EXPECTED_JSON_CONTENT_TYPE_HEADER.getKey(), EXPECTED_JSON_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getLegalIdsBatchWithPartialSuccess() {
		setupCall()
			.withServicePath("/2281/PRIVATE/legalIds")
			.withHttpMethod(POST)
			.withRequest("request.json")
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(EXPECTED_JSON_CONTENT_TYPE_HEADER.getKey(), EXPECTED_JSON_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getLegalIdsBatchEmpty() {
		setupCall()
			.withServicePath("/2281/PRIVATE/legalIds")
			.withHttpMethod(POST)
			.withRequest("request.json")
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(EXPECTED_JSON_CONTENT_TYPE_HEADER.getKey(), EXPECTED_JSON_CONTENT_TYPE_HEADER.getValue())
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}
}
