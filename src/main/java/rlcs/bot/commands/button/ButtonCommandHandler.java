package rlcs.bot.commands.button;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import rlcs.bot.commands.modal.ModalType;
import rlcs.bot.commands.twitch.TwitchClipper;
import rlcs.bot.commands.twitch.TwitchStatus;
import rlcs.series.Series;
import rlcs.series.SeriesStringParser;
import rlcs.series.TeamColour;

public class ButtonCommandHandler extends ListenerAdapter {

    private static TwitchClipper twitchClipper;

    public ButtonCommandHandler(final TwitchClipper twitchClipper)
    {
        this.twitchClipper = twitchClipper;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event)
    {
        try {
            switch (ButtonType.valueOf(event.getButton().getId()))
            {
                case goalblue:
                    handleGoalEvent(event, TeamColour.BLUE);
                    return;
                case goalorange:
                    handleGoalEvent(event, TeamColour.ORANGE);
                    return;
                case game:
                    handleGameEvent(event);
                    return;
                case overtime:
                    handleOvertimeEvent(event);
                    return;
                case comment:
                    handleCommentEvent(event);
                    return;
                case twitchclip:
                    handleTwitchClipEvent(event);
                    return;
                case removetwitchclip:
                    handleRemoveTwitchClipEvent(event);
                    return;
                case editscore:
                    handleEditScoreEvent(event);
                    return;
            }
        } catch (IllegalArgumentException e) {
            event.reply("Unrecognised button press").setEphemeral(true).queue();
        }
    }

    private static void handleGoalEvent(@NotNull ButtonInteractionEvent event, TeamColour teamColour)
    {
        Series series = getSeriesFromMessage(event.getMessage());

        String playerNames = createPlaceholderPlayerNameLabelForOneTeam(teamColour, series);

        TextInput scorer = TextInput.create("scorer", "⚽ Scorer - Enter Player ID per below", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(1)
                .setPlaceholder(playerNames)
                .setRequired(true)
                .build();

        TextInput assister = TextInput.create("assister", "🤝 Assist - Enter Player ID per below, or 0", TextInputStyle.SHORT)
                .setMaxLength(1)
                .setPlaceholder(playerNames)
                .setRequired(false)
                .build();

        TextInput gameTime = TextInput.create("gametime", "🕒 Time in game", TextInputStyle.SHORT)
                .setMaxLength(8)
                .setPlaceholder("0:00")
                .setRequired(false)
                .build();

        TextInput commentary = TextInput.create("commentary", "💬 Comment (eg \"[1]\" is replaced by player)", TextInputStyle.PARAGRAPH)
                .setMinLength(5)
                .setMaxLength(500)
                .setRequired(false)
                .build();

        String modalType;
        if (teamColour == TeamColour.BLUE)
        {
            modalType = ModalType.goalbluemodal.name();
        }
        else
        {
            modalType = ModalType.goalorangemodal.name();
        }

        Modal modal = Modal.create(modalType, "Goal Scored by " + series.getTeam(teamColour).getTeamName())
                .addActionRows(ActionRow.of(scorer), ActionRow.of(assister), ActionRow.of(gameTime), ActionRow.of(commentary))
                .build();
        event.replyModal(modal).queue();
    }

    private static void handleGameEvent(@NotNull ButtonInteractionEvent event)
    {
        Series series = getSeriesFromMessage(event.getMessage());

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
        String gameWinner = null;
        if (blueGameScore > orangeGameScore)
        {
            gameWinner = series.getBlueTeam().getTeamName();
        }
        else
        {
            gameWinner = series.getOrangeTeam().getTeamName();
        }

        String playerNames = createPlaceholderPlayerNameLabel(series);

        TextInput commentary = TextInput.create("commentary", "💬 Comment (\"[b1]\" blue p1, \"[o1]\" orange etc)", TextInputStyle.PARAGRAPH)
                .setMinLength(5)
                .setMaxLength(500)
                .setPlaceholder(playerNames)
                .setRequired(false)
                .build();

        Modal modal = Modal.create(ModalType.gamemodal.name(), "Game Victory: " + gameWinner)
                .addActionRows(ActionRow.of(commentary))
                .build();

        event.replyModal(modal).queue();

    }

    private static void handleOvertimeEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

        int blueGameScore = series.getGameScore().getBlueScore();
        int orangeGameScore = series.getGameScore().getOrangeScore();

        if (blueGameScore != orangeGameScore)
        {
            event.reply("Scores are not level - overtime not possible").setEphemeral(true).queue();
            return;
        }

        TextInput commentary = TextInput.create("commentary", "💬 Comment (\"[b1]\" blue p1, \"[o1]\" orange etc)", TextInputStyle.PARAGRAPH)
                .setMinLength(5)
                .setMaxLength(500)
                .setValue("🚨 🕒 OVERTIME 🕒 🚨")
                .setRequired(false)
                .build();

        Modal modal = Modal.create(ModalType.overtimemodal.name(), "Overtime!")
                .addActionRows(ActionRow.of(commentary))
                .build();

        event.replyModal(modal).queue();
    }

