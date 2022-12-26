package rlcs.bot.commands.slash;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import rlcs.bot.commands.button.ButtonType;
import rlcs.bot.commands.liquipedia.LiquipediaTeamGetter;
import rlcs.bot.commands.twitch.TwitchClipper;
import rlcs.bot.commands.twitch.TwitchStatus;
import rlcs.series.*;

import java.util.List;
import java.util.Map;

public class SlashCommandHandler extends ListenerAdapter {

    private static TwitchClipper twitchClipper;
    private LiquipediaTeamGetter liquipediaTeamGetter;
    private static final String COMMAND_CHANNEL = System.getenv("COMMAND_CHANNEL");
    private static final String RLCS_BOT_USER_ID = System.getenv("BOT_USER_ID");
    public SlashCommandHandler(final TwitchClipper twitchClipper, final LiquipediaTeamGetter liquipediaTeamGetter)
    {
        this.twitchClipper = twitchClipper;
        this.liquipediaTeamGetter = liquipediaTeamGetter;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
    {
        try
        {
            switch (SlashType.valueOf(event.getName()))
            {
                case createseriesmanually:
                    if (!validateSlashCommandSentFromExpectedChannel(event))
                    {
                        return;
                    }
                    handleCreateSeriesManuallyEvent(event);
                    return;
                case updateteamsfromliquipedia:
                    if (!validateSlashCommandSentFromExpectedChannel(event))
                    {
                        return;
                    }
                    handleUpdateTeamsFromLiquipediaEvent(event);
                    return;
                case createseries:
                    if (!validateSlashCommandSentFromExpectedChannel(event))
                    {
                        return;
                    }
                    handleCreateSeriesEvent(event);
                    return;
            }
        } catch (IllegalArgumentException e)
        {
            return;
        }
    }

    private void handleUpdateTeamsFromLiquipediaEvent(@NotNull SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();
        String oldUrl = this.liquipediaTeamGetter.getLiquipediaUrl();
        String url = event.getOption("liquipediaurl").getAsString();

        if (StringUtils.isEmpty(url))
        {
            event.getHook().sendMessage("Unable to update team data from empty url.  Try restoring from old url: " + oldUrl).queue();
            return;
        }
        if (!url.startsWith("https://liquipedia.net/rocketleague/"))
        {
            event.getHook().sendMessage("Unable to update team data from url: " + url + " - try a valid https://liquipedia.net/rocketleague/ domain link or restore from old url: " + oldUrl).queue();
            return;
        }
        this.liquipediaTeamGetter.setLiquipediaUrl(url);
        boolean successfulUpdate = this.liquipediaTeamGetter.updateLiquipediaRefData();
        if (!successfulUpdate)
        {
            event.getHook().sendMessage("Unable to update team data from url: " + url + " - does the Liquipedia page have Participants listed?  Try restoring from old url: " + oldUrl).queue();
            return;
        }
        else
        {
            // Upsert a new slash command with the updated dropdowns
            upsertCreateSeriesSlashCommand(event.getGuild());
            event.getHook().sendMessage("Successfully updated team/player ref data from Liquipedia url: " + url).queue();
            return;
        }
    }

    private void handleCreateSeriesEvent(@NotNull SlashCommandInteractionEvent event)
    {
        event.deferReply().queue();

        int seriesId = getNewSeriesIdFromMessageHistory(event);

        Map<String, Map<String, String>> teamToPlayerMap = this.liquipediaTeamGetter.getTeamToPlayerMap();

        String teamBlueOpt;
        String bluePlayer1Opt;
        String bluePlayer2Opt;
        String bluePlayer3Opt;
        String teamOrangeOpt;
        String orangePlayer1Opt;
        String orangePlayer2Opt;
        String orangePlayer3Opt;
        try
        {
            teamBlueOpt = event.getOption("teamblue").getAsString();
            Map<String, String> bluePlayers = teamToPlayerMap.get(teamBlueOpt);
            teamBlueOpt = teamBlueOpt.replace(":", "").replace("-", "");
            bluePlayer1Opt = bluePlayers.get("1").replace(";", "");
            bluePlayer2Opt = bluePlayers.get("2").replace(";", "");
            bluePlayer3Opt = bluePlayers.get("3").replace(";", "");

            teamOrangeOpt = event.getOption("teamorange").getAsString();
            Map<String, String> orangePlayers = teamToPlayerMap.get(teamOrangeOpt);
            teamOrangeOpt = teamOrangeOpt.replace(":", "").replace("-", "");
            orangePlayer1Opt = orangePlayers.get("1").replace(";", "");
            orangePlayer2Opt = orangePlayers.get("2").replace(";", "");
            orangePlayer3Opt = orangePlayers.get("3").replace(";", "");
        } catch (NullPointerException e)
        {
            event.getHook().sendMessage("Unable to create series from Liquipedia data.  Try /createseriesmanually, or /updateteamsfromliquipedia to update the reference data. " +
                    " Current Liquipedia data sourced from: " + this.liquipediaTeamGetter.getLiquipediaUrl()).queue();
            return;
        }

        int bestOfOpt = event.getOption("bestof").getAsInt();
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

        createSeriesInCommandChannel(event, twitchBroadcasterId, blueTeam, orangeTeam, series);
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

        createSeriesInCommandChannel(event, twitchBroadcasterId, blueTeam, orangeTeam, series);
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
            String rawMessageContent = message.getContentRaw();
            if (rawMessageContent.length() == 0)
            {
                continue;
            }
            if (!(rawMessageContent).substring(0, 1).equals("S") ||!rawMessageContent.split(System.getProperty("line.separator"))[0].contains("#"))
            {
                continue;
            }
            String seriesIdString = rawMessageContent.split("-")[0];
            oldSeriesId = Integer.parseInt(seriesIdString.substring(1, seriesIdString.length()));
            break;
        }
        return oldSeriesId + 1;
    }

    private static void createSeriesInCommandChannel(@NotNull SlashCommandInteractionEvent event, String twitchBroadcasterId, Team blueTeam, Team orangeTeam, Series series)
    {
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

    private boolean validateSlashCommandSentFromExpectedChannel(@NotNull SlashCommandInteractionEvent event)
    {
        if (!event.getChannel().getId().equals(COMMAND_CHANNEL))
        {
            event.reply("Commentary commands can only be used in the #rlcs-mission-control channel " +
                    "by users with the Commentator role").setEphemeral(true).queue();
            return false;
        }
        return true;
    }

    private void upsertCreateSeriesSlashCommand(Guild rlcsGuild)
    {
        rlcsGuild.upsertCommand(SlashType.createseries.name(), "Create RLCS Series between teams.  Use /updateTeamsFromLiquipedia to update team list")
                .addOptions(
                        new OptionData(OptionType.STRING, "teamblue", "Blue team name", true)
                                .addChoices(this.liquipediaTeamGetter.getListChoices()),
                        new OptionData(OptionType.STRING, "teamorange", "Orange team name", true)
                                .addChoices(this.liquipediaTeamGetter.getListChoices()),
                        new OptionData(OptionType.INTEGER, "bestof", "Number of games", true).setMinValue(1),
                        new OptionData(OptionType.STRING, "twitchchannel", "Twitch Channel Name", false)
                ).queue();
    }
}
