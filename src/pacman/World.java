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
import java.awt.image.BufferedImage;

/**
 * 20-Feb-2018, 22:16:38.
 *
 * @author Mo
 */
public class World extends GameObject {

    public static final float WORLD_WIDTH = 224 * GamePanel.scale;
    public static final float WORLD_HEIGHT = 288 * GamePanel.scale;
//    public static final float WORLD_WIDTH = 600;
//    public static final float WORLD_HEIGHT = 600;
//    public static final int NO_OF_TILES_X = 15;
//    public static final int NO_OF_TILES_Y = 15;

    public static final int NO_OF_TILES_X = (int) (WORLD_WIDTH / Tile.TILE_WIDTH);
    public static final int NO_OF_TILES_Y = (int) (WORLD_HEIGHT / Tile.TILE_HEIGHT);

    public static final int WORLD_STATE_READY = 0;
    public static final int WORLD_STATE_RUNNING = 1;
    public static final int WORLD_STATE_GAME_OVER = 2;
    public int state;

    private final WorldListener listener;
    public Tile[][] tiles;

    //Player info
    public static final float SPEED = 88;   //pixels per second

    public enum DIR {

        UP, DOWN, LEFT, RIGHT, STOP
    }
    private Point pacmanTile;
    private Point temp; //old position
    private DIR currentDir = DIR.STOP;
    private Point pacOffset; //-8 -> 8 (num of pixels before move) 88 pixels per second (11 tiles)

    private final Vector2D touchPos = new Vector2D();

    private Color backgroundColor = Color.BLACK;
    public static final int xShift = 0; //(int) (GamePanel.GAME_WIDTH / 2 - WORLD_WIDTH / 2)
    public static final int yShift = 0;
    private SpatialHashGrid grid;
    private float scaleTime = 1f;

    //test variable
    private int num = 0;
    private Point pixel;

    public World(WorldListener lis) {
        this.listener = lis;
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
        System.out.println("-----------------------------");
        System.out.println("background Color: " + backgroundColor);
        System.out.println("World loaded...");
    }

    public interface WorldListener {

        void fire();

        void enemySpawn();

        void enemyDie();

        void playerSpawn();

        void playerHurt();

        void playerDie();

        void loadNextWave();

        void sayPraise();
    }

