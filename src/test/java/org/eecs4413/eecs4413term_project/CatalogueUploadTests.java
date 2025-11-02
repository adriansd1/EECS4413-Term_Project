package org.eecs4413.eecs4413term_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eecs4413.eecs4413term_project.Eecs4413TermProjectApplication;
import org.eecs4413.eecs4413term_project.dto.UploadCatalogueRequest;
import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.service.CatalogueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CatalogueUploadTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogueService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void uploadValidItemReturnsCreated() throws Exception {
        UploadCatalogueRequest req = new UploadCatalogueRequest();
        req.setTitle("Nice Chair");
        req.setDescription("A comfy chair");
        req.setType("Furniture");
        req.setStartingPrice(30.0);
        req.setDurationMinutes(120);
        req.setSeller("seller1");

        Catalogue saved = new Catalogue();
        saved.setId(42L);
        saved.setTitle(req.getTitle());
        saved.setDescription(req.getDescription());
        saved.setType(req.getType());
        saved.setStartingPrice(req.getStartingPrice());
        saved.setCurrentBid(req.getStartingPrice());
        saved.setEndTime(LocalDateTime.now().plusMinutes(req.getDurationMinutes()));
        saved.setSeller(req.getSeller());

        when(service.createCatalogue(
                anyString(), anyString(), anyString(),
                anyDouble(), anyInt(), anyString(), anyString()
        )).thenReturn(saved);

        mockMvc.perform(post("/api/catalogue/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void uploadMissingFieldsReturnsBadRequest() throws Exception {
        // missing description and duration
        UploadCatalogueRequest req = new UploadCatalogueRequest();
        req.setTitle("Bad Item");
        req.setType("Misc");
        req.setStartingPrice(5.0);

        mockMvc.perform(post("/api/catalogue/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.durationMinutes").exists());
    }
}
