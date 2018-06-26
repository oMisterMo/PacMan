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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    public enum Dir {

        UP, DOWN, LEFT, RIGHT, EATEN, STOP;// NA

        private static final Dir[] DIRECTION = values();
        private static final int SIZE = DIRECTION.length;
        private static final Random RANDOM = new Random();

        public static Dir getRandomDir() {
            return Dir.DIRECTION[RANDOM.nextInt(SIZE - 2)];
        }
    }
    public Dir currentDir = Dir.LEFT;
    private Dir toTravel = Dir.LEFT;
    private List<Tile> neighbours;
    private Tile lookAt;

    private final Point pacmanTile;       //pacmans tile location
    private Astar aStar;
    private List<Tile> foundPath;

    //Given a tile position
    public Blinky(Tile[][] tiles, int x, int y, Point pacmanTile, Astar aStar,
            List<Point> allDots, List<Point> allEnergizers) {
        super(allDots, allEnergizers);
        this.tiles = tiles;
        //init
        blinkyTile = new Point();
        pixel = new Point();
        centerPoint = new Point();
        temp = new Point();
        //set
        setBlinkyPos(x, y);

        neighbours = new ArrayList<>();
        hold = new Point();
        this.pacmanTile = pacmanTile;
        this.aStar = aStar;

        color = new Color(255, 0, 0, 255);  //red (rgba)
    }

    public void setBlinkyPos(int x, int y) {
        blinkyTile.setLocation(x, y);
        pixel.setLocation(x * Tile.TILE_WIDTH + scaledNum(3),
                y * Tile.TILE_HEIGHT + scaledNum(4));
        centerPoint.setLocation(x * Tile.TILE_WIDTH + scaledNum(3),
                y * Tile.TILE_HEIGHT + scaledNum(4));
        currentDir = Dir.LEFT;
        tiles[blinkyTile.y][blinkyTile.x].id = Tile.BLINKY;

        currentDir = Dir.LEFT;
        toTravel = Dir.LEFT;
    }

    public void setPixel(int x, int y) {
        pixel.setLocation(x, y);
    }

    public int scaledNum(int i) {
        return GamePanel.scale * i;
    }

    public void blinkyInput() {
        if (Input.isKeyTyped(KeyEvent.VK_SPACE)) {
            System.out.println("blinky press space");
            clearCellPath();
        }

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

    private void updateTileInfo() {
        setTile(blinkyTile.x, blinkyTile.y, Tile.BLINKY);
        setTile(temp.x, temp.y, Tile.ACTIVE);
        setDotsBack(temp);  //Don't have to pass in temp as its just been set
        setEnergizersBack(temp);
    }

    public void moveUp() {
//        System.out.println("move tile UP");

        //Store current position
        temp.setLocation(blinkyTile.x, blinkyTile.y);
        blinkyTile.y -= 1;
        //Update the new tile id then set old tile empty
        updateTileInfo();
    }

    public void moveDown() {
//        System.out.println("move tile DOWN");
        temp.setLocation(blinkyTile.x, blinkyTile.y);
        blinkyTile.y += 1;
        updateTileInfo();
    }

    public void moveLeft() {
//        System.out.println("move tile LEFT");
        temp.setLocation(blinkyTile.x, blinkyTile.y);
        blinkyTile.x -= 1;
        updateTileInfo();
    }

    public void moveRight() {
//        System.out.println("move tile RIGHT");
        temp.setLocation(blinkyTile.x, blinkyTile.y);
        blinkyTile.x += 1;
        updateTileInfo();
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

    private void test2(Tile current) {
        Map<Dir, Tile> legalAdjacent = getAdjacent(current, currentDir);
        List<List<Tile>> bigTing = new ArrayList<>();
        for (Tile t : legalAdjacent.values()) {
            System.out.println(t);
        }
        //Search for shortest path on each adjacent tiles and add to big array
        for (Tile t : legalAdjacent.values()) {
            bigTing.add(searchShortest(t));
        }
        //Print the size of each time
        for (int i = 0; i < bigTing.size(); i++) {
            List<Tile> path = bigTing.get(i);
            System.out.println("size: " + path.size());
        }
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
                test2(current);

                //This is an intersection tile
                System.out.println("Reach TOP CENTER by wall");
                currentDir = Dir.STOP;
                System.out.println("currentDir: " + currentDir);

                //Get shortest distance to target tile and change directions
//                currentDir = Dir.getRandomDir();
//                test(current);
            }
        } else {
            //Otherwise let pacman continue till the end of the tile
            if (current.intersection && pixel.y == c.y) {
                test2(current);

                //Reached the center of an intersection TILE
                System.out.println("reached the center of an intersection TILE");
                currentDir = Dir.STOP;
                System.out.println("currentDir: " + currentDir);

                //Get shortest distance to target tile and change directions
//                currentDir = Dir.getRandomDir();
//                System.out.println("yes");
//                test(current);
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
                test2(current);

                System.out.println("Reached BOTTOM CENTER by wall");
                currentDir = Dir.STOP;
                System.out.println("currentDir: " + currentDir);

                //Get shortest distance to target tile and change directions
            }
        } else {
            //Otherwise let pacman continue till the end of the tile
            if (current.intersection && pixel.y == c.y) {
                test2(current);

                //reached the center of an intersection TILE
                System.out.println("reached the center of an intersection TILE");
                currentDir = Dir.STOP;
                System.out.println("currentDir: " + currentDir);

                //Get shortest distance to target tile and change directions
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
                test2(current);

                System.out.println("Reached LEFT CENTER by wall");
                currentDir = Dir.STOP;
                System.out.println("currentDir: " + currentDir);

                //Get shortest distance to target tile and change directions
            }
        } else {
            if (current.intersection && pixel.x == c.x) {
                test2(current);

                System.out.println("reached the center of an intersection TILE");
                currentDir = Dir.STOP;
                System.out.println("currentDir: " + currentDir);

                //Get shortest distance to target tile and change directions
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
                test2(current);

                System.out.println("Reached RIGHT CENTER by wall");
                currentDir = Dir.STOP;
                System.out.println("currentDir: " + currentDir);

                //Get shortest distance to target tile and change directions
//                searchShortest(current);
            }
        } else {
            if (current.intersection && pixel.x == c.x) {
                test2(current);

                System.out.println("reached the center of an intersection TILE");
                currentDir = Dir.STOP;
                System.out.println("currentDir: " + currentDir);

                //Get shortest distance to target tile and change directions
//                searchShortest(current);
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
//                currentDir = Dir.STOP;
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
            //Look ahead for next Dir
            hold.setLocation(blinkyTile.x - 1, blinkyTile.y);
            lookAt = getTile(hold.x, hold.y);

            if (getDown(hold.x, hold.y).legal) {
                System.out.println("moving DOWN on next intersection");
                toTravel = Dir.DOWN;
            }
            if (getLeft(hold.x, hold.y).legal) {
                System.out.println("moving LEFT on next intersection");
                toTravel = Dir.LEFT;
            }
            if (getUp(hold.x, hold.y).legal) {
                System.out.println("moving UP on next intersection");
                toTravel = Dir.UP;
            }
            System.out.println("chosen direction = " + toTravel);
        }
    }

    private void updateAdjacentTiles(Tile t) {
        //Clear previous tiles
        neighbours.clear();
        //Add 3 -> LEFT, UP, DOWN (if possible tiles)
        Tile up = getUp(temp.x, temp.y);
        if (up.legal) {
            neighbours.add(up);
            System.out.println("ADDING UP");

        }
        Tile down = getDown(temp.x, temp.y);
        if (down.legal) {
            neighbours.add(down);
            System.out.println("ADDING DOWN");

        }
        Tile left = getLeft(temp.x, temp.y);
        if (left.legal) {
            neighbours.add(left);
            System.out.println("ADDING LEFT");
        }
//        Tile right = getRight(temp.x, temp.y);
//        if(right.legal){
//            neighbours.add(right);
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

    public void setCurrentDir(Dir d) {
        this.currentDir = d;
    }

    private void setCellPath(List<Tile> path) {
        if (path != null) {
            foundPath = path;   //can remove this line
            int len = path.size();
            for (int i = 0; i < len; i++) {
                Tile c = path.get(i);
                tiles[c.grid.y][c.grid.x].id = Tile.PATH;
            }
        }
    }

    private void clearCellPath() {
//        if (foundPath != null) {
//            int len = foundPath.size();
//            for (int i = 0; i < len; i++) {
//                Tile c = foundPath.get(i);
//                tiles[c.grid.y][c.grid.x].id = Tile.ACTIVE;
//            }
//        }
        for (int y = 0; y < World.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < World.NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
                if (t.id == Tile.PATH) {
                    t.id = Tile.ACTIVE;
                }
            }
        }
    }

    private List<Tile> searchShortest(Tile start) {
//        System.out.println("Calling searchShortest()");
        List<Tile> path = aStar.aStarSearch(start,
                tiles[pacmanTile.y][pacmanTile.x]);
        setCellPath(path);
//        System.out.println("end searchShortest...");
        return path;
    }

    public boolean isWithinWorld(int x, int y) {
        return (y < World.NO_OF_TILES_Y && y >= 0 && x < World.NO_OF_TILES_X && x >= 0);
    }

    /* Gets legal adjacent tiles */     //Why pass in current???
    public Map<Dir, Tile> getAdjacent(Tile tile, Dir current) {
        //To break the tie, the ghost prefers directions in this order: up, left, down, right.
        //Was using HASHSET but order of insertion was not preserved.
        Map<Dir, Tile> adjacent = new LinkedHashMap<>();
        //Up
        if (isWithinWorld(tile.grid.x, tile.grid.y - 1)) {
            Tile up = tiles[tile.grid.y - 1][tile.grid.x];
            if (up.legal && current != Dir.DOWN) {
                adjacent.put(Dir.UP, up);
            }
        } else {
            System.out.println("Cant get tile above, Y < 0");
        }
        //Left
        if (isWithinWorld(tile.grid.x - 1, tile.grid.y)) {
            Tile left = tiles[tile.grid.y][tile.grid.x - 1];
            if (left.legal && current != Dir.RIGHT) {
                adjacent.put(Dir.LEFT, left);
            }
        } else {
            System.out.println("Cant get tile left, X < 0");
        }
        //Down
        if (isWithinWorld(tile.grid.x, tile.grid.y + 1)) {
            Tile down = tiles[tile.grid.y + 1][tile.grid.x];
            if (down.legal && current != Dir.UP) {
                adjacent.put(Dir.DOWN, down);
            }
        } else {
            System.out.println("Cant get tile below, Y > NO_OF_TILES_Y");
        }
        //Right
        if (isWithinWorld(tile.grid.x + 1, tile.grid.y)) {
            Tile right = tiles[tile.grid.y][tile.grid.x + 1];
            if (right.legal && current != Dir.LEFT) {
                adjacent.put(Dir.RIGHT, right);
            }
        } else {
            System.out.println("Cant get tile right, X > NO_OF_TILES_X");
        }
        return adjacent;
    }

    public Map<Dir, Tile> getAdjacent2(Tile tile, Dir current) {
        //To break the tie, the ghost prefers directions in this order: up, left, down, right.
        //Was using HASHSET but order of insertion was not preserved.
        Map<Dir, Tile> adjacent = new LinkedHashMap<>();

        //Right
        if (isWithinWorld(tile.grid.x + 1, tile.grid.y)) {
            Tile right = tiles[tile.grid.y][tile.grid.x + 1];
            if (right.legal && current != Dir.LEFT) {
                adjacent.put(Dir.RIGHT, right);
            }
        } else {
            System.out.println("Cant get tile right, X > NO_OF_TILES_X");
        }
        //Left
        if (isWithinWorld(tile.grid.x - 1, tile.grid.y)) {
            Tile left = tiles[tile.grid.y][tile.grid.x - 1];
            if (left.legal && current != Dir.RIGHT) {
                adjacent.put(Dir.LEFT, left);
            }
        } else {
            System.out.println("Cant get tile left, X < 0");
        }
        //Down
        if (isWithinWorld(tile.grid.x, tile.grid.y + 1)) {
            Tile down = tiles[tile.grid.y + 1][tile.grid.x];
            if (down.legal && current != Dir.UP) {
                adjacent.put(Dir.DOWN, down);
            }
        } else {
            System.out.println("Cant get tile below, Y > NO_OF_TILES_Y");
        }

        //Up
        if (isWithinWorld(tile.grid.x, tile.grid.y - 1)) {
            Tile up = tiles[tile.grid.y - 1][tile.grid.x];
            if (up.legal && current != Dir.DOWN) {
                adjacent.put(Dir.UP, up);
            }
        } else {
            System.out.println("Cant get tile above, Y < 0");
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
                currentDir = toTravel;
            }
        } else {
            if (pixel.y == c.y && currentDir != toTravel && current.intersection) {
                currentDir = toTravel;
                return;
            }
            //Otherwise let pacman continue till the end of the tile
            pixel.y -= scaledNum(1);
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next.position != current.position) {
//            System.out.println("--------------------");
            moveUp();
            setNextMove(Dir.UP);
        }
    }

    public void movePixelRight2() {
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
                currentDir = toTravel;
            }
        } else {
            if (current.intersection && pixel.x == c.x && currentDir != toTravel) {
                currentDir = toTravel;
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
            setNextMove(Dir.RIGHT);
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
                currentDir = toTravel;
            }
        } else {
            //Otherwise let pacman continue till the end of the tile
            if (current.intersection && pixel.y == c.y && currentDir != toTravel) {
                //reached the center of an intersection TILE
                currentDir = toTravel;

            } else {
                pixel.y += scaledNum(1);
            }
        }
        Tile next = pixelToTile(pixel.x, pixel.y);
        if (next.position != current.position) {
            moveDown();
            setNextMove(Dir.DOWN);
        }
    }

    public void movePixelLeft2() {
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
                currentDir = toTravel;
            }
        } else {
            if (current.intersection && pixel.x == c.x && currentDir != toTravel) {
                currentDir = toTravel;
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
            setNextMove(Dir.LEFT);
        }
    }

    private void setNextMove(Dir dir) {
        Tile test = null;
        switch (dir) {
            case UP:
                test = getUp(blinkyTile.x, blinkyTile.y);
                break;
            case DOWN:
                test = getDown(blinkyTile.x, blinkyTile.y);
                break;
            case LEFT:
                test = getLeft(blinkyTile.x, blinkyTile.y);
                break;
            case RIGHT:
                test = getRight(blinkyTile.x, blinkyTile.y);
                break;
        }
        //Get adjacent tiles of next tile
        Map<Dir, Tile> adj = getAdjacent(test, currentDir);
        //If theres only a single adjacent tile -> carry on up 
        if (adj.size() == 1) {
            //If blinky reached a corner, set new direction else continue
            for (Map.Entry<Dir, Tile> e : adj.entrySet()) {
                Dir testDir = e.getKey();
//                Tile testTile = e.getValue();
                if (currentDir != testDir) {
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
            for (Map.Entry<Dir, Tile> e : adj.entrySet()) {
                Dir testDir = e.getKey();
                Tile testTile = e.getValue();
                if (shortest == -1) {
                    //If shortest is undefined, set it to the first value
                    shortest = getDistance(testTile, getTarget(state));
                    toTravel = testDir;
                    System.out.println(testDir + ": " + testTile + ": "
                            + shortest);
                    continue;
                }
                int distance = getDistance(testTile, getTarget(state));
                if (distance < shortest) {
                    shortest = distance;
                    toTravel = testDir;
                }

                System.out.println(testDir + ": " + testTile + ": "
                        + distance);
            }
            System.out.println("Shortest path: " + toTravel);
            System.out.println("----------------------------------------");
        }
    }

//    private int getDistance(Tile nodaA, Tile goal) {
//        int dx = Math.abs(nodaA.grid.x - goal.grid.x);
//        int dy = Math.abs(nodaA.grid.y - goal.grid.y);
//        //D * (dx + dy)
////        int num = Math.sqrt()
//        int a = (int) (nodaA.bounds.lowerLeft.x + nodaA.bounds.width / 2);
//        int b = (int) (goal.bounds.lowerLeft.x + goal.bounds.width / 2);
//        int distance = (int) Math.sqrt(a * a + b * b);
////        System.out.println("distance: " + distance);
////        return 10 * (dy + dx);
//        return distance;
//    }
    private int getDistance(Tile nodaA, Tile goal) {
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

    private Tile getTarget(int mode) {
        switch (mode) {
//            case Enemy.STATE_CHASE:
//                return tiles[pacmanTile.y][pacmanTile.x];
            case Enemy.STATE_SCATTER:
                return tiles[blinkyScatter.y][blinkyScatter.x];
        }
        return tiles[pacmanTile.y][pacmanTile.x];
    }

    private void switchDir() {
        switch (currentDir) {
            case UP:
                currentDir = Dir.DOWN;
//                toTravel = Dir.DOWN;
                setNextMove(Dir.DOWN);
                break;
            case DOWN:
                currentDir = Dir.UP;
//                toTravel = Dir.UP;
                setNextMove(Dir.UP);
                break;
            case LEFT:
                currentDir = Dir.RIGHT;
//                toTravel = Dir.RIGHT;
                setNextMove(Dir.RIGHT);
                break;
            case RIGHT:
                currentDir = Dir.LEFT;
//                toTravel = Dir.LEFT;
                setNextMove(Dir.LEFT);
                break;

        }
    }

    public void update(float deltaTime) {
//        System.out.println("blinky");
//        movePixelLeft();
//        movePixelRight();
//        System.out.println("target: "+target);
        switch (currentDir) {
            case UP:
//                movePixelUp();
                movePixelUp2();
                break;
            case DOWN:
//                movePixelDown();
                movePixelDown2();
                break;
            case LEFT:
//                movePixelLeft();
                movePixelLeft2();
                break;
            case RIGHT:
//                movePixelRight();
                movePixelRight2();
                break;
        }
        Assets.blinky.update(deltaTime);
    }
}
