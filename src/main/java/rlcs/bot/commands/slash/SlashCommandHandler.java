package rlcs.bot.commands.slash;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import rlcs.bot.commands.button.ButtonType;
import rlcs.bot.commands.twitch.TwitchClipper;
import rlcs.bot.commands.twitch.TwitchStatus;
import rlcs.series.*;

public class SlashCommandHandler extends ListenerAdapter {

    private static TwitchClipper twitchClipper;
    private static final String COMMAND_CHANNEL = System.getenv("COMMAND_CHANNEL");
    public SlashCommandHandler(final TwitchClipper twitchClipper)
    {
        this.twitchClipper = twitchClipper;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        if (event.getName().equals(String.valueOf(SlashType.createseries)))
        {
            if (!event.getChannel().getId().equals(COMMAND_CHANNEL))
            {
                event.reply("Commentary commands can only be used in the #rlcs-mission-control channel " +
                        "by users with the Commentator role").setEphemeral(true).queue();
                return;
            }
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

        Series series = new Series(seriesIdOpt,
                messageCountOpt,
                new Score(blueGameScoreOpt, orangeGameScoreOpt),
                new Score(blueSeriesScoreOpt, orangeSeriesScoreOpt),
                blueTeam,
                orangeTeam,
                bestOfOpt,
                false,
                twitchName,
                twitchBroadcasterId,
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
}
