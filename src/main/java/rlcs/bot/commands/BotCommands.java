package rlcs.bot.commands;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
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

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event)
    {
        if (event.getModalId().equals("commentmodal"))
        {
            handleCommentModalEvent(event);
        }
        if (event.getModalId().equals("bluegoalmodal"))
        {
            handleGoalBlueModalEvent(event);
        }
        if (event.getModalId().equals("orangegoalmodal"))
        {
            handleGoalOrangeModalEvent(event);
        }
        if (event.getModalId().equals("gamemodal"))
        {
            handleGameModalEvent(event);
        }
        if (event.getModalId().equals("overtimemodal"))
        {
            handleOvertimeModalEvent(event);
        }
    }

    private static void handleGoalBlueModalEvent(@NotNull ModalInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);

        String scorer = event.getValue("scorer").getAsString();
        String assister = event.getValue("assister").getAsString();
        String gameTime = event.getValue("gametime").getAsString();
        String commentary = event.getValue("commentary").getAsString();

        if (scorer == assister)
        {
            event.reply("The same player cannot score and assist the same goal").setEphemeral(true).queue();
            return;
        }

        series.getGameScore().setBlueScore(series.getGameScore().getBlueScore() + 1);

        series.setOvertime(false);
        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesTemplateString = seriesActions.generateSeriesString(series);


        TextChannel publishChannel = event.getJDA().getTextChannelById(1049857570581516348L);


        String[] lines = updatedSeriesTemplateString.split(System.getProperty("line.separator"));
        StringBuilder publishStringBuilder = new StringBuilder(System.getProperty("line.separator"));
        publishStringBuilder.append(System.getProperty("line.separator"));

        for (int i = 0; i < 3; i++)
        {
            publishStringBuilder.append(lines[i]);
            publishStringBuilder.append(System.getProperty("line.separator"));
        }

        String scorerName = null;
        switch (scorer)
        {
            case "1":
                scorerName = series.getBlueTeam().getPlayer1().getName();
                break;
            case "2":
                scorerName = series.getBlueTeam().getPlayer2().getName();
                break;
            case "3":
                scorerName = series.getBlueTeam().getPlayer3().getName();
                break;
            default:
                break;
        }

        String assisterName = null;
        switch (assister)
        {
            case "1":
                assisterName = series.getBlueTeam().getPlayer1().getName();
                break;
            case "2":
                assisterName = series.getBlueTeam().getPlayer2().getName();
                break;
            case "3":
                assisterName = series.getBlueTeam().getPlayer3().getName();
                break;
            default:
                break;
        }

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
            commentary = commentary.replace("[1]", "**" + series.getBlueTeam().getPlayer1().getName()+ "**");
            commentary = commentary.replace("[2]", "**" + series.getBlueTeam().getPlayer2().getName() + "**");
            commentary = commentary.replace("[3]", "**" + series.getBlueTeam().getPlayer3().getName() + "**");

            publishStringBuilder.append(commentary);
        }

        if (publishChannel != null)
        {
            publishChannel.sendMessage(publishStringBuilder.toString()).queue();
        }

        event.editMessage(updatedSeriesTemplateString).queue();
    }

    private static void handleGoalOrangeModalEvent(@NotNull ModalInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);

        String scorer = event.getValue("scorer").getAsString();
        String assister = event.getValue("assister").getAsString();
        String gameTime = event.getValue("gametime").getAsString();
        String commentary = event.getValue("commentary").getAsString();

        if (scorer == assister)
        {
            event.reply("The same player cannot score and assist the same goal").setEphemeral(true).queue();
            return;
        }

        series.getGameScore().setOrangeScore(series.getGameScore().getOrangeScore() + 1);

        series.setOvertime(false);
        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesTemplateString = seriesActions.generateSeriesString(series);


        TextChannel publishChannel = event.getJDA().getTextChannelById(1049857570581516348L);


        String[] lines = updatedSeriesTemplateString.split(System.getProperty("line.separator"));
        StringBuilder publishStringBuilder = new StringBuilder(System.getProperty("line.separator"));
        publishStringBuilder.append(System.getProperty("line.separator"));

        for (int i = 0; i < 3; i++)
        {
            publishStringBuilder.append(lines[i]);
            publishStringBuilder.append(System.getProperty("line.separator"));
        }

        String scorerName = null;
        switch (scorer)
        {
            case "1":
                scorerName = series.getOrangeTeam().getPlayer1().getName();
                break;
            case "2":
                scorerName = series.getOrangeTeam().getPlayer2().getName();
                break;
            case "3":
                scorerName = series.getOrangeTeam().getPlayer3().getName();
                break;
            default:
                break;
        }

        String assisterName = null;
        switch (assister)
        {
            case "1":
                assisterName = series.getOrangeTeam().getPlayer1().getName();
                break;
            case "2":
                assisterName = series.getOrangeTeam().getPlayer2().getName();
                break;
            case "3":
                assisterName = series.getOrangeTeam().getPlayer3().getName();
                break;
            default:
                break;
        }

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
            commentary = commentary.replace("[1]", "**" + series.getOrangeTeam().getPlayer1().getName()+ "**");
            commentary = commentary.replace("[2]", "**" + series.getOrangeTeam().getPlayer2().getName() + "**");
            commentary = commentary.replace("[3]", "**" + series.getOrangeTeam().getPlayer3().getName() + "**");

            publishStringBuilder.append(commentary);
        }

        if (publishChannel != null)
        {
            publishChannel.sendMessage(publishStringBuilder.toString()).queue();
        }

        event.editMessage(updatedSeriesTemplateString).queue();
    }

    private static void handleGameModalEvent(@NotNull ModalInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);

        int blueGameScore = series.getGameScore().getBlueScore();
        int orangeGameScore = series.getGameScore().getOrangeScore();

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
        String updatedSeriesTemplateString = seriesActions.generateSeriesString(series);



        String commentary = event.getValue("commentary").getAsString();

        String bluePlayer1 = series.getBlueTeam().getPlayer1().getName();
        String bluePlayer2 = series.getBlueTeam().getPlayer2().getName();
        String bluePlayer3 = series.getBlueTeam().getPlayer3().getName();

        String orangePlayer1 = series.getOrangeTeam().getPlayer1().getName();
        String orangePlayer2 = series.getOrangeTeam().getPlayer2().getName();
        String orangePlayer3 = series.getOrangeTeam().getPlayer3().getName();


        TextChannel publishChannel = event.getJDA().getTextChannelById(1049857570581516348L);


        String[] lines = updatedSeriesTemplateString.split(System.getProperty("line.separator"));
        StringBuilder publishStringBuilder = new StringBuilder(System.getProperty("line.separator"));
        publishStringBuilder.append(System.getProperty("line.separator"));

        for (int i = 0; i < 3; i++)
        {
            publishStringBuilder.append(lines[i]);
            publishStringBuilder.append(System.getProperty("line.separator"));
        }

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

        if (publishChannel != null)
        {
            publishChannel.sendMessage(publishStringBuilder.toString()).queue();
        }

        event.editMessage(updatedSeriesTemplateString).queue();
    }

    private static void handleOvertimeModalEvent(@NotNull ModalInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);

        series.setOvertime(true);
        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesTemplateString = seriesActions.generateSeriesString(series);

        String commentary = event.getValue("commentary").getAsString();

        String bluePlayer1 = series.getBlueTeam().getPlayer1().getName();
        String bluePlayer2 = series.getBlueTeam().getPlayer2().getName();
        String bluePlayer3 = series.getBlueTeam().getPlayer3().getName();

        String orangePlayer1 = series.getOrangeTeam().getPlayer1().getName();
        String orangePlayer2 = series.getOrangeTeam().getPlayer2().getName();
        String orangePlayer3 = series.getOrangeTeam().getPlayer3().getName();


        TextChannel publishChannel = event.getJDA().getTextChannelById(1049857570581516348L);


        String[] lines = updatedSeriesTemplateString.split(System.getProperty("line.separator"));
        StringBuilder publishStringBuilder = new StringBuilder(System.getProperty("line.separator"));
        publishStringBuilder.append(System.getProperty("line.separator"));

        for (int i = 0; i < 3; i++)
        {
            publishStringBuilder.append(lines[i]);
            publishStringBuilder.append(System.getProperty("line.separator"));
        }

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

        if (publishChannel != null)
        {
            publishChannel.sendMessage(publishStringBuilder.toString()).queue();
        }

        event.editMessage(updatedSeriesTemplateString).queue();
    }

    private static void handleCommentModalEvent(@NotNull ModalInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = seriesActions.parseSeriesFromString(originalMessage);

        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesTemplateString = seriesActions.generateSeriesString(series);

        String commentary = event.getValue("commentary").getAsString();

        String bluePlayer1 = series.getBlueTeam().getPlayer1().getName();
        String bluePlayer2 = series.getBlueTeam().getPlayer2().getName();
        String bluePlayer3 = series.getBlueTeam().getPlayer3().getName();

        String orangePlayer1 = series.getOrangeTeam().getPlayer1().getName();
        String orangePlayer2 = series.getOrangeTeam().getPlayer2().getName();
        String orangePlayer3 = series.getOrangeTeam().getPlayer3().getName();


        TextChannel publishChannel = event.getJDA().getTextChannelById(1049857570581516348L);


        String[] lines = updatedSeriesTemplateString.split(System.getProperty("line.separator"));
        StringBuilder publishStringBuilder = new StringBuilder(System.getProperty("line.separator"));
        publishStringBuilder.append(System.getProperty("line.separator"));

        for (int i = 0; i < 3; i++)
        {
            publishStringBuilder.append(lines[i]);
            publishStringBuilder.append(System.getProperty("line.separator"));
        }

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

        if (publishChannel != null)
        {
            publishChannel.sendMessage(publishStringBuilder.toString()).queue();
        }

        event.editMessage(updatedSeriesTemplateString).queue();
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
        Series series = seriesActions.parseSeriesFromString(originalMessage);

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
        Series series = seriesActions.parseSeriesFromString(originalMessage);

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
        Series series = seriesActions.parseSeriesFromString(originalMessage);

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