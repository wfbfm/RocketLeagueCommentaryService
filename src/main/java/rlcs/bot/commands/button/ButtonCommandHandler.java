package rlcs.bot.commands.button;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import rlcs.series.Series;
import rlcs.series.SeriesStringParser;

public class ButtonCommandHandler extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        try {
            switch (ButtonType.valueOf(event.getButton().getId()))
            {
                case goalblue:
                    handleGoalBlueEvent(event);
                    return;
                case goalorange:
                    handleGoalOrangeEvent(event);
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
            }
        } catch (IllegalArgumentException e) {
            event.reply("Unrecognised button press").setEphemeral(true).queue();
        }
    }

    private static void handleCommentEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

        String bluePlayer1 = series.getBlueTeam().getPlayer1().getName();
        String bluePlayer2 = series.getBlueTeam().getPlayer2().getName();
        String bluePlayer3 = series.getBlueTeam().getPlayer3().getName();

        String orangePlayer1 = series.getOrangeTeam().getPlayer1().getName();
        String orangePlayer2 = series.getOrangeTeam().getPlayer2().getName();
        String orangePlayer3 = series.getOrangeTeam().getPlayer3().getName();


        String labelAddOn = "[b1]" + bluePlayer1.substring(0, Math.min(bluePlayer1.length(), 10))
                + " [b2]" + bluePlayer2.substring(0, Math.min(bluePlayer2.length(), 10))
                + " [b3]" + bluePlayer3.substring(0, Math.min(bluePlayer3.length(), 10))
                + System.getProperty("line.separator")
                + "[o1]" + orangePlayer1.substring(0, Math.min(orangePlayer1.length(), 10))
                + " [o2]" + orangePlayer2.substring(0, Math.min(orangePlayer2.length(), 10))
                + " [o3]" + orangePlayer3.substring(0, Math.min(orangePlayer3.length(), 10));

        TextInput commentary = TextInput.create("commentary", "💬 Comment (\"[b1]\" blue p1, \"[o1]\" orange etc)", TextInputStyle.PARAGRAPH)
                .setMinLength(5)
                .setMaxLength(500)
                .setPlaceholder(labelAddOn)
                .setRequired(true)
                .build();

        Modal modal = Modal.create("commentmodal", "Enter Commentary")
                .addActionRows(ActionRow.of(commentary))
                .build();

        event.replyModal(modal).queue();
    }

    private static void handleGoalBlueEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

        if (series.getGameScore().getBlueScore() >= 9)
        {
            event.reply("Sorry - only single digit goals.  Please raise a Jira™️").setEphemeral(true).queue();
            return;
        }

        String player1 = series.getBlueTeam().getPlayer1().getName();
        String player2 = series.getBlueTeam().getPlayer2().getName();
        String player3 = series.getBlueTeam().getPlayer3().getName();

        String labelAddOn = "[1]" + player1.substring(0, Math.min(player1.length(),10))
                + " [2]" + player2.substring(0, Math.min(player2.length(),10))
                + " [3]" + player3.substring(0, Math.min(player3.length(),10));

        TextInput scorer = TextInput.create("scorer", "⚽ Scorer - Enter Player ID per below", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(1)
                .setPlaceholder(labelAddOn)
                .setRequired(true)
                .build();

        TextInput assister = TextInput.create("assister", "🤝 Assist - Enter Player ID per below, or 0", TextInputStyle.SHORT)
                .setMaxLength(1)
                .setPlaceholder(labelAddOn)
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

        Modal modal = Modal.create("bluegoalmodal", "Goal Scored by " + series.getBlueTeam().getTeamName())
                .addActionRows(ActionRow.of(scorer), ActionRow.of(assister), ActionRow.of(gameTime), ActionRow.of(commentary))
                .build();

        event.replyModal(modal).queue();
    }

    private static void handleGoalOrangeEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

        if (series.getGameScore().getOrangeScore() >= 9)
        {
            event.reply("Sorry - only single digit goals.  Please raise a Jira™️").setEphemeral(true).queue();
            return;
        }

        String player1 = series.getOrangeTeam().getPlayer1().getName();
        String player2 = series.getOrangeTeam().getPlayer2().getName();
        String player3 = series.getOrangeTeam().getPlayer3().getName();

        String labelAddOn = "[1]" + player1.substring(0, Math.min(player1.length(),10))
                + " [2]" + player2.substring(0, Math.min(player2.length(),10))
                + " [3]" + player3.substring(0, Math.min(player3.length(),10));

        TextInput scorer = TextInput.create("scorer", "⚽ Scorer - Enter Player ID per below", TextInputStyle.SHORT)
                .setMinLength(1)
                .setMaxLength(1)
                .setPlaceholder(labelAddOn)
                .setRequired(true)
                .build();

        TextInput assister = TextInput.create("assister", "🤝 Assist - Enter Player ID per below, or 0", TextInputStyle.SHORT)
                .setMaxLength(1)
                .setPlaceholder(labelAddOn)
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

        Modal modal = Modal.create("orangegoalmodal", "Goal Scored by " + series.getOrangeTeam().getTeamName())
                .addActionRows(ActionRow.of(scorer), ActionRow.of(assister), ActionRow.of(gameTime), ActionRow.of(commentary))
                .build();

        event.replyModal(modal).queue();
    }

    private static void handleGameEvent(@NotNull ButtonInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

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

        String bluePlayer1 = series.getBlueTeam().getPlayer1().getName();
        String bluePlayer2 = series.getBlueTeam().getPlayer2().getName();
        String bluePlayer3 = series.getBlueTeam().getPlayer3().getName();

        String orangePlayer1 = series.getOrangeTeam().getPlayer1().getName();
        String orangePlayer2 = series.getOrangeTeam().getPlayer2().getName();
        String orangePlayer3 = series.getOrangeTeam().getPlayer3().getName();


        String labelAddOn = "[b1]" + bluePlayer1.substring(0, Math.min(bluePlayer1.length(), 10))
                + " [b2]" + bluePlayer2.substring(0, Math.min(bluePlayer2.length(), 10))
                + " [b3]" + bluePlayer3.substring(0, Math.min(bluePlayer3.length(), 10))
                + System.getProperty("line.separator")
                + "[o1]" + orangePlayer1.substring(0, Math.min(orangePlayer1.length(), 10))
                + " [o2]" + orangePlayer2.substring(0, Math.min(orangePlayer2.length(), 10))
                + " [o3]" + orangePlayer3.substring(0, Math.min(orangePlayer3.length(), 10));

        TextInput commentary = TextInput.create("commentary", "💬 Comment (\"[b1]\" blue p1, \"[o1]\" orange etc)", TextInputStyle.PARAGRAPH)
                .setMinLength(5)
                .setMaxLength(500)
                .setPlaceholder(labelAddOn)
                .setRequired(false)
                .build();

        Modal modal = Modal.create("gamemodal", "Game Victory: " + gameWinner)
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

        Modal modal = Modal.create("overtimemodal", "Overtime!")
                .addActionRows(ActionRow.of(commentary))
                .build();

        event.replyModal(modal).queue();
    }
}
