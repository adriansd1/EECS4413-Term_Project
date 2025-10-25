package org.eecs4413.eecs4413term_project;


import org.eecs4413.eecs4413term_project.model.Catalogue;
import org.eecs4413.eecs4413term_project.service.CatalogueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CatalogueSearchTests {

    @Autowired
    private CatalogueService service;

    @Test
    void testEmptyKeywordReturnsAll() {
        List<Catalogue> all = service.search("");
        assertNotNull(all);
    }

    @Test
    void testKeywordSearch() {
        List<Catalogue> results = service.search("phone");
        assertNotNull(results);
    }
}
