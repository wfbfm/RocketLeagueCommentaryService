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

    public Series(int seriesId, int messageCount, Score gameScore, Score seriesScore, Team blueTeam, Team orangeTeam, int bestOf)
    {
        this.seriesId = seriesId;
        this.messageCount = messageCount;
        this.gameScore = gameScore;
        this.seriesScore = seriesScore;
        this.blueTeam = blueTeam;
        this.orangeTeam = orangeTeam;
        this.bestOf = bestOf;
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
}
