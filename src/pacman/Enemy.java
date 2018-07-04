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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static pacman.World.WORLD_WIDTH;

/**
 * 13-Apr-2018, 00:11:54.
 *
 * @author Mo
 */
public class Enemy {

    public final static int STATE_CHASE = 0;
    public final static int STATE_SCATTER = 1;
    public final static int STATE_EVADE = 2;
    public static int state = STATE_SCATTER;

    protected final List<Point> allDots;
    protected final List<Point> allEnergizers;
    public float enemyStateTime;

    //Blinky target tile: Tile t = tiles[0][World.NO_OF_TILES_X-6];
    public static final Point blinkyScatter = new Point(World.NO_OF_TILES_X - 3, 0);
    public static final Point pinkyScatter = new Point(2, 0);
    public static final Point inkyScatter = new Point(World.NO_OF_TILES_X - 1, World.NO_OF_TILES_Y - 1);
    public static final Point clydeScatter = new Point(0, World.NO_OF_TILES_Y - 1);

    //-------------------------FROM BLINKY----------------------------------
    public static final int GHOST_WIDTH = 16 * GamePanel.scale;
    public static final int GHOST_HEIGHT = 16 * GamePanel.scale;

    protected final Tile[][] tiles;
    private Point centerPoint;  //of a tile

    public enum Direction {

        UP, DOWN, LEFT, RIGHT;//, STOP;// NA

        private static final Direction[] DIRECTION = values();
        private static final int SIZE = DIRECTION.length;
        private static final Random RANDOM = new Random();

        public static Direction getRandomDir() {
            return Direction.DIRECTION[RANDOM.nextInt(SIZE - 1)];// r = 0-3
        }
    }
    //GHOST
    private int id = -1;
    public Point ghostTile;
    public Point pixel;
    public Direction ghostDir = Direction.LEFT;
    private Direction toTravel = Direction.LEFT;
    public Point temp;                  //stores the pos of a pellet/energizer

    protected final Pacman pacman;      //Pacmans reference -> to get target
    public Color color;
    //------------------------------------------------------------------------

    public Enemy(Tile[][] tiles, Pacman pacman, List<Point> allDots, List<Point> allEnergizers,
            int x, int y, int id) {
        this.allDots = allDots;
        this.allEnergizers = allEnergizers;
        enemyStateTime = 0;

        this.tiles = tiles;
        this.id = id;
        ghostTile = new Point();
        pixel = new Point();
        centerPoint = new Point();
        temp = new Point();
        //set
        setGhostPos(x, y, id);

        this.pacman = pacman;
        color = new Color(255, 255, 255, 255);  //default color is white
        System.out.println("enemy constructor finsihed...");
    }

    public void setGhostPos(int x, int y, int id) {
        ghostTile.setLocation(x, y);
        pixel.setLocation(x * Tile.TILE_WIDTH + scaledNum(3),
                y * Tile.TILE_HEIGHT + scaledNum(4));
        centerPoint.setLocation(x * Tile.TILE_WIDTH + scaledNum(3),
                y * Tile.TILE_HEIGHT + scaledNum(4));
        ghostDir = Direction.LEFT;
        tiles[ghostTile.y][ghostTile.x].id = id;

        ghostDir = Direction.LEFT;
        toTravel = Direction.LEFT;
    }

    public void setPixel(int x, int y) {
        pixel.setLocation(x, y);
    }

    public int scaledNum(int i) {
        return GamePanel.scale * i;
    }

    public void ghostInput() {
        if (Input.isKeyTyped(KeyEvent.VK_T)) {
//            target = blinkyScatter;
//            pacmanTile.setLocation(World.NO_OF_TILES_X-6, 0);
            state = STATE_SCATTER;
            switchDir();
        }
        if (Input.isKeyTyped(KeyEvent.VK_Y)) {
//            target = pacmanTile;
            state = STATE_CHASE;
            switchDir();
        }
    }

    private void setDotsBack(Point p) {
        if (allDots.contains(p)) {
            setTile(p.x, p.y, Tile.DOT);
        }
    }

    private void setEnergizersBack(Point p) {
        if (allEnergizers.contains(p)) {
            setTile(p.x, p.y, Tile.ENERGIZER);
        }
    }

