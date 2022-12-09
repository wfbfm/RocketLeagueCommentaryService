package rlcs.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rlcs.series.*;

public class BotCommands extends ListenerAdapter
{
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
        if (event.getButton().getId().equals("goal"))
        {
            handleGoalEvent(event);
        }
        if (event.getButton().getId().equals("game"))
        {
            handleGameEvent(event);
        }
        if (event.getButton().getId().equals("overtime"))
        {
            handleOvertimeEvent(event);
        }
        if (event.getButton().getId().equals("edit"))
        {
            handleEditEvent(event);
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
                bestOfOpt
                );

        event.getHook().sendMessage(new SeriesActions().generateSeriesString(series))
                .setActionRow(
                        Button.secondary("comment", "💬 Comment"),
                        Button.primary("goal", "⚽ Goal"),
                        Button.success("game", "🏁 Game"),
                        Button.danger("overtime", "🕒 Overtime"),
                        Button.secondary("edit", "📝 Edit"))
                .queue();
    }

    private static void handleCommentEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        event.editMessage(originalMessage + System.getProperty("line.separator") + " - hi the comment button was pressed").queue();
    }

    private static void handleGoalEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        event.editMessage(originalMessage + System.getProperty("line.separator") + " - hi the goal button was pressed").queue();
    }

    private static void handleGameEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        event.editMessage(originalMessage + System.getProperty("line.separator") + " - hi the game button was pressed").queue();
    }

    private static void handleOvertimeEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        event.editMessage(originalMessage + System.getProperty("line.separator") + " - hi the overtime button was pressed").queue();
    }

    private static void handleEditEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        event.editMessage(originalMessage + System.getProperty("line.separator") + " - hi the edit button was pressed").queue();
    }
}