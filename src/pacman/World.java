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

import common.OverlapTester;
import common.Vector2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Formatter;

/**
 * 20-Feb-2018, 22:16:38.
 *
 * @author Mo
 */
public class World extends GameObject {

    //Size of the original pacman arcade world
    public static final float WORLD_WIDTH = 224 * GamePanel.scale;
    public static final float WORLD_HEIGHT = 288 * GamePanel.scale;

    public static final int NO_OF_TILES_X = (int) (WORLD_WIDTH / Tile.TILE_WIDTH);      //28
    public static final int NO_OF_TILES_Y = (int) (WORLD_HEIGHT / Tile.TILE_HEIGHT);    //36

    public static final int WORLD_STATE_READY = 0;
    public static final int WORLD_STATE_RUNNING = 1;
    public static final int WORLD_STATE_GAME_OVER = 2;
    public int state;

    public Tile[][] tiles;

    //Player info
    public static final float SPEED = 88;   //pixels per second

    //Pacman
    public enum DIR {

        UP, DOWN, LEFT, RIGHT, STOP, NA
    }
    private int rotation;
    private Point pacmanTile;
    private Point temp; //old position (temp variable)
    private DIR currentDir = DIR.STOP;
    private DIR turnBuffer = DIR.NA;
    private Point centerPoint;  //used as a temp variable
    public static final float TIME_TO_MOVE = 0.3f;
    private AffineTransform trans;

//    private final Vector2D touchPos = new Vector2D();
    private Color backgroundColor = Color.BLACK;
    private SpatialHashGrid grid;
    private float scaleTime = 1f;
    private float elapsedTime;

    public static final int FOOD_SCORE = 10;
    public static final int ENERGIZER_SCORE = 50;
    private int numFood;
    private int numEnergizers;
    private int score;
    private String totalScore;
    private int scoreX, scoreY;
    private StringBuilder sb;
    private Formatter formatter;

    //Enemies--------------------------------------
    private Blinky blinky;

    //test variable
    private int num = 0;
    private Point pixel;

    public World() {
        init();
//        test = new Tile(10, 10, 100, 100);

        state = WORLD_STATE_RUNNING;
        System.out.println("-----------------------------");
        System.out.println("*WORLD INFO*");
        System.out.println("Tile Width: " + Tile.TILE_WIDTH);
        System.out.println("Tile Height: " + Tile.TILE_HEIGHT);
        System.out.println("No of X Tiles: " + NO_OF_TILES_X);
        System.out.println("No of Y Tiles: " + NO_OF_TILES_Y);
        System.out.println("Total tiles: " + (NO_OF_TILES_X * NO_OF_TILES_Y));
        System.out.println("Num of Food: " + numFood);
        System.out.println("Num of Energizers: " + numEnergizers);
        System.out.println("-----------------------------");
        System.out.println("background Color: " + backgroundColor);
        System.out.println("World loaded...");
    }

    private void init() {
        tiles = new Tile[NO_OF_TILES_Y][NO_OF_TILES_X];
        nullTiles();    //sets to null
        initTiles();    //create empty tiles
        setEmpty();     //sets to empty (not needed here)
//        setBorder();
//        setRandomTiles();
        sb = new StringBuilder();
        formatter = new Formatter(sb);
        formatter.format("score %04d", score);
        System.out.println("sb.toString(): " + sb.toString());

        //PACMAN STUFF----------------
        rotation = 0;
        pacmanTile = new Point();
        temp = new Point();
//        pacOffset = new Point();
        pixel = new Point();
        centerPoint = new Point();
        System.out.println("currentDir = " + currentDir);
        trans = new AffineTransform();
        //end pacman stuff---------------------------

        //Score
        numFood = 0;
        numEnergizers = 0;
        score = 0;
        totalScore = "";
        scoreX = (int) (GamePanel.GAME_WIDTH / 2 - 10);
        scoreY = (int) Tile.TILE_HEIGHT * 2;

        //Time variables
        elapsedTime = 0;

        loadLevel();
        loadWalls();
        loadIntersections();
        initGrid();
        //-----------------------------Random test
        tiles[17][0].teleportTile = true;
        tiles[17][NO_OF_TILES_X - 1].teleportTile = true;
        tiles[17][0].id = Tile.POWER_UP;
        tiles[17][NO_OF_TILES_X - 1].id = Tile.POWER_UP;

        blinky = new Blinky(tiles, 6, 25, pacmanTile);
        Tile b = tiles[blinky.blinkyTile.y][blinky.blinkyTile.x];
        b.id = Tile.BLINKY;

        //------------------------------------------
//        //Get random tile
//        int x = 4;
//        int y = 4;
//        Tile p = tiles[y][x];
//        //Set player in temp location
//        p.id = Tile.PLAYER;
//        p.legal = true;
//
//        //Set original pacman to random tile
//        this.pacmanTile.setLocation(x, y);
//        System.out.println("player position: " + x + ", " + y);
//        //Set new packman to pixel located @ said tile
//        this.pixel.setLocation(pacmanTile.x * Tile.TILE_WIDTH + scaledNum(3),
//                pacmanTile.y * Tile.TILE_HEIGHT + scaledNum(4));
//        this.centerPoint.setLocation(pacmanTile.x * Tile.TILE_WIDTH + scaledNum(3),
//                pacmanTile.y * Tile.TILE_HEIGHT + scaledNum(4));
    }

