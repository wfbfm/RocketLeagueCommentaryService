package rlcs.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rlcs.series.*;

public class BotCommands extends ListenerAdapter
{
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals("createseries"))
        {
            event.deferReply().queue();

            String teamBlueOpt = event.getOption("teamblue").getAsString();
            String teamOrangeOpt = event.getOption("teamorange").getAsString();
            int bestOfOpt = event.getOption("bestof").getAsInt();
            int seriesIdOpt = event.getOption("seriesid").getAsInt();
            int messageCountOpt = event.getOption("messagecount").getAsInt();
            int blueSeriesScoreOpt = event.getOption("blueseriesscore").getAsInt();
            int orangeSeriesScoreOpt = event.getOption("orangeseriesscore").getAsInt();
            int blueGameScoreOpt = event.getOption("bluegamescore").getAsInt();
            int orangeGameScoreOpt = event.getOption("orangegamescore").getAsInt();
            String bluePlayer1Opt = event.getOption("blueplayer1").getAsString();
            String bluePlayer2Opt = event.getOption("blueplayer2").getAsString();
            String bluePlayer3Opt = event.getOption("blueplayer3").getAsString();
            String orangePlayer1Opt = event.getOption("orangeplayer1").getAsString();
            String orangePlayer2Opt = event.getOption("orangeplayer2").getAsString();
            String orangePlayer3Opt = event.getOption("orangeplayer3").getAsString();

            Team blueTeam = new Team(teamBlueOpt,
                    new Player(bluePlayer1Opt),
                    new Player(bluePlayer2Opt),
                    new Player(bluePlayer3Opt),
                    TeamColour.BLUE);

            Team orangeTeam = new Team(teamOrangeOpt,
                    new Player(orangePlayer1Opt),
                    new Player(orangePlayer2Opt),
                    new Player(orangePlayer3Opt),
                    TeamColour.ORANGE);

            Series series = new Series(seriesIdOpt,
                    messageCountOpt,
                    new Score(blueGameScoreOpt, orangeGameScoreOpt),
                    new Score(blueSeriesScoreOpt, orangeSeriesScoreOpt),
                    blueTeam,
                    orangeTeam,
                    bestOfOpt
                    );

            event.getHook().sendMessage(new SeriesActions().generateSeriesString(series)).queue();
        }
    }
}
