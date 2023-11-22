package packages.enemy;

public class EnemyBoss {
    public EnemyBoss next;
    public int x;
    public int y;

    public EnemyBoss(int x, int y) {
        this.x = x;
        this.y = y;
        next = null;
    }
}
