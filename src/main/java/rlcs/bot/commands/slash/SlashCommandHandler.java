package rlcs.bot.commands.slash;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rlcs.bot.commands.button.ButtonType;
import rlcs.bot.commands.twitch.TwitchClipper;
import rlcs.bot.commands.twitch.TwitchStatus;
import rlcs.series.*;

import java.util.List;

public class SlashCommandHandler extends ListenerAdapter {

    private static TwitchClipper twitchClipper;
    private static final String COMMAND_CHANNEL = System.getenv("COMMAND_CHANNEL");
    private static final String RLCS_BOT_USER_ID = System.getenv("BOT_USER_ID");
    public SlashCommandHandler(final TwitchClipper twitchClipper)
    {
        this.twitchClipper = twitchClipper;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals(String.valueOf(SlashType.createseriesmanually)))
        {
            if (!event.getChannel().getId().equals(COMMAND_CHANNEL))
            {
                event.reply("Commentary commands can only be used in the #rlcs-mission-control channel " +
                        "by users with the Commentator role").setEphemeral(true).queue();
                return;
            }
            handleCreateSeriesManuallyEvent(event);
        }
    }

    private static void handleCreateSeriesManuallyEvent(@NotNull SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();

        int seriesId = getNewSeriesIdFromMessageHistory(event);

        String teamBlueOpt = event.getOption("teamblue").getAsString()
                .replace(":", "")
                .replace("-", "");
        String teamOrangeOpt = event.getOption("teamorange").getAsString()
                .replace(":", "")
                .replace("-", "");
        int bestOfOpt = event.getOption("bestof").getAsInt();

        String bluePlayer1Opt = event.getOption("blueplayer1").getAsString().replace(";", "");
        String bluePlayer2Opt = event.getOption("blueplayer2").getAsString().replace(";", "");
        String bluePlayer3Opt = event.getOption("blueplayer3").getAsString().replace(";", "");
        String orangePlayer1Opt = event.getOption("orangeplayer1").getAsString().replace(";", "");
        String orangePlayer2Opt = event.getOption("orangeplayer2").getAsString().replace(";", "");
        String orangePlayer3Opt = event.getOption("orangeplayer3").getAsString().replace(";", "");
        String twitchName = "None";
        String twitchBroadcasterId = TwitchStatus.TWITCH_USER_NOT_FOUND.name();
        if (event.getOption("twitchchannel") != null)
        {
            twitchName = twitchClipper.parseTwitchName(event.getOption("twitchchannel").getAsString());
            twitchBroadcasterId = twitchClipper.getBroadcasterIdForTwitchName(twitchName);
        }

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

        Series series = new Series(seriesId,
                0,
                new Score(0, 0),
                new Score(0, 0),
                blueTeam,
                orangeTeam,
                bestOfOpt,
                false,
                twitchName,
                twitchBroadcasterId,
                "None",
                "None"
        );

        if (!twitchBroadcasterId.equals(TwitchStatus.TWITCH_USER_NOT_FOUND.name()))
        {
            // Additional action row for twitch clips
            event.getHook().sendMessage(SeriesStringParser.generateSeriesString(series))
                    .addActionRow(
                            Button.primary(ButtonType.goalblue.name(), "⚽ " + blueTeam.getTeamName()),
                            Button.danger(ButtonType.goalorange.name(), "⚽ " + orangeTeam.getTeamName()),
                            Button.success(ButtonType.game.name(), "🏁 Game"),
                            Button.secondary(ButtonType.overtime.name(), "🕒 Overtime"),
                            Button.secondary(ButtonType.comment.name(), "💬 Comment"))
                    .addActionRow(
                            Button.primary(ButtonType.twitchclip.name(), "🎬 Generate Twitch Clip for Next Message"),
                            Button.danger(ButtonType.removetwitchclip.name(), "❌ Remove Clip"),
                            Button.secondary(ButtonType.editscore.name(), "📝 Edit Score")
                    )
                    .queue();
        }
        else
        {
            event.getHook().sendMessage(SeriesStringParser.generateSeriesString(series))
                    .addActionRow(
                            Button.primary(ButtonType.goalblue.name(), "⚽ " + blueTeam.getTeamName()),
                            Button.danger(ButtonType.goalorange.name(), "⚽ " + orangeTeam.getTeamName()),
                            Button.success(ButtonType.game.name(), "🏁 Game"),
                            Button.secondary(ButtonType.overtime.name(), "🕒 Overtime"),
                            Button.secondary(ButtonType.comment.name(), "💬 Comment"))
                    .addActionRow(
                            Button.secondary(ButtonType.editscore.name(), "📝 Edit Score")
                    )
                    .queue();
        }
    }

    private static int getNewSeriesIdFromMessageHistory(@NotNull SlashCommandInteractionEvent event)
    {
        List<Message> historicalMessages = event.getMessageChannel().getHistory().retrievePast(50).complete();
        int oldSeriesId = 0;
        for (Message message: historicalMessages)
        {
            if (!message.getAuthor().getId().equals(RLCS_BOT_USER_ID))
            {
                continue;
            }
            if (message.getContentRaw().length() == 0)
            {
                continue;
            }
            if (!(message.getContentRaw()).substring(0, 1).equals("S"))
            {
                continue;
            }
            String seriesIdString = message.getContentRaw().split("-")[0];
            oldSeriesId = Integer.parseInt(seriesIdString.substring(1, seriesIdString.length()));
            break;
        }
        return oldSeriesId + 1;
    }
}
