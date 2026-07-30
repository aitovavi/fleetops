package com.aitovavi.fleetops.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
class SecurityIntegrationTest {

    private static final Pattern ACCESS_TOKEN_PATTERN =
            Pattern.compile(
                    "\"accessToken\"\\s*:\\s*\"([^\"]+)\""
            );

    @Autowired
    private WebApplicationContext applicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldAllowPublicHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowUserToReadCustomers() throws Exception {
        String token = login(
                "user",
                "user-change-me"
        );

        mockMvc.perform(
                        get("/api/v1/customers")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldForbidUserFromCreatingCustomer() throws Exception {
        String token = login(
                "user",
                "user-change-me"
        );

        mockMvc.perform(
                        post("/api/v1/customers")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAdminToReachCreateCustomerEndpoint()
            throws Exception {
        String token = login(
                "admin",
                "admin-change-me"
        );

        mockMvc.perform(
                        post("/api/v1/customers")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest());
    }

    private String login(
            String username,
            String password
    ) throws Exception {
        String requestBody = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result
                .getResponse()
                .getContentAsString();

        Matcher matcher = ACCESS_TOKEN_PATTERN.matcher(responseBody);

        assertTrue(
                matcher.find(),
                "Authentication response does not contain accessToken"
        );

        return matcher.group(1);
    }
}