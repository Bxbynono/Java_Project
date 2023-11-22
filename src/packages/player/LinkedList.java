package packages.player;

public class LinkedList {
    public Node head, tail;

    public LinkedList() {
        head = null;
        tail = null;
    }

    public int size() {
        Node current = head;
        int count = 0;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public void insertBullet(int x, int y, boolean isPlayerBullet, boolean isBossBullet) {
        Bullet bullet = new Bullet(x, y, isPlayerBullet, isBossBullet);
        Node newNode = new Node(bullet);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
    }

    public void deleteBullet(Bullet bullet) {
        if (head == null) {
            return;
        }
        if (head.data == bullet) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            return;
        }
        Node current = head;
        while (current.next != null) {
            if (current.next.data == bullet) {
                current.next = current.next.next;
                if (current.next == null) {
                    tail = current;
                }
                return;
            }
            current = current.next;
        }
    }

}