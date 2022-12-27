---
layout: default
title: Entering Commentary
nav_order: 4
---

# Entering Commentary

Now the series has been created, let's commentate!
{: .fs-6 .fw-300 }

A number of buttons are available to commentators to streamline
the process:

![]() <img src="https://user-images.githubusercontent.com/49768006/209709778-df2d1e16-b957-4cd8-b3ac-324f78b233af.png">

| Button               | Action Taken                                                 | Message Published to All Users |
|:---------------------|:-------------------------------------------------------------|:-------------------------------|
| Goal (Team)          | +1 to the team's **game score**                              | Yes                            |
| Game                 | +1 to the winning team's **series score**                    | Yes                            |
| Overtime             | 'Overtime' flag set                                          | Yes                            |
| Comment              | Publish comment, without impacting score                     | Yes                            |
| Generate Twitch Clip | Creates a Twitch clip, which is appended to the next message | _On next message_              |
| Remove Clip          | Removes the Twitch clip that was created                     | No                             |
| Edit Score           | Edit the score without publishing updates                    | No                             |


{: .no_toc }

<details open markdown="block">
  <summary>
    Table of contents
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

---

## Goal

Pressing **⚽ Quadrant** upticks Quadrant's **game score** by 1, and opens the
below menu.  We can fill it out to record a goal scored by RelatingWave, and assisted by Kash:
![]() <img src="https://user-images.githubusercontent.com/49768006/209724959-3314ac35-774a-4d5c-923a-fff3de8197d2.png">

{: .highlight }
In the Commentary field, any mentions of player ID in square brackets: `[1]` 
are replaced by the player's name!

On submit, the bot publishes a message in the **#rlcs-commentary** channel
which is available to all server members:
![]() <img src="https://user-images.githubusercontent.com/49768006/209724289-1d77474f-1cc1-4b29-8c72-85f73d71e6f7.png">

{: .note }
We see the message: `Commentary for series taken over by Minimus` because this is the first message
recorded for the series.  If someone else uses the bot to publish a message, we would see
`Commentary for series taken over by <Someone Else>`!

---

## Game

Pressing **Game** upticks the leading team's **series score** by 1, and opens the below menu:
![]() <img src="https://user-images.githubusercontent.com/49768006/209725326-15a89937-d8a8-4fa2-a9e9-86595269e029.png">

{: .highlight }
In the Commentary field, we can mention players from both teams - for example calling `[b1]` or `[o1]`!

On submit, the bot publishes a message in the **#rlcs-commentary** channel
which is available to all server members:
- The game score is reset to 0-0
- Overtime flag is set back to `false`
![]() <img src="https://user-images.githubusercontent.com/49768006/209725468-8fe9d586-2532-40d2-9e4b-865fb4bc245d.png">

{: .note }
The Game button can only be pressed if one team is ahead on **game score**!

---

## Overtime

Pressing the **Overtime** button automatically stages an update with the following comment:
![]() <img src="https://user-images.githubusercontent.com/49768006/209725797-ea0f21df-e5ec-4daf-93a9-4c5ad7f99e59.png">

{: .highlight }
The commentary can be modified as you wish, and supports player name substitution in the form of calling
`[b1]` or `[o1]`!

On submit, the bot publishes a message in the **#rlcs-commentary** channel
which is available to all server members:
![]() <img src="https://user-images.githubusercontent.com/49768006/209725825-e6ca1b65-c80c-4272-828b-9a8f4d33ec70.png">

{: .note }
The Overtime button can only be pressed if the **game scores** are level!

---

## Comment

Pressing the **Comment** button allows you to publish an update, without changing the score.
It's a good idea to commentate with some variety, beyond describing goals - and mention the things you are seeing
on/off the field!

This works similarly to [Overtime](#overtime) - but the comment box starts out empty:
![]() <img src="https://user-images.githubusercontent.com/49768006/209726068-e81f5a6b-2f2c-4d00-9063-87a9fc8ad8cb.png">

On submit, the bot publishes a message in the **#rlcs-commentary** channel
which is available to all server members:
![]() <img src="https://user-images.githubusercontent.com/49768006/209726171-c7f04176-bc8d-47b0-80ee-9dbf425d73f3.png">

---

## Twitch Clips

Pressing the **Generate Twitch Clip for Next Message** immediately generates a Twitch clip of the last 30 seconds
of action.

This clip is shared on the next [Goal](#goal), [Game](#game), [Overtime](#overtime) or [Comment](#comment) update.

This is explained more thoroughly in [Twitch Clips]({{ site.baseurl }}{% link twitch-clips.md %})!

---

## Editing the Score

At any point, you can edit the series/game score through the **Edit Score** button.
![]() <img src="https://user-images.githubusercontent.com/49768006/209726852-d159de2f-692e-4e2c-aeee-398ee944551c.png">

This edits the score of the series in the **#rlcs-mission-control** channel:
![]() <img src="https://user-images.githubusercontent.com/49768006/209726914-882c0382-558b-49a3-b641-54d97502c0d7.png">

{: .note }
This doesn't publish a message in the **#rlcs-commentary** channel, so you'll need a separate Comment to explain
the change!