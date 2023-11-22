package packages.enemy;

public class EnemyNode {
    public int x;
    public int y;
    public EnemyNode next;

    public EnemyNode(int x, int y) {
        this.x = x;
        this.y = y;
        this.next = null;
    }
}
