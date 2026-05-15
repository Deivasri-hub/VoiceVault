# 🏦 VoiceBank — Accessible Banking System
### College Project Demo | Voice + Accessibility + Local Storage

---

## 📦 Project Structure

```
voicebank/
├── index.html                          ← Full frontend (single file)
└── voicebank-backend/                  ← Spring Boot backend
    ├── pom.xml
    └── src/main/java/com/voicebank/
        ├── VoiceBankApplication.java
        ├── config/CorsConfig.java
        ├── controller/VoiceBankController.java
        ├── model/
        │   ├── VoiceCommand.java
        │   └── TransferRequest.java
        └── service/
            ├── NLPService.java
            └── TransferService.java
```

---

## 🚀 Quick Start

### Step 1 — Run the Frontend
Simply open `index.html` in **Google Chrome** or **Microsoft Edge**.
> ⚠️ Voice recognition requires Chrome/Edge (Web Speech API).

Demo accounts are pre-loaded automatically:
| Name | Email | Password | PIN |
|------|-------|----------|-----|
| Rahul Sharma | rahul@demo.com | demo123 | 1234 |
| Priya Patel | priya@demo.com | demo123 | 1234 |
| Amit Kumar | amit@demo.com | demo123 | 1234 |

---

### Step 2 — Run the Spring Boot Backend

**Prerequisites:** Java 17+, Maven 3.6+

```bash
cd voicebank-backend
mvn spring-boot:run
```

Backend starts at: `http://localhost:8080`

---

## 🎤 Voice Commands Reference

| You Say | Action |
|---------|--------|
| "Send 500 to Rahul" | Transfer ₹500 to Rahul |
| "Transfer 1000 to Priya" | Transfer ₹1000 to Priya |
| "Pay Amit 250" | Transfer ₹250 to Amit |
| "Check balance" | Speak current balance |
| "Show transactions" | Navigate to history |
| "Deposit 500" | Deposit ₹500 |
| "Withdraw 200" | Withdraw ₹200 |

---

## 🔌 API Endpoints

### `POST /api/parse-voice-command`
Parses raw voice text into structured command.

**Request:**
```json
{ "text": "Send 500 to Rahul" }
```

**Response:**
```json
{
  "intent": "TRANSFER",
  "amount": 500.0,
  "receiver": "Rahul",
  "rawText": "Send 500 to Rahul",
  "success": true,
  "message": "Transferring ₹500.00 to Rahul"
}
```

---

### `POST /api/validate-transfer`
Validates transfer before frontend commits to Local Storage.

**Request:**
```json
{
  "sender": "rahul@demo.com",
  "receiver": "Priya",
  "amount": 500.0,
  "senderBalance": 10000.0
}
```

**Response:**
```json
{
  "valid": true,
  "message": "₹500.00 transfer to Priya is valid."
}
```

---

### `GET /api/health`
```json
{ "status": "UP", "service": "VoiceBank NLP API", "version": "1.0.0" }
```

---

## 🗄️ Local Storage Schema

### Users (`vb_users`)
```json
[
  {
    "name": "Rahul Sharma",
    "email": "rahul@demo.com",
    "password": "demo123",
    "pin": "1234",
    "balance": 9500.00
  }
]
```

### Transactions (`vb_transactions`)
```json
[
  {
    "id": "TX1699876543210",
    "sender": "rahul@demo.com",
    "receiver": "priya@demo.com",
    "amount": 500.0,
    "type": "debit",
    "note": "Voice transfer to Priya Patel",
    "date": "2024-11-13T10:30:00.000Z"
  }
]
```

### Session (`vb_current_user`)
```
"rahul@demo.com"
```

---

## ♿ Accessibility Features

1. **Blind Mode Toggle** — Click "Blind Mode" in top-right
2. **Text-to-Speech** — All actions are spoken aloud using SpeechSynthesis API
3. **Voice Commands** — Full banking via microphone (no screen needed)
4. **High Contrast** — Dark theme with high contrast ratios
5. **Focus Indicators** — Yellow outlines in blind mode for keyboard navigation
6. **Large Text** — Balances and key numbers are displayed large

---

## 🔐 Security Features

1. **PIN Verification** — 4-digit PIN required for every transaction
2. **Simulated OTP** — Random 6-digit OTP shown on screen before transfer
3. **Negative Balance Prevention** — Server + client side validation
4. **Transfer Limits** — Max ₹1,00,000 per transfer (server validated)
5. **Session Management** — Login/logout with Local Storage session

---

## 🧠 NLP Patterns Supported

The `NLPService.java` uses Java regex to match:

```
"send/transfer/wire [AMOUNT] to [NAME]"
"pay [NAME] [AMOUNT]"
"deposit [AMOUNT]"
"withdraw [AMOUNT]"
"check/show/what's my balance"
"show/view my transactions/history"
```

Amount prefixes supported: `rs.`, `inr`, `rupees`
Example: "Transfer Rs. 500 to Priya"

---

## 🛠️ Technologies Used

| Layer | Technology |
|-------|------------|
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Voice Input | Web Speech API (SpeechRecognition) |
| Voice Output | Web Speech API (SpeechSynthesis) |
| Storage | Browser Local Storage (JSON) |
| Backend | Java 17, Spring Boot 3.2 |
| NLP | Java Regex Pattern Matching |
| Build | Maven |

---

## 📝 Demo Script for College Presentation

1. Open `index.html` in Chrome
2. Register a new account (or use demo accounts)
3. Go to Dashboard — show balance card
4. Click Voice tab — demonstrate "Check balance" command
5. Say "Send 500 to Rahul" — show transfer confirmation
6. Toggle Blind Mode — show TTS reading the balance
7. Say "Check balance" — hear TTS speak it
8. Open Admin panel — show all users and transactions

---

*Built as a college project demonstrating Voice + Accessibility + Local Storage banking.*
