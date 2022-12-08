package rlcs.series;

public class Team
{
    private Player player1;
    private Player player2;
    private Player player3;

    private TeamColour teamColour;
    private String teamName;

    public Team(String teamName, Player player1, Player player2, Player player3, TeamColour teamColour)
    {
        if (teamName.length() > 16)
        {
            this.teamName = teamName.substring(0, 16);
        }
        else
        {
            this.teamName = teamName;
        }
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.teamColour = teamColour;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public TeamColour getTeamColour()
    {
        return teamColour;
    }

    public void setTeamColour(TeamColour teamColour)
    {
        this.teamColour = teamColour;
    }

    public Player getPlayer1()
    {
        return player1;
    }

    public void setPlayer1(final Player player1)
    {
        this.player1 = player1;
    }

    public Player getPlayer2()
    {
        return player2;
    }

    public void setPlayer2(final Player player2)
    {
        this.player2 = player2;
    }

    public Player getPlayer3()
    {
        return player3;
    }

    public void setPlayer3(final Player player3)
    {
        this.player3 = player3;
    }
}