    /**
     * Given old location, updates the enemies tile position
     *
     * @param x
     * @param y
     */
    private void updateTileInfo(int x, int y) {
        temp.setLocation(x, y);

        setTile(ghostTile.x, ghostTile.y, id);
        setTile(x, y, Tile.ACTIVE);
        setDotsBack(temp);
        setEnergizersBack(temp);
    }

    public void moveUp() {
//        System.out.println("move tile UP");

        //Store current position
        int x, y;
        x = ghostTile.x;
        y = ghostTile.y;
        ghostTile.y -= 1;
        //Update the new tile id then set old tile empty
        updateTileInfo(x, y);
    }

    public void moveDown() {
//        System.out.println("move tile DOWN");
        int x, y;
        x = ghostTile.x;
        y = ghostTile.y;
        ghostTile.y += 1;
        updateTileInfo(x, y);
    }

    public void moveLeft() {
//        System.out.println("move tile LEFT");
        int x, y;
        x = ghostTile.x;
        y = ghostTile.y;
        ghostTile.x -= 1;
        updateTileInfo(x, y);
    }

    public void moveRight() {
//        System.out.println("move tile RIGHT");
        int x, y;
        x = ghostTile.x;
        y = ghostTile.y;
        ghostTile.x += 1;
        updateTileInfo(x, y);
    }

    private void setTile(int x, int y, int id) {
        tiles[y][x].id = id;
    }

    public Point getCenter(Tile t) {
        centerPoint.x = (int) t.bounds.lowerLeft.x + scaledNum(3);
        centerPoint.y = (int) t.bounds.lowerLeft.y + scaledNum(4);

//        centerPoint.x = (int) (t.bounds.lowerLeft.x / GamePanel.scale + 3);
//        centerPoint.y = (int) (t.bounds.lowerLeft.y / GamePanel.scale + 4);
        return centerPoint;
    }

    private boolean isWall(Tile t) {
        return t.id == Tile.WALL;
    }

    public Tile pixelToTile(int x, int y) {
        //Slightly optamized
        //-> int/int = int, without the decimal. So no need for Math.floor
        x /= Tile.TILE_WIDTH;
        y /= Tile.TILE_HEIGHT;
        x = capTileX(x);
        y = capTileY(y);
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

    public void setCurrentDir(Direction d) {
        this.ghostDir = d;
    }

    public boolean isTileWithinWorld(int x, int y) {
        return (y < World.NO_OF_TILES_Y && y >= 0 && x < World.NO_OF_TILES_X && x >= 0);
    }

    public int capTileX(int x) {
        if (x < 0) {
            return 0;
        }
        if (x > World.NO_OF_TILES_X - 1) {
            return World.NO_OF_TILES_X - 1;
        }
        return x;
    }

    public int capTileY(int y) {
        if (y < 0) {
            return 0;
        }
        if (y > World.NO_OF_TILES_Y - 1) {
            return World.NO_OF_TILES_Y - 1;
        }
        return y;
    }

    /* Gets legal adjacent tiles */     //Why pass in current???
    public Map<Direction, Tile> getAdjacent(Tile tile, Direction current) {
        //To break the tie, the ghost prefers directions in this order: up, left, down, right.
        //Was using HASHSET but order of insertion was not preserved.
        Map<Direction, Tile> adjacent = new LinkedHashMap<>();
        //Up
        if (isTileWithinWorld(tile.grid.x, tile.grid.y - 1)) {
            Tile up = tiles[tile.grid.y - 1][tile.grid.x];
            if (up.legal && current != Direction.DOWN) {
                adjacent.put(Direction.UP, up);
            }
        } else {
            System.out.println("Cant get tile above, Y < 0");
        }
        //Left
        if (isTileWithinWorld(tile.grid.x - 1, tile.grid.y)) {
            Tile left = tiles[tile.grid.y][tile.grid.x - 1];
            if (left.legal && current != Direction.RIGHT) {
                adjacent.put(Direction.LEFT, left);
            }
        } else {
            System.out.println("Cant get tile left, X < 0");
        }
        //Down
        if (isTileWithinWorld(tile.grid.x, tile.grid.y + 1)) {
            Tile down = tiles[tile.grid.y + 1][tile.grid.x];
            if (down.legal && current != Direction.UP) {
                adjacent.put(Direction.DOWN, down);
            }
        } else {
            System.out.println("Cant get tile below, Y > NO_OF_TILES_Y");
        }
        //Right
        if (isTileWithinWorld(tile.grid.x + 1, tile.grid.y)) {
            Tile right = tiles[tile.grid.y][tile.grid.x + 1];
            if (right.legal && current != Direction.LEFT) {
                adjacent.put(Direction.RIGHT, right);
            }
        } else {
            System.out.println("Cant get tile right, X > NO_OF_TILES_X");
        }
        return adjacent;
    }

    //Test handle new movemetn
    public void movePixelUp2() {
//        System.out.println("up...");
        Tile current = pixelToTile(pixel.x, pixel.y);
        Point c = getCenter(current);
        //If the TILE above pacman is a wall
        if (isWall(pixelToTileAbove(pixel.x, pixel.y))) {
            //Stop pacman at the mid point of the tile
            if (pixel.y > c.y) {
                pixel.y -= scaledNum(1);
            } else {
                ghostDir = toTravel;
            }
        } else {
            if (pixel.y == c.y && ghostDir != toTravel && current.intersection) {
                ghostDir = toTravel;
                return;
            }
            //Otherwise let pacman continue till the end of the tile
            pixel.y -= scaledNum(1);
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next.position != current.position) {
//            System.out.println("--------------------");
            moveUp();
            setNextMove(Direction.UP);
        }
    }

    public void movePixelRight2() {
//        System.out.println("right...");
        Tile current = pixelToTile(pixel.x, pixel.y);

        //-------------------Wrap Player------------------------
        if (pixel.x > WORLD_WIDTH - scaledNum(11)) {
            pixel.x = scaledNum(1);
            //update players tile
            ghostTile.x = pixel.x / Tile.TILE_WIDTH;
            ghostTile.y = pixel.y / Tile.TILE_HEIGHT;
            tiles[ghostTile.y][ghostTile.x].id = Tile.PLAYER;
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
                ghostDir = toTravel;
            }
        } else {
            if (current.intersection && pixel.x == c.x && ghostDir != toTravel) {
                ghostDir = toTravel;
            } else {
                pixel.x += scaledNum(1);
            }
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next.position != current.position) {
//            System.out.println("--------------------");
            moveRight();
            //Don't get direction if on special tile
            if (next == tiles[14][11] || next == tiles[14][14]) {
                return;
            }
            setNextMove(Direction.RIGHT);
        }
    }

