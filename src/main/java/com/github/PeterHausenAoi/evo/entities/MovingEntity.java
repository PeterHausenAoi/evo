package main.java.com.github.PeterHausenAoi.evo.entities;

import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public class MovingEntity extends BaseEntity {
    private static final String TAG = MovingEntity.class.getSimpleName();

    public MovingEntity(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void draw(GraphicsContext g) {

    }

    @Override
    public List<Point> getPoints() {
        return null;
    }
}