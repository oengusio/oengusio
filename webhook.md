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


### Models Listed
Mentioned models are available on this page under the models section at the bottom

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
        // DONATION MODEL //
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
    },
    "original_submission": {
        // SUBMISSION MODEL //
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
        // CATEGORY MODEL //
    },
    "deleted_by": {
        // USER MODEL //
    }
}
```
### Selection done event
```json5
{
    "event": "SELECTION_DONE",
    "selections": [
        {
            // SELECTION MODEL //
        }
    ]
}
```

## Models
### Donation
```json5
{
    "id": 0,
    "nickname": "duncte123",
    "date": "2021-01-11T19:50:40.390608+01:00",
    "amount": 1000,
    "comment": "I like trains"
}
```
### User
```json5
{ // user
    "id": 0,
    "username": "duncte123",
    "usernameJapanese": null, // string
    "enabled": true,
    "roles": [
        "ROLE_USER"
    ],
    "discordName": "duncte123#1245",
    "twitterName": "duncte123",
    "twitchName": "duncte123",
    "speedruncomName": "duncte123"
}
```
### Submission
```json5
{
    "id": 0,
    "user": {
        // USER MODEL //
    },
    "marathon": null,
    "games": [
        {
            // GAME MODEL //
        }
    ],
    "availabilities": [],
    "answers": [],
    "opponents": [],
    "opponentDtos": []
}
```
### Game
```json5
{
    "id": 0,
    "submission": null,
    "name": "Portal",
    "description": "The cake is a lie",
    "console": "PC",
    "ratio": "16:9",
    "emulated": false,
    "categories": [
        {
            // CATEGORY MODEL //
        }
    ]
}
```
### Category
```json5
 {
    "id": 0,
    "game": null,
    "name": "Glitchless",
    "estimate": "PT25M", // iso-8601 duration format
    "description": "Don't cheat :)",
    "video": "https://youtu.be/9_N3c_WW6rI",
    "code": "",
    "selection": null,
    "opponents": [],
    "opponentDtos": [],
    "status": null,
}
```
### Selection
```json5
{
    "id": 0,
    "marathon": null,
    "category": null,
    "status": "TODO | REJECTED | BONUS | VALIDATED | BACKUP"
}
```