    private static void handleCommentEvent(@NotNull ButtonInteractionEvent event)
    {
        Series series = getSeriesFromMessage(event.getMessage());

        String playerNames = createPlaceholderPlayerNameLabel(series);

        TextInput commentary = TextInput.create("commentary", "💬 Comment (\"[b1]\" blue p1, \"[o1]\" orange etc)", TextInputStyle.PARAGRAPH)
                .setMinLength(5)
                .setMaxLength(500)
                .setPlaceholder(playerNames)
                .setRequired(true)
                .build();

        Modal modal = Modal.create(ModalType.commentmodal.name(), "Enter Commentary")
                .addActionRows(ActionRow.of(commentary))
                .build();

        event.replyModal(modal).queue();
    }

    private static void handleTwitchClipEvent(@NotNull ButtonInteractionEvent event)
    {
        Series series = getSeriesFromMessage(event.getMessage());
        // Defer a reply to this event - as clip creation takes time
        event.deferReply().setEphemeral(true).queue();

        if (series.getTwitchBroadcasterId().equals(TwitchStatus.TWITCH_USER_NOT_FOUND.name()))
        {
            event.getHook().sendMessage("Sorry - the twitch user of this series " + series.getTwitchName() + " wasn't recognised, so I can't create a clip!").setEphemeral(true).queue();
            return;
        }

        String twitchClipId;
        try {
            twitchClipId = twitchClipper.createClipAndReturnClipId(series.getTwitchBroadcasterId());
        } catch (RuntimeException e)
        {
            event.getHook().sendMessage("Sorry - I was unable to create a Twitch clip for " + series.getTwitchName() + "!  " +
                    "The channel " + series.getTwitchName() + " may not support clips - or clipping may be allowed only for followers/subscribers").setEphemeral(true).queue();
            return;
        }

        if (twitchClipId.equals(TwitchStatus.UNABLE_TO_CREATE_CLIP.name()))
        {
            event.getHook().sendMessage("Sorry - I was unable to create a Twitch clip for " + series.getTwitchName() + "!  " +
                    "The channel " + series.getTwitchName() + " may not support clips - or clipping may be allowed only for followers/subscribers").setEphemeral(true).queue();
            return;
        }

        // uptick the series twitch clip ID, and edit the message
        series.setTwitchClipId(twitchClipId);
        event.getMessage().editMessage(SeriesStringParser.generateSeriesString(series)).queue();
        // delete the deferred reply
        event.getHook().deleteOriginal().queue();
    }

    private static void handleRemoveTwitchClipEvent(@NotNull ButtonInteractionEvent event)
    {
        Series series = getSeriesFromMessage(event.getMessage());
        // uptick the series with NONE twitch clip ID, and edit the message
        series.setTwitchClipId("None");
        event.editMessage(SeriesStringParser.generateSeriesString(series)).queue();
    }

