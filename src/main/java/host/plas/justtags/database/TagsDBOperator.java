package host.plas.justtags.database;

import host.plas.bou.sql.ConnectorSet;
import host.plas.bou.sql.DBOperator;
import host.plas.bou.sql.DatabaseType;
import host.plas.justtags.JustTags;
import host.plas.justtags.data.ConfiguredTag;
import host.plas.justtags.data.TagPlayer;
import host.plas.justtags.managers.TagManager;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class TagsDBOperator extends DBOperator {
    public TagsDBOperator(ConnectorSet set) {
        super(set, JustTags.getInstance());
    }

    @Override
    public void ensureDatabase() {
        String s1 = Statements.getStatement(Statements.StatementType.CREATE_DATABASE, this.getConnectorSet());

        this.execute(s1, stmt -> {});
    }

    @Override
    public void ensureTables() {
        String s1 = Statements.getStatement(Statements.StatementType.CREATE_TABLES, this.getConnectorSet());

        this.execute(s1, stmt -> {});
    }

    public void pushPlayer(TagPlayer player) {
        pushPlayer(player, true);
    }

    public void pushPlayer(TagPlayer player, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> savePlayer(player).join());
        } else {
            savePlayer(player).join();
        }
    }

    public CompletableFuture<Boolean> savePlayer(TagPlayer player) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PUSH_PLAYER_MAIN, this.getConnectorSet());

            this.execute(s1, stmt -> {
                try {
                    stmt.setString(1, player.getIdentifier());
                    stmt.setString(2, player.getComputableContainer());
                    stmt.setString(3, player.getComputableAvailableTags());

                    if (getType() == DatabaseType.MYSQL) {
                        stmt.setString(4, player.getComputableContainer());
                        stmt.setString(5, player.getComputableAvailableTags());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });

            return true;
        });
    }

    public CompletableFuture<Optional<TagPlayer>> loadPlayer(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PULL_PLAYER_MAIN, this.getConnectorSet());

            AtomicReference<Optional<TagPlayer>> atomicReference = new AtomicReference<>(Optional.empty());
            this.executeQuery(s1, stmt -> {
                try {
                    stmt.setString(1, uuid);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }, set -> {
                try {
                    if (set.next()) {
                        TagPlayer player = new TagPlayer(uuid);

                        String container = set.getString("Container");
                        String available = set.getString("Available");

                        player.computeContainer(container);
                        player.computeAvailableTags(available);

                        atomicReference.set(Optional.of(player));
                        return;
                    }
                    atomicReference.set(Optional.empty());
                } catch (Exception e) {
                    e.printStackTrace();
                    atomicReference.set(Optional.empty());
                }
            });

            return atomicReference.get();
        });
    }

    public CompletableFuture<Boolean> playerExists(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PLAYER_EXISTS, this.getConnectorSet());

            AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
            this.executeQuery(s1, stmt -> {
                try {
                    stmt.setString(1, uuid);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }, set -> {
                try {
                    if (set.next()) {
                        int i = set.getInt(1);

                        atomicReference.set(i > 0);
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                atomicReference.set(false);
            });

            return atomicReference.get();
        });
    }

    public void pushTag(ConfiguredTag tag) {
        pushTag(tag, true);
    }

    public void pushTag(ConfiguredTag tag, boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> saveTag(tag).join());
        } else {
            saveTag(tag).join();
        }
    }

    public CompletableFuture<Boolean> saveTag(ConfiguredTag tag) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PUSH_TAG, this.getConnectorSet());

            this.execute(s1, stmt -> {
                try {
                    stmt.setString(1, tag.getIdentifier());
                    stmt.setString(2, tag.getValue());

                    if (getType() == DatabaseType.MYSQL) {
                        stmt.setString(3, tag.getValue());
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });

            return true;
        });
    }

    public CompletableFuture<Optional<ConfiguredTag>> loadTag(String identifier) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PULL_TAG, this.getConnectorSet());

            AtomicReference<Optional<ConfiguredTag>> atomicReference = new AtomicReference<>(Optional.empty());
            this.executeQuery(s1, stmt -> {
                try {
                    stmt.setString(1, identifier);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }, set -> {
                if (set == null) {
                    atomicReference.set(Optional.empty());
                    return;
                }

                try {
                    if (set.next()) {
                        ConfiguredTag tag = new ConfiguredTag(identifier);

                        String value = set.getString("Value");

                        tag.setValue(value);

                        atomicReference.set(Optional.of(tag));
                    }

                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                atomicReference.set(Optional.empty());
            });

            return atomicReference.get();
        });
    }

    public CompletableFuture<Boolean> tagExists(String identifier) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.TAG_EXISTS, this.getConnectorSet());
            if (s1 == null) return false;
            if (s1.isBlank() || s1.isEmpty()) return false;

            s1 = s1.replace("%identifier%", identifier);

            AtomicReference<Boolean> atomicReference = new AtomicReference<>(false);
            this.executeQuery(s1, stmt -> {
                try {
                    stmt.setString(1, identifier);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }, set -> {
                if (set == null) {
                    atomicReference.set(false);
                    return;
                }

                try {
                    if (set.next()) {
                        int i = set.getInt(1);

                        atomicReference.set(i > 0);
                    }

                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                atomicReference.set(false);
            });

            return atomicReference.get();
        });
    }

    public CompletableFuture<Boolean> loadAllTags() {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.PULL_ALL_TAGS, this.getConnectorSet());

            TagManager.unregisterAllTags(); // after get statement in case of errors.

            this.executeQuery(s1, stmt -> {}, set -> {
                if (set == null) {
                    return;
                }

                try {
                    while (set.next()) {
                        String identifier = set.getString("Identifier");
                        String value = set.getString("Value");

                        ConfiguredTag tag = new ConfiguredTag(identifier, value);
                        tag.register();
                    }

                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return true;
        });
    }

    public CompletableFuture<Boolean> dropTag(String identifier) {
        return CompletableFuture.supplyAsync(() -> {
            ensureUsable();

            String s1 = Statements.getStatement(Statements.StatementType.DROP_TAG, this.getConnectorSet());

            this.execute(s1, stmt -> {
                try {
                    stmt.setString(1, identifier);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });

            return true;
        });
    }
}
