package pacman;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * Only handles static, solid spikeBlocks
 *
 * 19/05/2017 - updated to extend StaticGameObject
 *
 * 06-Sep-2016, 23:18:03.
 *
 * @author Mo
 */
public class Tile extends StaticGameObject {

    public static final int TILE_WIDTH = 8 * GamePanel.scale;
    public static final int TILE_HEIGHT = 8 * GamePanel.scale;
//    public static final int TILE_WIDTH = 8;   //original size
//    public static final int TILE_HEIGHT = 8;

    public final static int EMPTY = 0;
    public final static int ACTIVE = 1;
    public final static int FOOD = 2;
    public final static int POWER_UP = 3;
    public final static int PLAYER = 4;
    public final static int WALL = 5;
    public final static int BLINKY = 6;
    public final static int PINKY = 7;
    public final static int INKY = 8;
    public final static int CLYDE = 9;

    public final static int DOUBLE_TL = 0;
    public final static int DOUBLE_TM = 1;
    public final static int DOUBLE_TR = 2;
    public final static int DOUBLE_ML = 3;
    public final static int DOUBLE_MR = 4;
    public final static int DOUBLE_BL = 5;
    public final static int DOUBLE_BM = 6;
    public final static int DOUBLE_BR = 7;

    public final static int LINE_TL = 8;
    public final static int LINE_TM = 9;
    public final static int LINE_TR = 10;
    public final static int LINE_ML = 11;
    public final static int LINE_MR = 12;
    public final static int LINE_BL = 13;
    public final static int LINE_BM = 14;
    public final static int LINE_BR = 15;

    public final static int HOR_TL = 16;
    public final static int HOR_TR = 17;
    public final static int HOR_BL = 18;
    public final static int HOR_BR = 19;
    public final static int VER_TL = 20;
    public final static int VER_TR = 21;
    public final static int VER_BL = 22;
    public final static int VER_BR = 23;

    public final static int SQUARE_TL = 24;
    public final static int SQUARE_TR = 25;
    public final static int SQUARE_BL = 26;
    public final static int SQUARE_BR = 27;

    public final static int HOME_L = 28;
    public final static int HOME_R = 29;

    //Public so I can call marioTL.position.x;
    public BufferedImage tileImg;
    public int id = EMPTY;
    public int wallType = -1;
    public boolean teleportTile;   //dictates whether the pacman can turn on this tile
    public boolean intersection;

    public boolean legal;   //dictates whether the tile is active or not
    //For A* algorithm
    public int gCost;
    public int hCost;
    public Point pos;
    public Tile parent;

    public Tile(float x, float y, float width, float height) {
        super(x, y, width, height);
        legal = false;
        teleportTile = false;
        intersection = false;
    }

    public Tile(float x, float y, float width, float height, Point pos) {
        this(x, y, width, height);
        this.pos = pos;
        gCost = 0;
        hCost = 0;
    }

    /**
     * Never need to assign to, only get
     *
     * @return total cost travelling to tile
     */
    public int fCost() {
        return gCost + hCost;
    }

    @Override
    void gameUpdate(float deltaTime) {
    }

    @Override
    void gameRender(Graphics2D g) {
    }

    @Override
    public String toString() {
        String type = "";
        switch (id) {
            case ACTIVE:
                type = "ACTIVE";
                break;
            case FOOD:
                type = "FOOD";
                break;
            case POWER_UP:
                type = "POWER UP";
                break;
            case PLAYER:
                type = "PLAYER";
                break;
            case WALL:
                type = "WALL";
                break;
            default:
                type = "EMPTY";
        }
        return "type: " + type;
    }

}
