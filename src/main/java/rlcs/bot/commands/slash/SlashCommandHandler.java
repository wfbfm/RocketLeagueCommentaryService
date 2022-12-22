package rlcs.bot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rlcs.bot.commands.button.ButtonType;
import rlcs.series.*;

public class SlashCommandHandler extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        // TODO: Replace this with environment variable.  Add check on role or leave permissioning Discord-server side?
        if (event.getChannel().getIdLong() != 1049856241754705950L)
        {
            event.reply("Commentary commands can only be used in the #rlcs-mission-control channel " +
                    "by users with the Commentator role").setEphemeral(true).queue();
            return;
        }

        if (event.getName().equals(String.valueOf(SlashType.createseries)))
        {
            handleCreateSeriesEvent(event);
        }
    }

    private static void handleCreateSeriesEvent(@NotNull SlashCommandInteractionEvent event)
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
                bestOfOpt,
                false
        );

        event.getHook().sendMessage(SeriesStringParser.generateSeriesString(series))
                .setActionRow(
                        Button.primary(ButtonType.goalblue.name(), "⚽ " + blueTeam.getTeamName()),
                        Button.danger(ButtonType.goalorange.name(), "⚽ " + orangeTeam.getTeamName()),
                        Button.success(ButtonType.game.name(), "🏁 Game"),
                        Button.secondary(ButtonType.overtime.name(), "🕒 Overtime"),
                        Button.secondary(ButtonType.comment.name(), "💬 Comment"))
                .queue();
    }
}
