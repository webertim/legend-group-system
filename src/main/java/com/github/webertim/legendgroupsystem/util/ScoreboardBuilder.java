package com.github.webertim.legendgroupsystem.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardBuilder {

    private final String title;
    private final String key;
    private Scoreboard scoreboard;
    private int lineCount = 0;
    private final List<String> lines = new ArrayList<>();

    public ScoreboardBuilder(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public ScoreboardBuilder addLine(String content) {
        if (content != null) {
            lines.add(0, content);
        }
        return this;
    }

    public ScoreboardBuilder build() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(this.key, Criteria.DUMMY, Component.text(this.title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        lines.forEach(
                line -> {
                    Score score = objective.getScore(line);
                    score.setScore(lineCount++);
                }
        );

        return this;
    }

    public void setPlayer(Player player) {
        player.setScoreboard(this.scoreboard);
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
}
