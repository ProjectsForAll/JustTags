package host.plas.justtags.timers;

import host.plas.bou.scheduling.BaseRunnable;
import host.plas.justtags.data.ConfiguredTag;
import host.plas.justtags.data.TagPlayer;
import host.plas.justtags.managers.TagManager;

public class AutoSaveTimer extends BaseRunnable {
    public AutoSaveTimer() {
        super(60 * 20, 60 * 20);
    }

    @Override
    public void run() {
        TagManager.getLoadedPlayers().forEach(TagPlayer::save);
        TagManager.getTags().forEach(ConfiguredTag::save);
    }
}
