package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterInvoiceTest {

    private Database db;
    private QueryInvoicesDAO dao;
    private FilterInvoice filterInvoice;

    @BeforeEach
    void setUp() {
        db = new Database();
        dao = new QueryInvoicesDAO(db);
        dao.clear(); // ensure clean database
        filterInvoice = new FilterInvoice();
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    void filterInvoiceTest() {
        // Arrange – insert invoices into database
        dao.save(new Invoice("Alice", 50));   // should appear
        dao.save(new Invoice("Bob", 99));     // should appear
        dao.save(new Invoice("Charlie", 100)); // should NOT appear
        dao.save(new Invoice("Diana", 200));   // should NOT appear

        // Act
        List<Invoice> result = filterInvoice.lowValueInvoices();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(inv -> inv.getValue() < 100));
        assertTrue(result.stream().anyMatch(inv -> inv.getCustomer().equals("Alice")));
        assertTrue(result.stream().anyMatch(inv -> inv.getCustomer().equals("Bob")));
    }
}
