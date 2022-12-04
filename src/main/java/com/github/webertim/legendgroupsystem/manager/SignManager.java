package com.github.webertim.legendgroupsystem.manager;

import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.github.webertim.legendgroupsystem.LegendGroupSystem;
import com.github.webertim.legendgroupsystem.model.database.Group;
import com.github.webertim.legendgroupsystem.model.Identifiable;
import com.github.webertim.legendgroupsystem.model.database.PlayerGroupSign;
import com.github.webertim.legendgroupsystem.model.database.PlayerInfo;
import com.j256.ormlite.dao.Dao;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Manager class for managing signs.
 */
public class SignManager extends BaseManager<Location, PlayerGroupSign> {

    private final PlayerManager playerManager;
    public SignManager(LegendGroupSystem legendGroupSystem, Dao<PlayerGroupSign, Location> dao, PlayerManager playerManager) throws SQLException {
        super(legendGroupSystem, dao);
        this.playerManager = playerManager;
    }

    public void updatePacketSignInformation(NbtBase nbtBase, Player receiver, Location blockLocation) {
        if (!(contains(blockLocation))) {
            return;
        }

        if (!(nbtBase instanceof NbtCompound nbtCompound)) {
            return;
        }

        List<Component> signContent = buildSignContent(receiver);

        int lineNumber = 1;
        for (Component signLine : signContent) {
            String serializedComponent = GsonComponentSerializer.gson().serialize(signLine);
            nbtCompound.put("Text" + lineNumber, serializedComponent);

            lineNumber++;
        }
    }

    public void updateAllPlayersSingleSign(PlayerGroupSign sign) {
        Bukkit.getScheduler().runTaskAsynchronously(
                legendGroupSystem,
                () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        sendSignChange(player, sign);
                    }
                }
        );
    }
    public void updatePlayerAllSigns(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(
                legendGroupSystem,
                () -> {
                    for (PlayerGroupSign sign : this.getValues()) {
                        sendSignChange(player, sign);
                    }
                }
        );
    }
    private void sendSignChange(Player player, PlayerGroupSign sign) {
        player.sendSignChange(
                sign.getLocation(),
                Arrays.asList(null, null, null, null)
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

        this.updateAllPlayersSingleSign(data);
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
    public void edit(Location id, PlayerGroupSign data) {
        super.edit(id, data);

        this.updateAllPlayersSingleSign(data);
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

        this.updateAllPlayersSingleSign(data);
    }
}