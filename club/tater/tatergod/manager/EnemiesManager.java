package club.tater.tatergod.manager;

import club.tater.tatergod.Tater;
import club.tater.tatergod.util.Enemy;
import io.netty.util.internal.ConcurrentSet;
import java.util.Iterator;
import java.util.function.Predicate;

public class EnemiesManager extends RotationManager {

    private static final ConcurrentSet enemiesManager = new ConcurrentSet();

    public static void addEnemy(String name) {
        EnemiesManager.enemiesManager.add(new Enemy(name));
    }

    public static void delEnemy(String name) {
        EnemiesManager.enemiesManager.remove(getEnemyByName(name));
    }

    public static Enemy getEnemyByName(String name) {
        Iterator iterator = EnemiesManager.enemiesManager.iterator();

        Enemy e;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            e = (Enemy) iterator.next();
        } while (!Tater.enemy.username.equalsIgnoreCase(name));

        return e;
    }

    public static ConcurrentSet getEnemies() {
        return EnemiesManager.enemiesManager;
    }

    public static boolean isEnemy(String name) {
        return EnemiesManager.enemiesManager.stream().anyMatch((enemy) -> {
            return enemy.username.equalsIgnoreCase(name);
        });
    }
}
