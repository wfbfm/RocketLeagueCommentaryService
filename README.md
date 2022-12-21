# RocketLeagueCommentaryService

This Discord bot powers the Rocket League Commentary Service: https://discord.gg/Y8sBAmrzrq

The bot enables anyone with the **Commentator** role to create a new series, and publish in-game commentary updates to the _#rlcs-commentary_ channel.

![]() <img src="https://user-images.githubusercontent.com/49768006/209008160-2f85d409-668a-43cc-bc04-5504964fe77d.PNG"  width="500">


## Getting Started

1. Get the Commentator role by asking admins of the Discord server
2. Create a new series in the _#rlcs-mission-control_ channel.  This will create a template message holding all information about the ongoing series.

![]() <img src="https://user-images.githubusercontent.com/49768006/209007396-d6780d1f-2c8a-4f7b-bd21-a01e59de8bcd.png"  width="500">

3. Use the buttons to publish updates to the _#rlcs-commentary_ channel, which is visible to all members of the server:
- **Goal** (Blue/Orange team).  This upticks team gameScore by 1, and publishes a message with: goal scorer (req.), assister (opt.), comment (opt.)
- **Game**.  This concludes an individual game: upticks the seriesScore by 1 for the team that is ahead in the game, resets the gameScore to 0-0 and also resets the Overtime flag to false.  Publishes a message with comment (opt.)
- **Overtime**.  This sets the overtime flag to true and publishes a message with pre-filled comment (opt.)
- **Comment**.  Publish any comment at the current game/series score

## Consumer View

The end product is a colourful text coverage of unfolding games of Rocket League!

![]() <img src="https://user-images.githubusercontent.com/49768006/209010722-4f6fa6b3-759e-448d-acb5-ed61cd644568.jpg"  width="500">


