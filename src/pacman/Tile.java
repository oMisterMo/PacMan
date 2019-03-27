/* 
 * Copyright (C) 2019 Mohammed Ibrahim
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pacman;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 * The <code>Tile</code> class represents a single tile in Pacmans world. Each
 * tile is either in legal space or dead space.
 *
 * Actors only travel between the tiles in legal space. A tile may contain at
 * most a single object (Pacman, Ghost, Pellet, Energizer, etc).
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class Tile extends StaticGameObject {

    public static final int TILE_WIDTH = 8 * GamePanel.scale;
    public static final int TILE_HEIGHT = 8 * GamePanel.scale;

    public BufferedImage tileImg;
    public int id = EMPTY;
    public int wallType = -1;
    public boolean teleportTile;    //dictates whether the pacman can turn on this tile
    public boolean intersection;

    public boolean legal;           //dictates whether the tile is active or not
    //For A* algorithm
    public int gCost;       //How far from start node
    public int hCost;       //How far from end node
    public Point grid;
    public Tile parent;

    /**
     * Constructs a new tile at (x,y) with the width and height provided.
     *
     * @param x the x position
     * @param y the y position
     * @param width the width of the tile
     * @param height the height of the tile
     */
    private Tile(float x, float y, float width, float height) {
        super(x, y, width, height);
        legal = false;
        teleportTile = false;
        intersection = false;
    }

    /**
     * Constructs a new tile at (x,y) with the width and height provided.
     * Initialises the members needed for the A* algorithm.
     *
     * @param x the x position
     * @param y the y position
     * @param width the width of the tile
     * @param height the height of the tile
     * @param pos the index of the tile
     */
    public Tile(float x, float y, float width, float height, Point pos) {
        this(x, y, width, height);
        this.grid = pos;
        gCost = 0;
        hCost = 0;
    }

    /**
     * @return total cost travelling to tile
     */
    public int fCost() {
        return gCost + hCost;
    }

    /**
     * Returns true if the tile is inactive.
     *
     * @return true if tile is in dead space
     */
    public boolean isBlocked() {
        return !legal;
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
            case DOT:
                type = "DOT";
                break;
            case ENERGIZER:
                type = "ENERGIZER";
                break;
            case PLAYER:
                type = "PLAYER";
                break;
            case WALL:
                type = "WALL";
                break;
            case BLINKY:
                type = "BLINKY";
                break;
            default:
                type = "EMPTY";
        }
        return grid.x + ", " + grid.y + ": " + type;
    }

    //Tile types
    public final static int EMPTY = 0;
    public final static int ACTIVE = 1;
    public final static int DOT = 2;
    public final static int ENERGIZER = 3;
    public final static int PLAYER = 4;
    public final static int WALL = 5;
    public final static int BLINKY = 6;
    public final static int PINKY = 7;
    public final static int INKY = 8;
    public final static int CLYDE = 9;

    public final static int PATH = 20;

    //Walls
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
}
