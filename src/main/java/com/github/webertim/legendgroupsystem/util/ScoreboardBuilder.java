package com.github.webertim.legendgroupsystem.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple helper class for building primitive scoreboards.
 */
public class ScoreboardBuilder {

    private final String title;
    private final String key;
    private Scoreboard scoreboard;
    private int lineCount = 0;
    private final List<String> lines = new ArrayList<>();

    /**
     * Create a new builder instance for a scoreboard with a key and a title.
     *
     * @param key Key of the created Scoreboard instance.
     * @param title Title of the created Scoreboard instance.
     */
    public ScoreboardBuilder(String key, String title) {
        this.key = key;
        this.title = title;
    }

    /**
     * Adds a new line to the scoreboard.
     * Lines on the scoreboard are ordered based on the order they were inserted from top to bottom.
     *
     * @param content Content of the line
     * @return this
     */
    public ScoreboardBuilder addLine(String content) {
        if (content != null) {
            lines.add(0, content);
        }
        return this;
    }

    /**
     * Create the actual Scoreboard instance based on the internal state.
     *
     * @return this
     */
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

    /**
     * Show the scoreboard to a player.
     *
     * @param player The target player.
     */
    public void setPlayer(Player player) {
        player.setScoreboard(this.scoreboard);
    }

    /**
     * Get the Scoreboard instance created by the call to {@link ScoreboardBuilder#build()}
     *
     * @return Scoreboard instance.
     */
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
}
