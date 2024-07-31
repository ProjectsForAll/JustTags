package host.plas.justtags.utils;

import host.plas.justtags.JustTags;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ConcurrentSkipListSet;

public class MenuUtils {
    public static ConcurrentSkipListSet<Integer> getOuter(int rows) {
        ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<>();
        if (rows < 1) return set;
        if (rows > 6) rows = 6;

        // top and bottom
        for (int r = 1; r <= rows; r++) {
            for (int i = 1; i <= 9; i++) {
                int real = i * r - 1;

                if (real > 9 && real < 17) {
                    continue;
                }
                if (real > 18 && real < 26) {
                    continue;
                }
                if (real > 27 && real < 35) {
                    continue;
                }
                if (real > 36 && real < 44) {
                    continue;
                }
                if (real > 45 && real < 53) {
                    continue;
                }

                set.add(real);
            }
        }

        return set;
    }

    public static NamespacedKey getCheckKey() {
        return new NamespacedKey(JustTags.getInstance(), "check");
    }

    public static void insertKey(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(getCheckKey(), PersistentDataType.BOOLEAN, true);
            stack.setItemMeta(meta);
        }
    }

    public static boolean hasKey(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().has(getCheckKey(), PersistentDataType.BOOLEAN);
    }
}
