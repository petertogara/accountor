package com.accountor.prh;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.QueueDispatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class PrhServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MockWebServer mockPrhApi;

    private static final String API_BASE_PATH = "/api/v1/prh/companies";

    @BeforeEach
    void setUp() {
        mockPrhApi.setDispatcher(new QueueDispatcher());
    }

    @AfterEach
    void tearDown() throws IOException {

    }

    @Test
    @DisplayName("Should return 200 OK with company details when external API is OK")
    void getCompanyDetails_shouldReturnOk_whenExternalApiIsOk() throws Exception {
        String businessId = "0100002-9";

        String simpleJsonResponse = """
    {
        "totalResults": 1,
        "results": [
            {
                "businessId": {
                    "value": "0100002-9"
                }
            }
        ]
    }
    """;

        mockPrhApi.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(simpleJsonResponse));

        webTestClient.get().uri(API_BASE_PATH + "/{businessId}", businessId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();

    }
    @Test
    @DisplayName("Should return 404 when external API returns empty results")
    void getCompanyDetails_shouldReturn404_whenExternalApiReturnsEmptyResults() {
        String businessId = "0123456-7";
        String expectedDetailMessage = "Company not found for business ID: " + businessId;

        String emptyResultsJson = """
    {
        "totalResults": 0,
        "results": []
    }
    """;

        mockPrhApi.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(emptyResultsJson));

            webTestClient.get().uri(API_BASE_PATH + "/{businessId}", businessId)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.title").isEqualTo("Not Found")
                    .jsonPath("$.detail").isEqualTo(expectedDetailMessage)
                    .jsonPath("$.instance").exists();

    }


}