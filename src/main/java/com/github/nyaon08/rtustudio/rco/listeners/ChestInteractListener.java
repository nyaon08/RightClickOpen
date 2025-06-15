package com.github.nyaon08.rtustudio.rco.listeners;

import com.github.nyaon08.rtustudio.rco.RightClickOpen;
import kr.rtuserver.framework.bukkit.api.listener.RSListener;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestInteractListener extends RSListener<RightClickOpen> {

    private final Map<UUID, ItemStack> openedShulkerMap = new HashMap<>();

    public ChestInteractListener(RightClickOpen plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) return;

        if (player.isSneaking() && action == Action.RIGHT_CLICK_AIR && item.getType().name().endsWith("SHULKER_BOX")) {
            if (!(item.getItemMeta() instanceof BlockStateMeta meta)) return;
            if (!(meta.getBlockState() instanceof ShulkerBox shulkerBox)) return;

            e.setCancelled(true);
            player.openInventory(shulkerBox.getInventory());

            openedShulkerMap.put(player.getUniqueId(), item.clone());
        }

        if (action == Action.RIGHT_CLICK_AIR && item.getType() == Material.ENDER_CHEST) {
            e.setCancelled(true);
            player.openInventory(player.getEnderChest());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;

        UUID uuid = player.getUniqueId();
        if (!openedShulkerMap.containsKey(uuid)) return;

        ItemStack item = openedShulkerMap.remove(uuid);

        if (!(item.getItemMeta() instanceof BlockStateMeta meta)) return;
        if (!(meta.getBlockState() instanceof ShulkerBox shulkerBox)) return;

        Inventory inventory = e.getInventory();
        shulkerBox.getInventory().setContents(inventory.getContents());
        shulkerBox.update();
        meta.setBlockState(shulkerBox);
        item.setItemMeta(meta);

        player.getInventory().setItemInMainHand(item);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack current = e.getItemDrop().getItemStack();

        if (openedShulkerMap.containsKey(uuid)) {
            ItemStack opened = openedShulkerMap.get(uuid);
            if (current.isSimilar(opened)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        UUID uuid = player.getUniqueId();
        ItemStack opened = openedShulkerMap.get(uuid);
        if (opened == null) return;

        if (e.getClick() == ClickType.NUMBER_KEY) {
            int hotbarButton = e.getHotbarButton();
            ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);
            if (hotbarItem != null && hotbarItem.isSimilar(opened)) {
                e.setCancelled(true);
            }
        }

        ItemStack current = e.getCurrentItem();
        ItemStack cursor = e.getCursor();

        if (current != null && current.isSimilar(opened) || cursor.isSimilar(opened)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        UUID uuid = player.getUniqueId();
        ItemStack opened = openedShulkerMap.get(uuid);
        if (opened == null) return;

        ItemStack dragged = e.getOldCursor();
        if (dragged.isSimilar(opened)) {
            e.setCancelled(true);
        }
    }
}
