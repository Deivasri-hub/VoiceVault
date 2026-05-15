package com.voicebank.service;

import com.voicebank.model.TransferRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Transfer Validation Service.
 * Validates transfer requests server-side before the frontend updates Local Storage.
 * Note: Actual balance deduction is done by the frontend in Local Storage.
 */
@Service
public class TransferService {

    private static final double MIN_TRANSFER = 1.0;
    private static final double MAX_SINGLE_TRANSFER = 100000.0; // ₹1 lakh per transfer

    /**
     * Validate a transfer request.
     *
     * @param request TransferRequest from frontend
     * @return Map with "valid" (boolean) and "message" (string)
     */
    public Map<String, Object> validate(TransferRequest request) {
        Map<String, Object> result = new HashMap<>();

        // Null checks
        if (request == null) {
            return fail(result, "Invalid request payload.");
        }
        if (request.getSender() == null || request.getSender().trim().isEmpty()) {
            return fail(result, "Sender is required.");
        }
        if (request.getReceiver() == null || request.getReceiver().trim().isEmpty()) {
            return fail(result, "Receiver is required.");
        }
        if (request.getAmount() == null) {
            return fail(result, "Amount is required.");
        }

        double amount = request.getAmount();
        double senderBalance = request.getSenderBalance() != null ? request.getSenderBalance() : 0.0;

        // Self-transfer check
        if (request.getSender().equalsIgnoreCase(request.getReceiver())) {
            return fail(result, "Sender and receiver cannot be the same.");
        }

        // Amount range check
        if (amount < MIN_TRANSFER) {
            return fail(result, String.format("Minimum transfer amount is ₹%.2f.", MIN_TRANSFER));
        }
        if (amount > MAX_SINGLE_TRANSFER) {
            return fail(result, String.format("Maximum single transfer limit is ₹%.2f.", MAX_SINGLE_TRANSFER));
        }

        // Balance check
        if (senderBalance < amount) {
            return fail(result,
                String.format("Insufficient balance. Available: ₹%.2f, Required: ₹%.2f", senderBalance, amount));
        }

        // All checks passed
        result.put("valid", true);
        result.put("message", String.format("₹%.2f transfer to %s is valid.", amount, request.getReceiver()));
        return result;
    }

    private Map<String, Object> fail(Map<String, Object> result, String message) {
        result.put("valid", false);
        result.put("message", message);
        return result;
    }
}
