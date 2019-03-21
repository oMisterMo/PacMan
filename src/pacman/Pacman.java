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
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * 28-Jun-2018, 20:56:52.
 *
 * @author Mohammed Ibrahim
 */
public class Pacman {

    //Pacman
    public static final int PACMAN_WIDTH = 16 * GamePanel.scale;
    public static final int PACMAN_HEIGHT = 16 * GamePanel.scale;

    private final Tile[][] tiles;

    public enum DIR {

        UP, DOWN, LEFT, RIGHT, STOP, NA
    }
    public int rotation;
    public Point pacmanTile;
    public Point pixel;
    public DIR pacmanDir = DIR.STOP;
    private DIR turnBuffer = DIR.NA;
    public DIR recentDir = DIR.STOP;
    private Point centerPoint;  //used as a temp variable (center of a tile

    public static final int FOOD_SCORE = 10;
    public static final int ENERGIZER_SCORE = 50;
    public int score;

    //Reference to the location of all pellets in the world
    private final List<Point> allDots;
    private final List<Point> allEnergizers;
    public Color color;

    public Pacman(Tile[][] tiles, List<Point> allDots, List<Point> allEnergizers) {
        this.tiles = tiles;
        this.allDots = allDots;
        this.allEnergizers = allEnergizers;
        init();
        this.color = new Color(255, 255, 0, 255);
        //Print some info here
//        System.out.println(-);
        System.out.println("pacman constructor finsihed...");
    }

    private void init() {
        rotation = 0;
        pacmanTile = new Point();
        pixel = new Point();
        centerPoint = new Point();
        System.out.println("currentDir = " + pacmanDir);

        score = 0;
    }

    public void setPacmanPos(int x, int y) {
        this.pacmanTile.setLocation(x, y);
        this.pixel.setLocation(x * Tile.TILE_WIDTH + scaledNum(3),
                y * Tile.TILE_HEIGHT + scaledNum(4));
        this.centerPoint.setLocation(x * Tile.TILE_WIDTH + scaledNum(3),
                y * Tile.TILE_HEIGHT + scaledNum(4));
        this.pacmanDir = DIR.STOP;
        this.turnBuffer = DIR.NA;
        this.recentDir = DIR.STOP;
        Assets.pacman.resetAnimation();
    }

    private int scaledNum(int i) {
        return GamePanel.scale * i;
    }

    public void pacmanInput() {
        //UP
        if (Input.isKeyTyped(KeyEvent.VK_I)) {
            turnBuffer = DIR.UP;
            //Get tile above -> if its not active ignore movement
            if (isLegal(pixelToTileAbove(pixel.x, pixel.y))) {
                pacmanDir = DIR.UP;
                recentDir = DIR.UP;
//                System.out.println("currentDir: " + currentDir);
            } else {
//                System.out.println("cant press up, tile above blocked");
            }
        }
        //DOWN
        if (Input.isKeyTyped(KeyEvent.VK_K)) {
            turnBuffer = DIR.DOWN;
            if (isLegal(pixelToTileBelow(pixel.x, pixel.y))) {
                pacmanDir = DIR.DOWN;
                recentDir = DIR.DOWN;
//                System.out.println("currentDir: " + currentDir);
            } else {
//                System.out.println("cant press down, tile above blocked");
            }
        }
        //LEFT
        if (Input.isKeyTyped(KeyEvent.VK_J)) {
            //if current tile is left teleport tile, return
//            Tile t = tiles[17][0];
            if (pixelToTile(pixel.x, pixel.y).teleportTile) {
//                System.out.println("We are on left telport tile");
                return;
            }
            turnBuffer = DIR.LEFT;
            if (isLegal(pixelToTileLeft(pixel.x, pixel.y))) {
                pacmanDir = DIR.LEFT;
                recentDir = DIR.LEFT;
//                System.out.println("currentDir: " + currentDir);
            } else {
//                System.out.println("cant press left, tile above blocked");
            }
        }
        //RIGHT
        if (Input.isKeyTyped(KeyEvent.VK_L)) {
            //if current tile is left teleport tile, return
//            Tile t = tiles[17][NO_OF_TILES_X - 1];
            if (pixelToTile(pixel.x, pixel.y).teleportTile) {
//                System.out.println("We are on left telport tile");
                return;
            }
            turnBuffer = DIR.RIGHT;
            if (isLegal(pixelToTileRight(pixel.x, pixel.y))) {
                pacmanDir = DIR.RIGHT;
                recentDir = DIR.RIGHT;
//                System.out.println("currentDir: " + currentDir);
            } else {
//                System.out.println("cant press right, tile above blocked");
            }
        }
//        //Print debug
//        if (Input.isKeyTyped(KeyEvent.VK_I) || Input.isKeyTyped(KeyEvent.VK_K)
//                || Input.isKeyTyped(KeyEvent.VK_J) || Input.isKeyTyped(KeyEvent.VK_L)) {
////            System.out.println("pixel (scaled): " + pixel.x + "," + pixel.y);
//            System.out.println("pixel: " + (pixel.x / GamePanel.scale) + ","
//                    + (pixel.y / GamePanel.scale));
//            System.out.println("tile: " + pixel.x / Tile.TILE_WIDTH + ","
//                    + pixel.y / Tile.TILE_HEIGHT);
//            System.out.println("");
//        }
    }