    public void movePixelDown2() {
//        System.out.println("down...");
        Tile current = pixelToTile(pixel.x, pixel.y);

        Point c = getCenter(pixelToTile(pixel.x, pixel.y));
        if (isWall(pixelToTileBelow(pixel.x, pixel.y))) {
            if (pixel.y < c.y) {
                pixel.y += scaledNum(1);
            } else {
                ghostDir = toTravel;
            }
        } else {
            //Otherwise let pacman continue till the end of the tile
            if (current.intersection && pixel.y == c.y && ghostDir != toTravel) {
                //reached the center of an intersection TILE
                ghostDir = toTravel;

            } else {
                pixel.y += scaledNum(1);
            }
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next.position != current.position) {
            moveDown();
            setNextMove(Direction.DOWN);
        }
    }

    public void movePixelLeft2() {
//        System.out.println("left...");
        Tile current = pixelToTile(pixel.x, pixel.y);
        //-------------------Wrap Player------------------------
        if (pixel.x < scaledNum(9)) {
            pixel.x = (int) WORLD_WIDTH - scaledNum(1);
            //update players tile
            ghostTile.x = pixel.x / Tile.TILE_WIDTH;
            ghostTile.y = pixel.y / Tile.TILE_HEIGHT;
            tiles[ghostTile.y][ghostTile.x].id = Tile.PLAYER;
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
                ghostDir = toTravel;
            }
        } else {
            if (current.intersection && pixel.x == c.x && ghostDir != toTravel) {
                ghostDir = toTravel;
            } else {
                pixel.x -= scaledNum(1);
            }
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        //Just entered a new tile
        if (next.position != current.position) {
            moveLeft();
            if (next == tiles[14][13] || next == tiles[14][16]) {
                return;
            }
            setNextMove(Direction.LEFT);
        }
    }

