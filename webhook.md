# Webhook information

## JSON structure
```json5
{
    "event": "PING | DONATION | SUBMISSION_ADD | SUBMISSION_EDIT",
    // ONLY SEND WHEN EVENT IS DONATION
    "donation": {
        "id": 0,
        "marathon": {},
        "functionalId": "",
        "paymentSource": "",
        "nickname": "duncte123",
        "date": "2021-01-01T15:10:00Z",
        "amount": 1000,
        "comment": "I like trains",
        "approved": false,
        "donationIncentiveLinks": [],
        "answers": [] 
    },
    // ONLY WITH ANY SUBMISSION* EVENT
    "submission": {
        // SUBMISSION MODEL //
    },
    // original submission info, ONLY WITH SUBMISSION_EDIT EVENT
    "original_submission": {
      // SUBMISSION MODEL //
      // This moel contains the old submission data in case of an edit event
    }
}
```

## Models
Mentioned models are available on this page under the models section at the bottom: https://oengus.io/api/swagger-ui.html