    private static void handleEditScoreEvent(@NotNull ButtonInteractionEvent event)
    {
        // allows users to change orange/blue game/series scores, and bestOf amount
        Series series = getSeriesFromMessage(event.getMessage());

        TextInput bestOf = TextInput.create("bestof", "Series: Best of <X> Games", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(1)
                .setValue(String.valueOf(series.getBestOf()))
                .setRequired(true)
                .build();

        TextInput blueSeriesScore = TextInput.create("blueseriesscore", series.getBlueTeam().getTeamName() + " SERIES Score", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(1)
                .setValue(String.valueOf(series.getSeriesScore().getBlueScore()))
                .setRequired(true)
                .build();

        TextInput orangeSeriesScore = TextInput.create("orangeseriesscore", series.getOrangeTeam().getTeamName() + " SERIES Score", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(1)
                .setValue(String.valueOf(series.getSeriesScore().getOrangeScore()))
                .setRequired(true)
                .build();

        TextInput blueGameScore = TextInput.create("bluegamescore", series.getBlueTeam().getTeamName() + " GAME Score", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(2)
                .setValue(String.valueOf(series.getGameScore().getBlueScore()))
                .setRequired(true)
                .build();

        TextInput orangeGameScore = TextInput.create("orangegamescore", series.getOrangeTeam().getTeamName() + " GAME Score", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(2)
                .setValue(String.valueOf(series.getGameScore().getOrangeScore()))
                .setRequired(true)
                .build();

        Modal modal = Modal.create(ModalType.editscoremodal.name(), "Enter Series & Game Scores")
                .addActionRows(ActionRow.of(bestOf), ActionRow.of(blueSeriesScore), ActionRow.of(orangeSeriesScore),
                        ActionRow.of(blueGameScore), ActionRow.of(orangeGameScore))
                .build();

        event.replyModal(modal).queue();
    }

    private static Series getSeriesFromMessage(final Message message)
    {
        final String originalMessage = message.getContentRaw();
        return SeriesStringParser.parseSeriesFromString(originalMessage);
    }
    @NotNull
    private static String createPlaceholderPlayerNameLabelForOneTeam(TeamColour teamColour, Series series)
    {
        String player1 = series.getTeam(teamColour).getPlayer1().getName();
        String player2 = series.getTeam(teamColour).getPlayer2().getName();
        String player3 = series.getTeam(teamColour).getPlayer3().getName();

        String playerNames = "[1]" + player1.substring(0, Math.min(player1.length(), 10))
                + " [2]" + player2.substring(0, Math.min(player2.length(), 10))
                + " [3]" + player3.substring(0, Math.min(player3.length(), 10));
        return playerNames;
    }

    @NotNull
    private static String createPlaceholderPlayerNameLabel(Series series)
    {
        String bluePlayer1 = series.getBlueTeam().getPlayer1().getName();
        String bluePlayer2 = series.getBlueTeam().getPlayer2().getName();
        String bluePlayer3 = series.getBlueTeam().getPlayer3().getName();

        String orangePlayer1 = series.getOrangeTeam().getPlayer1().getName();
        String orangePlayer2 = series.getOrangeTeam().getPlayer2().getName();
        String orangePlayer3 = series.getOrangeTeam().getPlayer3().getName();


        String playerNames = "[b1]" + bluePlayer1.substring(0, Math.min(bluePlayer1.length(), 10))
                + " [b2]" + bluePlayer2.substring(0, Math.min(bluePlayer2.length(), 10))
                + " [b3]" + bluePlayer3.substring(0, Math.min(bluePlayer3.length(), 10))
                + System.getProperty("line.separator")
                + "[o1]" + orangePlayer1.substring(0, Math.min(orangePlayer1.length(), 10))
                + " [o2]" + orangePlayer2.substring(0, Math.min(orangePlayer2.length(), 10))
                + " [o3]" + orangePlayer3.substring(0, Math.min(orangePlayer3.length(), 10));
        return playerNames;
    }
}
