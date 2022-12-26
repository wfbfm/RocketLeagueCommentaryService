package rlcs.bot.commands.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.Clip;
import com.github.twitch4j.helix.domain.ClipList;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.github.twitch4j.helix.domain.UserList;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class TwitchClipper
{
    private static final String TWITCH_ACCESS_TOKEN = System.getenv("TWITCH_ACCESS_TOKEN");
    private static final int MAX_NUMBER_GET_CLIP_RETRIES = 8;
    private TwitchClient twitchClient;

    public TwitchClipper()
    {
        this.twitchClient = TwitchClientBuilder.builder()
                .withDefaultAuthToken(new OAuth2Credential("twitch", TWITCH_ACCESS_TOKEN))
                .withEnableHelix(true)
                .build();
    }

    public String parseTwitchName(final String twitchName)
    {
        if (StringUtils.isEmpty(twitchName))
        {
            return "None";
        }
        String parsedTwitchName;
        if (twitchName.contains("twitch.tv"))
        {
            parsedTwitchName = twitchName.split("twitch.tv")[1].replace("/", "").toLowerCase();
        }
        else
        {
            parsedTwitchName = twitchName.replace("/", "").toLowerCase();
        }
        return parsedTwitchName;
    }

    public String getBroadcasterIdForTwitchName(final String twitchName)
    {
        if (StringUtils.isEmpty(twitchName))
        {
            return TwitchStatus.TWITCH_USER_NOT_FOUND.name();
        }

        UserList userList = twitchClient.getHelix().getUsers(null, null, Arrays.asList(twitchName)).execute();
        if (userList.getUsers().size() > 0)
        {
            return userList.getUsers().get(0).getId();
        }
        else
        {
            return TwitchStatus.TWITCH_USER_NOT_FOUND.name();
        }
    }

    public String createClipAndReturnClipId(final String broadcasterId)
    {
        if (broadcasterId.equals(TwitchStatus.TWITCH_USER_NOT_FOUND.name()))
        {
            return TwitchStatus.UNABLE_TO_CREATE_CLIP.name();
        }

        CreateClipList clipList = twitchClient.getHelix().createClip(TWITCH_ACCESS_TOKEN, broadcasterId, false).execute();
        if (clipList.getData().size() > 0)
        {
            return clipList.getData().get(0).getId();
        }
        else
        {
            return TwitchStatus.UNABLE_TO_CREATE_CLIP.name();
        }
    }

    public String getUrlFromClipId(final String clipId)
    {
        // Twitch clips can take some time to create.  Poll clip list a few times before giving up
        int numberOfTries = 0;

        Clip clip = null;
        ClipList clipList;
        while (numberOfTries < MAX_NUMBER_GET_CLIP_RETRIES)
        {
            numberOfTries += 1;
            clipList = twitchClient.getHelix().getClips(null, null, null, clipId, null, null, null, null, null).execute();

            if (clipList.getData().size() > 0)
            {
                clip = clipList.getData().get(0);
                break;
            }
            try
            {
                Thread.sleep(2000);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (clip != null)
        {
            return clip.getUrl();
        }
        else
        {
            return TwitchStatus.UNABLE_TO_FIND_CLIP.name();
        }
    }
}
