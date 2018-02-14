package com.redhat.utility;

import java.io.IOException;
import java.io.StringWriter;
import java.security.PublicKey;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.jose.jwk.JWKParser;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jbutler@redhat.com">Joseph S. Butler</a>
 */
public class PublicKeyProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(PublicKeyProcessor.class);

	public void process(Exchange exchange) throws Exception {

		String jwkTokens = exchange.getIn().getBody(String.class);

		LOG.info("In PublicKeyProcessor, this is body: " + jwkTokens);

		JSONWebKeySet webKeySet = JsonSerialization.readValue(jwkTokens, JSONWebKeySet.class);
		JWK[] jwkArray = webKeySet.getKeys();

		if (1 == jwkArray.length) {
			final JWKParser jwkParser = JWKParser.create(jwkArray[0]);
			final PublicKey publicKey = jwkParser.toPublicKey();

			StringWriter sw = new StringWriter();
			JcaPEMWriter writer = new JcaPEMWriter(sw);
			try {
				writer.writeObject(publicKey);
				writer.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				writer.close();
			}
						
			exchange.getIn().setBody(trimAndSlurp(sw.toString()));

		} else {

			throw new Exception("Multiple JWKs not supported");

		}
		
	}
	
	private String trimAndSlurp(String publicKeyStr) {
		
		String returnableStr = "";
		
		String[] lines = publicKeyStr.split("\\r?\\n");
		for (int i = 1; i < lines.length - 1; i++) {
			returnableStr = returnableStr + lines[i];
		}
		
		return returnableStr;
		
	}

}
