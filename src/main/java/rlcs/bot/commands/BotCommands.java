package rlcs.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rlcs.series.*;

public class BotCommands extends ListenerAdapter
{
    private static SeriesActions seriesActions = new SeriesActions();
    public BotCommands()
    {

    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals("createseries"))
        {
            handleCreateSeriesEvent(event);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {
        if (event.getButton().getId().equals("comment"))
        {
            handleCommentEvent(event);
        }
        if (event.getButton().getId().equals("goalblue"))
        {
            handleGoalBlueEvent(event);
        }
        if (event.getButton().getId().equals("goalorange"))
        {
            handleGoalOrangeEvent(event);
        }
        if (event.getButton().getId().equals("game"))
        {
            handleGameEvent(event);
        }
        if (event.getButton().getId().equals("overtime"))
        {
            handleOvertimeEvent(event);
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

        event.getHook().sendMessage(new SeriesActions().generateSeriesString(series))
                .setActionRow(
                        Button.primary("goalblue", "⚽ " + blueTeam.getTeamName()),
                        Button.danger("goalorange", "⚽ " + orangeTeam.getTeamName()),
                        Button.success("game", "🏁 Game"),
                        Button.secondary("overtime", "🕒 Overtime"),
                        Button.secondary("comment", "💬 Comment"))
                .queue();
    }

    private static void handleCommentEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);
        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesString = seriesActions.generateSeriesString(series);
        event.editMessage(updatedSeriesString).queue();
    }

    private static void handleGoalBlueEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);
        series.getGameScore().setBlueScore(series.getGameScore().getBlueScore() + 1);
        if (series.getGameScore().getBlueScore() >= 10)
        {
            event.reply("Sorry - only single digit goals.  Please raise a Jira™️").setEphemeral(true).queue();
            return;
        }
        series.setOvertime(false);
        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesString = seriesActions.generateSeriesString(series);
        event.editMessage(updatedSeriesString).queue();
    }

    private static void handleGoalOrangeEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);
        series.getGameScore().setOrangeScore(series.getGameScore().getOrangeScore() + 1);
        if (series.getGameScore().getOrangeScore() >= 10)
        {
            event.reply("Sorry - only single digit goals.  Please raise a Jira™️").setEphemeral(true).queue();
            return;
        }
        series.setOvertime(false);
        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesString = seriesActions.generateSeriesString(series);
        event.editMessage(updatedSeriesString).queue();
    }

    private static void handleGameEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);

        int blueGameScore = series.getGameScore().getBlueScore();
        int orangeGameScore = series.getGameScore().getOrangeScore();
        int maxScore = (series.getBestOf() + 1) / 2;

        if (blueGameScore == orangeGameScore)
        {
            event.reply("Scores are level - cannot end game").setEphemeral(true).queue();
            return;
        }
        if (series.getSeriesScore().getBlueScore() >= maxScore || series.getSeriesScore().getOrangeScore() >= maxScore)
        {
            event.reply("Series already won - cannot end game").setEphemeral(true).queue();
            return;
        }
        if (blueGameScore > orangeGameScore)
        {
            series.getSeriesScore().setBlueScore(series.getSeriesScore().getBlueScore() + 1);
        }
        else
        {
            series.getSeriesScore().setOrangeScore(series.getSeriesScore().getOrangeScore() + 1);
        }
        series.setMessageCount(series.getMessageCount() + 1);
        series.setOvertime(false);
        series.getGameScore().setBlueScore(0);
        series.getGameScore().setOrangeScore(0);
        String updatedSeriesString = seriesActions.generateSeriesString(series);
        event.editMessage(updatedSeriesString).queue();
    }

    private static void handleOvertimeEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);

        int blueGameScore = series.getGameScore().getBlueScore();
        int orangeGameScore = series.getGameScore().getOrangeScore();

        if (blueGameScore != orangeGameScore)
        {
            event.reply("Scores are not level - overtime not possible").setEphemeral(true).queue();
            return;
        }

        series.setOvertime(true);
        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesString = seriesActions.generateSeriesString(series);
        event.editMessage(updatedSeriesString).queue();
    }
}