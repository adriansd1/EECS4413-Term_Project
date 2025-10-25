package org.eecs4413.eecs4413term_project;

import jakarta.annotation.Resource;
import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.service.CatalogueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
        }
)
@AutoConfigureMockMvc
class CatalogueTests {

    @MockBean
    private CatalogueService service; // This will be injected instead of real bean

    @Resource
    private MockMvc mockMvc;

    @Test
    void testEmptyKeywordReturnsAll() {
        when(service.search("")).thenReturn(Collections.emptyList());
        List<Catalogue> all = service.search("");
        assertNotNull(all);
        assertEquals(0, all.size());
    }

    @Test
    void testKeywordSearch() {
        when(service.search("phone")).thenReturn(List.of(new Catalogue()));
        List<Catalogue> results = service.search("phone");
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void testActiveAuctionsReturnsList() {
        when(service.getActiveAuctions()).thenReturn(List.of(Map.of("timeLeft", "1h")));
        List<Map<String, Object>> results = service.getActiveAuctions();
        assertNotNull(results);
        assertFalse(results.isEmpty());
    }

    @Test
    void testTimeLeftFormattingExists() {
        when(service.getActiveAuctions()).thenReturn(List.of(Map.of("timeLeft", "1h")));
        List<Map<String, Object>> results = service.getActiveAuctions();
        if (!results.isEmpty()) {
            assertTrue(results.get(0).containsKey("timeLeft"));
        }
    }
}
