package com.github.webertim.legendgroupsystem.manager;

import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.manager.enums.SignStatusEnum;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.github.webertim.legendgroupsystem.model.database.PlayerGroupSign;
import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manager class for managing signs.
 */
public class SignManager extends BaseManager<String, PlayerGroupSign> {

    private final PlayerManager playerManager;
    public SignManager(LegendGroupSystem legendGroupSystem, Dao<PlayerGroupSign, String> dao, PlayerManager playerManager) throws SQLException {
        super(legendGroupSystem, dao);
        this.playerManager = playerManager;
    }

    /**
     * Sends an update to all online players about one sign.
     *
     * @param sign Sign containing the location of the sign.
     * @param signStatus Whether to remove or update the sign for each player.
     */
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

    /**
     * Sends an update to one player about all signs.
     *
     * @param player The player to update.
     * @param signStatus Whether to remove or update the signs for the player.
     */
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

    /**
     * Insert a new resource into the internal hash map.
     * Also updates all players about the sign update.
     * This operation notifies all change listeners.
     *
     * @param data The data to insert.
     */
    @Override
    public void insert(PlayerGroupSign data) {
        super.insert(data);

        updateAllPlayersSingleSign(data, SignStatusEnum.UPDATE);
    }

    /**
     * Update an existing resource inside the internal hash map. Note that the only difference between this method and
     * {@link com.github.webertim.legendgroupsystem.manager.BaseManager#insert(Identifiable i)} is the way the change
     * listener is informed.
     * Also updates all players about the sign update.
     * This operation notifies all change listeners.
     *
     * @param id The Id of the data to update.
     * @param data The data to update.
     */
    @Override
    public void edit(String id, PlayerGroupSign data) {
        super.edit(id, data);

        updateAllPlayersSingleSign(data, SignStatusEnum.UPDATE);
    }

    /**
     * Remove a resource from the internal hash map.
     * Also updates all players about the sign update.
     * This operation notifies all change listeners.
     *
     * @param data The sign to remove.
     */
    @Override
    public void remove(PlayerGroupSign data) {
        super.remove(data);

        updateAllPlayersSingleSign(data, SignStatusEnum.REMOVE);
    }
}

