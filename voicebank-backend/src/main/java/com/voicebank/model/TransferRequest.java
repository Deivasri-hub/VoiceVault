package com.voicebank.model;

/**
 * Request payload for /validate-transfer endpoint.
 * Frontend sends this after user confirms voice/manual transfer.
 */
public class TransferRequest {
    private String sender;    // sender email
    private String receiver;  // receiver name or email
    private Double amount;    // amount to transfer
    private Double senderBalance; // current sender balance (from Local Storage)

    public TransferRequest() {}

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getSenderBalance() { return senderBalance; }
    public void setSenderBalance(Double senderBalance) { this.senderBalance = senderBalance; }
}
