package packages.enemy;

public class EnemyLinkedList {
    public EnemyNode head;
    public EnemyNode tail;

    public EnemyLinkedList() {
        head = null;
        tail = null;
    }
    
    public void insertEnemy(int x, int y) {
        EnemyNode newNode = new EnemyNode(x, y);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public void deleteEnemy(EnemyNode enemy) {
        if (head == enemy) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            return;
        }

        EnemyNode current = head;
        while (current != null && current.next != enemy) {
            current = current.next;
        }
        if (current != null) {
            current.next = enemy.next;
            if (enemy == tail) {
                tail = current;
            }
        }
    }
    public void clear() {
    }
}
