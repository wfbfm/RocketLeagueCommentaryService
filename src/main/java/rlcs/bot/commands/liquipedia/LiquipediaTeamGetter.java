package rlcs.bot.commands.liquipedia;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiquipediaTeamGetter
{
    private Map<String, Map<String, String>> teamToPlayerMap = new HashMap<>();
    private List<Command.Choice> listChoices = new ArrayList<>();
    private String liquipediaUrl;

    public LiquipediaTeamGetter()
    {
        this.liquipediaUrl = null;
    }

    public String getLiquipediaUrl()
    {
        return liquipediaUrl;
    }

    public void setLiquipediaUrl(String liquipediaUrl)
    {
        this.liquipediaUrl = liquipediaUrl;
    }

    public Map<String, Map<String, String>> getTeamToPlayerMap()
    {
        return teamToPlayerMap;
    }

    public List<Command.Choice> getListChoices()
    {
        return listChoices;
    }

    public boolean updateLiquipediaRefData()
    {
        Document document = getDocumentFromLiquipediaUrl(liquipediaUrl);
        if (document == null)
        {
            // Unable to fetch Liquipedia page
            return false;
        }
        Elements teamCards = document.getElementsByAttributeValueContaining("class", "teamcard-column");

        if (teamCards.size() == 0)
        {
            // Unable to find participants on Liquipedia page
            return false;
        }
        else
        {
            parseTeamToPlayerMap(teamCards.get(0));
            generateChoiceList();
            return true;
        }
    }

    private void parseTeamToPlayerMap(Element teamCard)
    {
        teamToPlayerMap.clear();

        Elements teamNameHolders = teamCard.select("center");

        Elements teamCardInners = teamCard.getElementsByAttributeValue("class", "teamcard-inner");


        for (int i = 0; i < teamCardInners.size(); i++)
        {
            Element teamCardInner = teamCardInners.get(i);
            // parse teamCardInner for player names
            // data-toggle-area-content=1 ignores any Substitute data
            Element table = teamCardInner.select("table[data-toggle-area-content=1]").get(0);

            Elements rows = table.select("tr");

            Map<String, String> playerMap = new HashMap<String, String>();
            for (int j = 0; j < rows.size(); j++)
            {
                Elements colVals = rows.get(j).select("th,td");
                String playerId = colVals.get(0).text();
                if (playerId.length() > 1)
                {
                    // ignore any substitutes row interfering with this data
                    continue;
                }
                String playerName = colVals.get(1).text();

                playerMap.put(playerId, playerName);
            }

            // parse teamNameHolder for teamName
            String teamName = null;
            if (teamNameHolders.get(i).select("a").size() == 0)
            {
                // case where team is TBD
                teamName = teamNameHolders.get(i).text();
            }
            if (teamNameHolders.get(i).select("a").size() == 1)
            {
                // usual case where teamName is nested
                teamName = teamNameHolders.get(i).select("a").get(0).text();
            }
            if (teamNameHolders.get(i).select("a").size() > 1)
            {
                // skip over the Flag that is added for international event pages
                teamName = teamNameHolders.get(i).select("a").get(1).text();
            }

            // Add to map
            teamToPlayerMap.put(teamName, playerMap);
        }
    }

    private void generateChoiceList()
    {
        listChoices.clear();
        for (String team: teamToPlayerMap.keySet())
        {
            listChoices.add(new Command.Choice(team, team));
        }
    }

    private static Document getDocumentFromLiquipediaUrl(String url)
    {
        Connection connection = Jsoup.connect(url)
                .header("User-Agent", "RLCommentaryService")
                .header("Accept-Encoding", "gzip");
        try
        {
            Document document = connection.get();
            return document;
        } catch (IOException e)
        {
            return null;
        }
    }
}
