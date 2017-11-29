package com.redhat.utility;

import org.apache.camel.builder.RouteBuilder;

public class FetchJWKSetRouteBuilder extends RouteBuilder {
	
	private String urlString;
	
	public FetchJWKSetRouteBuilder(String urlString) {
		this.urlString = urlString;
	}

	@Override
	public void configure() throws Exception {
	
		from("direct:obtainKeyFromHttp")
			.routeId("ObtainPublicKeyFromHttp")
			.log("Invoke HTTP Address")
			.to(urlString)
			.to("direct:obtainKey");
		
	}

}
