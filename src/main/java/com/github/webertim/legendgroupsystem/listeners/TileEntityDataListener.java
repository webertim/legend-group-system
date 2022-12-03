package com.github.webertim.legendgroupsystem.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class TileEntityDataListener extends PacketAdapter {
    private final SignManager signManager;

    public TileEntityDataListener(Plugin plugin, SignManager signManager) {
        super(plugin, PacketType.Play.Server.TILE_ENTITY_DATA);
        this.signManager = signManager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packetContainer = event.getPacket();
        Player receiver = event.getPlayer();
        BlockPosition blockPosition = packetContainer.getBlockPositionModifier().getValues().get(0);

        Location blockLocation = new Location(receiver.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());

        NbtBase nbtBase = packetContainer.getNbtModifier().getValues().get(0);

        signManager.updatePacketSignInformation(nbtBase, receiver, blockLocation);
    }
}
