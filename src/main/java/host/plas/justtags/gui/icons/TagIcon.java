package host.plas.justtags.gui.icons;

import host.plas.justtags.data.ConfiguredTag;
import host.plas.justtags.data.TagPlayer;
import host.plas.justtags.managers.TagManager;
import host.plas.justtags.utils.MenuUtils;
import host.plas.justtags.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import mc.obliviate.inventory.Icon;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter @Setter
public class TagIcon extends Icon {
    private String tagIdentifier;
    private TagPlayer player;

    public TagIcon(String tagIdentifier, TagPlayer player) {
        super(getTagItem(player, tagIdentifier));

        this.tagIdentifier = tagIdentifier;
        this.player = player;

        applyEdits();
    }

    public void applyEdits() {
//        setLore(getLore());
    }

    public static boolean isEquipped(TagPlayer player, String tagIdentifier) {
        return getIndex(player, tagIdentifier) >= 0;
    }

    public static boolean isShown(TagPlayer player, String tagIdentifier) {
        return
                getIndex(player, tagIdentifier) <= player.getFinalMaxTags()
                        && isEquipped(player, tagIdentifier);
    }

    public static Map.Entry<Integer, ConfiguredTag> getEntry(TagPlayer player, String tagIdentifier) {
        Map.Entry<Integer, ConfiguredTag> entry = null;

        for (Map.Entry<Integer, ConfiguredTag> e : player.getContainer().entrySet()) {
            if (e.getValue().getIdentifier().equals(tagIdentifier)) {
                entry = e;
                break;
            }
        }

        return entry;
    }

    public static int getIndex(TagPlayer player, String tagIdentifier) {
        Map.Entry<Integer, ConfiguredTag> entry = getEntry(player, tagIdentifier);
        if (entry == null) {
            return -2; // not found -> -1 = not equipped
        }

        return entry.getKey();
    }
    
    public Optional<ConfiguredTag> getTag() {
        return TagManager.getTag(getTagIdentifier());
    }

    public static BaseComponent[] getIconName(TagPlayer player, String tagIdentifier) {
        Optional<ConfiguredTag> tag = TagManager.getTag(tagIdentifier);
        if (tag.isPresent()) {
            ConfiguredTag configuredTag = tag.get();
            return MessageUtils.color("&7(" + (player.hasTag(tagIdentifier) ? "&aEquipped" : "&cUnequipped") + "&7) &bTag&7: &r" + configuredTag.getValue());
        }

        return MessageUtils.color("&cUnknown Tag");
    }

    public static Component getIconNameComp(TagPlayer player, String tagIdentifier) {
        Optional<ConfiguredTag> tag = TagManager.getTag(tagIdentifier);
        if (tag.isPresent()) {
            ConfiguredTag configuredTag = tag.get();
            return MessageUtils.colorizeComp("&7(" + (player.hasTag(tagIdentifier) ? "&aEquipped" : "&cUnequipped") + "&7) &bTag&7: &r" + configuredTag.getValue());
        }

        return MessageUtils.colorizeComp("&cUnknown Tag");
    }

    public static String getIconNameHard(TagPlayer player, String tagIdentifier) {
        Optional<ConfiguredTag> tag = TagManager.getTag(tagIdentifier);
        if (tag.isPresent()) {
            ConfiguredTag configuredTag = tag.get();
            return MessageUtils.colorizeHard("&7(" + (player.hasTag(tagIdentifier) ? "&aEquipped" : "&cUnequipped") + "&7) &bTag&7: &r" + configuredTag.getValue());
        }

        return MessageUtils.colorizeHard("&cUnknown Tag");
    }

    public static List<BaseComponent[]> getLore(TagPlayer player, String tagIdentifier) {
        return List.of(
                MessageUtils.color("&eEquipped&7? " + (isEquipped(player, tagIdentifier) ? "&aYes" : "&cNo")),
                MessageUtils.color("&eShown&7? " + (isShown(player, tagIdentifier) ? "&aYes" : "&cNo")),
                MessageUtils.color("&eIndex&7: &f" + getIndex(player, tagIdentifier)),
                MessageUtils.color(""),
                MessageUtils.color("&7Click to " + (isEquipped(player, tagIdentifier) ? "&cunequip" : "&aequip") + " &7this tag.")
        );
    }

    public static List<Component> getLoreComp(TagPlayer player, String tagIdentifier) {
        return List.of(
                MessageUtils.colorizeComp("&eEquipped&7? " + (isEquipped(player, tagIdentifier) ? "&aYes" : "&cNo")),
                MessageUtils.colorizeComp("&eShown&7? " + (isShown(player, tagIdentifier) ? "&aYes" : "&cNo")),
                MessageUtils.colorizeComp("&eIndex&7: &f" + getIndex(player, tagIdentifier)),
                MessageUtils.colorizeComp(""),
                MessageUtils.colorizeComp("&7Click to " + (isEquipped(player, tagIdentifier) ? "&cunequip" : "&aequip") + " &7this tag.")
        );
    }

    public static List<String> getLoreHard(TagPlayer player, String tagIdentifier) {
        return List.of(
                MessageUtils.colorizeHard("&eEquipped&7? " + (isEquipped(player, tagIdentifier) ? "&aYes" : "&cNo")),
                MessageUtils.colorizeHard("&eShown&7? " + (isShown(player, tagIdentifier) ? "&aYes" : "&cNo")),
                MessageUtils.colorizeHard("&eIndex&7: &f" + getIndex(player, tagIdentifier)),
                MessageUtils.colorizeHard(""),
                MessageUtils.colorizeHard("&7Click to " + (isEquipped(player, tagIdentifier) ? "&cunequip" : "&aequip") + " &7this tag.")
        );
    }

    public static ItemStack getTagItem(TagPlayer player, String tagIdentifier) {
        ItemStack stack = new ItemStack(Material.NAME_TAG, 1);
        if (player.hasAvailableTag(tagIdentifier)) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
//                meta.setDisplayNameComponent(getIconName(player, tagIdentifier));
//                meta.setLoreComponents(getLore(player, tagIdentifier));

//                meta.displayName(getIconNameComp(player, tagIdentifier));
//                meta.lore(getLoreComp(player, tagIdentifier));

                meta.setDisplayName(getIconNameHard(player, tagIdentifier));
                meta.setLore(getLoreHard(player, tagIdentifier));

                stack.addUnsafeEnchantment(Enchantment.MENDING, 1);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                stack.setItemMeta(meta);
            }
        }

        MenuUtils.insertKey(stack);

        return stack;
    }
    
    public static Optional<TagIcon> validateAndGet(String tagIdentifier, TagPlayer player) {
        Optional<ConfiguredTag> tag = TagManager.getTag(tagIdentifier);
        if (tag.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(new TagIcon(tagIdentifier, player));
    }
}
