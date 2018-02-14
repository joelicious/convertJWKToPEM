package com.redhat.utility;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jbutler@redhat.com">Joseph S. Butler</a>
 */
public class ObtainPublicKeyTest extends CamelTestSupport {

	private static final Logger LOG = LoggerFactory.getLogger(ObtainPublicKeyTest.class);

	@EndpointInject(uri = "mock:success")
	protected MockEndpoint successEndpoint;

	@EndpointInject(uri = "mock:authorizationException")
	protected MockEndpoint failureEndpoint;

	final static String jwkJsonKeys = "{\"keys\":[{\"kty\":\"RSA\",\"kid\":\"SylLC6Njt1KGQktD9Mt+0zceQSU=\",\"use\":\"sig\",\"alg\":\"RS256\",\"n\":\"AK0kHP1O-RgdgLSoWxkuaYoi5Jic6hLKeuKw8WzCfsQ68ntBDf6tVOTn_kZA7Gjf4oJAL1dXLlxIEy-kZWnxT3FF-0MQ4WQYbGBfaW8LTM4uAOLLvYZ8SIVEXmxhJsSlvaiTWCbNFaOfiII8bhFp4551YB07NfpquUGEwOxOmci_\",\"e\":\"AQAB\"}]}";
	
	@Test
	public void testPublicKeyCreationTest() throws Exception {
		successEndpoint.expectedMessageCount(1);
		failureEndpoint.expectedMessageCount(0);

		String result = template.requestBody("direct:testPublicKey", jwkJsonKeys, String.class);

		LOG.info("Result: " + result);

		assertTrue(
				result.equals(
						"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCtJBz9TvkYHYC0qFsZLmmKIuSYnOoSynrisPFswn7EOvJ7QQ3+rVTk5/5GQOxo3+KCQC9XVy5cSBMvpGVp8U9xRftDEOFkGGxgX2lvC0zOLgDiy72GfEiFRF5sYSbEpb2ok1gmzRWjn4iCPG4RaeOedWAdOzX6arlBhMDsTpnIvwIDAQAB"));

		successEndpoint.assertIsSatisfied();
		failureEndpoint.assertIsSatisfied();
	}

	@Override
	protected RouteBuilder[] createRouteBuilders() throws Exception {

		return new RouteBuilder[] {

				new RouteBuilder() {

					public void configure() {

						onException(Exception.class).to("mock:authorizationException");

						from("direct:testPublicKey")
							.routeId("PKTest")
							.log("Invoking Obtain Key")
							.to("direct:obtainKey")
							.log("This is the Body ${body}")
							.to("mock:success");

					}

				}, new PublicKeyRouteBuilder() };
	}

}