package com.voicebank.controller;

import com.voicebank.model.TransferRequest;
import com.voicebank.model.VoiceCommand;
import com.voicebank.service.NLPService;
import com.voicebank.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * VoiceBank REST Controller
 *
 * Endpoints:
 *   POST /api/parse-voice-command   → Parse voice text → return intent + amount + receiver
 *   POST /api/validate-transfer     → Validate transfer params → return success/failure
 *   GET  /api/health                → Health check
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // Allow all origins for local dev / frontend access
public class VoiceBankController {

    @Autowired
    private NLPService nlpService;

    @Autowired
    private TransferService transferService;

    // -------------------------------------------------------
    //  HEALTH CHECK
    // -------------------------------------------------------

    /**
     * GET /api/health
     * Simple health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "UP");
        resp.put("service", "VoiceBank NLP API");
        resp.put("version", "1.0.0");
        return ResponseEntity.ok(resp);
    }

    // -------------------------------------------------------
    //  PARSE VOICE COMMAND
    // -------------------------------------------------------

    /**
     * POST /api/parse-voice-command
     *
     * Request body:
     * {
     *   "text": "Send 500 to Rahul"
     * }
     *
     * Response (success):
     * {
     *   "intent": "TRANSFER",
     *   "amount": 500.0,
     *   "receiver": "Rahul",
     *   "rawText": "Send 500 to Rahul",
     *   "success": true,
     *   "message": "Transferring ₹500.00 to Rahul"
     * }
     *
     * Response (error):
     * {
     *   "success": false,
     *   "message": "Could not understand the command."
     * }
     */
    @PostMapping("/parse-voice-command")
    public ResponseEntity<VoiceCommand> parseVoiceCommand(@RequestBody Map<String, String> body) {
        String text = body.get("text");

        if (text == null || text.trim().isEmpty()) {
            VoiceCommand errorCmd = VoiceCommand.error("No voice text provided.");
            return ResponseEntity.badRequest().body(errorCmd);
        }

        VoiceCommand result = nlpService.parse(text.trim());
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------
    //  VALIDATE TRANSFER
    // -------------------------------------------------------

    /**
     * POST /api/validate-transfer
     *
     * Request body:
     * {
     *   "sender": "user@email.com",
     *   "receiver": "Rahul",
     *   "amount": 500.0,
     *   "senderBalance": 2000.0
     * }
     *
     * Response (success):
     * {
     *   "valid": true,
     *   "message": "₹500.00 transfer to Rahul is valid."
     * }
     *
     * Response (failure):
     * {
     *   "valid": false,
     *   "message": "Insufficient balance. Available: ₹200.00, Required: ₹500.00"
     * }
     */
    @PostMapping("/validate-transfer")
    public ResponseEntity<Map<String, Object>> validateTransfer(@RequestBody TransferRequest request) {
        Map<String, Object> result = transferService.validate(request);
        return ResponseEntity.ok(result);
    }
}
