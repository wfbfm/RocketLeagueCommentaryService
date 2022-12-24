package rlcs.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import rlcs.bot.commands.button.ButtonCommandHandler;
import rlcs.bot.commands.modal.ModalCommandHandler;
import rlcs.bot.commands.modal.ModalType;
import rlcs.bot.commands.slash.SlashCommandHandler;
import rlcs.bot.commands.slash.SlashType;
import rlcs.bot.commands.twitch.TwitchClipper;

public class RLCSDiscordBot {
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final String GUILD_ID = System.getenv("GUILD_ID");
    public static void main(String[] args) throws InterruptedException
    {
        TwitchClipper twitchClipper = new TwitchClipper();

        JDA jda = JDABuilder.createDefault(BOT_TOKEN)
                .setActivity(Activity.watching("RLCS"))
                .addEventListeners(new SlashCommandHandler(twitchClipper),
                        new ButtonCommandHandler(twitchClipper),
                        new ModalCommandHandler(twitchClipper))
                .build()
                .awaitReady();

        Guild rlcsGuild = jda.getGuildById(GUILD_ID);
        if (rlcsGuild != null)
        {
            rlcsGuild.upsertCommand(SlashType.createseries.name(), "Create RLCS Series between teams")
                    .addOptions(
                            new OptionData(OptionType.STRING, "teamblue", "Blue team name", true),
                            new OptionData(OptionType.STRING, "teamorange", "Orange team name", true),
                            new OptionData(OptionType.INTEGER, "bestof", "Number of games", true),
                            new OptionData(OptionType.INTEGER, "seriesid", "Series ID", true),
                            new OptionData(OptionType.INTEGER, "messagecount", "Message number", true),
                            new OptionData(OptionType.INTEGER, "blueseriesscore", "Blue series score", true),
                            new OptionData(OptionType.INTEGER, "orangeseriesscore", "Orange series score", true),
                            new OptionData(OptionType.INTEGER, "bluegamescore", "Blue game score", true),
                            new OptionData(OptionType.INTEGER, "orangegamescore", "Orange score score", true),
                            new OptionData(OptionType.STRING, "blueplayer1", "Blue player 1", true),
                            new OptionData(OptionType.STRING, "blueplayer2", "Blue player 2", true),
                            new OptionData(OptionType.STRING, "blueplayer3", "Blue player 3", true),
                            new OptionData(OptionType.STRING, "orangeplayer1", "Orange player 1", true),
                            new OptionData(OptionType.STRING, "orangeplayer2", "Orange player 2", true),
                            new OptionData(OptionType.STRING, "orangeplayer3", "Orange player 3", true),
                            new OptionData(OptionType.STRING, "twitchchannel", "Twitch Channel Name", false)
                    ).queue();

            rlcsGuild.upsertCommand(ModalType.goalbluemodal.name(), "Goal for Blue team").queue();
            rlcsGuild.upsertCommand(ModalType.goalorangemodal.name(), "Goal for Orange team").queue();
            rlcsGuild.upsertCommand(ModalType.gamemodal.name(), "End Game").queue();
            rlcsGuild.upsertCommand(ModalType.overtimemodal.name(), "Enter Overtime").queue();
            rlcsGuild.upsertCommand(ModalType.commentmodal.name(), "Enter Comment").queue();
        }
    }

}
