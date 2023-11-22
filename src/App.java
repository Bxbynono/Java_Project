import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import packages.enemy.EnemyBoss;
import packages.enemy.EnemyBossImplement;
import packages.enemy.EnemyBossHealth;
import packages.enemy.EnemyLinkedList;
import packages.enemy.EnemyNode;
import packages.GameLogic.GameLogic;
import packages.player.Bullet;
import packages.player.LinkedList;
import packages.player.Node;

public class App extends Application {

    private Button tryAgainButton;
    private Button gameOverButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GameLogic.playerBullets = new LinkedList();
        GameLogic.enemyBullets = new LinkedList();
        GameLogic.bossBullets = new LinkedList();
        GameLogic.enemyLinkedList = new EnemyLinkedList();
        GameLogic.enemyBossStack = new EnemyBossImplement();
        GameLogic.playerX = GameLogic.WIDTH / 2;

        Pane root = new Pane();
        Scene scene = new Scene(root, GameLogic.WIDTH, GameLogic.HEIGHT);
        Canvas canvas = new Canvas(GameLogic.WIDTH, GameLogic.HEIGHT);
        root.getChildren().add(canvas);
        Image backgroundImage = new Image("./img/Game_bg.png");

        GraphicsContext gc = canvas.getGraphicsContext2D();

        tryAgainButton = new Button("Try Again");
        tryAgainButton.setStyle("-fx-font-size: 20;");
        tryAgainButton.setTranslateX(GameLogic.WIDTH / 2 - 75);
        tryAgainButton.setTranslateY(GameLogic.HEIGHT / 2 - 25);
        tryAgainButton.setVisible(false);

        gameOverButton = new Button("Game Over");
        gameOverButton.setStyle("-fx-font-size: 20;");
        gameOverButton.setTranslateX(GameLogic.WIDTH / 2 - 75);
        gameOverButton.setTranslateY(GameLogic.HEIGHT / 2 - 25);
        gameOverButton.setVisible(false);

        Pane gameOverPane = new Pane();
        gameOverPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        gameOverPane.getChildren().addAll(tryAgainButton, gameOverButton);
        root.getChildren().add(gameOverPane);

        // Set initial visibility of gameOverPane to false
        gameOverPane.setVisible(false);

        //play sound
        if (!GameLogic.isGameOver) {
            try {
                GameLogic.playBackgroundMusic();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                gc.drawImage(backgroundImage, 0, 0, GameLogic.WIDTH, GameLogic.HEIGHT);
                updateGame(currentNanoTime);
                drawGame(gc);
            }

            private void updateGame(long currentNanoTime) {
                GameLogic.spawnEnemies(currentNanoTime);
                GameLogic.movePlayerBullets();
                GameLogic.checkCollisions();
                GameLogic.shootEnemyBullet(currentNanoTime);
                GameLogic.moveEnemyBullets();
                GameLogic.moveEnemyShips(currentNanoTime);
                GameLogic.checkPlayerCollisionWithEnemyBullets();

                if (GameLogic.minionsDefeated) {
                    GameLogic.spawnBoss(currentNanoTime);
                    GameLogic.moveEnemyBoss(currentNanoTime);
                    GameLogic.fireBossBullet(currentNanoTime);
                    GameLogic.moveBossBullets();
                    GameLogic.checkPlayerCollisionWithBossBullets();
                    GameLogic.bossHitByPlayer();

                }

                if (GameLogic.playerHealth == 0 || EnemyBossHealth.health == 0) {
                    stop();
                    GameLogic.isGameOver = true;
                    showGameOverScreen();
                }
            }

            private void showGameOverScreen() {
                if (EnemyBossHealth.health == 0) {
                    gameOverButton.setVisible(true);
                }

                if (GameLogic.playerHealth == 0) {
                    tryAgainButton.setVisible(true);
                    tryAgainButton.setOnAction(e -> {
                        resetGame();
                        gameOverPane.setVisible(false);
                        GameLogic.isGameOver = false;
                        gameOverButton.setVisible(false);
                        tryAgainButton.setVisible(false);
                        start();
                    });
                }

                gameOverPane.setVisible(true);
            }
        }.start();

