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

import common.AnimationA;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * 20-Feb-2018, 22:16:38.
 *
 * @author Mohammed Ibrahim
 */
public class World extends GameObject {

    //Size of the original pacman arcade world
    public static final float WORLD_WIDTH = 224 * GamePanel.scale;
    public static final float WORLD_HEIGHT = 288 * GamePanel.scale;

    public static final int NO_OF_TILES_X = (int) (WORLD_WIDTH / Tile.TILE_WIDTH);      //28
    public static final int NO_OF_TILES_Y = (int) (WORLD_HEIGHT / Tile.TILE_HEIGHT);    //36

    public static final int WORLD_STATE_READY = 0;
    public static final int WORLD_STATE_RUNNING = 1;
    public static final int WORLD_STATE_WAIT = 2;
    public static final int WORLD_STATE_DEAD = 3;
    public static final int WORLD_STATE_GAME_OVER = 4;
    public int state;

    public Tile[][] tiles;

    //removed pacman stuff here
    private Pacman pacman;
    private Enemy blinky, pinky, inky, clyde;
    public float stateTime = 0;
    private AffineTransform trans;

//    private final Vector2D touchPos = new Vector2D();
    private Color backgroundColor = Color.BLACK;
    private SpatialHashGrid grid;
    private float scaleTime = 1f;
    private float elapsedTime;

    //Misc
    public static final float TIME_TO_MOVE = 0.00f;
    private int numFood;
    private int numEnergizers;
    private String totalScore;
    private int scoreX, scoreY;
    private StringBuilder sb;
    private Formatter formatter;

    private List<Point> allDots;
    private List<Point> allEnergizers;
    //TEST--------------------------------------
    private Astar aStar;
//    private Enemy testGhost;

    public static final float DEATH_WAIT = 1;   //in seconds

