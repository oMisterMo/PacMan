package pacman;

import common.Vector2D;

/**
 * An abstract class may have static fields and static methods.
 *
 * 02-Feb-2017, 21:39:38.
 *
 * @author Mo
 */
public class DynamicGameObject extends StaticGameObject {

    //Add other members
    protected Vector2D acceleration;
    protected Vector2D velocity;

    public DynamicGameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        acceleration = new Vector2D();
        velocity = new Vector2D();
    }
    
}
