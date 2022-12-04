package com.github.webertim.legendgroupsystem.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.github.webertim.legendgroupsystem.manager.SignManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;

public class MapChunkListener extends PacketAdapter {

    private final SignManager signManager;

    public MapChunkListener(Plugin plugin, SignManager signManager) {
        super(plugin, PacketType.Play.Server.MAP_CHUNK);
        this.signManager = signManager;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packetContainer = event.getPacket();
        Player receiver = event.getPlayer();
        InternalStructure chunkData = packetContainer.getStructures().read(0);

        List<InternalStructure> entities = chunkData.getLists(getConverter()).read(0);
        int chunkX = packetContainer.getIntegers().read(0) * 16;
        int chunkZ = packetContainer.getIntegers().read(1) * 16;

        entities.forEach(
                entity -> {
                    int packedXZ = entity.getIntegers().read(0);
                    int inChunkZ = packedXZ & 0x0F;
                    int inChunkX = (packedXZ >> 4) & 0x0F;

                    int x = chunkX + inChunkX;
                    int z = chunkZ + inChunkZ;
                    int y = entity.getIntegers().read(1);

                    Location blockEntityLocation = new Location(receiver.getWorld(), x, y, z);

                    plugin.getLogger().info(blockEntityLocation.toString());

                    NbtBase nbtBase = entity.getNbtModifier().getValues().get(0);

                    signManager.updatePacketSignInformation(nbtBase, receiver, blockEntityLocation);
                }
        );

    }

    private EquivalentConverter<InternalStructure> getConverter() {
        EquivalentConverter<InternalStructure> converter;
        try {
            Field converterField = InternalStructure.class.getDeclaredField("CONVERTER");
            converterField.setAccessible(true);
            converter = (EquivalentConverter<InternalStructure>) converterField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return converter;
    }
}
