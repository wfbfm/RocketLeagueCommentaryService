package rlcs.series;

public class Series
{

    private int seriesId;
    private int messageCount;
    private Score gameScore;
    private Score seriesScore;
    private Team blueTeam;
    private Team orangeTeam;

    private int bestOf;
    private boolean overtime;
    private String twitchName;
    private String twitchBroadcasterId;

    private String twitchClipId;

    private String commentator;

    public Series(int seriesId, int messageCount, Score gameScore, Score seriesScore, Team blueTeam, Team orangeTeam, int bestOf, boolean overtime, String twitchName, String twitchBroadcasterId, String twitchClipId, String commentator)
    {
        this.seriesId = seriesId;
        this.messageCount = messageCount;
        this.gameScore = gameScore;
        this.seriesScore = seriesScore;
        this.blueTeam = blueTeam;
        this.orangeTeam = orangeTeam;
        this.bestOf = bestOf;
        this.overtime = overtime;
        this.twitchName = twitchName;
        this.twitchBroadcasterId = twitchBroadcasterId;
        this.twitchClipId = twitchClipId;
        this.commentator = commentator;
    }


    public int getSeriesId()
    {
        return seriesId;
    }

    public void setSeriesId(int seriesId)
    {
        this.seriesId = seriesId;
    }

    public int getMessageCount()
    {
        return messageCount;
    }

    public void setMessageCount(int messageCount)
    {
        this.messageCount = messageCount;
    }

    public Score getGameScore()
    {
        return gameScore;
    }

    public void setGameScore(Score gameScore)
    {
        this.gameScore = gameScore;
    }

    public Score getSeriesScore()
    {
        return seriesScore;
    }

    public void setSeriesScore(Score seriesScore)
    {
        this.seriesScore = seriesScore;
    }

    public Team getTeam(TeamColour teamColour)
    {
        if (teamColour == TeamColour.BLUE)
        {
            return this.blueTeam;
        }
        else
        {
            return this.orangeTeam;
        }
    }

    public Team getBlueTeam()
    {
        return blueTeam;
    }

    public void setBlueTeam(Team blueTeam)
    {
        this.blueTeam = blueTeam;
    }

    public Team getOrangeTeam()
    {
        return orangeTeam;
    }

    public void setOrangeTeam(Team orangeTeam)
    {
        this.orangeTeam = orangeTeam;
    }

    public int getBestOf()
    {
        return bestOf;
    }

    public void setBestOf(int bestOf)
    {
        this.bestOf = bestOf;
    }

    public boolean isOvertime()
    {
        return overtime;
    }

    public void setOvertime(boolean overtime)
    {
        this.overtime = overtime;
    }

    public String getTwitchName()
    {
        return twitchName;
    }

    public void setTwitchName(final String twitchName)
    {
        this.twitchName = twitchName;
    }

    public String getTwitchBroadcasterId()
    {
        return twitchBroadcasterId;
    }

    public void setTwitchBroadcasterId(final String twitchBroadcasterId)
    {
        this.twitchBroadcasterId = twitchBroadcasterId;
    }

    public String getTwitchClipId()
    {
        return twitchClipId;
    }

    public void setTwitchClipId(final String twitchClipId)
    {
        this.twitchClipId = twitchClipId;
    }

    public String getCommentator()
    {
        return commentator;
    }

    public void setCommentator(final String commentator)
    {
        this.commentator = commentator;
    }
}