        scene.setOnKeyPressed(e -> {
            if (!GameLogic.isGameOver) {
                KeyCode keyCode = e.getCode();
                if (keyCode == KeyCode.A) {
                    GameLogic.movePlayerLeft();
                } else if (keyCode == KeyCode.D) {
                    GameLogic.movePlayerRight();
                } else if (keyCode == KeyCode.W) {
                    GameLogic.fireBullet();
                    try {
                        GameLogic.playShootingSoud();
                    } catch (UnsupportedAudioFileException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (LineUnavailableException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Galactic Invader");
        primaryStage.show();
    }

    private void resetGame() {
        GameLogic.playerHealth = 10; // Reset player health to the initial value
        GameLogic.playerX = GameLogic.WIDTH / 2;
        GameLogic.minionsDefeated = false;
        GameLogic.enemyLinkedList.clear();
        GameLogic.enemyBossStack.clear();

        EnemyBossHealth.health = 20; // Reset boss health to the initial value

        gameOverButton.setVisible(false);
    }

    public void drawGame(GraphicsContext gc) {
        Image playerImage = new Image("./img/spaceship.png");
        double playerImageWidth = 25;
        double playerImageHeight = 25;
        double yOffset = 10;
        double playerHealthX = GameLogic.WIDTH - 150;
        double playerHealthY = 20;
        double playerHealthWidth = 100; // Width of the player health bar

        // Draw player
        gc.drawImage(playerImage, GameLogic.playerX, GameLogic.HEIGHT - 20 - yOffset, playerImageWidth,
                playerImageHeight);

        // Draw player bullets
        Node current = GameLogic.playerBullets.head;
        while (current != null) {
            Bullet bullet = current.data;
            gc.setFill(Color.WHITE);

            // Use the bullet size from the Bullet object
            gc.fillOval(bullet.x, bullet.y, 3, 3);

            current = current.next;
        }

        // Draw enemy bullets
        gc.setFill(Color.YELLOW);
        current = GameLogic.enemyBullets.head;
        while (current != null) {
            Bullet bullet = current.data;
            gc.fillOval(bullet.x, bullet.y, 3, 3);
            gc.fillText(String.valueOf(GameLogic.ENEMY_BULLET_CHAR), bullet.x, bullet.y);
            current = current.next;
        }

        // Draw enemy ships
        Image enemyImage = new Image("./img/enemyShip.png");
        double enemyImageWidth = 20;
        double enemyImageHeight = 20;
        gc.setFill(Color.BLUE);
        EnemyNode enemy = GameLogic.enemyLinkedList.head;
        while (enemy != null) {
            gc.drawImage(enemyImage, enemy.x, enemy.y, enemyImageWidth, enemyImageHeight);
            enemy = enemy.next;
        }

        if (GameLogic.minionsDefeated) {
            // Draw boss and its bullets
            Image bossImage = new Image("./img/boss_img.png");
            double bossImageWidth = 70;
            double bossImageHeight = 70;
            gc.setFill(Color.GREENYELLOW);
            EnemyBoss enemyboss = GameLogic.enemyBossStack.first;
            while (enemyboss != null) {
                gc.drawImage(bossImage, enemyboss.x, enemyboss.y, bossImageWidth, bossImageHeight);
                enemyboss = enemyboss.next;
            }

            gc.setFill(Color.YELLOWGREEN);
            current = GameLogic.bossBullets.head;
            while (current != null) {
                Bullet bullet = current.data;
                gc.fillOval(bullet.x, bullet.y, 3, 3);
                gc.fillText(String.valueOf(GameLogic.ENEMY_BULLET_CHAR), bullet.x, bullet.y);
                current = current.next;
            }

            // Draw boss health bar at the top left of the window
            double bossHealthX = 20; // Adjusted X-coordinate for boss health bar
            double bossHealthY = 20; // Adjusted Y-coordinate for boss health bar
            double bossHealthWidth = 150; // Width of the boss health bar
            double bossHealthHeight = 10; // Height of the boss health bar
            gc.setFill(Color.RED);
            gc.fillRect(bossHealthX, bossHealthY, bossHealthWidth * (EnemyBossHealth.health / 10.0), bossHealthHeight);
            gc.setFill(Color.WHITE);
            gc.fillText("Boss Health:", bossHealthWidth, bossHealthHeight + 5);
        }

        // Draw player health bar
        double playerHealthHeight = 10; // Height of the player health bar
        gc.setFill(Color.GREEN);
        gc.fillRect(playerHealthX, playerHealthY, playerHealthWidth * (GameLogic.playerHealth / 10.0),
                playerHealthHeight);

        // Draw player health text
        gc.setFill(Color.WHITE);
        gc.fillText("Player Health: " + GameLogic.playerHealth, playerHealthX, playerHealthY - 5);

    }
}
