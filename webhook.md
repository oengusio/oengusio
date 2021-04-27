# Webhook information

## Bot webhook
There is a special url format that oengus accepts to make the oengus bot send embeds to the channel you specify.

The format is as following: `oengus-bot?marathon={shortcode}&donation={channelId}&newsub={channelId}&editsub={channelId}`.

You can use this tool to generate url format: https://duncte123.me/oengus

Parameter explanation:
1. marathon: this must hold the marathon short name that you specified when creating the marathon. This is the only required parameter (but the bot won't work without any of the other ones)
2. donation: New donations are being sent to the channel specified. This is the ID of a **text channel on discord that the bot can talk in**
3. newsub: New submissions are sent to the channel specified, this will also announce runs that have been accepted. This is the ID of a **text channel on discord that the bot can talk in**
4. editsub: Edited/deleted submissions/games/categores are sent to the channel specified. **New submissions will also be sent to this channel.** This is the ID of a **text channel on discord that the bot can talk in**

TIP: for the best result with logging of submissions, set both the `newsub` and `editsub` fields as the `editsub` field detects new categories/games on a submission as well.

How to get these text channel ids: [https://support.discord.com/hc/en-us/articles/206346498](https://support.discord.com/hc/en-us/articles/206346498)

## JSON structure
### Events
- Ping: Ping event is sent to test the webhook (must respond with 2xx status code).
- Donation: Donation event is sent for donations.
- Submission add: Submission add event is sent when a user made a submission.
- Submission edit: submission edit event is sent when a user edits their submission.
- submission delete: submission delete event is sent when a submission is deleted


### Models
Mentioned models are available on this page under the models section at the bottom: https://oengus.io/api/swagger-ui.html


### Ping event
```json5
{
    "event": "PING"
}
```
### Donation event
```json5
{
    "event": "DONATION",
    "donation": {
        "id": 0,
        "nickname": "duncte123",
        "date": "2021-01-11T19:50:40.390608+01:00",
        "amount": 1000,
        "comment": "I like trains"
    }
}
```
### Submission add event
```json5
{
    "event": "SUBMISSION_ADD",
    "submission": {
        // SUBMISSION MODEL //
    }
}
```
### Submission edit event
```json5
{
    "event": "SUBMISSION_EDIT",
    "submission": {
        // SUBMISSION MODEL //
        // This is the updated submission
    },
    "original_submission": {
        // SUBMISSION MODEL //
        // This model contains the old submission data in case of an edit event
    }
}
```
### Submission delete event
```json5
{
    "event": "SUBMISSION_DELETE",
    "submission": {
        // SUBMISSION MODEL //
    },
    "deleted_by": {
        // USER MODEL //
    }
}
```
### Game delete event
```json5
{
    "event": "GAME_DELETE",
    "game": {
        // GAME MODEL //
    },
    "deleted_by": {
        // USER MODEL //
    }
}
```
### Category delete event
```json5
{
    "event": "CATEGORY_DELETE",
    "category": {
        // category MODEL //
    },
    "deleted_by": {
        // USER MODEL //
    }
}
```
