package host.plas.justtags.managers;

import host.plas.justtags.JustTags;
import host.plas.justtags.data.ConfiguredTag;
import host.plas.justtags.data.TagPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;

public class TagManager {
    @Getter @Setter
    private static ConcurrentSkipListSet<ConfiguredTag> tags = new ConcurrentSkipListSet<>();

    public static void registerTag(ConfiguredTag tag) {
        unregisterTag(tag);
        tags.add(tag);
    }

    public static void unregisterTag(ConfiguredTag tag) {
        unregisterTag(tag.getIdentifier());
    }

    public static void unregisterTag(String identifier) {
        getTags().removeIf(tag -> tag.getIdentifier().equals(identifier));
    }

    public static void unregisterAllTags() {
        getTags().forEach(ConfiguredTag::save);

        tags.clear();
    }

    public static Optional<ConfiguredTag> getTag(String identifier) {
        return tags.stream().filter(tag -> tag.getIdentifier().equals(identifier)).findFirst();
    }

    public static boolean isTagLoaded(String identifier) {
        return tags.stream().anyMatch(tag -> tag.getIdentifier().equals(identifier));
    }

    @Getter @Setter
    private static ConcurrentSkipListSet<TagPlayer> loadedPlayers = new ConcurrentSkipListSet<>();

    public static void loadPlayer(TagPlayer player) {
        loadedPlayers.add(player);
    }

    public static void unloadPlayer(TagPlayer player, boolean save) {
        loadedPlayers.remove(player);

        if (save) {
            player.save();
        }
    }

    public static Optional<TagPlayer> getPlayer(String identifier) {
        return loadedPlayers.stream().filter(player -> player.getIdentifier().equalsIgnoreCase(identifier)).findFirst();
    }

    public static boolean isPlayerLoaded(String identifier) {
        return getPlayer(identifier).isPresent();
    }

    public static TagPlayer createNewPlayer(String identifier) {
        return new TagPlayer(identifier);
    }

    public static Optional<TagPlayer> getOrGetPlayer(String identifier) {
        Optional<TagPlayer> player = getPlayer(identifier);
        if (player.isPresent()) return player;

        TagPlayer newPlayer = createNewPlayer(identifier);
        newPlayer = newPlayer.augment(JustTags.getMainDatabase().loadPlayer(identifier));

        loadPlayer(newPlayer);

        return Optional.of(newPlayer);
    }
}
