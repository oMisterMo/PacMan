package pacman;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.geom.AffineTransform;

/**
 * Represents a player within our game world
 *
 * 32 pixels = 1 meter
 *
 * 07-Sep-2016, 01:56:02.
 *
 * @author Mo
 */
public class Pac extends DynamicGameObject {

    public static final float PLAYER_WIDTH = 32;    //32
    public static final float PLAYER_HEIGHT = 32;   //32

    public static final int NO_OF_LIVES = 3;
    public int lives = NO_OF_LIVES;
    public static final int X_VEL = 200;
    public static final int Y_VEL = 200;

    //Player states
    public static final int PLAYER_ALIVE = 0;
    public static final int PLAYER_HURT = 1;
    public static final int PLAYER_DEAD = 2;
    public int state = PLAYER_ALIVE;
    public float stateTime = 0;

    private float rotation;
    private AffineTransform trans;

    public Pac(float x, float y) {
        super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);

        System.out.println("Player created...");
    }

    private void upDownLeftRight() {
        //Up pressed
        if (Input.isKeyPressed(KeyEvent.VK_W)) {
//            System.out.println("W");
            velocity.x = 0;
            velocity.y = -Y_VEL;
            rotation = 180;
        } else if (Input.isKeyReleased(KeyEvent.VK_W)) {
//            velocity.y = 0;
        }
        //Right pressed
        if (Input.isKeyPressed(KeyEvent.VK_D)) {
//            System.out.println("D");
            velocity.y = 0;
            velocity.x = X_VEL;
            rotation = 270;
        } else if (Input.isKeyReleased(KeyEvent.VK_D)) {
//            velocity.x = 0;
        }
        //Down pressed
        if (Input.isKeyPressed(KeyEvent.VK_S)) {
//            System.out.println("S");
            velocity.x = 0;
            velocity.y = Y_VEL;
            rotation = 0;
        } else if (Input.isKeyReleased(KeyEvent.VK_S)) {
//            velocity.y = 0;
        }
        //Left pressed
        if (Input.isKeyPressed(KeyEvent.VK_A)) {
//            System.out.println("A");
            velocity.y = 0;
            velocity.x = -X_VEL;
            rotation = 90;
        } else if (Input.isKeyReleased(KeyEvent.VK_A)) {
//            velocity.x = 0;
        }
    }

    private void diagonals() {
        //Up-Right
        if (Input.isKeyPressed(KeyEvent.VK_W) && Input.isKeyPressed(KeyEvent.VK_D)) {
            rotation = 225;
        }
        //Right-Down
        if (Input.isKeyPressed(KeyEvent.VK_D) && Input.isKeyPressed(KeyEvent.VK_S)) {
            rotation = 315;
        }
        //Down-Left
        if (Input.isKeyPressed(KeyEvent.VK_S) && Input.isKeyPressed(KeyEvent.VK_A)) {
            rotation = 45;
        }
        //Left-Up
        if (Input.isKeyPressed(KeyEvent.VK_A) && Input.isKeyPressed(KeyEvent.VK_W)) {
            rotation = 135;
        }
    }

    public void handleInput() {
        upDownLeftRight();
        diagonals();
    }

    public void hurt() {
        state = PLAYER_HURT;
    }

    public void die() {
//        velocity.set(0,0);
//        state = STATE_DEAD;
    }

    /**
     * NOT USED CURRENTLY!!!!
     *
     * @return
     */
    public boolean playerOutOfBounds() {
        return (position.x < 0
                || position.x + PLAYER_WIDTH > World.WORLD_WIDTH
                || position.y < 0
                || position.y + PLAYER_HEIGHT > World.WORLD_HEIGHT);
    }

    private void wrapPlayer() {
//        System.out.println(player.position);
        //Wrap from left
        if (position.x < 0 + World.xShift) {
            position.x = World.WORLD_WIDTH - Pac.PLAYER_WIDTH + World.xShift;
            bounds.lowerLeft.x = World.WORLD_WIDTH - Pac.PLAYER_WIDTH + World.xShift;
        }
        //Wrap from right
        if (position.x > World.WORLD_WIDTH - Pac.PLAYER_WIDTH + World.xShift) {
            position.x = 0 + World.xShift;
            bounds.lowerLeft.x = 0 + World.xShift;
        }
        //Wrap from top
        if (position.y < 0 + World.yShift) {
            position.y = World.WORLD_HEIGHT - Pac.PLAYER_HEIGHT + World.yShift;
            bounds.lowerLeft.y = World.WORLD_HEIGHT - Pac.PLAYER_HEIGHT + World.yShift;
        }
        //Wrap from bottom
        if (position.y > World.WORLD_HEIGHT - Pac.PLAYER_HEIGHT + World.yShift) {
            position.y = 0 + World.yShift;
            bounds.lowerLeft.y = 0 + World.yShift;
        }
    }

    /**
     * ************UPDATE & RENDER
     *
     **************
     * @param deltaTime
     */
    @Override
    void gameUpdate(float deltaTime) {
//        System.out.println(state);
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
//        bounds.lowerLeft.add(velocity.x * deltaTime, velocity.y * deltaTime);
        bounds.lowerLeft.set(position);

        //Handle collision AFTER we commit our movement
        wrapPlayer();
    }

    @Override
    void gameRender(Graphics2D g) {
        //Draw player
        drawHitbox(g);

//        g.setColor(Color.WHITE);
//        g.drawString("vel: " + velocity, 10, 50);
    }

    private void drawHitbox(Graphics2D g) {
//        //Draw Sprite
//        AffineTransform old = g.getTransform();
//        trans.setToIdentity();  //AffineTransform trans = new AffineTransform();
//        float centerX = (position.x + PLAYER_WIDTH / 2);
//        float centerY = (position.y + PLAYER_HEIGHT / 2);
//        trans.translate(centerX, centerY);
//        trans.rotate(Math.toRadians(rotation));
//        trans.translate(-centerX, -centerY);
//        g.setTransform(trans);
//
//        g.drawImage(Assets.player, (int) position.x, (int) position.y,
//                (int) PLAYER_WIDTH, (int) PLAYER_HEIGHT, null);
////        g.setColor(Color.BLUE);
////        g.drawRect((int) position.x, (int) position.y,
////                (int) PLAYER_WIDTH, (int) PLAYER_HEIGHT);
//
//        g.setTransform(old);

//        //Draw bounds
        g.setColor(Color.YELLOW);
////        g.draw(bounds);
//        g.drawRect((int) bounds.lowerLeft.x, (int) bounds.lowerLeft.y,
//                (int) bounds.width, (int) bounds.height);
//        g.setColor(Color.WHITE);
        g.drawRect((int) position.x, (int) position.y,
                (int) PLAYER_WIDTH, (int) PLAYER_HEIGHT);
    }

}