    /**
     * Sets all tiles to null
     */
    private void nullTiles() {
        System.out.println("Setting all tiles to null...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x] = null;
            }
        }
    }

    /**
     * Called from the constructor, sets the position of all tiles
     */
    private void initTiles() {
        System.out.println("Initializing tiles...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x] = new Tile(x * Tile.TILE_WIDTH, y * Tile.TILE_HEIGHT,
                        Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
            }
        }
    }

    /**
     * Sets all tiles to empty
     */
    private void setEmpty() {
        System.out.println("Setting the id of all tiles to EMTPY");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x].id = Tile.EMPTY;
            }
        }
    }

    private void setIntersection() {
        System.out.println("Setting intersection tiles...");
        //row 1
        tiles[4][1].intersection = true;
        tiles[4][6].intersection = true;
        tiles[4][12].intersection = true;
        tiles[4][15].intersection = true;
        tiles[4][21].intersection = true;
        tiles[4][26].intersection = true;
        //row 2
        tiles[8][1].intersection = true;
        tiles[8][6].intersection = true;
        tiles[8][12].intersection = true;
        tiles[8][15].intersection = true;
        tiles[8][21].intersection = true;
        tiles[8][26].intersection = true;

    }

    private void setBorder() {
        //Sets the TOP wall blocked
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            tiles[y][0].id = Tile.ACTIVE;
        }
        //Sets the BOTTOM wall blocked
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            tiles[y][NO_OF_TILES_X - 1].id = Tile.ACTIVE;
        }
        //Sets the TL wall blocked
        for (int x = 0; x < NO_OF_TILES_X; x++) {
            tiles[0][x].id = Tile.ACTIVE;
        }
        //Sets the TR wall blocked
        for (int x = 0; x < NO_OF_TILES_X; x++) {
            tiles[NO_OF_TILES_Y - 1][x].id = Tile.ACTIVE;
        }
    }

    private void setRandomTiles() {
        tiles[7][4].id = Tile.ACTIVE;
        tiles[7][10].id = Tile.ACTIVE;
        tiles[10][4].id = Tile.ACTIVE;
        tiles[10][10].id = Tile.ACTIVE;
        tiles[4][4].id = Tile.ACTIVE;
        tiles[4][10].id = Tile.ACTIVE;
    }

    private void initGrid() {
        //Experiment with cellsize -> bigger size = more collision checks
        grid = new SpatialHashGrid(WORLD_WIDTH, WORLD_HEIGHT, 100f);
        //Add all static objects
        //Add all dynamic objects

        grid.printInfo();
        System.out.println("SpatialHashGrid created...");
    }

    public void loadLevel() {
        BufferedImage level = Assets.world;
        int w = level.getWidth();
        int h = level.getHeight();
//        System.out.println("w " + w + "\nh " + h);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = level.getRGB(x, y);
                //Get color of pixel in testLevel array
                int a = ((pixel & 0xff000000) >>> 24);
                int r = ((pixel & 0x00ff0000) >>> 16);
                int g = ((pixel & 0x0000ff00) >>> 8);
                int b = ((pixel & 0x000000ff));
                Tile t = tiles[y][x];

                //Depending on the color of a pixel, set the tile
                if (r == 0 && g == 0 && b == 0) {
                    //System.out.println("Black block at: " + x + " " + y);
                    t.id = Tile.EMPTY;
                    continue;   //We found a tile, do not need to check others
                }
                //Active (white tile)
                if (r == 255 && g == 255 && b == 255) {
                    //System.out.println("Wall block at: " + x + " " + y);
                    t.id = Tile.ACTIVE;
                    t.legal = true;
                    continue;
                }
                //Food (gray tile)
                if (r == 120 && g == 120 && b == 120) {
                    t.id = Tile.FOOD;
                    t.legal = true;
                    numFood++;
                    continue;
                }
                //Power up (green tile)
                if (r == 0 && g == 255 && b == 0) {
                    t.id = Tile.POWER_UP;
                    t.legal = true;
                    numEnergizers++;
                    continue;
                }
                //Player (yellow tile)
                if (r == 255 && g == 255 && b == 0) {
                    t.id = Tile.PLAYER;
                    t.legal = true;

                    System.out.println("player position: " + x + ", " + y);
                    pacmanTile.setLocation(x, y);
                    temp.setLocation(x, y);

                    this.pixel.setLocation(pacmanTile.x * Tile.TILE_WIDTH + scaledNum(3),
                            pacmanTile.y * Tile.TILE_HEIGHT + scaledNum(4));
                    this.centerPoint.setLocation(pacmanTile.x * Tile.TILE_WIDTH + scaledNum(3),
                            pacmanTile.y * Tile.TILE_HEIGHT + scaledNum(4));
                    continue;
                }
            }
        }
    }

    public void loadWalls() {
        BufferedImage level = Assets.walls;
        int w = level.getWidth();
        int h = level.getHeight();
//        System.out.println("w " + w + "\nh " + h);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = level.getRGB(x, y);
                //Get color of pixel in testLevel array
                int a = ((pixel & 0xff000000) >>> 24);
                int r = ((pixel & 0x00ff0000) >>> 16);
                int g = ((pixel & 0x0000ff00) >>> 8);
                int b = ((pixel & 0x000000ff));

                //***DOUBLE***
                //TL
                if (r == 255 && g == 20 && b == 20) {
                    //System.out.println("Wall block at: " + x + " " + y);
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.DOUBLE_TL;
                    continue;
                }
                //TM
                if (r == 255 && g == 50 && b == 50) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.DOUBLE_TM;
                    continue;
                }
                //TR
                if (r == 255 && g == 71 && b == 71) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.DOUBLE_TR;
                    continue;
                }
                //ML
                if (r == 255 && g == 112 && b == 112) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.DOUBLE_ML;
                    continue;
                }
                //MR
                if (r == 255 && g == 131 && b == 131) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.DOUBLE_MR;
                    continue;
                }
                //BL
                if (r == 255 && g == 155 && b == 155) {
                    //System.out.println("Wall block at: " + x + " " + y);
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.DOUBLE_BL;
                    continue;
                }
                //BM
                if (r == 255 && g == 173 && b == 173) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.DOUBLE_BM;
                    continue;
                }
                //BR
                if (r == 255 && g == 188 && b == 188) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.DOUBLE_BR;
                    continue;
                }
                //***LINE***
                //TL
                if (r == 255 && g == 0 && b == 234) {
                    //System.out.println("Wall block at: " + x + " " + y);
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.LINE_TL;
                    continue;
                }
                //TM
                if (r == 255 && g == 28 && b == 236) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.LINE_TM;
                    continue;
                }
                //TR
                if (r == 255 && g == 52 && b == 238) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.LINE_TR;
                    continue;
                }
                //ML
                if (r == 255 && g == 82 && b == 241) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.LINE_ML;
                    continue;
                }
                //MR
                if (r == 255 && g == 108 && b == 243) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.LINE_MR;
                    continue;
                }
                //BL
                if (r == 255 && g == 125 && b == 244) {
                    //System.out.println("Wall block at: " + x + " " + y);
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.LINE_BL;
                    continue;
                }
                //BM
                if (r == 255 && g == 146 && b == 246) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.LINE_BM;
                    continue;
                }
                //BR
                if (r == 255 && g == 174 && b == 248) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.LINE_BR;
                    continue;
                }
                //***Hor***
                //TL
                if (r == 192 && g == 0 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.HOR_TL;
                    continue;
                }
                //TR
                if (r == 208 && g == 64 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.HOR_TR;
                    continue;
                }
                //BL
                if (r == 220 && g == 113 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.HOR_BL;
                    continue;
                }
                //BR
                if (r == 231 && g == 157 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.HOR_BR;
                    continue;
                }
                //***Ver***
                //TL
                if (r == 126 && g == 0 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.VER_TL;
                    continue;
                }
                //TR
                if (r == 156 && g == 60 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.VER_TR;
                    continue;
                }
                //BL
                if (r == 182 && g == 110 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.VER_BL;
                    continue;
                }
                //BR
                if (r == 206 && g == 158 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.VER_BR;
                    continue;
                }
                //**Square**
                //TL
                if (r == 255 && g == 120 && b == 0) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.SQUARE_TL;
                    continue;
                }
                //TR
                if (r == 255 && g == 149 && b == 55) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.SQUARE_TR;
                    continue;
                }
                //BL
                if (r == 255 && g == 176 && b == 105) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.SQUARE_BL;
                    continue;
                }
                //BR
                if (r == 255 && g == 199 && b == 149) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.SQUARE_BR;
                    continue;
                }
                //HOME_L
                if (r == 0 && g == 255 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.HOME_L;
                    continue;
                }
                //HOME_R
                if (r == 127 && g == 255 && b == 255) {
                    tiles[y][x].id = Tile.WALL;
                    tiles[y][x].wallType = Tile.HOME_R;
                    continue;
                }
            }
        }
    }

    public void loadIntersections() {
        BufferedImage level = Assets.intersections;
        int w = level.getWidth();
        int h = level.getHeight();
//        System.out.println("w " + w + "\nh " + h);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = level.getRGB(x, y);
                //Get color of pixel in testLevel array
                int a = ((pixel & 0xff000000) >>> 24);
                int r = ((pixel & 0x00ff0000) >>> 16);
                int g = ((pixel & 0x0000ff00) >>> 8);
                int b = ((pixel & 0x000000ff));
                Tile t = tiles[y][x];

//                //Depending on the color of a pixel, set the tile
//                if (r == 0 && g == 0 && b == 0) {
//                    //System.out.println("Black block at: " + x + " " + y);
//                    t.id = Tile.EMPTY;
//                    continue;   //We found a tile, do not need to check others
//                }
                //Active (white tile)
                if (r == 0 && g == 22 && b == 156) {
                    System.out.println(x + " " + y);
                    t.intersection = true;
                    continue;
                }
            }
        }
    }

    public void handleKeyEvents() {
        //Handle sub class input
//        player.handleInput();
//        if (Input.isKeyPressed(KeyEvent.VK_W)) {
        if (Input.isKeyTyped(KeyEvent.VK_W)) {
            if (blinky.currentDir != Blinky.DIR.UP) {
                System.out.println("Up");
                if (pixelToTileAbove(blinky.pixel.x, blinky.pixel.y).legal) {
                    blinky.pixel.y -= scaledNum(1);
                    blinky.setCurrentDir(Blinky.DIR.UP);
                }
            }
//            blinky.movePixelUp();
//            blinky.moveUp();
        }
        if (Input.isKeyTyped(KeyEvent.VK_S)) {
            if (blinky.currentDir != Blinky.DIR.DOWN) {
                System.out.println("Down");
                if (pixelToTileBelow(blinky.pixel.x, blinky.pixel.y).legal) {
                    blinky.pixel.y += scaledNum(1);
                    blinky.setCurrentDir(Blinky.DIR.DOWN);
                }
            }
//            blinky.movePixelDown();
//            blinky.moveDown();
        }
        if (Input.isKeyTyped(KeyEvent.VK_A)) {
            if (blinky.currentDir != Blinky.DIR.LEFT) {
                System.out.println("Left");
                if (pixelToTileLeft(blinky.pixel.x, blinky.pixel.y).legal) {
                    blinky.pixel.x -= scaledNum(1);
                    blinky.setCurrentDir(Blinky.DIR.LEFT);
                }
            }
//            blinky.movePixelLeft();
//            blinky.moveLeft();
        }
        if (Input.isKeyTyped(KeyEvent.VK_D)) {
            if (blinky.currentDir != Blinky.DIR.RIGHT) {
                System.out.println("Right");
                if (pixelToTileRight(blinky.pixel.x, blinky.pixel.y).legal) {
                    blinky.pixel.x += scaledNum(1);
                    blinky.setCurrentDir(Blinky.DIR.RIGHT);
                }
            }
//            blinky.movePixelRight();
//            blinky.moveRight();
        }
//        movePixel();
        movePixel2();
    }

    private void movePixel() {
        //UP
        if (Input.isKeyTyped(KeyEvent.VK_I)) {
            //check if above pixel is not a wall
            if (!isWall(pixelToTile(pixel.x, pixel.y - scaledNum(1)))) {
                pixel.y -= scaledNum(1);
            } else {
                System.out.println("can't move UP");
            }
        }
        //DOWN
        if (Input.isKeyTyped(KeyEvent.VK_K)) {
            if (!isWall(pixelToTile(pixel.x, pixel.y + scaledNum(1)))) {
                pixel.y += scaledNum(1);

            } else {
                System.out.println("can't move DOWN");
            }
        }
        //LEFT
        if (Input.isKeyTyped(KeyEvent.VK_J)) {
            if (!isWall(pixelToTile(pixel.x - scaledNum(1), pixel.y))) {
                pixel.x -= scaledNum(1);
            } else {
                System.out.println("can't move LEFT");
            }
        }
        //RIGHT
        if (Input.isKeyTyped(KeyEvent.VK_L)) {
            if (!isWall(pixelToTile(pixel.x + scaledNum(1), pixel.y))) {
                pixel.x += scaledNum(1);
            } else {
                System.out.println("can't move RIGHT");
            }
        }
        //Print debug
        if (Input.isKeyTyped(KeyEvent.VK_I) || Input.isKeyTyped(KeyEvent.VK_K)
                || Input.isKeyTyped(KeyEvent.VK_J) || Input.isKeyTyped(KeyEvent.VK_L)) {
//            System.out.println("pixel (scaled): " + pixel.x + "," + pixel.y);
            System.out.println("pixel: " + (pixel.x / GamePanel.scale) + ","
                    + (pixel.y / GamePanel.scale));
            System.out.println("tile: " + pixel.x / Tile.TILE_WIDTH + ","
                    + pixel.y / Tile.TILE_HEIGHT);
            System.out.println("");
        }
    }

    private void movePixel2() {
        //UP
        if (Input.isKeyTyped(KeyEvent.VK_I)) {
            turnBuffer = DIR.UP;
            //Get tile above -> if its not active ignore movement
            if (isLegal(pixelToTileAbove(pixel.x, pixel.y))) {
                currentDir = DIR.UP;
                System.out.println("currentDir: " + currentDir);
            } else {
                System.out.println("cant press up, tile above blocked");
            }
        }
        //DOWN
        if (Input.isKeyTyped(KeyEvent.VK_K)) {
            turnBuffer = DIR.DOWN;
            if (isLegal(pixelToTileBelow(pixel.x, pixel.y))) {
                currentDir = DIR.DOWN;
                System.out.println("currentDir: " + currentDir);
            } else {

            }
        }
        //LEFT
        if (Input.isKeyTyped(KeyEvent.VK_J)) {
            //if current tile is left teleport tile, return
//            Tile t = tiles[17][0];
            if (pixelToTile(pixel.x, pixel.y).teleportTile) {
                System.out.println("We are on left telport tile");
                return;
            }
            turnBuffer = DIR.LEFT;
            if (isLegal(pixelToTileLeft(pixel.x, pixel.y))) {
                currentDir = DIR.LEFT;
                System.out.println("currentDir: " + currentDir);
            } else {

            }
        }
        //RIGHT
        if (Input.isKeyTyped(KeyEvent.VK_L)) {
            //if current tile is left teleport tile, return
//            Tile t = tiles[17][NO_OF_TILES_X - 1];
            if (pixelToTile(pixel.x, pixel.y).teleportTile) {
                System.out.println("We are on left telport tile");
                return;
            }
            turnBuffer = DIR.RIGHT;
            if (isLegal(pixelToTileRight(pixel.x, pixel.y))) {
                currentDir = DIR.RIGHT;
                System.out.println("currentDir: " + currentDir);
            } else {

            }
        }
        //Print debug
        if (Input.isKeyTyped(KeyEvent.VK_I) || Input.isKeyTyped(KeyEvent.VK_K)
                || Input.isKeyTyped(KeyEvent.VK_J) || Input.isKeyTyped(KeyEvent.VK_L)) {
//            System.out.println("pixel (scaled): " + pixel.x + "," + pixel.y);
            System.out.println("pixel: " + (pixel.x / GamePanel.scale) + ","
                    + (pixel.y / GamePanel.scale));
            System.out.println("tile: " + pixel.x / Tile.TILE_WIDTH + ","
                    + pixel.y / Tile.TILE_HEIGHT);
            System.out.println("");
        }
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

    private void moveUpOLDSIES() {
        //Store current position
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        //Check if we pac-man move
        if (isLegal(pacmanTile.x, pacmanTile.y - 1)) {
            pacmanTile.y -= 1;
            //Update the new tile id then set old tile empty
            setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
            setTile(temp.x, temp.y, Tile.ACTIVE);
        } else {
            System.out.println("Blocked Up");
            currentDir = DIR.STOP;
        }
    }

    private void updateScore() {
        Tile t = tiles[pacmanTile.y][pacmanTile.x];
        switch (t.id) {
            case Tile.FOOD:
//                System.out.println("FOOD!");
                score += FOOD_SCORE;
                break;
            case Tile.POWER_UP:
//                System.out.println("Energizer!");
                score += ENERGIZER_SCORE;
                break;
        }
    }

    private void moveUp() {
//        System.out.println("move tile UP");

        //Store current position
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        pacmanTile.y -= 1;

        //Handle collision with item
        updateScore();

        //Update the new tile id then set old tile empty
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(temp.x, temp.y, Tile.ACTIVE);

    }

    private void moveDown() {
//        System.out.println("move tile DOWN");
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        pacmanTile.y += 1;
        updateScore();
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    private void moveLeft() {
//        System.out.println("move tile LEFT");
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        pacmanTile.x -= 1;
        updateScore();
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    private void moveRight() {
//        System.out.println("move tile RIGHT");
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        pacmanTile.x += 1;
        updateScore();
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    private void moveGhosts(float deltaTime) {
        blinky.update(deltaTime);
    }

    private void setTile(int x, int y, int id) {
        tiles[y][x].id = id;
    }

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
        return tiles[y][x].id == Tile.FOOD;
    }

    private boolean isPowerUp(int x, int y) {
        System.out.println("isPowerUp");
        return tiles[y][x].id == Tile.POWER_UP;
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

    private void drawActiveTiles(Graphics2D g) {
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
//                int tileId = t.id;
//                switch (tileId) {
//                    case Tile.ACTIVE:
//                    case Tile.FOOD:
//                    case Tile.POWER_UP:
//                        g.setColor(Color.WHITE);
//                        g.drawRect((int) t.position.x, (int) t.position.y,
//                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//                        break;
//                }
                if (t.legal) {
                    g.setColor(Color.WHITE);
                    g.drawRect((int) t.position.x, (int) t.position.y,
                            Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
                }
            }
        }
    }

    /**
     * This method could use some work
     *
     * @param g graphics object
     */
    private void drawTiles(Graphics2D g) {
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
                int tileId = t.id;
                float size;
                switch (tileId) {
//                    case Tile.ACTIVE:
//                        g.setColor(Color.WHITE);
//                        g.drawRect((int) t.position.x, (int) t.position.y,
//                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//                        break;
                    case Tile.FOOD:
                        g.setColor(Color.DARK_GRAY);
                        size = 4;
                        g.fillRect((int) (t.position.x + Tile.TILE_WIDTH / 2f - size / 2 + 1),
                                (int) (t.position.y + Tile.TILE_HEIGHT / 2f - size / 2 + 1),
                                (int) size,
                                (int) size);
                        break;
                    case Tile.POWER_UP:
                        size = 16;
                        g.setColor(Color.GREEN);
                        g.fillOval((int) (t.position.x + Tile.TILE_WIDTH / 2f - size / 2 + 1),
                                (int) (t.position.y + Tile.TILE_HEIGHT / 2f - size / 2 + 1),
                                (int) size, (int) size);
                        break;
                    case Tile.PLAYER:
//                        Tile p = tiles[currentPos.y][currentPos.x];
//                        //Jumps from tile to tile
//                        g.setColor(Color.YELLOW);
//                        g.fillRect((int) t.position.x, (int) t.position.y,
//                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
                        break;
                    case Tile.BLINKY:
                        g.setColor(blinky.color);
                        g.fillRect((int) t.position.x, (int) t.position.y,
                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
                        break;
                }
            }
        }
    }

    /**
     * This method could use some work
     *
     * @param g graphics object
     */
    private void drawWalls(Graphics2D g) {
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
                int tileId = t.wallType;
                switch (tileId) {
                    case Tile.DOUBLE_TL:
                        g.drawImage(Assets.double_tl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.DOUBLE_TM:
                        g.drawImage(Assets.double_tm, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.DOUBLE_TR:
                        g.drawImage(Assets.double_tr, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.DOUBLE_ML:
                        g.drawImage(Assets.double_ml, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.DOUBLE_MR:
                        g.drawImage(Assets.double_mr, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.DOUBLE_BL:
                        g.drawImage(Assets.double_bl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.DOUBLE_BM:
                        g.drawImage(Assets.double_bm, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.DOUBLE_BR:
                        g.drawImage(Assets.double_br, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    //***Line***
                    case Tile.LINE_TL:
                        g.drawImage(Assets.line_tl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.LINE_TM:
                        g.drawImage(Assets.line_tm, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.LINE_TR:
                        g.drawImage(Assets.line_tr, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.LINE_ML:
                        g.drawImage(Assets.line_ml, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.LINE_MR:
                        g.drawImage(Assets.line_mr, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.LINE_BL:
                        g.drawImage(Assets.line_bl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.LINE_BM:
                        g.drawImage(Assets.line_bm, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.LINE_BR:
                        g.drawImage(Assets.line_br, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    //Hor
                    case Tile.HOR_TL:
                        g.drawImage(Assets.hor_tl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.HOR_TR:
                        g.drawImage(Assets.hor_tr, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.HOR_BL:
                        g.drawImage(Assets.hor_bl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.HOR_BR:
                        g.drawImage(Assets.hor_br, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    //Ver
                    case Tile.VER_TL:
                        g.drawImage(Assets.ver_tl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.VER_TR:
                        g.drawImage(Assets.ver_tr, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.VER_BL:
                        g.drawImage(Assets.ver_bl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.VER_BR:
                        g.drawImage(Assets.ver_br, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    //Square
                    case Tile.SQUARE_TL:
                        g.drawImage(Assets.square_tl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.SQUARE_TR:
                        g.drawImage(Assets.square_tr, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.SQUARE_BL:
                        g.drawImage(Assets.square_bl, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.SQUARE_BR:
                        g.drawImage(Assets.square_br, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.HOME_L:
                        g.drawImage(Assets.home_l, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                    case Tile.HOME_R:
                        g.drawImage(Assets.home_r, (int) t.position.x,
                                (int) t.position.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
                        break;
                }
            }
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

    private int scaledNum(int i) {
        return GamePanel.scale * i;
    }

    private Point getCenter(Tile t) {
        centerPoint.x = (int) t.bounds.lowerLeft.x + scaledNum(3);
        centerPoint.y = (int) t.bounds.lowerLeft.y + scaledNum(4);

//        centerPoint.x = (int) (t.bounds.lowerLeft.x / GamePanel.scale + 3);
//        centerPoint.y = (int) (t.bounds.lowerLeft.y / GamePanel.scale + 4);
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
                    currentDir = DIR.STOP;
                    System.out.println("currentDir: " + currentDir);
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
            currentDir = DIR.STOP;
            System.out.println("currentDir: " + currentDir);
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
                    currentDir = DIR.STOP;
                    System.out.println("currentDir: " + currentDir);
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
            currentDir = DIR.STOP;
            System.out.println("currentDir: " + currentDir);
        }
    }

    private void movePixelLeft() {
//        System.out.println("left...");
        Tile previous = pixelToTile(pixel.x, pixel.y);
        if (!isWall(pixelToTile(pixel.x - scaledNum(1), pixel.y))) {
            //-------------------Wrap Player------------------------
            if (pixel.x < scaledNum(9)) {
                pixel.x = (int) WORLD_WIDTH - scaledNum(1);
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
                    currentDir = DIR.STOP;
                    System.out.println("currentDir: " + currentDir);
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
            currentDir = DIR.STOP;
            System.out.println("currentDir: " + currentDir);
        }
    }

    private void movePixelRight() {
//        System.out.println("right...");
        Tile previous = pixelToTile(pixel.x, pixel.y);

        if (!isWall(pixelToTile(pixel.x + scaledNum(1), pixel.y))) {
            //-------------------Wrap Player------------------------
            if (pixel.x > WORLD_WIDTH - scaledNum(11)) {
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
                    currentDir = DIR.STOP;
                    System.out.println("currentDir: " + currentDir);
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
            currentDir = DIR.STOP;
            System.out.println("currentDir: " + currentDir);
        }
    }

    private void handleTurnBuffer() {
        if (turnBuffer != DIR.NA) {
            switch (turnBuffer) {
                case UP:
                    if (pixelToTileAbove(pixel.x, pixel.y).legal) {
//                        System.out.println("consuming UP turn");
                        currentDir = DIR.UP;
                        turnBuffer = DIR.NA;
                    }
                    break;
                case DOWN:
                    if (pixelToTileBelow(pixel.x, pixel.y).legal) {
//                        System.out.println("consuming DOWN turn");
                        currentDir = DIR.DOWN;
                        turnBuffer = DIR.NA;
                    }
                    break;
                case LEFT:
                    if (pixelToTileLeft(pixel.x, pixel.y).legal) {
//                        System.out.println("consuming LEFT turn");
                        currentDir = DIR.LEFT;
                        turnBuffer = DIR.NA;
                    }
                    break;
                case RIGHT:
                    if (pixelToTileRight(pixel.x, pixel.y).legal) {
//                        System.out.println("consuming RIGHT turn");
                        currentDir = DIR.RIGHT;
                        turnBuffer = DIR.NA;
                    }
                    break;
            }
        }
    }

    private void movePacman(float deltaTime) {
        handleTurnBuffer();
        switch (currentDir) {
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

//        //test align
//        Point center = getCenter(pixelToTile(pixel.x, pixel.y));
//        System.out.println("Test align");
////        System.out.println("center (scaled): " + center.x + "," + center.y);
//        System.out.println("center: " + (center.x / GamePanel.scale)
//                + "," + (center.y / GamePanel.scale));
//        //Print pixel location
//        System.out.println("pixel: " + (pixel.x / GamePanel.scale) + ","
//                + (pixel.y / GamePanel.scale));
//        //Print current tile
//        System.out.println("Tile: [" + (pixel.x / Tile.TILE_WIDTH) + ","
//                + (pixel.y / Tile.TILE_HEIGHT) + "]");
//        System.out.println("");
    }

    @Override
    void gameUpdate(float deltaTime) {
        //********** Do updates HERE **********
        deltaTime *= scaleTime; //Objects that are slow mo after this line
        /*slow down pacman*/
//        oldMovePacman();
        elapsedTime += deltaTime;   //in seconds
        if (elapsedTime >= TIME_TO_MOVE) {
//            System.out.println("move");
            movePacman(deltaTime);
            moveGhosts(deltaTime);
            elapsedTime = 0;
        }
    }

    @Override
    void gameRender(Graphics2D g) {
        //Clear screen
        g.setColor(backgroundColor);
        g.fillRect(0, 0, (int) WORLD_WIDTH, (int) WORLD_HEIGHT);

        //********** Do drawings HERE **********
        //COULD DRAW ALL TILES IN ONE LOOP
        drawActiveTiles(g);
        drawTiles(g);
//        drawWalls(g);

        //Draw Pac-Man
        drawPacman(g);
        //Draw pixel position
        g.setColor(Color.BLUE);
        g.fillRect(pixel.x, pixel.y, scaledNum(1), scaledNum(1));
        g.fillRect(blinky.pixel.x, blinky.pixel.y, scaledNum(1), scaledNum(1));

        //Draw Score
        g.setColor(Color.WHITE);
        totalScore = String.format("score %04d", score);
        g.drawString(totalScore, scoreX, scoreY);
        //Draw grid
//        drawGrid(g);
//        drawHashGrid(g);
    }

    private void drawPacman(Graphics2D g) {
        //draw circle
//        int size = 24;
//        g.setColor(Color.RED);
//        g.fillOval(pixel.x - size / 2, pixel.y - size / 2, size, size);

        //draw image
//        int size = 26;
//        g.drawImage(Assets.pacman.getImage(), pixel.x - size / 2, pixel.y - size / 2, null);
        //Test rotation
        AffineTransform old = g.getTransform();
        trans.setToIdentity();  //AffineTransform trans = new AffineTransform();
        float centerX = (pixel.x);
        float centerY = (pixel.y);
        trans.translate(centerX, centerY);
        trans.rotate(Math.toRadians(rotation));
        trans.translate(-centerX, -centerY);
        g.setTransform(trans);

        int size = 26;
        g.drawImage(Assets.pacman.getImage(), pixel.x - size / 2, pixel.y - size / 2, null);
//        g.setColor(Color.BLUE);
//        g.drawRect((int) position.x, (int) position.y,
//                (int) PLAYER_WIDTH, (int) PLAYER_HEIGHT);

        g.setTransform(old);
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(Color.WHITE);
        Tile tile = tiles[4][1];
        for (int i = 0; i < 8; i++) {
            //draw horizontal line
            g.drawLine((int) tile.bounds.lowerLeft.x,
                    (int) tile.bounds.lowerLeft.y + (i * Tile.TILE_HEIGHT / 8),
                    (int) WORLD_WIDTH,
                    (int) tile.bounds.lowerLeft.y + (i * Tile.TILE_HEIGHT / 8));

            //draw vertical lines
            g.drawLine((int) tile.bounds.lowerLeft.x + (i * Tile.TILE_WIDTH / 8), //x1
                    (int) tile.bounds.lowerLeft.y, //y1
                    (int) tile.bounds.lowerLeft.x + (i * Tile.TILE_WIDTH / 8), //x2
                    (int) World.WORLD_HEIGHT);    //y2
        }
    }

    private void drawHashGrid(Graphics2D g) {
        g.setColor(Color.YELLOW);
        int cell = (int) grid.getCellSize();
//        cell += (int) WORLD_POS.x;
        //draw vertical
        for (int i = cell; i < WORLD_WIDTH; i += cell) {
//            g.drawLine(i, 0, i, (int) WORLD_HEIGHT);
            g.drawLine(i, 0, i, (int) WORLD_HEIGHT);
        }
//        //draw horizontal
        for (int i = cell; i < WORLD_HEIGHT; i += cell) {
            g.drawLine(0, i, (int) WORLD_WIDTH, i);
        }
    }

    public void setBackgroundColor(int r, int g, int b, int a) {
        backgroundColor = new Color(r, g, b, a);
    }
}
