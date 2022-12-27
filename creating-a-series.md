---
layout: default
title: Creating a Series
nav_order: 3
---

# Creating a Series

The first step in your commentary journey: the setup.
{: .fs-6 .fw-300 }

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

## /updateteamsfromliquipedia

Use _/updateteamsfromliquipedia_ to retrieve a list of teams & players participating in a specific
Rocket League event documented on Liquipedia.

This updates the list of teams shown in the [/createseries](#/createseries) command - which 
automatically fetches the player names for you!
![]() <img src="https://user-images.githubusercontent.com/49768006/209707022-44991128-b787-4acd-a5c2-5fdf7b07146b.png">

{: .note }
The Liquipedia event page needs to have a Participants grid similar to the 
[RLCS 2022-23 Fall Major page](https://liquipedia.net/rocketleague/Rocket_League_Championship_Series/2022-23/Fall#Participants).

{: .important }
You only need to use _/updateteamsfromliquipedia_ **once** - because the
bot will store the reference data in memory.

The bot will reply in the **#rlcs-mission-control** channel indicating which 
Liquipedia page is currently stored in its memory.
![]() <img src="https://user-images.githubusercontent.com/49768006/209708413-d0d33dce-4bb9-46b4-b38f-adcac9afd9f3.png">

---

## /createseries

Use _/createseries_ to initialise a new series, using team/player information
from a Liquipedia event page.
![]() <img src="https://user-images.githubusercontent.com/49768006/209708898-6e1cecfd-ee9c-47a7-a363-66ed73fada27.png">

_/createseries_ takes four inputs:

- teamblue: Choose from dropdown
- teamorange: Choose from dropdown
- bestof: Odd integer
- _**(Optional)**_ twitchchannel: Twitch channel streaming the game 
(see [Twitch Clips]({{ site.baseurl }}{% link twitch-clips.md %}))

For example, let's create a Bo7 series between Quadrant and Team Falcons,
hosted on https://www.twitch.tv/rocketleague:
![]() <img src="https://user-images.githubusercontent.com/49768006/209709334-b62cf069-69f4-4a87-bb80-86d0161605ef.png">

The bot will respond by creating a series in the **#rlcs-mission-control** channel:
![]() <img src="https://user-images.githubusercontent.com/49768006/209709778-df2d1e16-b957-4cd8-b3ac-324f78b233af.png">

{: .note }
To update the list of teams shown in this command, you need to run
[/updateteamsfromliquipedia](#/updateteamsfromliquipedia)

{: .highlight }
When entering the twitchchannel field, you can provide either the username (rocketleague)
or the full Twitch link (https://www.twitch.tv/rocketleague).

{: .highlight }
When creating a series, the bot will automatically assign a "Series ID"
(in this example, "S65") - which allows users to search
for commentary of a historical series.

---

## /createseriesmanually

This is practically identical to [/createseries](#/createseries) - except
you have to populate the team/player names manually:
![]() <img src="https://user-images.githubusercontent.com/49768006/209710542-11c87ac1-f620-437e-a95b-c834029f7fe0.png">
