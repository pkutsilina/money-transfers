package com.kutilina.transactions.model;

import java.sql.Timestamp;
import java.util.UUID;

public class MoneyTransaction {
    private Long id;

    private UUID transferID;

    private Double amount;

    private Timestamp date;

    public MoneyTransaction() {
    }

    public MoneyTransaction(Long id, UUID transactionID, Double amount, Timestamp date) {
        this.id = id;
        this.transferID = transactionID;
        this.amount = amount;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public UUID getTransferID() {
        return transferID;
    }

    public void setTransferID(UUID transferID) {
        this.transferID = transferID;
    }
}
