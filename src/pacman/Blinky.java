/*
 * Copyright (C) 2018 Mo
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

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import static pacman.World.WORLD_WIDTH;

/**
 * 22-May-2018, 02:29:30.
 *
 * @author Mo
 */
public class Blinky extends Enemy {

    private final Tile[][] tiles;
    public Point blinkyTile;
    public Point pixel;
    public Point temp;
    public Point hold;

    private Point centerPoint;  //of a tile
    public Color color;

    private Point target;       //pacmans tile location

    public enum DIR {

        UP, DOWN, LEFT, RIGHT, EATEN, STOP,// NA
    }
    public DIR currentDir = DIR.UP;
    private DIR toTravel = DIR.STOP;
    private List<Tile> adjacent;
    private Tile lookAt;

    public Blinky(Tile[][] tiles) {
        this.tiles = tiles;
        blinkyTile = new Point();
        pixel = new Point();
        temp = new Point();

        color = new Color(255, 0, 0, 255);

        centerPoint = new Point();
        target = new Point();
        adjacent = new ArrayList<>();

        hold = new Point();
    }

    //Given a tile position
    public Blinky(Tile[][] tiles, int x, int y, Point target) {
        this.tiles = tiles;
        blinkyTile = new Point(x, y);
        pixel = new Point();
        pixel.setLocation(blinkyTile.x * Tile.TILE_WIDTH + scaledNum(3),
                blinkyTile.y * Tile.TILE_HEIGHT + scaledNum(4));
        temp = new Point();

        color = new Color(255, 0, 0, 255);  //red (rgba)

        centerPoint = new Point(blinkyTile.x * Tile.TILE_WIDTH + scaledNum(3),
                blinkyTile.y * Tile.TILE_HEIGHT + scaledNum(4));
        this.target = target;
        adjacent = new ArrayList<>();

        hold = new Point();
    }

    public void setPixel(int x, int y) {
        pixel.setLocation(x, y);
    }

    public int scaledNum(int i) {
        return GamePanel.scale * i;
    }

    public void moveUp() {
//        System.out.println("move tile UP");

        //Store current position
        temp.setLocation(blinkyTile.x, blinkyTile.y);
        blinkyTile.y -= 1;

        //Update the new tile id then set old tile empty
        setTile(blinkyTile.x, blinkyTile.y, Tile.BLINKY);
        setTile(temp.x, temp.y, Tile.ACTIVE);

    }

