package main.java.com.github.PeterHausenAoi.evo.entities;

abstract public class Actor extends BaseEntity implements Movable {
    private static final String TAG = Actor.class.getSimpleName();

    public Actor(Point topLeft, Point topRight, Point botLeft, Point botRight) {
        super(topLeft, topRight, botLeft, botRight);
    }

    public Actor(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}