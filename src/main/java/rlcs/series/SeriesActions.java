package rlcs.series;

public class SeriesActions
{
    private static final int ACCEPTABLE_CHAR_LENGTH = 40;
    private static final int ACCEPTABLE_TEAM_LENGTH = 16;
    public void uptickSeriesWithGoal(Series series, TeamColour teamColour)
    {
        if (teamColour == TeamColour.BLUE)
        {
            series.getGameScore().setBlueScore(series.getGameScore().getBlueScore() + 1);
        }
        if (teamColour == TeamColour.ORANGE)
        {
            series.getGameScore().setOrangeScore(series.getGameScore().getOrangeScore() + 1);
        }
    }

    public void uptickSeriesWithGame(Series series, TeamColour teamColour)
    {
        if (teamColour == TeamColour.BLUE)
        {
            series.getSeriesScore().setBlueScore(series.getSeriesScore().getBlueScore() + 1);
        }
        if (teamColour == TeamColour.ORANGE)
        {
            series.getSeriesScore().setOrangeScore(series.getSeriesScore().getOrangeScore() + 1);
        }
    }

    public String generateHeaderString(Series series)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("S");
        stringBuilder.append(series.getSeriesId());
        int dashesToAppend = ACCEPTABLE_CHAR_LENGTH - stringBuilder.length() - String.valueOf(series.getMessageCount()).length();
        for (int i = 0; i < dashesToAppend; i++)
        {
            stringBuilder.append("-");
        }
        stringBuilder.append("#");
        stringBuilder.append(series.getMessageCount());
        return stringBuilder.toString();
    }

    public String generateGameScoreString(Series series)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(series.getBlueTeam().getTeamName());
        int spacesToAppend = ACCEPTABLE_TEAM_LENGTH - stringBuilder.length();
        for (int i = 0; i < spacesToAppend; i++)
        {
            stringBuilder.append(" ");
        }
        stringBuilder.append(" :");
        stringBuilder.append(EmojiNumber.values()[series.getGameScore().getBlueScore()].toString().toLowerCase());
        stringBuilder.append(": - :");
        stringBuilder.append(EmojiNumber.values()[series.getGameScore().getOrangeScore()].toString().toLowerCase());
        stringBuilder.append(": ");
        for (int i = 0; i < spacesToAppend; i++)
        {
            stringBuilder.append(" ");
        }
        stringBuilder.append(series.getOrangeTeam().getTeamName());
        return stringBuilder.toString();
    }

    public String generateSeriesScoreString(Series series)
    {
        int targetLength = generateGameScoreString(series).length();

        // assume each circle is 3 spaces
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < series.getSeriesScore().getBlueScore(); i++)
        {
            stringBuilder.append(":blue_circle:");
        }
        for (int i = 0; i < (series.getBestOf() + 1) / 2 - series.getSeriesScore().getBlueScore(); i++)
        {
            stringBuilder.append(":black_circle:");
        }
        for (int i = 0; i < targetLength - ((series.getBestOf() + 1) * 3); i++)
        {
            stringBuilder.append(" ");
        }
        for (int i = 0; i < (series.getBestOf() + 1) / 2 - series.getSeriesScore().getOrangeScore(); i++)
        {
            stringBuilder.append(":black_circle:");
        }
        for (int i = 0; i < series.getSeriesScore().getOrangeScore(); i++)
        {
            stringBuilder.append(":orange_circle:");
        }
        return stringBuilder.toString();
    }
    public String generatePlayerString(Series series, TeamColour teamColour)
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(teamColour.toString());
        stringBuilder.append(" players: ");
        if (teamColour == TeamColour.BLUE)
        {
            stringBuilder.append(series.getBlueTeam().getPlayer1().getName());
            stringBuilder.append("; ");
            stringBuilder.append(series.getBlueTeam().getPlayer2().getName());
            stringBuilder.append("; ");
            stringBuilder.append(series.getBlueTeam().getPlayer3().getName());
        }
        if (teamColour == TeamColour.ORANGE)
        {
            stringBuilder.append(series.getOrangeTeam().getPlayer1().getName());
            stringBuilder.append("; ");
            stringBuilder.append(series.getOrangeTeam().getPlayer2().getName());
            stringBuilder.append("; ");
            stringBuilder.append(series.getOrangeTeam().getPlayer3().getName());
        }
        return stringBuilder.toString();
    }

    public String generateSeriesString(Series series)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(generateHeaderString(series));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(generateGameScoreString(series));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(generateSeriesScoreString(series));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(generatePlayerString(series, TeamColour.BLUE));
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append(generatePlayerString(series, TeamColour.ORANGE));
        stringBuilder.append(System.getProperty("line.separator"));
        return stringBuilder.toString();
    }

}