    public void moveDown() {
//        System.out.println("move tile DOWN");
        temp.setLocation(blinkyTile.x, blinkyTile.y);
        blinkyTile.y += 1;
        setTile(blinkyTile.x, blinkyTile.y, Tile.BLINKY);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    public void moveLeft() {
//        System.out.println("move tile LEFT");
        temp.setLocation(blinkyTile.x, blinkyTile.y);
        blinkyTile.x -= 1;
        setTile(blinkyTile.x, blinkyTile.y, Tile.BLINKY);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    public void moveRight() {
//        System.out.println("move tile RIGHT");
        temp.setLocation(blinkyTile.x, blinkyTile.y);
        blinkyTile.x += 1;
        setTile(blinkyTile.x, blinkyTile.y, Tile.BLINKY);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    private void setTile(int x, int y, int id) {
        tiles[y][x].id = id;
    }

    //////////////////////////////////////
    private Point getCenter(Tile t) {
        centerPoint.x = (int) t.bounds.lowerLeft.x + scaledNum(3);
        centerPoint.y = (int) t.bounds.lowerLeft.y + scaledNum(4);

//        centerPoint.x = (int) (t.bounds.lowerLeft.x / GamePanel.scale + 3);
//        centerPoint.y = (int) (t.bounds.lowerLeft.y / GamePanel.scale + 4);
        return centerPoint;
    }

    private boolean isWall(Tile t) {
        return t.id == Tile.WALL;
    }

    private Tile pixelToTile(int x, int y) {
        //Slightly optamized
        //-> int/int = int, without the decimal. So no need for Math.floor
        x /= Tile.TILE_WIDTH;
        y /= Tile.TILE_HEIGHT;
//        System.out.println("tile[" + x + "][" + y + "]");
        return tiles[y][x];
    }

    private Tile pixelToTileAbove(int x, int y) {
        x /= Tile.TILE_WIDTH;
        y /= Tile.TILE_HEIGHT;
        return tiles[y - 1][x];
    }

    private Tile pixelToTileBelow(int x, int y) {
        x /= Tile.TILE_WIDTH;
        y /= Tile.TILE_HEIGHT;
        return tiles[y + 1][x];
    }

    private Tile pixelToTileLeft(int x, int y) {
        x /= Tile.TILE_WIDTH;
        y /= Tile.TILE_HEIGHT;
        return tiles[y][x - 1];
    }

    private Tile pixelToTileRight(int x, int y) {
        x /= Tile.TILE_WIDTH;
        y /= Tile.TILE_HEIGHT;
        return tiles[y][x + 1];
    }

    public void movePixelUp() {
//        System.out.println("up...");
        Tile current = pixelToTile(pixel.x, pixel.y);
        Point c = getCenter(current);
        //If the TILE above pacman is a wall
        if (isWall(pixelToTileAbove(pixel.x, pixel.y))) {
            //Stop pacman at the mid point of the tile
            if (pixel.y > c.y) {
                pixel.y -= scaledNum(1);
            } else {
                //This is an intersection tile
                System.out.println("Reach TOP CENTER by wall");
                currentDir = DIR.STOP;
                System.out.println("currentDir: " + currentDir);
                //Get shortest distance to target tile and change directions
                
            }
        } else {
            //Otherwise let pacman continue till the end of the tile
            if (current.intersection && pixel.y == c.y) {
                //reached the center of an intersection TILE
                System.out.println("reached the center of an intersection TILE");
                currentDir = DIR.STOP;
                System.out.println("currentDir: " + currentDir);
                //Get shortest distance to target tile and change directions
                
            } else {
                pixel.y -= scaledNum(1);
            }
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next.position != current.position) {
            moveUp();
        }
    }

    public void movePixelDown() {
//        System.out.println("down...");
        Tile current = pixelToTile(pixel.x, pixel.y);

        Point c = getCenter(pixelToTile(pixel.x, pixel.y));
        if (isWall(pixelToTileBelow(pixel.x, pixel.y))) {
            if (pixel.y < c.y) {
                pixel.y += scaledNum(1);
            } else {
                System.out.println("Reached BOTTOM CENTER by wall");
                currentDir = DIR.STOP;
                System.out.println("currentDir: " + currentDir);
            }
        } else {
            //Otherwise let pacman continue till the end of the tile
            if (current.intersection && pixel.y == c.y) {
                //reached the center of an intersection TILE
                System.out.println("reached the center of an intersection TILE");
                currentDir = DIR.STOP;
                System.out.println("currentDir: " + currentDir);
            } else {
                pixel.y += scaledNum(1);
            }
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next.position != current.position) {
            moveDown();
        }
    }

    public void movePixelLeft() {
//        System.out.println("left...");
        Tile current = pixelToTile(pixel.x, pixel.y);
        //-------------------Wrap Player------------------------
        if (pixel.x < scaledNum(9)) {
            pixel.x = (int) WORLD_WIDTH - scaledNum(1);
            //update players tile
            blinkyTile.x = pixel.x / Tile.TILE_WIDTH;
            blinkyTile.y = pixel.y / Tile.TILE_HEIGHT;
            tiles[blinkyTile.y][blinkyTile.x].id = Tile.PLAYER;
            //remove old occupied player tile
            current.id = Tile.ACTIVE;
            return;
        }
        //-------------------------------------------------------
        Point c = getCenter(pixelToTile(pixel.x, pixel.y));
        if (isWall(pixelToTileLeft(pixel.x, pixel.y))) {
            if (pixel.x > c.x) {
                pixel.x -= scaledNum(1);
            } else {
                System.out.println("Reached LEFT CENTER by wall");
                currentDir = DIR.STOP;
                System.out.println("currentDir: " + currentDir);
            }
        } else {
            if (current.intersection && pixel.x == c.x) {
                System.out.println("reached the center of an intersection TILE");
                currentDir = DIR.STOP;
                System.out.println("currentDir: " + currentDir);
            } else {
                pixel.x -= scaledNum(1);
            }
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        //Just entered a new tile
        if (next.position != current.position) {
            moveLeft();
        }
    }

    public void movePixelRight() {
//        System.out.println("right...");
        Tile current = pixelToTile(pixel.x, pixel.y);

        //-------------------Wrap Player------------------------
        if (pixel.x > WORLD_WIDTH - scaledNum(11)) {
            pixel.x = scaledNum(1);
            //update players tile
            blinkyTile.x = pixel.x / Tile.TILE_WIDTH;
            blinkyTile.y = pixel.y / Tile.TILE_HEIGHT;
            tiles[blinkyTile.y][blinkyTile.x].id = Tile.PLAYER;
            //remove old occupied player tile

            current.id = Tile.ACTIVE;
            return;
        }
        //-------------------------------------------------------
        Point c = getCenter(pixelToTile(pixel.x, pixel.y));
        if (isWall(pixelToTileRight(pixel.x, pixel.y))) {
            if (pixel.x < c.x) {
                pixel.x += scaledNum(1);
            } else {
                System.out.println("Reached RIGHT CENTER by wall");
                currentDir = DIR.STOP;
                System.out.println("currentDir: " + currentDir);
            }
        } else {
            if (current.intersection && pixel.x == c.x) {
                System.out.println("reached the center of an intersection TILE");
                currentDir = DIR.STOP;
                System.out.println("currentDir: " + currentDir);
            } else {
                pixel.x += scaledNum(1);
            }
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next.position != current.position) {
            moveRight();
        }
    }

    public void movePixelLeftOLD() {
//        System.out.println("left...");
        Tile current = pixelToTile(pixel.x, pixel.y);
        //-------------------Wrap Player------------------------
        if (pixel.x < scaledNum(9)) {
            pixel.x = (int) WORLD_WIDTH - scaledNum(1);
            //update players tile
            blinkyTile.x = pixel.x / Tile.TILE_WIDTH;
            blinkyTile.y = pixel.y / Tile.TILE_HEIGHT;
            tiles[blinkyTile.y][blinkyTile.x].id = Tile.PLAYER;
            //remove old occupied player tile
            current.id = Tile.ACTIVE;
            return;
        }
        //-------------------------------------------------------
        Point c = getCenter(pixelToTile(pixel.x, pixel.y));
        if (isWall(pixelToTileLeft(pixel.x, pixel.y))) {
            if (pixel.x > c.x) {
                pixel.x -= scaledNum(1);
            } else {
                System.out.println("Reach LEFT wall");
//                currentDir = DIR.STOP;
//                System.out.println("currentDir: " + currentDir);
            }
        } else {
            pixel.x -= scaledNum(1);
        }
//            alignY(c);
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next == lookAt) {
            System.out.println("next = lookAt");
            if (pixel.x == c.x) {
                System.out.println("Reached center of lookat: curentDir -> " + currentDir);
                currentDir = toTravel;
            }
        }
        //Just entered a new tile
        if (next.position != current.position) {
            moveLeft();
        }
        if (pixel.x == c.x) {
            //Look ahead for next DIR
            hold.setLocation(blinkyTile.x - 1, blinkyTile.y);
            lookAt = getTile(hold.x, hold.y);

            if (getDown(hold.x, hold.y).legal) {
                System.out.println("moving DOWN on next intersection");
                toTravel = DIR.DOWN;
            }
            if (getLeft(hold.x, hold.y).legal) {
                System.out.println("moving LEFT on next intersection");
                toTravel = DIR.LEFT;
            }
            if (getUp(hold.x, hold.y).legal) {
                System.out.println("moving UP on next intersection");
                toTravel = DIR.UP;
            }
            System.out.println("chosen direction = " + toTravel);
        }
    }

    private void updateAdjacentTiles(Tile t) {
        //Clear previous tiles
        adjacent.clear();
        //Add 3 -> LEFT, UP, DOWN (if possible tiles)
        Tile up = getUp(temp.x, temp.y);
        if (up.legal) {
            adjacent.add(up);
            System.out.println("ADDING UP");

        }
        Tile down = getDown(temp.x, temp.y);
        if (down.legal) {
            adjacent.add(down);
            System.out.println("ADDING DOWN");

        }
        Tile left = getLeft(temp.x, temp.y);
        if (left.legal) {
            adjacent.add(left);
            System.out.println("ADDING LEFT");
        }
//        Tile right = getRight(temp.x, temp.y);
//        if(right.legal){
//            adjacent.add(right);
//            System.out.println("ADDING RIGHT");
//        }

    }

    private Tile getUp(int x, int y) {
        return tiles[y - 1][x];
    }

    private Tile getDown(int x, int y) {
        return tiles[y + 1][x];
    }

    private Tile getLeft(int x, int y) {
        return tiles[y][x - 1];
    }

    private Tile getRight(int x, int y) {
        return tiles[y][x + 1];
    }

    private Tile getTile(int x, int y) {
        return tiles[y][x];
    }

    public void setCurrentDir(DIR d) {
        this.currentDir = d;
    }

    public void update(float deltaTime) {
//        System.out.println("blinky");
//        movePixelLeft();
//        movePixelRight();
//        System.out.println("target: "+target);
        switch (currentDir) {
            case UP:
                movePixelUp();
                break;
            case DOWN:
                movePixelDown();
                break;
            case LEFT:
                movePixelLeft();
                break;
            case RIGHT:
                movePixelRight();
                break;
        }
    }
}
