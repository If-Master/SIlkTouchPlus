package me.IfMasterPluSilk.silkTouchPlus;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.Location;

import java.util.Iterator;
import java.util.List;

public class BlockBreakListener implements Listener {

    private final SilkTouchPlus plugin;

    public BlockBreakListener(SilkTouchPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!event.isDropItems()) {
            return;
        }

        Material blockType = block.getType();
        String blockName = blockType.name().toLowerCase();

        List<String> whitelist = plugin.getConfig().getStringList("whitelist");
        List<String> blacklist = plugin.getConfig().getStringList("blacklist");
        List<String> useNbtData = plugin.getConfig().getStringList("use-nbt-data");

        if (blacklist.contains(blockName)) {
            event.setDropItems(false);
            event.setExpToDrop(0);
            return;
        }

        if (!tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        if (whitelist.contains(blockName)) {
            event.setDropItems(false);
            event.setExpToDrop(0);

            boolean preserveNBT = useNbtData.contains(blockName);

            ItemStack drop;
            if (preserveNBT) {
                drop = createItemWithNBT(block, blockType);
            } else {
                drop = new ItemStack(blockType);
            }

            if (drop != null) {
                final Location loc = block.getLocation().add(0.5, 0.5, 0.5);
                final ItemStack finalDrop = drop;

                Bukkit.getRegionScheduler().runDelayed(plugin, loc, (task) -> {
                    block.getWorld().dropItemNaturally(loc, finalDrop);
                }, 2L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Block block = event.getBlockPlaced();

        if (item.hasItemMeta() && item.getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();

            if (block.getType() == Material.SPAWNER && meta.getBlockState() instanceof CreatureSpawner) {
                CreatureSpawner itemSpawner = (CreatureSpawner) meta.getBlockState();
                EntityType spawnedType = itemSpawner.getSpawnedType();

                final Location loc = block.getLocation();

                Bukkit.getRegionScheduler().run(plugin, loc, (task) -> {
                    if (block.getState() instanceof CreatureSpawner) {
                        CreatureSpawner placedSpawner = (CreatureSpawner) block.getState();
                        placedSpawner.setSpawnedType(spawnedType);
                        placedSpawner.update(true, false);
                    }
                });
            }

            else if (block.getState() instanceof org.bukkit.block.TileState) {
                final Location loc = block.getLocation();

                Bukkit.getRegionScheduler().run(plugin, loc, (task) -> {
                    try {
                        block.getState().update(true, false);
                    } catch (Exception e) {
                        plugin.getLogger().warning("Could not restore NBT data for placed block: " + block.getType().name());
                    }
                });
            }
        }
    }

    private ItemStack createItemWithNBT(Block block, Material material) {
        ItemStack item = new ItemStack(material);

        if (material == Material.SPAWNER && block.getState() instanceof CreatureSpawner) {
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            EntityType spawnedType = spawner.getSpawnedType();

            BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
            if (meta != null) {
                CreatureSpawner spawnerState = (CreatureSpawner) meta.getBlockState();
                spawnerState.setSpawnedType(spawnedType);
                meta.setBlockState(spawnerState);
                item.setItemMeta(meta);
            }
        }


        else if (block.getState() instanceof org.bukkit.block.TileState) {
            try {
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                if (meta != null) {
                    meta.setBlockState(block.getState());
                    item.setItemMeta(meta);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not preserve NBT data for " + material.name());
            }
        }

        return item;
    }
}