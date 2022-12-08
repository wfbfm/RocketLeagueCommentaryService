package rlcs.series;

public class Score
{

    private int blueScore;
    private int orangeScore;

    public Score(final int blueScore, final int orangeScore)
    {
        this.blueScore = blueScore;
        this.orangeScore = orangeScore;
    }

    public int getBlueScore()
    {
        return blueScore;
    }

    public int getOrangeScore()
    {
        return orangeScore;
    }

    public void setBlueScore(final int blueScore)
    {
        this.blueScore = blueScore;
    }

    public void setOrangeScore(final int orangeScore)
    {
        this.orangeScore = orangeScore;
    }
}
