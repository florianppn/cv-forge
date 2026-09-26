package com.cvforge.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitingFilterTest {

    @Test
    @DisplayName("Blocage 429 Too Many Requests après dépassement du quota de requêtes IA")
    void testRateLimiting_Exceeded() throws Exception {
        // Filtre configuré avec une capacité de 2 requêtes
        RateLimitingFilter filter = new RateLimitingFilter(2, 2, 1);
        String testIp = "192.168.1.100";

        // Requête 1 : Succès (200 / passage au filtre suivant)
        MockHttpServletRequest req1 = new MockHttpServletRequest("POST", "/api/v1/cv/generate");
        req1.setRemoteAddr(testIp);
        MockHttpServletResponse res1 = new MockHttpServletResponse();
        MockFilterChain chain1 = new MockFilterChain();
        filter.doFilterInternal(req1, res1, chain1);
        assertEquals(200, res1.getStatus());

        // Requête 2 : Succès (200 / passage au filtre suivant)
        MockHttpServletRequest req2 = new MockHttpServletRequest("POST", "/api/v1/cv/generate");
        req2.setRemoteAddr(testIp);
        MockHttpServletResponse res2 = new MockHttpServletResponse();
        MockFilterChain chain2 = new MockFilterChain();
        filter.doFilterInternal(req2, res2, chain2);
        assertEquals(200, res2.getStatus());

        // Requête 3 : Dépassement du quota -> HTTP 429
        MockHttpServletRequest req3 = new MockHttpServletRequest("POST", "/api/v1/cv/generate");
        req3.setRemoteAddr(testIp);
        MockHttpServletResponse res3 = new MockHttpServletResponse();
        MockFilterChain chain3 = new MockFilterChain();
        filter.doFilterInternal(req3, res3, chain3);
        assertEquals(429, res3.getStatus());
        org.junit.jupiter.api.Assertions.assertTrue(res3.getContentAsString().contains("429"));
        org.junit.jupiter.api.Assertions.assertTrue(res3.getContentAsString().contains("Quota de requêtes"));
    }

    @Test
    @DisplayName("Les requêtes GET ordinaires ne sont pas soumises au rate-limiting de l'IA")
    void testNonAiEndpoints_NotRateLimited() throws Exception {
        RateLimitingFilter filter = new RateLimitingFilter(1, 1, 1);
        String testIp = "192.168.1.101";

        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/cv/123");
            req.setRemoteAddr(testIp);
            MockHttpServletResponse res = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();
            filter.doFilterInternal(req, res, chain);
            assertEquals(200, res.getStatus());
        }
    }
}