    private void init() {
        tiles = new Tile[NO_OF_TILES_Y][NO_OF_TILES_X];
        nullTiles();    //sets to null
        initTiles();    //create empty tiles
        setEmpty();     //sets to empty (not needed here)
//        setBorder();
//        setRandomTiles();

        pacmanTile = new Point();
        temp = new Point();
        pacOffset = new Point();
        pixel = new Point();
        System.out.println("currentDir = " + currentDir);

        loadLevel();
        loadWalls();

        initGrid();
        //old below
//        Tile playerPos = tiles[2][1];
//        player = new Pac(playerPos.position.x, playerPos.position.y);
        //-----------------------------END my random test
//        backgroundColor = new Color(0, 0, 0);    //Represents colour of background
//        backgroundColor = new Color(135,206,235);    //Represents colour of background
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
                    continue;
                }
                //Power up (green tile)
                if (r == 0 && g == 255 && b == 0) {
                    t.id = Tile.POWER_UP;
                    t.legal = true;
                    continue;
                }
                //Player (yellow tile)
                if (r == 255 && g == 255 && b == 0) {
                    t.id = Tile.PLAYER;
                    t.legal = true;
                    System.out.println("player position: " + x + ", " + y);
                    pacmanTile.setLocation(x, y);
                    temp.setLocation(x, y);
                    pacOffset.setLocation(pacmanTile.x * Tile.TILE_WIDTH,
                            pacmanTile.y * Tile.TILE_HEIGHT);
                    this.pixel.setLocation(pacmanTile.x * Tile.TILE_WIDTH,
                            pacmanTile.y * Tile.TILE_HEIGHT);
//                    pacOffset.setLocation(playerPos.x + 3, playerPos.y + 3);
                    System.out.println("setting packoffset: " + pacOffset);
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
                    tiles[y][x].wallType = Tile.DOUBLE_TL;
                    continue;
                }
                //TM
                if (r == 255 && g == 50 && b == 50) {
                    tiles[y][x].wallType = Tile.DOUBLE_TM;
                    continue;
                }
                //TR
                if (r == 255 && g == 71 && b == 71) {
                    tiles[y][x].wallType = Tile.DOUBLE_TR;
                    continue;
                }
                //ML
                if (r == 255 && g == 112 && b == 112) {
                    tiles[y][x].wallType = Tile.DOUBLE_ML;
                    continue;
                }
                //MR
                if (r == 255 && g == 131 && b == 131) {
                    tiles[y][x].wallType = Tile.DOUBLE_MR;
                    continue;
                }
                //BL
                if (r == 255 && g == 155 && b == 155) {
                    //System.out.println("Wall block at: " + x + " " + y);
                    tiles[y][x].wallType = Tile.DOUBLE_BL;
                    continue;
                }
                //BM
                if (r == 255 && g == 173 && b == 173) {
                    tiles[y][x].wallType = Tile.DOUBLE_BM;
                    continue;
                }
                //BR
                if (r == 255 && g == 188 && b == 188) {
                    tiles[y][x].wallType = Tile.DOUBLE_BR;
                    continue;
                }
                //***LINE***
                //TL
                if (r == 255 && g == 0 && b == 234) {
                    //System.out.println("Wall block at: " + x + " " + y);
                    tiles[y][x].wallType = Tile.LINE_TL;
                    continue;
                }
                //TM
                if (r == 255 && g == 28 && b == 236) {
                    tiles[y][x].wallType = Tile.LINE_TM;
                    continue;
                }
                //TR
                if (r == 255 && g == 52 && b == 238) {
                    tiles[y][x].wallType = Tile.LINE_TR;
                    continue;
                }
                //ML
                if (r == 255 && g == 82 && b == 241) {
                    tiles[y][x].wallType = Tile.LINE_ML;
                    continue;
                }
                //MR
                if (r == 255 && g == 108 && b == 243) {
                    tiles[y][x].wallType = Tile.LINE_MR;
                    continue;
                }
                //BL
                if (r == 255 && g == 125 && b == 244) {
                    //System.out.println("Wall block at: " + x + " " + y);
                    tiles[y][x].wallType = Tile.LINE_BL;
                    continue;
                }
                //BM
                if (r == 255 && g == 146 && b == 246) {
                    tiles[y][x].wallType = Tile.LINE_BM;
                    continue;
                }
                //BR
                if (r == 255 && g == 174 && b == 248) {
                    tiles[y][x].wallType = Tile.LINE_BR;
                    continue;
                }
                //***Hor***
                //TL
                if (r == 192 && g == 0 && b == 255) {
                    tiles[y][x].wallType = Tile.HOR_TL;
                    continue;
                }
                //TR
                if (r == 208 && g == 64 && b == 255) {
                    tiles[y][x].wallType = Tile.HOR_TR;
                    continue;
                }
                //BL
                if (r == 220 && g == 113 && b == 255) {
                    tiles[y][x].wallType = Tile.HOR_BL;
                    continue;
                }
                //BR
                if (r == 231 && g == 157 && b == 255) {
                    tiles[y][x].wallType = Tile.HOR_BR;
                    continue;
                }
                //***Ver***
                //TL
                if (r == 126 && g == 0 && b == 255) {
                    tiles[y][x].wallType = Tile.VER_TL;
                    continue;
                }
                //TR
                if (r == 156 && g == 60 && b == 255) {
                    tiles[y][x].wallType = Tile.VER_TR;
                    continue;
                }
                //BL
                if (r == 182 && g == 110 && b == 255) {
                    tiles[y][x].wallType = Tile.VER_BL;
                    continue;
                }
                //BR
                if (r == 206 && g == 158 && b == 255) {
                    tiles[y][x].wallType = Tile.VER_BR;
                    continue;
                }
                //**Square**
                //TL
                if (r == 255 && g == 120 && b == 0) {
                    tiles[y][x].wallType = Tile.SQUARE_TL;
                    continue;
                }
                //TR
                if (r == 255 && g == 149 && b == 55) {
                    tiles[y][x].wallType = Tile.SQUARE_TR;
                    continue;
                }
                //BL
                if (r == 255 && g == 176 && b == 105) {
                    tiles[y][x].wallType = Tile.SQUARE_BL;
                    continue;
                }
                //BR
                if (r == 255 && g == 199 && b == 149) {
                    tiles[y][x].wallType = Tile.SQUARE_BR;
                    continue;
                }
                //HOME_L
                if (r == 0 && g == 255 && b == 255) {
                    tiles[y][x].wallType = Tile.HOME_L;
                    continue;
                }
                //HOME_R
                if (r == 127 && g == 255 && b == 255) {
                    tiles[y][x].wallType = Tile.HOME_R;
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
            System.out.println("Up");
//            moveUp();
            currentDir = DIR.UP;
        }
        if (Input.isKeyTyped(KeyEvent.VK_S)) {
            System.out.println("Down");
//            moveDown();
            currentDir = DIR.DOWN;
        }
        if (Input.isKeyTyped(KeyEvent.VK_A)) {
            System.out.println("Left");
//            moveLeft();
            currentDir = DIR.LEFT;
        }
        if (Input.isKeyTyped(KeyEvent.VK_D)) {
            System.out.println("Right");
//            moveRight();
            currentDir = DIR.RIGHT;
        }
        movePixel();
    }

    private void movePixel() {
        //UP
        if (Input.isKeyTyped(KeyEvent.VK_I)) {
            //1 pixel (depending on scale used)
            pixel.y -= 1 * GamePanel.scale;
        }
        //DOWN
        if (Input.isKeyTyped(KeyEvent.VK_K)) {
            pixel.y += 1 * GamePanel.scale;

        }
        //LEFT
        if (Input.isKeyTyped(KeyEvent.VK_J)) {
            pixel.x -= 1 * GamePanel.scale;
        }
        //RIGHT
        if (Input.isKeyTyped(KeyEvent.VK_L)) {
            pixel.x += 1 * GamePanel.scale;
        }
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

    private void moveUp() {
        //Store current position
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        pacmanTile.y -= 1;
        //Update the new tile id then set old tile empty
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(temp.x, temp.y, Tile.ACTIVE);

    }

    private void moveDown() {
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        pacmanTile.y += 1;
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    private void moveLeft() {
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        pacmanTile.x -= 1;
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    private void moveRight() {
        temp.setLocation(pacmanTile.x, pacmanTile.y);
        pacmanTile.x += 1;
        setTile(pacmanTile.x, pacmanTile.y, Tile.PLAYER);
        setTile(temp.x, temp.y, Tile.ACTIVE);
    }

    private boolean canMoveUp() {
        return isLegal(pacmanTile.x, pacmanTile.y - 1);
    }

    private boolean canMoveDown() {
        return isLegal(pacmanTile.x, pacmanTile.y + 1);
    }

    private boolean canMoveLeft() {
        return isLegal(pacmanTile.x - 1, pacmanTile.y);
    }

    private boolean canMoveRight() {
        return isLegal(pacmanTile.x + 1, pacmanTile.y);
    }

    private void setTile(int x, int y, int id) {
        tiles[y][x].id = id;
    }

    private boolean isLegal(int x, int y) {
//        System.out.println("isLegal");
        return tiles[y][x].legal;
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

    private void drawActiveTiles(Graphics2D g) {
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
                int tileId = t.id;
                switch (tileId) {
                    case Tile.ACTIVE:
                    case Tile.FOOD:
                    case Tile.POWER_UP:
                        g.setColor(Color.WHITE);
                        g.drawRect((int) t.position.x, (int) t.position.y,
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
                        g.setColor(Color.YELLOW);
                        g.fillRect((int) t.position.x, (int) t.position.y,
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
        x = (int) Math.floor(x / Tile.TILE_WIDTH);
        y = (int) Math.floor(y / Tile.TILE_HEIGHT);
        return tiles[y][x];
    }

    private void recenter(int x, int y) {
        Tile t = pixelToTile(x, y);
        pacOffset.x = (int) t.bounds.lowerLeft.x;
        pacOffset.y = (int) t.bounds.lowerLeft.y;
    }

    @Override
    void gameUpdate(float deltaTime) {
        //********** Do updates HERE **********
        deltaTime *= scaleTime; //Objects that are slow mo after this line
        //slow down pack man
        int speed = 8;
        switch (currentDir) {
            case UP:
                if (canMoveUp()) {
                    pacOffset.y -= 1;
                    if (pacOffset.y % speed == 0) {
                        moveUp();   //only when center point reaches middle of next tile
                    }
                } else {
                    currentDir = DIR.STOP;
                    System.out.println(currentDir);
                }
                break;
            case DOWN:
                if (canMoveDown()) {
                    pacOffset.y += 1;
                    if (pacOffset.y % speed == 0) {
                        moveDown();
                    }
                } else {
                    currentDir = DIR.STOP;
                    System.out.println(currentDir);
                }
                break;
            case LEFT:
                if (canMoveLeft()) {
                    pacOffset.x -= 1;
                    if (pacOffset.x % speed == 0) {
                        moveLeft();
                    }
                } else {
                    currentDir = DIR.STOP;
                    System.out.println(currentDir);
                }
                break;
            case RIGHT:
                if (canMoveRight()) {
                    pacOffset.x += 1;
                    if (pacOffset.x % speed == 0) {
                        moveRight();
                    }
                } else {
                    currentDir = DIR.STOP;
                    System.out.println(currentDir);
                }
                break;
            case STOP:
                //pacOffset.set(0, 0);
                break;
        }
//        System.out.println("player: " + pacmanTile.x + ", " + pacmanTile.y);
//        System.out.println("offset: " + pacOffset.x + ", " + pacOffset.y);
//        int x,y;
//        x = (int) Math.floor(pacOffset.x / Tile.TILE_WIDTH);
//        y = (int) Math.floor(pacOffset.y / Tile.TILE_HEIGHT);
//        System.out.println("(x, y) -> "+x+", "+y);
    }

    @Override
    void gameRender(Graphics2D g) {
        //Clear screen
        g.setColor(backgroundColor);
        g.fillRect(0 + xShift, 0 + yShift, (int) WORLD_WIDTH, (int) WORLD_HEIGHT);

        //********** Do drawings HERE **********
        //COULD DRAW ALL TILES IN ONE LOOP
        drawActiveTiles(g);
        drawTiles(g);
//        drawWalls(g);

        g.setColor(Color.BLUE);
        g.fillRect(pixel.x, pixel.y, 1 * GamePanel.scale, 1 * GamePanel.scale);
//        int width = 8 * GamePanel.scale;
//        int height = 8 * GamePanel.scale;
//        g.drawRect((int)(playerPos.x * Tile.TILE_WIDTH) - width/2, 
//                (int)(playerPos.y * Tile.TILE_HEIGHT) - height/2, width, height);
//        g.drawRect((int) (pacOffset.x * Tile.TILE_WIDTH) - width / 2,
//                (int) (pacOffset.y * Tile.TILE_HEIGHT) - height / 2, width, height);
//        drawHashGrid(g);
    }

    private void drawHashGrid(Graphics2D g) {
        g.setColor(Color.YELLOW);
        int cell = (int) grid.getCellSize();
//        cell += (int) WORLD_POS.x;
        //draw vertical
        for (int i = cell; i < WORLD_WIDTH; i += cell) {
//            g.drawLine(i, 0, i, (int) WORLD_HEIGHT);
            g.drawLine(i + xShift, 0 + yShift, i + xShift, (int) WORLD_HEIGHT + yShift);
        }
//        //draw horizontal
        for (int i = cell; i < WORLD_HEIGHT; i += cell) {
            g.drawLine(0 + xShift, i + yShift, (int) WORLD_WIDTH + xShift, i + yShift);
        }
    }

    public void setBackgroundColor(int r, int g, int b, int a) {
        backgroundColor = new Color(r, g, b, a);
    }
}
