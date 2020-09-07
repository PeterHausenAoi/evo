package main.java.com.github.PeterHausenAoi.evo.entities;

import java.awt.geom.Line2D;
import java.util.List;

public interface Collidable {
    boolean isColliding(Collidable other);
    Point getTopLeft();
    Point getTopRight();
    Point getBotLeft();
    Point getBotRight();
    Line2D getTop();
    Line2D getLeft();
    Line2D getRight();
    Line2D getBot();
    List<Point> getPoints();
    List<Line2D> getBorders();
}