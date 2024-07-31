package host.plas.justtags.events;

import host.plas.justtags.JustTags;
import host.plas.justtags.data.TagPlayer;
import host.plas.justtags.managers.TagManager;
import host.plas.justtags.utils.MenuUtils;
import host.plas.justtags.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class MainListener implements Listener {
    public MainListener() {
        Bukkit.getPluginManager().registerEvents(this, JustTags.getInstance());

        JustTags.getInstance().logInfo("Registered MainListener!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        TagManager.getOrGetPlayer(player.getUniqueId().toString());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Optional<TagPlayer> optional = TagManager.getOrGetPlayer(player.getUniqueId().toString());
        if (optional.isEmpty()) return;
        TagPlayer tagPlayer = optional.get();

        tagPlayer.save();
        tagPlayer.unregister();
    }

    @EventHandler
    private void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }

        if (item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (MenuUtils.hasKey(item)) {
            event.setCancelled(true);
        }
    }

//    @EventHandler
//    public void onDrag(InventoryDragEvent event) {
//        Player player = (Player) event.getWhoClicked();
//        ItemStack item = event.getOldCursor();
//        if (item == null) {
//            return;
//        }
//
//        if (item.getType() == Material.AIR) {
//            return;
//        }
//
//        ItemMeta meta = item.getItemMeta();
//        if (meta == null) {
//            event.setCancelled(true);
//            return;
//        }
//
//        if (MenuUtils.hasKey(item)) {
//            event.setCancelled(true);
//        }
//    }
}
