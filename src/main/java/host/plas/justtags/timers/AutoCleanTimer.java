package host.plas.justtags.timers;

import host.plas.bou.scheduling.BaseRunnable;
import host.plas.justtags.managers.TagManager;

public class AutoCleanTimer extends BaseRunnable {
    public AutoCleanTimer() {
        super(5 * 20, 5 * 20);
    }

    @Override
    public void run() {
        TagManager.getLoadedPlayers().forEach(tagPlayer -> {
            tagPlayer.cleanMap();

            if (! tagPlayer.isOnline()) {
                TagManager.unloadPlayer(tagPlayer, true);
            }
        });
    }
}
