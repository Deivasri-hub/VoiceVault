package com.voicebank.service;

import com.voicebank.model.VoiceCommand;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NLP Service: Parses natural language voice commands into structured data.
 *
 * Supported intents:
 *  - TRANSFER:           "Send 500 to Rahul", "Transfer 1000 to Priya", "Pay Amit 250"
 *  - DEPOSIT:            "Deposit 500"
 *  - WITHDRAW:           "Withdraw 200"
 *  - CHECK_BALANCE:      "Check balance", "What's my balance"
 *  - SHOW_TRANSACTIONS:  "Show transactions", "View history"
 */
@Service
public class NLPService {

    // -------------------------------------------------------
    //  TRANSFER PATTERNS
    // -------------------------------------------------------

    /** Pattern: "send/transfer/wire 500 to Rahul" */
    private static final Pattern SEND_TO = Pattern.compile(
        "\\b(?:send|transfer|wire)\\s+(?:rs\\.?\\s*|inr\\s*|rupees?\\s*)?(\\d+(?:\\.\\d+)?)\\s+(?:to|for)\\s+([a-zA-Z\\s]+)",
        Pattern.CASE_INSENSITIVE
    );

    /** Pattern: "pay Rahul 500" (name before amount) */
    private static final Pattern PAY_NAME_AMOUNT = Pattern.compile(
        "\\bpay\\s+([a-zA-Z\\s]+?)\\s+(?:rs\\.?\\s*|inr\\s*|rupees?\\s*)?(\\d+(?:\\.\\d+)?)",
        Pattern.CASE_INSENSITIVE
    );

    /** Pattern: "send 500 Rahul" (no 'to') */
    private static final Pattern SEND_AMOUNT_NAME = Pattern.compile(
        "\\bsend\\s+(?:rs\\.?\\s*|inr\\s*|rupees?\\s*)?(\\d+(?:\\.\\d+)?)\\s+(?:to\\s+)?([a-zA-Z\\s]+)",
        Pattern.CASE_INSENSITIVE
    );

    // -------------------------------------------------------
    //  DEPOSIT / WITHDRAW PATTERNS
    // -------------------------------------------------------

    private static final Pattern DEPOSIT = Pattern.compile(
        "\\bdeposit\\s+(?:rs\\.?\\s*|inr\\s*|rupees?\\s*)?(\\d+(?:\\.\\d+)?)",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern WITHDRAW = Pattern.compile(
        "\\bwithdraw(?:al)?\\s+(?:rs\\.?\\s*|inr\\s*|rupees?\\s*)?(\\d+(?:\\.\\d+)?)",
        Pattern.CASE_INSENSITIVE
    );

    // -------------------------------------------------------
    //  BALANCE / HISTORY PATTERNS
    // -------------------------------------------------------

    private static final Pattern BALANCE = Pattern.compile(
        "\\b(?:check|show|what('s| is)?|my)\\s*(my\\s*)?(?:balance|money|funds|account)\\b",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TRANSACTIONS = Pattern.compile(
        "\\b(?:show|view|see|list|my)\\s*(my\\s*)?(?:transactions?|history|transfers?)\\b",
        Pattern.CASE_INSENSITIVE
    );

    // -------------------------------------------------------
    //  MAIN PARSE METHOD
    // -------------------------------------------------------

    /**
     * Parse a raw voice text string into a VoiceCommand object.
     *
     * @param rawText The raw speech-to-text string
     * @return VoiceCommand with intent and extracted parameters
     */
    public VoiceCommand parse(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return VoiceCommand.error("Empty voice input received.");
        }

        String text = rawText.trim().toLowerCase();

        // 1. Check balance
        if (BALANCE.matcher(text).find() || text.equals("balance")) {
            VoiceCommand cmd = new VoiceCommand("CHECK_BALANCE", null, null, rawText);
            cmd.setMessage("Fetching your balance.");
            return cmd;
        }

        // 2. Show transactions
        if (TRANSACTIONS.matcher(text).find()) {
            VoiceCommand cmd = new VoiceCommand("SHOW_TRANSACTIONS", null, null, rawText);
            cmd.setMessage("Showing your transaction history.");
            return cmd;
        }

        // 3. Deposit
        Matcher depositMatcher = DEPOSIT.matcher(text);
        if (depositMatcher.find()) {
            double amount = Double.parseDouble(depositMatcher.group(1));
            if (amount <= 0) return VoiceCommand.error("Deposit amount must be greater than zero.");
            VoiceCommand cmd = new VoiceCommand("DEPOSIT", amount, null, rawText);
            cmd.setMessage(String.format("Depositing ₹%.2f", amount));
            return cmd;
        }

        // 4. Withdraw
        Matcher withdrawMatcher = WITHDRAW.matcher(text);
        if (withdrawMatcher.find()) {
            double amount = Double.parseDouble(withdrawMatcher.group(1));
            if (amount <= 0) return VoiceCommand.error("Withdrawal amount must be greater than zero.");
            VoiceCommand cmd = new VoiceCommand("WITHDRAW", amount, null, rawText);
            cmd.setMessage(String.format("Withdrawing ₹%.2f", amount));
            return cmd;
        }

        // 5. Transfer: "send/transfer 500 to Rahul"
        Matcher sendToMatcher = SEND_TO.matcher(text);
        if (sendToMatcher.find()) {
            double amount = Double.parseDouble(sendToMatcher.group(1));
            String receiver = capitalise(sendToMatcher.group(2).trim());
            return buildTransferCommand(amount, receiver, rawText);
        }

        // 6. Transfer: "pay Rahul 500"
        Matcher payMatcher = PAY_NAME_AMOUNT.matcher(text);
        if (payMatcher.find()) {
            String receiver = capitalise(payMatcher.group(1).trim());
            double amount = Double.parseDouble(payMatcher.group(2));
            return buildTransferCommand(amount, receiver, rawText);
        }

        // 7. Transfer: "send 500 Rahul"
        Matcher sendNameMatcher = SEND_AMOUNT_NAME.matcher(text);
        if (sendNameMatcher.find()) {
            double amount = Double.parseDouble(sendNameMatcher.group(1));
            String receiver = capitalise(sendNameMatcher.group(2).trim());
            return buildTransferCommand(amount, receiver, rawText);
        }

        // No pattern matched
        return VoiceCommand.error(
            "Could not understand: \"" + rawText + "\". " +
            "Try: \"Send 500 to Rahul\", \"Check balance\", or \"Deposit 1000\"."
        );
    }

    // -------------------------------------------------------
    //  HELPER METHODS
    // -------------------------------------------------------

    /**
     * Build a validated TRANSFER command
     */
    private VoiceCommand buildTransferCommand(double amount, String receiver, String rawText) {
        if (amount <= 0) {
            return VoiceCommand.error("Transfer amount must be greater than zero.");
        }
        if (receiver == null || receiver.isEmpty()) {
            return VoiceCommand.error("Could not identify the receiver name.");
        }
        VoiceCommand cmd = new VoiceCommand("TRANSFER", amount, receiver, rawText);
        cmd.setMessage(String.format("Transferring ₹%.2f to %s", amount, receiver));
        return cmd;
    }

    /**
     * Capitalise first letter of each word
     */
    private String capitalise(String text) {
        if (text == null || text.isEmpty()) return text;
        String[] words = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
