package packages.player;

public class Node {
    public Bullet data;
    public Node next;

    public Node(Bullet data) {
        this.data = data;
        next = null;
    }
}
