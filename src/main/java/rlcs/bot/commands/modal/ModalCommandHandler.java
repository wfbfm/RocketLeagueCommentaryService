package rlcs.bot.commands.modal;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlcs.series.Series;
import rlcs.series.SeriesStringParser;
import rlcs.series.TeamColour;

public class ModalCommandHandler extends ListenerAdapter
{
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event)
    {
        try
        {
            switch (ModalType.valueOf(event.getModalId()))
            {
                case goalbluemodal:
                    handleGoalModalEvent(event, TeamColour.BLUE);
                    return;
                case goalorangemodal:
                    handleGoalModalEvent(event, TeamColour.ORANGE);
                    return;
                case gamemodal:
                    handleGameModalEvent(event);
                    return;
                case overtimemodal:
                    handleOvertimeModalEvent(event);
                    return;
                case commentmodal:
                    handleCommentModalEvent(event);
                    return;
            }
        }
        catch (IllegalArgumentException e)
        {
            event.reply("Unrecognised modal").setEphemeral(true).queue();
        }
    }

    private static void handleGoalModalEvent(@NotNull ModalInteractionEvent event, TeamColour teamColour)
    {
        Series series = getSeriesFromMessageAndUptickMessageCount(event.getMessage());

        String scorer = event.getValue("scorer").getAsString();
        String assister = event.getValue("assister").getAsString();
        String gameTime = event.getValue("gametime").getAsString();
        String commentary = event.getValue("commentary").getAsString();

        if (scorer == assister)
        {
            event.reply("The same player cannot score and assist the same goal").setEphemeral(true).queue();
            return;
        }
        // uptick the game score, set the overtime to false
        series.getGameScore().setTeamScore(series.getGameScore().getTeamScore(teamColour) + 1, teamColour);
        series.setOvertime(false);

        // Original holder message will be updated with series template string
        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);
        // Publish string will be posted in the commentary channel with additional attributes
        StringBuilder publishStringBuilder = createStringBuilderFromSeries(updatedSeriesTemplateString);

        String scorerName = parseGoalParticipant(teamColour, series, scorer);
        String assisterName = parseGoalParticipant(teamColour, series, assister);

        if (scorerName != null)
        {
            publishStringBuilder.append("⚽ **" + scorerName + "**     ");
            if (assisterName != null)
            {
                publishStringBuilder.append("🤝 **" + assisterName +"**     ");
            }
            if (gameTime != null)
            {
                publishStringBuilder.append("🕒 " + gameTime);
            }
        }

        publishStringBuilder.append(System.getProperty("line.separator"));

        if (commentary != null)
        {
            commentary = commentary.replace("[1]", "**" + series.getTeam(teamColour).getPlayer1().getName()+ "**");
            commentary = commentary.replace("[2]", "**" + series.getTeam(teamColour).getPlayer2().getName() + "**");
            commentary = commentary.replace("[3]", "**" + series.getTeam(teamColour).getPlayer3().getName() + "**");

            publishStringBuilder.append(commentary);
        }
        publishCommentaryMessage(event, publishStringBuilder);

        event.editMessage(updatedSeriesTemplateString).queue();
    }

    private static void handleGameModalEvent(@NotNull ModalInteractionEvent event)
    {
        Series series = getSeriesFromMessageAndUptickMessageCount(event.getMessage());

        int blueGameScore = series.getGameScore().getBlueScore();
        int orangeGameScore = series.getGameScore().getOrangeScore();

        // uptick series score
        if (blueGameScore > orangeGameScore)
        {
            series.getSeriesScore().setBlueScore(series.getSeriesScore().getBlueScore() + 1);
        }
        else
        {
            series.getSeriesScore().setOrangeScore(series.getSeriesScore().getOrangeScore() + 1);
        }

        // reset game score
        series.setOvertime(false);
        series.getGameScore().setBlueScore(0);
        series.getGameScore().setOrangeScore(0);

        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);
        StringBuilder publishStringBuilder = createStringBuilderFromSeries(updatedSeriesTemplateString);

        extractAndFormatCommentary(event, series, publishStringBuilder);

        publishCommentaryMessage(event, publishStringBuilder);

        event.editMessage(updatedSeriesTemplateString).queue();
    }

    private static void handleOvertimeModalEvent(@NotNull ModalInteractionEvent event)
    {
        Series series = getSeriesFromMessageAndUptickMessageCount(event.getMessage());

        series.setOvertime(true);
        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);
        StringBuilder publishStringBuilder = createStringBuilderFromSeries(updatedSeriesTemplateString);

        extractAndFormatCommentary(event, series, publishStringBuilder);

        publishCommentaryMessage(event, publishStringBuilder);

        event.editMessage(updatedSeriesTemplateString).queue();
    }

    private static void handleCommentModalEvent(@NotNull ModalInteractionEvent event)
    {
        Series series = getSeriesFromMessageAndUptickMessageCount(event.getMessage());

        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);
        StringBuilder publishStringBuilder = createStringBuilderFromSeries(updatedSeriesTemplateString);

        extractAndFormatCommentary(event, series, publishStringBuilder);

        publishCommentaryMessage(event, publishStringBuilder);

        event.editMessage(updatedSeriesTemplateString).queue();
    }

    private static Series getSeriesFromMessageAndUptickMessageCount(final Message message)
    {
        final String originalMessage = message.getContentRaw();
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);
        series.setMessageCount(series.getMessageCount() + 1);
        return series;
    }

    @NotNull
    private static StringBuilder createStringBuilderFromSeries(final String updatedSeriesTemplateString)
    {
        String[] lines = updatedSeriesTemplateString.split(System.getProperty("line.separator"));
        StringBuilder publishStringBuilder = new StringBuilder(System.getProperty("line.separator"));
        publishStringBuilder.append(System.getProperty("line.separator"));
        // only loop over certain lines because we don't want to publish player names/overtime flag in the final commentary
        for (int i = 0; i < 3; i++)
        {
            publishStringBuilder.append(lines[i]);
            publishStringBuilder.append(System.getProperty("line.separator"));
        }
        return publishStringBuilder;
    }

    @Nullable
    private static String parseGoalParticipant(TeamColour teamColour, Series series, String participant)
    {
        String participantName = null;
        switch (participant)
        {
            case "1":
                participantName = series.getTeam(teamColour).getPlayer1().getName();
                break;
            case "2":
                participantName = series.getTeam(teamColour).getPlayer2().getName();
                break;
            case "3":
                participantName = series.getTeam(teamColour).getPlayer3().getName();
                break;
            default:
                break;
        }
        return participantName;
    }

    private static void extractAndFormatCommentary(@NotNull ModalInteractionEvent event, Series series, StringBuilder publishStringBuilder)
    {
        String commentary = event.getValue("commentary").getAsString();

        String bluePlayer1 = series.getBlueTeam().getPlayer1().getName();
        String bluePlayer2 = series.getBlueTeam().getPlayer2().getName();
        String bluePlayer3 = series.getBlueTeam().getPlayer3().getName();

        String orangePlayer1 = series.getOrangeTeam().getPlayer1().getName();
        String orangePlayer2 = series.getOrangeTeam().getPlayer2().getName();
        String orangePlayer3 = series.getOrangeTeam().getPlayer3().getName();

        publishStringBuilder.append(System.getProperty("line.separator"));

        if (commentary != null)
        {
            commentary = commentary.replace("[b1]", "**" + bluePlayer1+ "**");
            commentary = commentary.replace("[b2]", "**" + bluePlayer2 + "**");
            commentary = commentary.replace("[b3]", "**" + bluePlayer3 + "**");

            commentary = commentary.replace("[o1]", "**" + orangePlayer1+ "**");
            commentary = commentary.replace("[o2]", "**" + orangePlayer2 + "**");
            commentary = commentary.replace("[o3]", "**" + orangePlayer3 + "**");

            publishStringBuilder.append(commentary);
        }
    }

    private static void publishCommentaryMessage(@NotNull ModalInteractionEvent event, StringBuilder publishStringBuilder)
    {
        // TODO: Replace hardcoded channel id with environment variable
        TextChannel publishChannel = event.getJDA().getTextChannelById(1049857570581516348L);
        if (publishChannel != null)
        {
            publishChannel.sendMessage(publishStringBuilder.toString()).queue();
        }
    }
}