# Webhook information
NOTE: Webhooks will only fire the ping event on the sandbox environment.

## JSON structure
### Events
- Ping: Ping event is sent to test the webhook (must respond with 2xx status code).
- Donation: Donation event is sent for donations.
- Submission add: Submission add event is sent when a user made a submission.
- Submission edit: submission edit event is sent when a user edits their submission.

```json5
{
    "event": "PING | DONATION | SUBMISSION_ADD | SUBMISSION_EDIT",
    // ONLY SEND WHEN EVENT IS DONATION
    "donation": {
        "id": 0,
        "nickname": "duncte123",
        "date": "2021-01-11T19:50:40.390608+01:00",
        "amount": 1000,
        "comment": "I like trains",
        "test": false // this is always false, idk why it's there
    },
    // ONLY WITH ANY SUBMISSION* EVENT
    "submission": {
        // SUBMISSION MODEL //
    },
    // original submission info, ONLY WITH SUBMISSION_EDIT EVENT
    "original_submission": {
      // SUBMISSION MODEL //
      // This model contains the old submission data in case of an edit event
    }
}
```

## Models
Mentioned models are available on this page under the models section at the bottom: https://oengus.io/api/swagger-ui.html
