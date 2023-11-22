
package packages.GameLogic;

import packages.enemy.EnemyLinkedList;
import packages.enemy.EnemyNode;
import packages.player.Bullet;
import packages.player.LinkedList;
import packages.player.Node;
import packages.enemy.EnemyBoss;
import packages.enemy.EnemyBossImplement;
import packages.enemy.EnemyBossHealth;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class GameLogic {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    public static final char PLAYER_CHAR = '^';
    public static final char PLAYER_BULLET_CHAR = 'o';
    public static final char ENEMY_CHAR = '8';
    public static final char ENEMY_BULLET_CHAR = 'o';
    public static final int BULLET_SPEED = 3;
    public static final int ENEMY_FIRE_RATE = 40;
    public static final int ENEMY_BULLET_RATE = 0;
    private static final int ENEMY_BULLET_INTERVAL = 1000;
    private static final int BOSS_BULLET_INTERVAL = 1000;

    public static boolean isGameOver;
    public static int enemySpawnedCount = 0;
    public static int maxEnemies = 15;
    public static long lastEnemyFireTime = 0;
    public static long lastEnemyMoveTime = 0;
    public static long lastBossMoveTime = 0;
    public static long lastBossBulletTime = 0;

    public static final long ENEMY_MOVE_INTERVAL = 1000;
    public static int playerX;
    private static final int PLAYER_SPEED = 10;
    private static long lastEnemyBulletTime = 0;
    public static int playerHealth = 10;
    public static boolean minionsDefeated = false;
    static int startX = 20;
    static int startY = 20;
    public static int bossNumber = 1;
    public static LinkedList playerBullets;
    public static LinkedList enemyBullets;
    public static LinkedList bossBullets;
    public static EnemyLinkedList enemyLinkedList;
    public static double enemyImageWidth = 20;
    public static double enemyImageHeight = 20;
    public static double bossImageWidth = 70;
    public static double bossImageHeight = 70;
    // public static EnemyBoss enemyBossStack;
    public static EnemyBossImplement enemyBossStack;
    public static String playerUsername;

    public static void spawnBoss(long currentNanoTime) {
        if (!isGameOver && minionsDefeated && EnemyBossImplement.size < bossNumber) {
            int centerX = WIDTH / 2;
            int centerY = 200 / 2; // Assuming HEIGHT is the height of your game window
            enemyBossStack.push((int) (centerX - bossImageWidth / 2), (int) (centerY - bossImageHeight / 2));

        }
    }

    public static void fireBossBullet(long currentNanoTime) {
        if (currentNanoTime - lastBossBulletTime > BOSS_BULLET_INTERVAL * 1_000_000) {
            if (enemyBossStack.first != null) { // Check if the boss has spawned
                EnemyBoss boss = enemyBossStack.first;
                while (boss != null) {
                    int bulletX = (int) (boss.x + (bossImageWidth / 2));
                    int bulletY = (int) (boss.y + (bossImageHeight / 2));
                    bossBullets.insertBullet(bulletX, bulletY, false, true);
                    boss = boss.next;
                }
                lastBossBulletTime = currentNanoTime;
            }
        }
    }

    public static void moveBossBullets() {
        Node current = bossBullets.head;

        while (current != null) {
            Bullet bullet = current.data;
            int bulletY = bullet.y + BULLET_SPEED;
            if (bulletY > HEIGHT) {
                bossBullets.deleteBullet(bullet);

            } else {
                bullet.y = bulletY;
            }

            current = current.next;
        }
    }

    public static void moveEnemyBoss(long currentNanoTime) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastBossMoveTime >= 1000) {
            int moveDirection = Math.random() < 0.5 ? -1 : 1;

            EnemyBoss boss = enemyBossStack.first;
            while (boss != null) {
                int newX = boss.x + moveDirection * 10;

                newX = Math.max(10, Math.min(WIDTH - 10, newX));

                boss.x = newX;

                boss = boss.next;
            }

            lastBossMoveTime = currentTime;
        }
    }

    public static void checkPlayerCollisionWithBossBullets() {
        Node bulletNode = bossBullets.head;
        while (bulletNode != null) {
            Bullet bossBullet = bulletNode.data;

            if (playerHitByBoss(bossBullet, playerX, HEIGHT - 20)) {
                bossBullets.deleteBullet(bossBullet);
                playerIsHit();
                try {
                    playDestroyMusic();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }

            }

            bulletNode = bulletNode.next;
        }
    }

    public static void spawnEnemies(long currentNanoTime) {
        if (!isGameOver && enemySpawnedCount < maxEnemies
                && currentNanoTime - lastEnemyFireTime > ENEMY_FIRE_RATE * 1_000_000) {

            // Adjusted bullet position based on enemy image width and height
            int bulletX = (int) (startX + (enemyImageWidth / 2));
            int bulletY = (int) (startY + (enemyImageHeight / 2));
            enemyLinkedList.insertEnemy(startX, startY);

            enemyBullets.insertBullet(bulletX, bulletY, false, false);
            lastEnemyFireTime = currentNanoTime;
            enemySpawnedCount++;
            startX += 25;
            if (startX >= WIDTH) {
                startX = 20;
                startY += 25;
            }
        }
    }

    public static void moveEnemyShips(long currentNanoTime) {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastEnemyMoveTime >= 1000) {
            EnemyNode enemy = enemyLinkedList.head;
            while (enemy != null) {
                enemy.x += 15;
                if (enemy.x >= WIDTH) {
                    enemy.x = 20;
                    enemy.y += 25;
                }
                enemy = enemy.next;
            }

            lastEnemyMoveTime = currentTime;
        }

    }

    public static void movePlayerBullets() {
        Node current = playerBullets.head;
        while (current != null) {
            int bulletY = current.data.y;
            bulletY -= BULLET_SPEED;
            if (bulletY < 0) {
                playerBullets.deleteBullet(current.data);
            } else {
                current.data.y = bulletY;
            }
            current = current.next;
        }
    }

    public static void checkPlayerCollisionWithEnemyBullets() {
        Node bulletNode = enemyBullets.head;
        while (bulletNode != null) {
            Bullet enemyBullet = bulletNode.data;

            if (playerHit(enemyBullet, playerX, HEIGHT - 20)) {
                enemyBullets.deleteBullet(enemyBullet);
                try {
                    GameLogic.playDestroyMusic();
                } catch (UnsupportedAudioFileException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
                playerIsHit();

            }

            bulletNode = bulletNode.next;
        }
    }

    public static void playerIsHit() {
        playerHealth--;
        System.out.println("You have been hit! Your remaining health is " + playerHealth);

        if (playerHealth <= 0) {
            isGameOver = true;
        }
    }

    public static boolean playerHit(Bullet bullet, int playerX, int playerY) {
        double collisionThreshold = 15.0;
        // Adjust the collision check based on the center of the boss image
        double playerImageWidth = 25;
        double playerImageHeight = 25;

        double playerCenterX = playerX + playerImageWidth / 2;
        double playerCenterY = playerY + playerImageHeight / 2;
        double dx = bullet.x - playerCenterX;
        double dy = bullet.y - playerCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < collisionThreshold;
    }

    public static boolean playerHitByBoss(Bullet bullet, int playerX, int playerY) {
        double collisionThreshold = 15.0;
        // Adjust the collision check based on the center of the boss image
        double playerImageWidth = 25;
        double playerImageHeight = 25;

        double playerCenterX = playerX + playerImageWidth / 2;
        double playerCenterY = playerY + playerImageHeight / 2;
        double dx = bullet.x - playerCenterX;
        double dy = bullet.y - playerCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < collisionThreshold;
    }

    public static boolean isCollisionWithBoss(Bullet bullet, EnemyBoss boss, double bossImageWidth,
            double bossImageHeight) {
        double collisionThreshold = 20.0;

        // Adjust the collision check based on the center of the boss image
        double bossCenterX = boss.x + bossImageWidth / 2;
        double bossCenterY = boss.y + bossImageHeight / 2;

        double dx = bullet.x - bossCenterX;
        double dy = bullet.y - bossCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < collisionThreshold;
    }

    public static void bossHitByPlayer() {
        Node playerBulletNode = playerBullets.head;

        while (playerBulletNode != null) {
            Bullet playerBullet = playerBulletNode.data;
            Node currentBulletNode = playerBulletNode;

            EnemyBoss enemyBossNode = enemyBossStack.first;

            while (enemyBossNode != null) {
                if (isCollisionWithBoss(playerBullet, enemyBossNode, bossImageWidth, bossImageHeight)) {
                    playerBullets.deleteBullet(playerBullet);
                    EnemyBossHealth.health--;
                    try {
                        playDestroyMusic();
                    } catch (UnsupportedAudioFileException e) {
                            e.printStackTrace();
                    } catch (IOException e) {
                            e.printStackTrace();
                    } catch (LineUnavailableException e) {
                            e.printStackTrace();
                    }
                    break; // You can decide whether to break the loop after a collision
                }

                enemyBossNode = enemyBossNode.next;
            }

            playerBulletNode = currentBulletNode.next;
        }
    }

    public static void checkCollisions() {
        Node playerBulletNode = playerBullets.head;

        while (playerBulletNode != null) {
            Bullet playerBullet = playerBulletNode.data;
            Node currentBulletNode = playerBulletNode;

            EnemyNode enemyNode = enemyLinkedList.head;

            while (enemyNode != null) {
                EnemyNode enemy = enemyNode;

                if (isCollision(playerBullet, enemy, enemyImageWidth, enemyImageHeight)) {
                    playerBullets.deleteBullet(playerBullet);
                    enemyLinkedList.deleteEnemy(enemy);
                    try {
                        playDestroyMusic();
                    } catch (UnsupportedAudioFileException e) {
                            e.printStackTrace();
                    } catch (IOException e) {
                            e.printStackTrace();
                    } catch (LineUnavailableException e) {
                            e.printStackTrace();
                    }
                    break;
                }

                enemyNode = enemyNode.next;
            }

            playerBulletNode = currentBulletNode.next;
        }

        if (enemyLinkedList.head == null) {
            minionsDefeated = true;
        }
    }

    public static boolean isCollision(Bullet bullet, EnemyNode enemy, double enemyImageWidth, double enemyImageHeight) {
        double collisionThreshold = 15.0;

        double enemyCenterX = enemy.x + enemyImageWidth / 2;
        double enemyCenterY = enemy.y + enemyImageHeight / 2;
        double dx = bullet.x - enemyCenterX;
        double dy = bullet.y - enemyCenterY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance < (collisionThreshold);
    }

    public static void movePlayerLeft() {
        if (playerX > 0) {
            playerX -= PLAYER_SPEED;
        }
    }

    public static void movePlayerRight() {
        if (playerX < WIDTH - 10) {
            playerX += PLAYER_SPEED;
        }
    }

    public static void fireBullet() {
        double playerImageWidth = 25;
        double playerImageHeight = 25;
        // int bulletY = HEIGHT - 40;
        int bulletX = (int) (playerX + (playerImageWidth / 2));
        int bulletY = (int) (HEIGHT - 40 + (playerImageHeight / 2));

        playerBullets.insertBullet(bulletX, bulletY, true, false);
    }

    public static void moveEnemyBullets() {
        Node current = enemyBullets.head;
        while (current != null) {
            int bulletY = current.data.y;
            bulletY += BULLET_SPEED;
            if (bulletY > HEIGHT) {
                enemyBullets.deleteBullet(current.data);
            } else {
                current.data.y = bulletY;
            }
            current = current.next;
        }
    }

    public static void shootEnemyBullet(long currentNanoTime) {
        if (currentNanoTime - lastEnemyBulletTime > ENEMY_BULLET_INTERVAL * 1_000_000) {
            EnemyNode enemy = enemyLinkedList.head;
            while (enemy != null) {
                // Adjusted bullet position based on enemy image width and height
                int bulletX = enemy.x + (int) (enemyImageWidth / 2);
                int bulletY = enemy.y + (int) (enemyImageHeight / 2);

                enemyBullets.insertBullet(bulletX, bulletY, false, false);
                enemy = enemy.next;
            }
            lastEnemyBulletTime = currentNanoTime;
        }
    }

    public static void playShootingSoud() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File file = new File("D://GalacticInvader//GalacticInvader//src//sound//shoot.wav");

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        clip.start();

    }

    public static void playBackgroundMusic()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File file = new File("D://GalacticInvader//GalacticInvader//src//sound//2_theme.wav");

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        clip.start();

    }

    public static void playDestroyMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File file = new File("D://GalacticInvader//GalacticInvader//src//sound//4_target_hit_effect.wav");

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        clip.start();

    }

}
