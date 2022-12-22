package rlcs.bot.commands.modal;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rlcs.series.Series;
import rlcs.series.SeriesStringParser;

public class ModalCommandHandler extends ListenerAdapter
{
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event)
    {
        try {
            switch (ModalType.valueOf(event.getModalId()))
            {
                case goalbluemodal:
                    handleGoalBlueModalEvent(event);
                    return;
                case goalorangemodal:
                    handleGoalOrangeModalEvent(event);
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
        } catch (IllegalArgumentException e) {
            event.reply("Unrecognised modal").setEphemeral(true).queue();
        }
    }

    private static void handleGoalBlueModalEvent(@NotNull ModalInteractionEvent event)
    {
        String originalMessage = event.getMessage().getContentRaw();
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

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
        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);


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
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

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
        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);


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
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

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
        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);



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
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

        series.setOvertime(true);
        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);

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
        Series series = SeriesStringParser.parseSeriesFromString(originalMessage);

        series.setMessageCount(series.getMessageCount() + 1);
        String updatedSeriesTemplateString = SeriesStringParser.generateSeriesString(series);

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
}