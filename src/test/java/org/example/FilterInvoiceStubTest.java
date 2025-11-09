package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class FilterInvoiceStubTest {

    // Simple stub class that avoids real DB work
    static class StubQueryInvoicesDAO extends QueryInvoicesDAO {
        public StubQueryInvoicesDAO() { super(null); }

        @Override
        public List<Invoice> all() {
            // Return fake in-memory data instead of querying the DB
            return Arrays.asList(
                    new Invoice("Alice", 50),
                    new Invoice("Bob", 99),
                    new Invoice("Charlie", 100),
                    new Invoice("Diana", 200)
            );
        }
    }

    // Custom FilterInvoice that accepts a stub DAO
    static class FilterInvoiceWithStub extends FilterInvoice {
        public FilterInvoiceWithStub(QueryInvoicesDAO stubDao) {
            this.dao = stubDao; // replace real DAO
        }
    }

    @Test
    void filterInvoiceStubbedTest() {
        // Arrange
        QueryInvoicesDAO stubDao = new StubQueryInvoicesDAO();
        FilterInvoice filterInvoice = new FilterInvoiceWithStub(stubDao);

        // Act
        List<Invoice> result = filterInvoice.lowValueInvoices();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(inv -> inv.getValue() < 100));
        assertTrue(result.stream().anyMatch(inv -> inv.getCustomer().equals("Alice")));
        assertTrue(result.stream().anyMatch(inv -> inv.getCustomer().equals("Bob")));
    }
}
