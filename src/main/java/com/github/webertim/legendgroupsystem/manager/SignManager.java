package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.manager.enums.SignStatusEnum;
import com.github.webertim.legendgroupsystem.model.Group;
import com.github.webertim.legendgroupsystem.model.PlayerGroupSign;
import com.github.webertim.legendgroupsystem.model.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

public class SignManager extends BaseManager<String, PlayerGroupSign> {

    private final PlayerManager playerManager;
    public SignManager(LegendGroupSystem legendGroupSystem, Dao<PlayerGroupSign, String> dao, PlayerManager playerManager) throws SQLException {
        super(legendGroupSystem, dao);
        this.playerManager = playerManager;
    }

    public void updateAllPlayersAllSigns(SignStatusEnum signStatus) {
        Bukkit.getScheduler().runTaskAsynchronously(
                legendGroupSystem,
                () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (PlayerGroupSign sign : this.getValues()) {
                            sendSignChange(player, sign, signStatus);
                        }
                    }
                }
        );
    }

    public void updateAllPlayersSingleSign(PlayerGroupSign sign, SignStatusEnum signStatus) {
        Bukkit.getScheduler().runTaskAsynchronously(
                legendGroupSystem,
                () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sendSignChange(player, sign, signStatus);
                    }
                }
        );
    }

    public void updatePlayerAllSigns(Player player, SignStatusEnum signStatus) {
        Bukkit.getScheduler().runTaskAsynchronously(
                legendGroupSystem,
                () -> {
                    for (PlayerGroupSign sign : this.getValues()) {
                        sendSignChange(player, sign, signStatus);
                    }
                }
        );
    }

    private void sendSignChange(Player player, PlayerGroupSign sign, SignStatusEnum signStatus) {
        switch (signStatus) {
            case UPDATE -> sendSignUpdate(player, sign);
            case REMOVE -> sendSignRemove(player, sign);
        }
    }

    private void sendSignUpdate(Player player, PlayerGroupSign sign) {
        player.sendBlockChange(
                sign.getLocation(),
                Material.OAK_SIGN.createBlockData()
        );

        player.sendSignChange(
                sign.getLocation(),
                buildSignContent(player)
        );
    }

    private void sendSignRemove(Player player, PlayerGroupSign sign) {
        player.sendBlockChange(
                sign.getLocation(),
                Material.AIR.createBlockData()
        );
    }

    private List<Component> buildSignContent(Player player) {
        Group playerGroup = this.playerManager.getGroupInfo(player.getUniqueId());
        PlayerInfo playerInfo = this.playerManager.get(player.getUniqueId());
        String expirationString = "";
        if (playerInfo != null) {
            expirationString = playerInfo.getExpirationDateString();
        }

        return Stream.of(
                this.playerManager.buildPlayerName(player),
                playerGroup.getName(),
                expirationString,
                ""
        ).<Component>map(Component::text).toList();
    }

    @Override
    public void insert(PlayerGroupSign data) {
        super.insert(data);

        updateAllPlayersSingleSign(data, SignStatusEnum.UPDATE);
    }

    @Override
    public void remove(PlayerGroupSign data) {
        super.remove(data);

        updateAllPlayersSingleSign(data, SignStatusEnum.REMOVE);
    }
}

