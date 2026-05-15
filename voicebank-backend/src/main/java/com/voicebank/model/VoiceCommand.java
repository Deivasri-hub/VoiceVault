package com.voicebank.model;

/**
 * Represents a parsed voice command.
 * Intent can be: TRANSFER, DEPOSIT, WITHDRAW, CHECK_BALANCE, SHOW_TRANSACTIONS
 */
public class VoiceCommand {
    private String intent;     // e.g., "TRANSFER"
    private String receiver;   // e.g., "Rahul"
    private Double amount;     // e.g., 500.0
    private String rawText;    // original voice input
    private boolean success;
    private String message;    // response message

    // Constructors
    public VoiceCommand() {}

    public VoiceCommand(String intent, Double amount, String receiver, String rawText) {
        this.intent = intent;
        this.amount = amount;
        this.receiver = receiver;
        this.rawText = rawText;
        this.success = true;
    }

    // Static factory for errors
    public static VoiceCommand error(String message) {
        VoiceCommand cmd = new VoiceCommand();
        cmd.success = false;
        cmd.message = message;
        return cmd;
    }

    // Getters and Setters
    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
