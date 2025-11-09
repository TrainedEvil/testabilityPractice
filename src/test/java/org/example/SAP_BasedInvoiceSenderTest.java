package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SAP_BasedInvoiceSenderTest {

    // Stub FilterInvoice that returns a preset list
    static class StubFilterInvoice extends FilterInvoice {
        private final List<Invoice> toReturn;

        StubFilterInvoice(List<Invoice> toReturn) {
            // super() will run and build real internals, but we override usage below
            this.toReturn = toReturn;
        }

        @Override
        public List<Invoice> lowValueInvoices() {
            return toReturn;
        }
    }

    // Spy SAP that records sends
    static class SpySAP implements SAP {
        final List<Invoice> sent = new ArrayList<>();
        @Override
        public void send(Invoice invoice) {
            sent.add(invoice);
        }
    }

    @Test
    void testWhenLowInvoicesSent() {
        // Arrange
        // Prepare stub data and spy
        List<Invoice> low = List.of(
                new Invoice("Alice", 50),
                new Invoice("Bob", 99)
        );
        StubFilterInvoice filterStub = new StubFilterInvoice(low);
        SpySAP sapSpy = new SpySAP();
        SAP_BasedInvoiceSender sender = new SAP_BasedInvoiceSender(filterStub, sapSpy);

        // Act
        // Call method under test
        sender.sendLowValuedInvoices();

        // Assert - verify sap.send was called for each invoice
        assertEquals(2, sapSpy.sent.size());
        assertTrue(sapSpy.sent.containsAll(low));
    }

    @Test
    void testWhenNoInvoices() {
        // Arrange
        // Prepare empty stub and spy
        StubFilterInvoice filterStub = new StubFilterInvoice(List.of());
        SpySAP sapSpy = new SpySAP();
        SAP_BasedInvoiceSender sender = new SAP_BasedInvoiceSender(filterStub, sapSpy);

        // Act
        // Call method under test
        sender.sendLowValuedInvoices();

        // Assert - verify no sap.send calls occurred
        assertTrue(sapSpy.sent.isEmpty());
    }
}
