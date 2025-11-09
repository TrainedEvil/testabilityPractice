package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SAP_BasedInvoiceSenderTest {
// Test sending low valued invoices with various scenarios
    @Test
    void testWhenLowInvoicesSent() {
        FilterInvoice filter = mock(FilterInvoice.class);
        SAP sap = mock(SAP.class);
    // Arrange - stub low value invoices
        List<Invoice> invoices = List.of(
                new Invoice("Alice", 50),
                new Invoice("Bob", 80)
        );
        when(filter.lowValueInvoices()).thenReturn(invoices);
        // Act
        SAP_BasedInvoiceSender sender = new SAP_BasedInvoiceSender(filter, sap);

        List<Invoice> failed = sender.sendLowValuedInvoices();

        // Verify both invoices were sent successfully
        verify(sap, times(1)).send(invoices.get(0));
        verify(sap, times(1)).send(invoices.get(1));
        assertTrue(failed.isEmpty());
    }

    // Test when there are no low value invoices
    @Test
    void testWhenNoInvoices() {
        FilterInvoice filter = mock(FilterInvoice.class);
        SAP sap = mock(SAP.class);
// Arrange - stub no low value invoices
        when(filter.lowValueInvoices()).thenReturn(List.of());
        // Act
        SAP_BasedInvoiceSender sender = new SAP_BasedInvoiceSender(filter, sap);

        List<Invoice> failed = sender.sendLowValuedInvoices();
        // Verify no invoices were sent
        verifyNoInteractions(sap);
        assertTrue(failed.isEmpty());
    }

    // Test handling exception when sending an invoice fails
    @Test
    void testThrowExceptionWhenBadInvoice() {
        FilterInvoice filter = mock(FilterInvoice.class);
        SAP sap = mock(SAP.class);

        Invoice good = new Invoice("Alice", 50);
        Invoice bad = new Invoice("Bob", 80);

        when(filter.lowValueInvoices()).thenReturn(List.of(good, bad));

        // Simulate SAP failing for "bad"
        doThrow(new FailToSendSAPInvoiceException("SAP failure", new RuntimeException()))
                .when(sap).send(bad);

        SAP_BasedInvoiceSender sender = new SAP_BasedInvoiceSender(filter, sap);

        List<Invoice> failed = sender.sendLowValuedInvoices();

        // Verify both were attempted
        verify(sap).send(good);
        verify(sap).send(bad);

        // Ensure the bad one is returned as failed
        assertEquals(1, failed.size());
        assertEquals(bad, failed.get(0));
    }
}