    public World() {
        init();
        state = WORLD_STATE_READY;
//        state = WORLD_STATE_RUNNING;
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
        nullTiles();
        initTiles();
        setEmpty();

        //PACMAN STUFF----------------
//        rotation = 0;
//        pacmanTile = new Point();
//        pixel = new Point();
//        centerPoint = new Point();
//        System.out.println("ghostDir = " + pacmanDir);
//        trans = new AffineTransform();
//        stateTime = 0;
        stateTime = 0;
        trans = new AffineTransform();
        //end pacman stuff---------------------------

        //Score
        sb = new StringBuilder();
        formatter = new Formatter(sb);
        formatter.format("score %04d", 0);
        System.out.println("sb.toString(): " + sb.toString());
        allDots = new ArrayList<>();
        allEnergizers = new ArrayList<>();
        numFood = 0;
        numEnergizers = 0;
        totalScore = "";
        scoreX = (int) (GamePanel.GAME_WIDTH / 2 - 10);
        scoreY = (int) Tile.TILE_HEIGHT * 2;

        //Time gone by until another mover can be made
        elapsedTime = 0;

        pacman = new Pacman(tiles, allDots, allEnergizers);
        pacman.setPacmanPos(14, 26);    //debug
        blinky = new Blinky(Tile.BLINKY, tiles, pacman, allDots, allEnergizers, 14, 14);
        blinky.setInHome(false);
        pinky = new Pinky(Tile.PINKY, tiles, pacman, allDots, allEnergizers, 14, 17);
        pinky.setGhostHomeInterval(0);
        pinky.setGhostPos(14, 17, 0, 4, Tile.PINKY);
        inky = new Inky(Tile.INKY, tiles, pacman, allDots, allEnergizers, 12, 17, blinky);
        inky.setGhostHomeInterval(10);
        inky.setGhostPos(12, 17, 0, 4, Tile.INKY);
        clyde = new Clyde(Tile.CLYDE, tiles, pacman, allDots, allEnergizers, 16, 17);
        clyde.setGhostPos(16, 17, 0, 4, Tile.CLYDE);
        clyde.setGhostHomeInterval(30);

        loadLevel();
        loadWalls();
        loadIntersections();
        initGrid();
        //-----------------------------Random test
        aStar = new Astar(this);
        //Set telport tiles
        tiles[17][0].teleportTile = true;
        tiles[17][NO_OF_TILES_X - 1].teleportTile = true;
        //------------------------------------------
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
     * Called from the constructor, sets the position of all tiles.
     */
    private void initTiles() {
        System.out.println("Initializing tiles...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x] = new Tile(x * Tile.TILE_WIDTH, y * Tile.TILE_HEIGHT,
                        Tile.TILE_WIDTH, Tile.TILE_HEIGHT, new Point(x, y));
            }
        }
    }

    /**
     * Sets all tiles to empty.
     */
    private void setEmpty() {
        System.out.println("Setting the id of all tiles to EMTPY");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x].id = Tile.EMPTY;
            }
        }
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
                    t.id = Tile.DOT;
                    t.legal = true;
                    numFood++;
                    allDots.add(new Point(x, y));
                    continue;
                }
                //Power up (green tile)
                if (r == 0 && g == 255 && b == 0) {
                    t.id = Tile.ENERGIZER;
                    t.legal = true;
                    numEnergizers++;
                    allEnergizers.add(new Point(x, y));
                    continue;
                }
                //Player (yellow tile)
                if (r == 255 && g == 255 && b == 0) {
                    t.id = Tile.PLAYER;
                    t.legal = true;

                    System.out.println("player position: " + x + ", " + y);
                    pacman.setPacmanPos(x, y);
//                    setPacmanPos(x, y);
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

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = level.getRGB(x, y);
                //Get color of pixel in testLevel array
                int a = ((pixel & 0xff000000) >>> 24);
                int r = ((pixel & 0x00ff0000) >>> 16);
                int g = ((pixel & 0x0000ff00) >>> 8);
                int b = ((pixel & 0x000000ff));
                Tile t = tiles[y][x];

                //Found intersection tile
                if (r == 0 && g == 22 && b == 156) {
                    t.intersection = true;
                    continue;
                }
            }
        }
    }

    public void handleKeyEvents() {
        pacman.pacmanInput();
        blinky.ghostInput();
        pinky.ghostInput();
        inky.ghostInput();
        clyde.ghostInput();
    }

    public boolean isWithinWorld(int x, int y) {
        return (y < NO_OF_TILES_Y && y >= 0 && x < NO_OF_TILES_X && x >= 0);
    }

    public List<Tile> getAdjacentLegal(Tile tile) {
        List<Tile> adjacent = new ArrayList<>();
        //Up
        if (isWithinWorld(tile.grid.x, tile.grid.y - 1)) {
//            System.out.println("UP tile: " + tile.grid.x + " " + (tile.grid.y - 1));
            Tile up = tiles[tile.grid.y - 1][tile.grid.x];
            if (up.legal) {
                adjacent.add(up);
            }
        } else {
            System.out.println("Cant get tile above, Y < 0");
        }
        //Left
        if (isWithinWorld(tile.grid.x - 1, tile.grid.y)) {
//            System.out.println("LEFT tile: " + (tile.grid.x - 1) + " " + (tile.grid.y));
            Tile left = tiles[tile.grid.y][tile.grid.x - 1];
            if (left.legal) {
                adjacent.add(left);
            }
        } else {
            System.out.println("Cant get tile above, X < 0");
        }
        //Down
        if (isWithinWorld(tile.grid.x, tile.grid.y + 1)) {
//            System.out.println("DOWN tile: " + tile.grid.x + " " + (tile.grid.y + 1));
            Tile down = tiles[tile.grid.y + 1][tile.grid.x];
            if (down.legal) {
                adjacent.add(down);
            }
        } else {
            System.out.println("Cant get tile below, Y > NO_OF_TILES_Y");
        }
        //Right
        if (isWithinWorld(tile.grid.x + 1, tile.grid.y)) {
//            System.out.println("RIGHT tile: " + (tile.grid.x + 1) + " " + (tile.grid.y));
            Tile right = tiles[tile.grid.y][tile.grid.x + 1];
            if (right.legal) {
                adjacent.add(right);
            }
        } else {
            System.out.println("Cant get tile above, X > NO_OF_TILES_X");
        }
        return adjacent;
    }

    private void reset() {
        System.out.println("reset");
        state = WORLD_STATE_READY;
        stateTime = 0;
        
        pacman.setPacmanPos(13, 26);
        blinky.setGhostPos(14, 14, Tile.BLINKY);
        pinky.setGhostPos(6, 23, Tile.PINKY);
    }

    private int scaledNum(int i) {
        return GamePanel.scale * i;
    }

    public void setBackgroundColor(int r, int g, int b, int a) {
        backgroundColor = new Color(r, g, b, a);
    }

    private void movePacman(float deltaTime) {
        pacman.update(deltaTime);
    }

    private void moveGhosts(float deltaTime) {
        blinky.update(deltaTime);
        pinky.update(deltaTime);
        inky.update(deltaTime);
        clyde.update(deltaTime);
    }

    private void drawPacman(Graphics2D g) {
        //Set rotation based on direction heading
        AffineTransform old = g.getTransform();
        trans.setToIdentity();  //AffineTransform trans = new AffineTransform();
        float centerX = (pacman.pixel.x);
        float centerY = (pacman.pixel.y);
        trans.translate(centerX, centerY);
        trans.rotate(Math.toRadians(pacman.rotation));
        trans.translate(-centerX, -centerY);
        g.setTransform(trans);

        g.drawImage(Assets.pacman.getImage(),
                pacman.pixel.x - Pacman.PACMAN_WIDTH / 2,
                pacman.pixel.y - Pacman.PACMAN_HEIGHT / 2,
                Pacman.PACMAN_WIDTH, Pacman.PACMAN_HEIGHT, null);
//        g.setColor(Color.BLUE);
//        g.drawRect((int) position.x, (int) position.y,
//                (int) PLAYER_WIDTH, (int) PLAYER_HEIGHT);

        g.setTransform(old);
    }

    private void drawGhosts(Graphics2D g) {
        //Draw Blinky
        g.drawImage(Assets.blinky.getImage(),
                blinky.pixel.x - Blinky.GHOST_WIDTH / 2,
                blinky.pixel.y - Blinky.GHOST_HEIGHT / 2,
                Enemy.GHOST_WIDTH, Enemy.GHOST_HEIGHT, null);
        //Draw Pinky
        g.drawImage(Assets.pinky.getImage(),
                pinky.pixel.x - Blinky.GHOST_WIDTH / 2,
                pinky.pixel.y - Blinky.GHOST_HEIGHT / 2,
                Enemy.GHOST_WIDTH, Enemy.GHOST_HEIGHT, null);
        //Draw Inky
        g.drawImage(Assets.inky.getImage(),
                inky.pixel.x - Blinky.GHOST_WIDTH / 2,
                inky.pixel.y - Blinky.GHOST_HEIGHT / 2,
                Enemy.GHOST_WIDTH, Enemy.GHOST_HEIGHT, null);
//        //Draw Clyde
        g.drawImage(Assets.clyde.getImage(),
                clyde.pixel.x - Blinky.GHOST_WIDTH / 2,
                clyde.pixel.y - Blinky.GHOST_HEIGHT / 2,
                Enemy.GHOST_WIDTH, Enemy.GHOST_HEIGHT, null);

        /*
        //Draw Blinky target tile
        g.setColor(blinky.color);
        Tile t = tiles[blinky.blinkyScatter.y][blinky.blinkyScatter.x];
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                (int) t.bounds.width, (int) t.bounds.height);
        //Draw Pinky target tile
        g.setColor(pinky.color);
        t = tiles[pinky.pinkyScatter.y][pinky.pinkyScatter.x];
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                (int) t.bounds.width, (int) t.bounds.height);
        g.setColor(inky.color);
        t = tiles[inky.inkyScatter.y][inky.inkyScatter.x];
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                (int) t.bounds.width, (int) t.bounds.height);
        //Draw Pinky target tile
        g.setColor(clyde.color);
        t = tiles[clyde.clydeScatter.y][clyde.clydeScatter.x];
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                (int) t.bounds.width, (int) t.bounds.height);*/
    }

    private void drawActiveTiles(Graphics2D g) {
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
//                int tileId = t.id;
//                switch (tileId) {
//                    case Tile.ACTIVE:
//                    case Tile.DOT:
//                    case Tile.ENERGIZER:
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
                    case Tile.DOT:
                        g.setColor(Color.DARK_GRAY);
                        size = 4;
                        g.fillRect((int) (t.position.x + Tile.TILE_WIDTH / 2f - size / 2 + 1),
                                (int) (t.position.y + Tile.TILE_HEIGHT / 2f - size / 2 + 1),
                                (int) size,
                                (int) size);
                        break;
                    case Tile.ENERGIZER:
                        size = 16;
                        g.setColor(Color.GREEN);
                        g.fillOval((int) (t.position.x + Tile.TILE_WIDTH / 2f - size / 2 + 1),
                                (int) (t.position.y + Tile.TILE_HEIGHT / 2f - size / 2 + 1),
                                (int) size, (int) size);
                        break;
//                    case Tile.PLAYER:
////                        Tile p = tiles[currentPos.y][currentPos.x];
////                        //Jumps from tile to tile
//                        g.setColor(pacman.color);
//                        g.fillRect((int) t.position.x, (int) t.position.y,
//                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//                        break;
//                    case Tile.BLINKY:
//                        g.setColor(blinky.color);
//                        g.fillRect((int) t.position.x, (int) t.position.y,
//                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//                        break;
//                    case Tile.PINKY:
//                        g.setColor(pinky.color);
//                        g.fillRect((int) t.position.x, (int) t.position.y,
//                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//                        break;
//                    case Tile.INKY:
//                        g.setColor(inky.color);
//                        g.fillRect((int) t.position.x, (int) t.position.y,
//                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//                        break;
//                    case Tile.CLYDE:
//                        g.setColor(clyde.color);
//                        g.fillRect((int) t.position.x, (int) t.position.y,
//                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//                        break;
                    case Tile.PATH:
                        g.setColor(Color.GRAY);
                        g.fillRect((int) t.position.x, (int) t.position.y,
                                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
                        break;
                }
            }
        }
    }

    private void drawSquares(Graphics2D g) {
        Tile t = tiles[pacman.pacmanTile.y][pacman.pacmanTile.x];
        g.setColor(pacman.color);
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
        t = tiles[blinky.ghostTile.y][blinky.ghostTile.x];
        g.setColor(blinky.color);
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);

        t = tiles[pinky.ghostTile.y][pinky.ghostTile.x];
        g.setColor(pinky.color);
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);

        t = tiles[inky.ghostTile.y][inky.ghostTile.x];
        g.setColor(inky.color);
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);

        t = tiles[clyde.ghostTile.y][clyde.ghostTile.x];
        g.setColor(clyde.color);
        g.fillRect((int) t.bounds.topLeft.x, (int) t.bounds.topLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);

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

    private void drawGrid(Graphics2D g) {
        g.setColor(Color.WHITE);
        Tile tile = tiles[4][1];
        for (int i = 0; i < 8; i++) {
            //draw horizontal line
            g.drawLine((int) tile.bounds.topLeft.x,
                    (int) tile.bounds.topLeft.y + (i * Tile.TILE_HEIGHT / 8),
                    (int) WORLD_WIDTH,
                    (int) tile.bounds.topLeft.y + (i * Tile.TILE_HEIGHT / 8));

            //draw vertical lines
            g.drawLine((int) tile.bounds.topLeft.x + (i * Tile.TILE_WIDTH / 8), //x1
                    (int) tile.bounds.topLeft.y, //y1
                    (int) tile.bounds.topLeft.x + (i * Tile.TILE_WIDTH / 8), //x2
                    (int) World.WORLD_HEIGHT);    //y2
        }
    }

    private void drawScore(Graphics2D g) {
        //Draw Score
        g.setColor(Color.WHITE);
        totalScore = String.format("SCORE %04d", pacman.score);
        g.drawString(totalScore, scoreX, scoreY);
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

    private void updateRunning(float deltaTime) {
        deltaTime *= scaleTime; //Objects that are slow mo after this line

        /*slow down pacman*/
        elapsedTime += deltaTime;   //in seconds
        if (elapsedTime >= TIME_TO_MOVE) {
//            System.out.println("move");
            movePacman(deltaTime);
            moveGhosts(deltaTime);
            elapsedTime = 0;
        }
    }

    private void drawRunning(Graphics2D g) {
        //Draw Pac-Man
        drawPacman(g);
        drawGhosts(g);

//        //Debug draw blinky as a pixel
//        g.setColor(Color.BLUE);
//        g.fillRect(blinky.pixel.x, blinky.pixel.y, scaledNum(1), scaledNum(1));
        
        //Draw grid
//        drawGrid(g);
//        drawHashGrid(g);
    }

    @Override
    void gameUpdate(float deltaTime) {
        //********** Do updates HERE **********
        switch (state) {
            case WORLD_STATE_READY:
                stateTime += deltaTime;
                if (Input.isKeyPressed(KeyEvent.VK_R)) {
                    //R for ready
                    System.out.println("R for READY Pressed");
                    state = WORLD_STATE_RUNNING;
                    stateTime = 0;
                }
                break;
            case WORLD_STATE_RUNNING:
                stateTime += deltaTime;
                updateRunning(deltaTime);
                break;
            case WORLD_STATE_WAIT:
                stateTime += deltaTime;
                if (stateTime >= DEATH_WAIT) {
                    state = WORLD_STATE_DEAD;
                    stateTime = 0;
                }
                break;
            case WORLD_STATE_DEAD:
                stateTime += deltaTime;
                //Play death animation
                if (stateTime > 2f) {
                    //Restart after animation
                    reset();
                    stateTime = 0;
                }
                break;
            case WORLD_STATE_GAME_OVER:
                break;
        }
    }

    @Override
    void gameRender(Graphics2D g) {
        //Clear screen
        g.setColor(backgroundColor);
        g.fillRect(0, 0, (int) WORLD_WIDTH, (int) WORLD_HEIGHT);

        //Draw  static tiles
//        drawActiveTiles(g);
        drawTiles(g);
//        drawSquares(g);
        drawWalls(g);
        drawScore(g);

        switch (state) {
            case WORLD_STATE_READY:
                g.setColor(Color.YELLOW);
                g.drawString("READY!", WORLD_WIDTH / 2 - 50, WORLD_HEIGHT / 2 + 50);
            case WORLD_STATE_RUNNING:
            case WORLD_STATE_WAIT:
                drawRunning(g);
                break;
            case WORLD_STATE_DEAD:
//                System.out.println("stateTime: "+stateTime);
                BufferedImage pac = Assets.pacmanDeath.getKeyFrame(stateTime,
                        AnimationA.ANIMATION_NON_LOOPING);
                g.drawImage(pac,
                        pacman.pixel.x - Pacman.PACMAN_WIDTH / 2,
                        pacman.pixel.y - Pacman.PACMAN_HEIGHT / 2,
                        Pacman.PACMAN_WIDTH, Pacman.PACMAN_HEIGHT, null);
                break;
            case WORLD_STATE_GAME_OVER:
                //Not reached at the moment
                break;
        }
    }
}
