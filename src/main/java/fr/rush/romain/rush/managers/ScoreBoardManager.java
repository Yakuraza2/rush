package fr.rush.romain.rush.managers;

import fr.rush.romain.rush.objects.Rush;
import fr.rush.romain.rush.objects.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

public class ScoreBoardManager {

    public static HashMap<Player, Scoreboard> playerScoreboard = new HashMap<>();
    public static Scoreboard getScoreboard(Player p){
        if (!playerScoreboard.containsKey(p)) {
            Scoreboard newScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            newScoreboard.registerNewObjective("stats", "dummy");
            playerScoreboard.put(p, newScoreboard);
            p.setScoreboard(newScoreboard);
        }
        return playerScoreboard.get(p);
    }

    public static void updateScoreboard(Player p, Rush rush, int timer){

        Scoreboard sc = getScoreboard(p);
        Objective obj = sc.getObjective("stats");

        Team team = rush.getPlayerTeam(p);

        obj.setDisplayName("§3SCORES RUSH");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);


        String numbers = "12345678";
        StringBuilder sb = new StringBuilder();

        for (char num : numbers.toCharArray()) {
            if (numbers.indexOf(num) < team.getHealBoost()) {
                sb.append(ChatColor.GREEN).append(num).append(" ");
            } else {
                sb.append(ChatColor.RED).append(num).append(" ");
            }
        }

        StringBuilder sb2 = new StringBuilder();
        char oldLetter = '*';
        for(Team teams : rush.getTeams().values()){
            for(char letter : teams.getDisplayName().toUpperCase().toCharArray()){
                if('A' <= letter && letter <= 'Z' && oldLetter != '§' && oldLetter != '&'){
                    sb2.append(letter).append(" ");
                    if(teams.hasBed())  sb2.append(ChatColor.GREEN).append("✔");
                    else sb2.append(ChatColor.RED).append("✖");
                    sb2.append("  ");
                    break;
                }
                sb2.append(letter);
                oldLetter = letter;
            }
        }

        updatePerLine("Chemin: " + sb, 12,p,obj);
        obj.getScore(" ").setScore(11);
        updatePerLine(sb2.toString(),10,p,obj);
        obj.getScore("  ").setScore(9);
        updatePerLine("Kills/Morts: " + "§6" + rush.getKills(p) + "§f/"+"§6" + rush.getDeaths(p),8,p,obj);
        updatePerLine("Chrono: " + ChatColor.GOLD + timer/60+":"+timer%60+"s",7,p,obj);
        obj.getScore("   ").setScore(6);
        obj.getScore("Tu es dans").setScore(5);
        obj.getScore("l'équipe " + team.getDisplayName()).setScore(4);
        obj.getScore("    ").setScore(3);
        obj.getScore(ChatColor.GRAY + "Alliances entre").setScore(2);
        obj.getScore(ChatColor.GRAY + "équipes interdites").setScore(1);
    }

    public static void updatePerLine(String line, int scoreSlot, Player p, Objective obj) {
        for (String str : getScoreboard(p).getEntries()) {
            if (obj.getScore(str).getScore() == scoreSlot && !str.equals(line)) {
                getScoreboard(p).resetScores(str);
            }
            obj.getScore(line).setScore(scoreSlot);
        }
    }

    public static void clearScoreBoard(Player p){
        p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

}
