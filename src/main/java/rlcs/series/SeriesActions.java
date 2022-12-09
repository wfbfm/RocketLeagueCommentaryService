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
        stringBuilder.append("**");
        stringBuilder.append(series.getBlueTeam().getTeamName());
        stringBuilder.append("**");
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
        stringBuilder.append("**");
        stringBuilder.append(series.getOrangeTeam().getTeamName());
        stringBuilder.append("**");
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
        stringBuilder.append(": ");
        if (teamColour == TeamColour.BLUE)
        {
            stringBuilder.append("[1] ");
            stringBuilder.append(series.getBlueTeam().getPlayer1().getName());
            stringBuilder.append("; ");
            stringBuilder.append("[2] ");
            stringBuilder.append(series.getBlueTeam().getPlayer2().getName());
            stringBuilder.append("; ");
            stringBuilder.append("[3] ");
            stringBuilder.append(series.getBlueTeam().getPlayer3().getName());
        }
        if (teamColour == TeamColour.ORANGE)
        {
            stringBuilder.append("[1] ");
            stringBuilder.append(series.getOrangeTeam().getPlayer1().getName());
            stringBuilder.append("; ");
            stringBuilder.append("[2] ");
            stringBuilder.append(series.getOrangeTeam().getPlayer2().getName());
            stringBuilder.append("; ");
            stringBuilder.append("[3] ");
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

    public Series parseSeriesFromString(String string)
    {
        String[] lines = string.split(System.getProperty("line.separator"));
        String headerString = lines[0];
        String gameScoreString = lines[1].replace("*","");
        String seriesScoreString = lines[2];
        String bluePlayers = lines[3];
        String orangePlayers = lines[4];

        // parse header
        String[] parsedHeaderString = headerString.split("-");
        String seriesIdString = parsedHeaderString[0];
        String messageCountString = parsedHeaderString[parsedHeaderString.length - 1];
        int seriesId = Integer.parseInt(seriesIdString.substring(1, seriesIdString.length()));
        int messageCount = Integer.parseInt(messageCountString.substring(1, messageCountString.length()));

        // parse gameScore
        gameScoreString = gameScoreString.replace("-", "");
        String[] parsedGameScoreString = gameScoreString.split(":");
        String blueTeamName = parsedGameScoreString[0].trim();
        String orangeTeamName = parsedGameScoreString[parsedGameScoreString.length - 1].trim();

        int blueGameScore = EmojiNumber.valueOf(parsedGameScoreString[1].toUpperCase().trim()).ordinal();
        int orangeGameScore = EmojiNumber.valueOf(parsedGameScoreString[3].toUpperCase().trim()).ordinal();

        // parse seriesScore
        int blueSeriesScore = seriesScoreString.split("blue_circle").length - 1;
        int orangeSeriesScore = seriesScoreString.split("orange_circle").length - 1;
        int bestOf = seriesScoreString.split("circle").length - 2;  // -2 because eg bestOf7 has 8 circles

        // parse players
        bluePlayers = bluePlayers.split(":")[1];
        String[] parsedBluePlayers = bluePlayers.split(";");

        String bluePlayer1 = parsedBluePlayers[0].replace("[1] ", "").trim();
        String bluePlayer2 = parsedBluePlayers[1].replace("[2] ", "").trim();
        String bluePlayer3 = parsedBluePlayers[2].replace("[3] ", "").trim();

        orangePlayers = orangePlayers.split(":")[1];
        String[] parsedOrangePlayers = orangePlayers.split(";");
        String orangePlayer1 = parsedOrangePlayers[0].replace("[1] ", "").trim();
        String orangePlayer2 = parsedOrangePlayers[1].replace("[2] ", "").trim();
        String orangePlayer3 = parsedOrangePlayers[2].replace("[3] ", "").trim();

        Team blueTeam = new Team(blueTeamName,
                new Player(bluePlayer1),
                new Player(bluePlayer2),
                new Player(bluePlayer3),
                TeamColour.BLUE);

        Team orangeTeam = new Team(orangeTeamName,
                new Player(orangePlayer1),
                new Player(orangePlayer2),
                new Player(orangePlayer3),
                TeamColour.ORANGE);

        Series series = new Series(seriesId,
                messageCount,
                new Score(blueGameScore, orangeGameScore),
                new Score(blueSeriesScore, orangeSeriesScore),
                blueTeam,
                orangeTeam,
                bestOf
        );

        return series;
    }

}
