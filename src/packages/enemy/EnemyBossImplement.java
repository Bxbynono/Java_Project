package packages.enemy;

public class EnemyBossImplement {

    public static EnemyBoss first;
    public static int size;

    public EnemyBossImplement() {
        first = null;
        size = 0;
    }

    public void push(int x, int y) {
        EnemyBoss newNode = new EnemyBoss(x, y);
        newNode.next = first;
        first = newNode;
        size++;
    }

    public void pop() {
        first = first.next;
        size--;
    }

    public boolean isStackEmpty() {
        return size == 0;
    }
    public void clear() {
    }
}
