package pacman;

import java.awt.Graphics2D;
import common.Vector2D;
//import java.awt.Rectangle;
import common.Rectangle;

/**
 * 05-Feb-2017, 18:54:47.
 *
 * @author Mo
 */
public class StaticGameObject extends GameObject {

    protected Vector2D position;
//    protected Vector2D center;
//    protected Rectangle.Float bounds;
    protected Rectangle bounds;

    /**
     * Should initialise here and not in subclass
     *
     * @param x x position
     * @param y y position
     * @param width width
     * @param height height
     */
    public StaticGameObject(float x, float y, float width, float height) {
        this.position = new Vector2D(x, y);
        this.bounds = new Rectangle(x, y, width, height);
//        this.bounds = new Rectangle.Float(x, y, width, height);
//        this.center = new Vector2D(position.x + width / 2, position.y + height / 2);
//        System.out.println("WIDTH: "+width);
    }

//    public void updateCenter() {
////        System.out.println("updating center");
//        center.x = position.x + bounds.width / 2f;
//        center.y = position.y + bounds.height / 2f;
//    }

    @Override
    void gameUpdate(float deltaTime) {
    }

    @Override
    void gameRender(Graphics2D g) {
    }

}
