package org.example;

import java.util.ArrayList;
import java.util.List;

public class SAP_BasedInvoiceSender {

    private final FilterInvoice filter;
    private final SAP sap;

    public SAP_BasedInvoiceSender(FilterInvoice filter, SAP sap) {
        this.filter = filter;
        this.sap = sap;
    }

    // Now returns a list of invoices that failed to send
    public List<Invoice> sendLowValuedInvoices() {
        List<Invoice> lowValuedInvoices = filter.lowValueInvoices();
        List<Invoice> failedInvoices = new ArrayList<>();

        for (Invoice invoice : lowValuedInvoices) {
            try {
                sap.send(invoice);
            } catch (Exception e) {
                failedInvoices.add(invoice);
                // Log or wrap if needed, but continue sending others
            }
        }

        return failedInvoices;
    }
}
