package com.goodskill.oauth2.authserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2AuthorizationServerApplicationTests {

	@Autowired
	MockMvc mvc;

	@Test
	@DisplayName("request token when using password grant type then ok")
	public void requestTokenWhenUsingPasswordGrantTypeThenOk() throws Exception {
		this.mvc.perform(post("/oauth/token")
			.param("grant_type", "password")
			.param("username", "enduser")
			.param("password", "password")
			// Basic token为client-id:client-secret Base64编码
			.header("Authorization", "Basic c2Vjb25kLWNsaWVudDpub29uZXdpbGxldmVyZ3Vlc3M="))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("request jwk set when using defaults then ok")
	public void requestJwkSetWhenUsingDefaultsThenOk()
			throws Exception {
		this.mvc.perform(get("/.well-known/jwks.json"))
				.andExpect(status().isOk());
	}

}
