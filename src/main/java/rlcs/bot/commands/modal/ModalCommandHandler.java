package rlcs.bot.commands.modal;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlcs.bot.commands.twitch.TwitchClipper;
import rlcs.bot.commands.twitch.TwitchStatus;
import rlcs.series.Score;
import rlcs.series.Series;
import rlcs.series.SeriesStringParser;
import rlcs.series.TeamColour;

public class ModalCommandHandler extends ListenerAdapter
{
    private static TwitchClipper twitchClipper;
    private static final String COMMENTARY_CHANNEL = System.getenv("COMMENTARY_CHANNEL");
    public ModalCommandHandler(final TwitchClipper twitchClipper)
    {
        this.twitchClipper = twitchClipper;
    }

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
                case editscoremodal:
                    handleEditScoreModalEvent(event);
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
        String twitchClipId = getAndResetTwitchClipIdForPublishing(series);
        boolean hasCommentatorChanged = hasCommentatorChanged(event, series);
        // Defer a reply to the event - this lets people know the bot is busy
        event.deferReply().setEphemeral(true).queue();

        String scorer = event.getValue("scorer").getAsString();
        String assister = event.getValue("assister").getAsString();
        String gameTime = event.getValue("gametime").getAsString();
        String commentary = event.getValue("commentary").getAsString();

        if (scorer.equals(assister))
        {
            event.getHook().sendMessage("The same player cannot score and assist a goal").setEphemeral(true).queue();
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
        // linebreak between goal details and commentary
        publishStringBuilder.append(System.getProperty("line.separator"));

        if (commentary != null)
        {
            commentary = commentary.replace("[1]", "**" + series.getTeam(teamColour).getPlayer1().getName()+ "**");
            commentary = commentary.replace("[2]", "**" + series.getTeam(teamColour).getPlayer2().getName() + "**");
            commentary = commentary.replace("[3]", "**" + series.getTeam(teamColour).getPlayer3().getName() + "**");

            publishStringBuilder.append(commentary);
        }
        editAndPublishFinalMessages(event, twitchClipId, updatedSeriesTemplateString, publishStringBuilder, hasCommentatorChanged, series.getCommentator());
    }

    private static void handleGameModalEvent(@NotNull ModalInteractionEvent event)
    {
        Series series = getSeriesFromMessageAndUptickMessageCount(event.getMessage());
        String twitchClipId = getAndResetTwitchClipIdForPublishing(series);
        boolean hasCommentatorChanged = hasCommentatorChanged(event, series);
        // Defer a reply to the event - this lets people know the bot is busy
        event.deferReply().setEphemeral(true).queue();

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
        editAndPublishFinalMessages(event, twitchClipId, updatedSeriesTemplateString, publishStringBuilder, hasCommentatorChanged, series.getCommentator());
    }

    private static void handleOvertimeModalEvent(@NotNull ModalInteractionEvent event)
    {
        Series series = getSeriesFromMessageAndUptickMessageCount(event.getMessage());
        String twitchClipId = getAndResetTwitchClipIdForPublishing(series);
        boolean hasCommentatorChanged = hasCommentatorChanged(event, series);
        // Defer a reply to the event - this lets people know the bot is busy
        event.deferReply().setEphemeral(true).queue();

        series.setOvertime(true);
        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);

        StringBuilder publishStringBuilder = createStringBuilderFromSeries(updatedSeriesTemplateString);

