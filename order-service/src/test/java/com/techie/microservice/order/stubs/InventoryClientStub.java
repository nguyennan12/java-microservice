package com.techie.microservice.order.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class InventoryClientStub {

    private static WireMockServer wireMockServer;

    public static void start() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
    }

    public static void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    public static void reset() {
        if (wireMockServer != null) {
            wireMockServer.resetAll();
        }
    }

    public static String baseUrl() {
        return "http://localhost:" + wireMockServer.port();
    }

    public static void stubInventoryCall(String skuCode, Integer quantity) {
        String url = "/api/inventory?skuCode=" + skuCode + "&quantity=" + quantity;
        wireMockServer.stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
    }
}