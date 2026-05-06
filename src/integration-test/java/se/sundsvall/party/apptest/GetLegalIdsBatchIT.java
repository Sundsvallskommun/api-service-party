package se.sundsvall.party.apptest;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.party.Application;

/**
 * Get legalIds by partyIds in batch tests.
 */
@WireMockAppTestSuite(files = "classpath:/GetLegalIdsBatchIT/", classes = Application.class)
class GetLegalIdsBatchIT extends AbstractAppTest {

	private static final String REQUEST = "request.json";
	private static final String RESPONSE = "response.json";

	@Test
	void test01_getLegalIdsBatch() {
		setupCall()
			.withServicePath("/2281/PRIVATE/legalIds")
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_getLegalIdsBatchWithPartialSuccess() {
		setupCall()
			.withServicePath("/2281/PRIVATE/legalIds")
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getLegalIdsBatchEmpty() {
		setupCall()
			.withServicePath("/2281/PRIVATE/legalIds")
			.withHttpMethod(POST)
			.withRequest(REQUEST)
			.withExpectedResponseStatus(OK)
			.withExpectedResponseHeader(CONTENT_TYPE, List.of(APPLICATION_JSON_VALUE))
			.withExpectedResponse(RESPONSE)
			.sendRequestAndVerifyResponse();
	}
}