        extractAndFormatCommentary(event, series, publishStringBuilder);
        editAndPublishFinalMessages(event, twitchClipId, updatedSeriesTemplateString, publishStringBuilder, hasCommentatorChanged, series.getCommentator());
    }

    private static void handleCommentModalEvent(@NotNull ModalInteractionEvent event)
    {
        Series series = getSeriesFromMessageAndUptickMessageCount(event.getMessage());
        String twitchClipId = getAndResetTwitchClipIdForPublishing(series);
        boolean hasCommentatorChanged = hasCommentatorChanged(event, series);
        // Defer a reply to the event - this lets people know the bot is busy
        event.deferReply().setEphemeral(true).queue();

        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);
        StringBuilder publishStringBuilder = createStringBuilderFromSeries(updatedSeriesTemplateString);

        extractAndFormatCommentary(event, series, publishStringBuilder);
        editAndPublishFinalMessages(event, twitchClipId, updatedSeriesTemplateString, publishStringBuilder, hasCommentatorChanged, series.getCommentator());
    }

    private static void handleEditScoreModalEvent(@NotNull ModalInteractionEvent event)
    {
        Series series = getSeriesFromMessageAndUptickMessageCount(event.getMessage());
        // Defer a reply to the event - this lets people know the bot is busy
        event.deferReply().setEphemeral(true).queue();
        try {
            int bestOf = Integer.parseInt(event.getValue("bestof").getAsString());
            int blueSeriesScore = Integer.parseInt(event.getValue("blueseriesscore").getAsString());
            int orangeSeriesScore = Integer.parseInt(event.getValue("orangeseriesscore").getAsString());
            int blueGameScore = Integer.parseInt(event.getValue("bluegamescore").getAsString());
            int orangeGameScore = Integer.parseInt(event.getValue("orangegamescore").getAsString());

            series.setBestOf(bestOf);
            Score seriesScore = series.getSeriesScore();
            seriesScore.setBlueScore(blueSeriesScore);
            seriesScore.setOrangeScore(orangeSeriesScore);
            series.setSeriesScore(seriesScore);
            Score gameScore = series.getGameScore();
            gameScore.setBlueScore(blueGameScore);
            gameScore.setOrangeScore(orangeGameScore);
            series.setGameScore(gameScore);
        } catch (NumberFormatException e)
        {
            event.getHook().sendMessage("Invalid score attributes passed through Edit Score - these must be integers").setEphemeral(true).queue();
            return;
        }
        if (series.getBestOf() % 2 == 0)
        {
            event.getHook().sendMessage("Best Of can only be an ODD number - i.e. not " + series.getBestOf()).setEphemeral(true).queue();
            return;
        }
        int maxScore = (series.getBestOf() + 1) / 2;
        if (series.getSeriesScore().getBlueScore() > maxScore || series.getSeriesScore().getOrangeScore() > maxScore)
        {
            event.getHook().sendMessage("In a best of " + series.getBestOf() + ", the max series score allowed is " + maxScore).setEphemeral(true).queue();
            return;
        }

        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);
        // Edits the template message in the command channel
        event.getMessage().editMessage(updatedSeriesTemplateString).queue();
        // Deletes the deferred reply, as the bot is no longer busy
        event.getHook().deleteOriginal().queue();
    }

    private static Series getSeriesFromMessageAndUptickMessageCount(final Message message)
    {
        final String originalMessage = message.getContentRaw();
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);
        series.setMessageCount(series.getMessageCount() + 1);
        return series;
    }

    private static String getAndResetTwitchClipIdForPublishing(final Series series)
    {
        final String twitchClipId = series.getTwitchClipId();
        series.setTwitchClipId("None");
        return twitchClipId;
    }

    @NotNull
    private static StringBuilder createStringBuilderFromSeries(final String updatedSeriesTemplateString)
    {
        String[] lines = updatedSeriesTemplateString.split(System.getProperty("line.separator"));
        // "_ _" gives a line break at the top of the message, when displayed in Discord
        StringBuilder publishStringBuilder = new StringBuilder("_ _");
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

        if (commentary != null)
        {
            commentary = commentary.replace("[b1]", "**" + bluePlayer1+ "**");
            commentary = commentary.replace("[b2]", "**" + bluePlayer2 + "**");
            commentary = commentary.replace("[b3]", "**" + bluePlayer3 + "**");

            commentary = commentary.replace("[o1]", "**" + orangePlayer1+ "**");
            commentary = commentary.replace("[o2]", "**" + orangePlayer2 + "**");
            commentary = commentary.replace("[o3]", "**" + orangePlayer3 + "**");

            commentary = commentary.replace("[B1]", "**" + bluePlayer1+ "**");
            commentary = commentary.replace("[B2]", "**" + bluePlayer2 + "**");
            commentary = commentary.replace("[B3]", "**" + bluePlayer3 + "**");

            commentary = commentary.replace("[O1]", "**" + orangePlayer1+ "**");
            commentary = commentary.replace("[O2]", "**" + orangePlayer2 + "**");
            commentary = commentary.replace("[O3]", "**" + orangePlayer3 + "**");

            publishStringBuilder.append(commentary);
        }
    }

    private static boolean publishCommentaryMessage(@NotNull ModalInteractionEvent event, StringBuilder publishStringBuilder, String twitchClipId)
    {
        boolean failedDuringPublishing = false;
        // Check if we need to publish a twitch clip
        if (!twitchClipId.equals("None"))
        {
            String twitchClipUrl = twitchClipper.getUrlFromClipId(twitchClipId);
            if (twitchClipUrl.equals(TwitchStatus.UNABLE_TO_FIND_CLIP.name()))
            {
                event.getHook().sendMessage("Oops - I wasn't able to find the clip ID: " + twitchClipId +
                        "  The streamer may not allow me to create clips, or the clipping service is unavailable.").setEphemeral(true).queue();
                failedDuringPublishing = true;
            }
            else
            {
                publishStringBuilder.append(System.getProperty("line.separator"));
                publishStringBuilder.append(twitchClipUrl);
            }
        }

        TextChannel publishChannel = event.getJDA().getTextChannelById(COMMENTARY_CHANNEL);
        if (publishChannel != null)
        {
            publishChannel.sendMessage(publishStringBuilder.toString()).queue();
        }
        return failedDuringPublishing;
    }

    private static void editAndPublishFinalMessages(@NotNull ModalInteractionEvent event, String twitchClipId, String updatedSeriesTemplateString, StringBuilder publishStringBuilder, boolean hasCommentatorChanged, String commentator)
    {
        // Edits the template message in the command channel
        event.getMessage().editMessage(updatedSeriesTemplateString).queue();
        // Publishes message in commentary channel
        StringBuilder publishStringWithCommentator = new StringBuilder();
        if (hasCommentatorChanged)
        {
            publishStringWithCommentator.append("```Commentary for series taken over by " + commentator + "```");
        }
        publishStringWithCommentator.append(publishStringBuilder);
        boolean failedDuringPublishing = publishCommentaryMessage(event, publishStringWithCommentator, twitchClipId);
        // Deletes the deferred reply, as the bot is no longer busy - only if error message hasn't been returned
        if (!failedDuringPublishing)
        {
            event.getHook().deleteOriginal().queue();
        }
    }

    private static boolean hasCommentatorChanged(@NotNull ModalInteractionEvent event, Series series)
    {
        String oldCommentator = series.getCommentator();
        String newCommentator = event.getInteraction().getMember().getUser().getName();

        series.setCommentator(newCommentator);
        return !oldCommentator.equals(newCommentator);
    }

}