---
layout: default
title: Twitch Clips
nav_order: 5
---

# Twitch Clips

Create and share Twitch clips at the press of a button!
{: .fs-6 .fw-300 }

---

## Setup

When [Creating a Series]({{ site.baseurl }}{% link creating-a-series.md %}), ensure the optional **twitchchannel**
parameter has been specified.  Either form is accepted:
- Username: rocketleague
- Full Twitch link: https://www.twitch.tv/rocketleague

{: .important }
Check the Twitch Broadcaster ID has been successfully set when the series has been created.  If you see
`TWITCH_USER_NOT_FOUND`, you won't be able to record any clips!

## Creating and Publishing a Clip

When something exciting happens on stream, click the **Generate Twitch Clip for Next Message** button!

This clip is shared on the next Goal, Game, Overtime, or Comment event (see
[Entering Commentary]({{ site.baseurl }}{% link entering-commentary.md %})).

{: .important }
This will **immediately** generate a new Twitch clip from the last 30 seconds of action.  So take your time
when entering your next commentary update!

{: .highlight }
Clips can be removed from the next update simply by clicking the **Remove Clip** button.

## Worked Example

Saizen[^1] is live now, so let's demonstrate this using his stream!

First we'll create a series using his Twitch handle. The created series has the Twitch Broadcaster ID set - perfect!
![]() <img src="https://user-images.githubusercontent.com/49768006/209730589-b7e33d28-24ca-4f7e-8feb-564f3261b6f8.png">

Now we'll **Generate Twitch Clip for Next Message**.
We can now see a Twitch Clip ID which is staged on the series, and ready for publishing:
![]() <img src="https://user-images.githubusercontent.com/49768006/209730934-64bc847b-57a9-4fdd-b318-c4fbe7cd7a1e.png">

Now let's enter a Goal update for eg Quadrant.
The published message includes the highlight!
![]() <img src="https://user-images.githubusercontent.com/49768006/209731420-96f78ec3-44e0-4288-a12d-a857eafeda35.png">

## Limitations

{: .important }
Unfortunately, not all channels support clipping - and some channels limit this feature to followers/subscribers.
This bot doesn't circumvent those restrictions, so keep them in mind!

---

[^1]: https://www.twitch.tv/saizen