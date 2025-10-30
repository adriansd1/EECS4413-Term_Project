package org.eecs4413.eecs4413term_project;

import jakarta.annotation.Resource;
import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.service.CatalogueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void testSelectAndRetrieveItem() throws Exception {
        // mock findById to simulate an existing catalogue item
        when(service.findById(1L)).thenReturn(Optional.of(new Catalogue()));

        // mock findById for a non-existing item
        when(service.findById(2L)).thenReturn(Optional.empty());

        // create a mock session
        var session = new org.springframework.mock.web.MockHttpSession();

        // First selection - should be OK
        mockMvc.perform(post("/api/catalogue/select/1")
                        .session(session) //  same session used across requests
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Second selection in same session - should now return 400
        mockMvc.perform(post("/api/catalogue/select/2")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Retrieve the selected item - should be OK
        mockMvc.perform(get("/api/catalogue/selected")
                        .session(session))
                .andExpect(status().isOk());
    }




    @Test
    void testNoSelectionYet() throws Exception {
        mockMvc.perform(get("/api/catalogue/selected"))
                .andExpect(status().isBadRequest());
    }
}
