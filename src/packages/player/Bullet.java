package packages.player;

public class Bullet {
    public int x;
    public int y;
    boolean isPlayerBullet;
    boolean isBossBullet;

    public Bullet(int x, int y, boolean isPlayerBullet, boolean isBossBullet) {
        this.x = x;
        this.y = y;
        this.isPlayerBullet = isPlayerBullet;
        this.isBossBullet = isBossBullet;
    }
}