    private void updateScore() {
        Tile t = tiles[pacmanTile.y][pacmanTile.x];
        switch (t.id) {
            case Tile.DOT:
                score += FOOD_SCORE;
                if (allDots.contains(t.grid)) {
                    allDots.remove(t.grid);
                }
                break;
            case Tile.ENERGIZER:
                score += ENERGIZER_SCORE;
                if (allEnergizers.contains(t.grid)) {
                    allEnergizers.remove(t.grid);
                }
                break;
        }
    }

    private Tile pixelToTile(int x, int y) {
//        x = (int) Math.floor(x / Tile.TILE_WIDTH);
//        y = (int) Math.floor(y / Tile.TILE_HEIGHT);

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

    private void moveUp() {
//        System.out.println("move tile UP");
        //Store current position
        int x, y;
        x = pacmanTile.x;
        y = pacmanTile.y;
        pacmanTile.y -= 1;

        //Handle collision with item
        updateScore();

        //Update the new tile id then set old tile empty
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(x, y, Tile.ACTIVE);
    }

    private void moveDown() {
//        System.out.println("move tile DOWN");
        int x, y;
        x = pacmanTile.x;
        y = pacmanTile.y;
        pacmanTile.y += 1;
        updateScore();
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(x, y, Tile.ACTIVE);
    }

    private void moveLeft() {
//        System.out.println("move tile LEFT");
        int x, y;
        x = pacmanTile.x;
        y = pacmanTile.y;
        pacmanTile.x -= 1;
        updateScore();
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(x, y, Tile.ACTIVE);
    }

    private void moveRight() {
//        System.out.println("move tile RIGHT");
        int x, y;
        x = pacmanTile.x;
        y = pacmanTile.y;
        pacmanTile.x += 1;
        updateScore();
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(x, y, Tile.ACTIVE);
    }

    private void movePixelUp() {
//        System.out.println("up...");
        Tile previous = pixelToTile(pixel.x, pixel.y);  //TEST

        //Is the PIXEL above pacman is not a wall
        if (!isWall(pixelToTile(pixel.x, pixel.y - scaledNum(1)))) {
            Point c = getCenter(pixelToTile(pixel.x, pixel.y));
            //If the TILE above pacman is a wall
            if (isWall(pixelToTileAbove(pixel.x, pixel.y))) {
                //Stop pacman at the mid point of the tile
                if (pixel.y > c.y) {
                    pixel.y -= scaledNum(1);
                } else {
                    System.out.println("Reach TOP wall");
                    pacmanDir = DIR.STOP;
                    System.out.println("pacman dir: " + pacmanDir);
                }
            } else {
                //Otherwise let pacman continue till the end of the tile
                pixel.y -= scaledNum(1);
            }
            alignX(c);
            //------------------TEST---------------------------
            Tile curent = pixelToTile(pixel.x, pixel.y);
            if (curent.position != previous.position) {
                moveUp();
            }
            //--------------------------------------------------
        } else {
            //The pixel above is a wall, stop moving
            System.out.println("can't move UP");
            pacmanDir = DIR.STOP;
            System.out.println("pacman dir: " + pacmanDir);
        }
    }

    private void movePixelDown() {
//        System.out.println("down...");
        Tile previous = pixelToTile(pixel.x, pixel.y);

        if (!isWall(pixelToTile(pixel.x, pixel.y + scaledNum(1)))) {
            Point c = getCenter(pixelToTile(pixel.x, pixel.y));
            if (isWall(pixelToTileBelow(pixel.x, pixel.y))) {
                if (pixel.y < c.y) {
                    pixel.y += scaledNum(1);
                } else {
                    System.out.println("Reach BOTTOM wall");
                    pacmanDir = DIR.STOP;
                    System.out.println("pacman dir: " + pacmanDir);
                }
            } else {
                pixel.y += scaledNum(1);
            }
            alignX(c);
            //------------------TEST---------------------------
            Tile curent = pixelToTile(pixel.x, pixel.y);
            if (curent.position != previous.position) {
                moveDown();
            }
            //--------------------------------------------------
        } else {
            System.out.println("can't move DOWN");
            pacmanDir = DIR.STOP;
            System.out.println("pacman dir: " + pacmanDir);
        }
    }

    private void movePixelLeft() {
//        System.out.println("left...");
        Tile previous = pixelToTile(pixel.x, pixel.y);
        if (!isWall(pixelToTile(pixel.x - scaledNum(1), pixel.y))) {
            //-------------------Wrap Player------------------------
            if (pixel.x < scaledNum(9)) {
                pixel.x = (int) World.WORLD_WIDTH - scaledNum(1);
                //update players tile
                pacmanTile.x = pixel.x / Tile.TILE_WIDTH;
                pacmanTile.y = pixel.y / Tile.TILE_HEIGHT;
                tiles[pacmanTile.y][pacmanTile.x].id = Tile.PLAYER;
                //remove old occupied player tile
                previous.id = Tile.ACTIVE;
                return;
            }
            //-------------------------------------------------------
//            System.out.println("pixel to left: " + (pixel.x - scaledNum(1)));
            Point c = getCenter(pixelToTile(pixel.x, pixel.y));
            if (isWall(pixelToTileLeft(pixel.x, pixel.y))) {
                if (pixel.x > c.x) {
                    pixel.x -= scaledNum(1);
                } else {
                    System.out.println("Reach LEFT wall");
                    pacmanDir = DIR.STOP;
                    System.out.println("pacman dir: " + pacmanDir);
                }
            } else {
                pixel.x -= scaledNum(1);
            }
            alignY(c);
            //------------------TEST---------------------------
            Tile curent = pixelToTile(pixel.x, pixel.y);
            if (curent.position != previous.position) {
                moveLeft();
            }
            //--------------------------------------------------
        } else {
            System.out.println("can't move LEFT");
            pacmanDir = DIR.STOP;
            System.out.println("pacman dir: " + pacmanDir);
        }
    }

    private void movePixelRight() {
//        System.out.println("right...");
        Tile previous = pixelToTile(pixel.x, pixel.y);

        if (!isWall(pixelToTile(pixel.x + scaledNum(1), pixel.y))) {
            //-------------------Wrap Player------------------------
            if (pixel.x > World.WORLD_WIDTH - scaledNum(11)) {
                pixel.x = scaledNum(1);
                //update players tile
                pacmanTile.x = pixel.x / Tile.TILE_WIDTH;
                pacmanTile.y = pixel.y / Tile.TILE_HEIGHT;
                tiles[pacmanTile.y][pacmanTile.x].id = Tile.PLAYER;
                //remove old occupied player tile

                previous.id = Tile.ACTIVE;
                return;
            }
            //-------------------------------------------------------
            Point c = getCenter(pixelToTile(pixel.x, pixel.y));
            if (isWall(pixelToTileRight(pixel.x, pixel.y))) {
                if (pixel.x < c.x) {
                    pixel.x += scaledNum(1);
                } else {
                    System.out.println("Reach RIGHT wall");
                    pacmanDir = DIR.STOP;
                    System.out.println("pacman dir: " + pacmanDir);
                }
            } else {
                pixel.x += scaledNum(1);
            }
            alignY(c);
            //------------------TEST---------------------------
            Tile curent = pixelToTile(pixel.x, pixel.y);
            if (curent.position != previous.position) {
                moveRight();
            }
            //--------------------------------------------------
        } else {
            System.out.println("can't move RIGHT");
            pacmanDir = DIR.STOP;
            System.out.println("pacman dir: " + pacmanDir);
        }
    }

    private Point getCenter(Tile t) {
        centerPoint.x = (int) t.bounds.topLeft.x + scaledNum(3);
        centerPoint.y = (int) t.bounds.topLeft.y + scaledNum(4);

//        centerPoint.x = (int) (t.bounds.topLeft.x / GamePanel.scale + 3);
//        centerPoint.y = (int) (t.bounds.topLeft.y / GamePanel.scale + 4);
        return centerPoint;
    }

    private void alignX(Point c) {
        //If pacman is not position on center.x
        if (pixel.x < c.x) {
            pixel.x += scaledNum(1);
        } else if (pixel.x > c.x) {
            pixel.x -= scaledNum(1);
        }
    }

    private void alignY(Point c) {
        if (pixel.y < c.y) {
            pixel.y += scaledNum(1);
        } else if (pixel.y > c.y) {
            pixel.y -= scaledNum(1);
        }
    }

//    public boolean isWithinWorld(int x, int y) {
//        return (y < World.NO_OF_TILES_Y && y >= 0 && x < World.NO_OF_TILES_X && x >= 0);
//    }
    private boolean isLegal(int x, int y) {
//        System.out.println("isLegal");
        return tiles[y][x].legal;
    }

    private boolean isLegal(Tile t) {
//        System.out.println("isLegal");
        return t.legal;
    }

    private boolean isActive(int x, int y) {
        System.out.println("isActive");
        return tiles[y][x].id == Tile.ACTIVE;
    }

    private boolean isFood(int x, int y) {
        System.out.println("isFood");
        return tiles[y][x].id == Tile.DOT;
    }

    private boolean isPowerUp(int x, int y) {
        System.out.println("isPowerUp");
        return tiles[y][x].id == Tile.ENERGIZER;
    }

    private boolean isEmpty(int x, int y) {
        System.out.println("isEmpty");
        return tiles[y][x].id == Tile.EMPTY;
    }

    private boolean isWall(int x, int y) {
        System.out.println("isWall");
        return tiles[y][x].id == Tile.WALL;
    }

    private boolean isWall(Tile t) {
        return t.id == Tile.WALL;
    }

    private void setTile(int x, int y, int id) {
        tiles[y][x].id = id;
    }

    public Tile getPacmanTile() {
        return tiles[pacmanTile.y][pacmanTile.x];
    }

    private void handleTurnBuffer() {
        if (turnBuffer != DIR.NA) {
            switch (turnBuffer) {
                case UP:
                    if (pixelToTileAbove(pixel.x, pixel.y).legal) {
//                        System.out.println("consuming UP turn");
                        pacmanDir = DIR.UP;
                        turnBuffer = DIR.NA;
                        recentDir = DIR.UP;
                    }
                    break;
                case DOWN:
                    if (pixelToTileBelow(pixel.x, pixel.y).legal) {
//                        System.out.println("consuming DOWN turn");
                        pacmanDir = DIR.DOWN;
                        turnBuffer = DIR.NA;
                        recentDir = DIR.DOWN;
                    }
                    break;
                case LEFT:
                    if (pixelToTileLeft(pixel.x, pixel.y).legal) {
//                        System.out.println("consuming LEFT turn");
                        pacmanDir = DIR.LEFT;
                        turnBuffer = DIR.NA;
                        recentDir = DIR.LEFT;
                    }
                    break;
                case RIGHT:
                    if (pixelToTileRight(pixel.x, pixel.y).legal) {
//                        System.out.println("consuming RIGHT turn");
                        pacmanDir = DIR.RIGHT;
                        turnBuffer = DIR.NA;
                        recentDir = DIR.RIGHT;
                    }
                    break;
            }
        }
    }

    public void update(float deltaTime) {
        handleTurnBuffer();
        switch (pacmanDir) {
            case UP:
                rotation = 270;
                movePixelUp();
                Assets.pacman.update(deltaTime);
                break;
            case DOWN:
                rotation = 90;
                movePixelDown();
                Assets.pacman.update(deltaTime);
                break;
            case LEFT:
                rotation = 180;
                movePixelLeft();
                Assets.pacman.update(deltaTime);
                break;
            case RIGHT:
                rotation = 0;
                movePixelRight();
                Assets.pacman.update(deltaTime);
                break;
            case STOP:
                //pacOffset.set(0, 0);
                break;
        }
    }
}