    private void setNextMove(Direction dir) {
        Tile test = null;
        switch (dir) {
            case UP:
                test = getUp(ghostTile.x, ghostTile.y);
                break;
            case DOWN:
                test = getDown(ghostTile.x, ghostTile.y);
                break;
            case LEFT:
                test = getLeft(ghostTile.x, ghostTile.y);
                break;
            case RIGHT:
                test = getRight(ghostTile.x, ghostTile.y);
                break;
        }
        //Get adjacent tiles of next tile
        Map<Direction, Tile> adj = getAdjacent(test, ghostDir);
        //If theres only a single adjacent tile -> carry on up 
        if (adj.size() == 1) {
            //If blinky reached a corner, set new direction else continue
            for (Map.Entry<Direction, Tile> e : adj.entrySet()) {
                Direction testDir = e.getKey();
//                Tile testTile = e.getValue();
                if (ghostDir != testDir) {
                    toTravel = testDir;
                }
            }
        } else {
            /*
             -> There is a choice to be made next tiles
             -> Choose move for next tile -> based on shortest distance
             -> up, left, down, right.
             */
            int shortest = -1;  //shortest will always be > 10
            for (Map.Entry<Direction, Tile> e : adj.entrySet()) {
                Direction testDir = e.getKey();
                Tile testTile = e.getValue();
                if (shortest == -1) {
                    //If shortest is undefined, set it to the first value
                    shortest = getDistance(testTile, getTarget(state));
                    toTravel = testDir;
//                    System.out.println(testDir + ": " + testTile + ": "
//                            + shortest);
                    continue;
                }
                int distance = getDistance(testTile, getTarget(state));
                if (distance < shortest) {
                    shortest = distance;
                    toTravel = testDir;
                }
//                System.out.println(testDir + ": " + testTile + ": "
//                        + distance);
            }
//            System.out.println("Shortest path: " + toTravel);
//            System.out.println("----------------------------------------");
        }
    }

    public int getDistance(Tile nodaA, Tile goal) {
        int dx = Math.abs(nodaA.grid.x - goal.grid.x);
        int dy = Math.abs(nodaA.grid.y - goal.grid.y);
//        //Attemp 2
//        if (dx > dy) {
//            return 14 * dy + 10 * (dx - dy);
//        }
//        return 14 * dx + 10 * (dy - dx);

        //Attemp 3
        //D = horizontal cost, D2 = Diagonal cost
        //D * (dx + dy) + (D2 - 2 * D) * min(dx, dy) (same thing)
        return 10 * (dx + dy) + (14 - 2 * 10) * Math.min(dx, dy);
    }

    public Tile getTarget(int mode) {
        switch (mode) {
//            case Enemy.STATE_CHASE:
//                return tiles[pacmanTile.y][pacmanTile.x];
            case Enemy.STATE_SCATTER:
                return tiles[blinkyScatter.y][blinkyScatter.x];
        }
        return tiles[pacman.pacmanTile.y][pacman.pacmanTile.x];
    }

    private void switchDir() {
        switch (ghostDir) {
            case UP:
                ghostDir = Direction.DOWN;
//                toTravel = Direction.DOWN;
                setNextMove(Direction.DOWN);
                break;
            case DOWN:
                ghostDir = Direction.UP;
//                toTravel = Direction.UP;
                setNextMove(Direction.UP);
                break;
            case LEFT:
                ghostDir = Direction.RIGHT;
//                toTravel = Direction.RIGHT;
                setNextMove(Direction.RIGHT);
                break;
            case RIGHT:
                ghostDir = Direction.LEFT;
//                toTravel = Direction.LEFT;
                setNextMove(Direction.LEFT);
                break;

        }
    }

//    //Swtich to custom state
//    public void switchState(int state) {
//        Blinky.state = state;
//        switchDir();
//    }

    //Simple swap between two states
    public void switchState() {
        if (state == STATE_CHASE) {
            state = STATE_SCATTER;
        } else {
            state = STATE_CHASE;
        }
        switchDir();
    }

    //--------------------------------------------------------------------------
    public void update(float deltaTime) {
        switch (state) {
            case STATE_CHASE:
                enemyStateTime += deltaTime;
                break;
            case STATE_SCATTER:
                enemyStateTime += deltaTime;
                break;
            case STATE_EVADE:
                break;
        }

        //-----------------------------new below
        switch (ghostDir) {
            case UP:
                movePixelUp2();
                break;
            case DOWN:
                movePixelDown2();
                break;
            case LEFT:
                movePixelLeft2();
                break;
            case RIGHT:
                movePixelRight2();
                break;
        }
    }

    public void draw(Graphics2D g) {
        //Over ridden
    }

    public void restartEnemyStateTime() {
        enemyStateTime = 0;
    }
}
