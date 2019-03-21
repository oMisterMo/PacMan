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

import common.Vector2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * 29-Jun-2018, 19:17:00.
 *
 * @author Mohammed Ibrahim
 */
public class Inky extends Enemy {

    private final Enemy blinky;
    public Point twoTiles = new Point();
    Vector2D length = new Vector2D();
    Vector2D blinkyPixel = new Vector2D();
    Vector2D offsetPixel = new Vector2D();

    //--------------------------------------------ATTEMP 1
    private ArrayList<Vector2D> path;
    private Vector2D position;
    private float distanceTraveled;
    private float distanceBetweenCurrentPoints;
    private int currentPointIndex = 0;
    //--------------------------------------------ATTEMPT 2
    private Tile t1, t2, t3;
    private Point[] points;
    private int travel = 0;

    public Inky(int id, Tile[][] tiles, Pacman pacman, List<Point> allDots,
            List<Point> allEnergizers, int x, int y, Enemy blinky) {
        super(id, tiles, pacman, allDots, allEnergizers, x, y);

        this.blinky = blinky;
        color = new Color(0, 255, 255, 255);

        //-----------------------------------------------
        this.path = createPath();
        position = new Vector2D(path.get(currentPointIndex));
        System.out.println("QUICK TEST");

        System.out.println("x-> " + scaledNum(96) + " " + 12 * Tile.TILE_WIDTH);
        System.out.println("y-> " + scaledNum(139) + " " + ((17 * Tile.TILE_HEIGHT) + scaledNum(3)));
        distanceTraveled = 0;
        distanceBetweenCurrentPoints = distance(path.get(currentPointIndex),
                path.get(currentPointIndex + 1));
        System.out.println("distanceBetweenCurrentPoints: " + distanceBetweenCurrentPoints);
        //-----------------------------------------------
        t1 = tiles[17][12];
        t2 = tiles[17][14];
        t3 = tiles[14][14];
        points = new Point[3];
//        points[0] = new Point((int) t1.bounds.topLeft.x + scaledNum(3),
//                (int) t1.bounds.topLeft.y + scaledNum(4));
//        points[1] = new Point((int) t2.bounds.topLeft.x + scaledNum(3),
//                (int) t2.bounds.topLeft.y + scaledNum(4));
//        points[2] = new Point((int) t3.bounds.topLeft.x + scaledNum(3),
//                (int) t3.bounds.topLeft.y + scaledNum(4));
        points[0] = new Point((int) t1.bounds.topLeft.x,
                (int) t1.bounds.topLeft.y + scaledNum(4));
        points[1] = new Point((int) t2.bounds.topLeft.x,
                (int) t2.bounds.topLeft.y + scaledNum(4));
        points[2] = new Point((int) t3.bounds.topLeft.x,
                (int) t3.bounds.topLeft.y + scaledNum(4));

    }

    @Override
    public Tile getTarget(int mode) {
//        System.out.println("blinketTarget");
        switch (mode) {
            case Enemy.STATE_SCATTER:
                return tiles[inkyScatter.y][inkyScatter.x];
        }
        int x, y, offset;
        x = pacman.pacmanTile.x;
        y = pacman.pacmanTile.y;
//        System.out.println("pacman tile: "+pacman.pacmanTile);
        offset = 2;
        switch (pacman.recentDir) {
            case UP:
                y -= offset;
                y = capTileY(y);
                break;
            case DOWN:
                y += offset;
                y = capTileY(y);
                break;
            case LEFT:
                x -= offset;
                x = capTileX(x);
                break;
            case RIGHT:
                x += offset;
                x = capTileX(x);
                break;
        }
        /*
         ->imagine an intermediate offset two tiles away from Pac-Man's tile 
         in the direction Pac-Man is moving 
         ->then draw a line from Blinky's tile to that offset. 
         ->Now double the line length by extending the line out just as far again, 
         and you will have Inky's target tile
         */
        twoTiles.setLocation(x, y); //set offset tile (debug)
        //ATTEMPT 2
        int tileX, tileY;
        Point c = getCenter(tiles[y][x]);
        blinkyPixel.set(blinky.pixel.x, blinky.pixel.y);
        offsetPixel.set(c.x, c.y);
        length.set(offsetPixel.x - blinkyPixel.x, offsetPixel.y - blinkyPixel.y);
        length.mult(2);
        tileX = (int) (blinky.pixel.x + length.x);
        tileY = (int) (blinky.pixel.y + length.y);

//        return tiles[y][x];
//        return tiles[dy][dx];
//        return tiles[tileY][tileX];
        return pixelToTile(tileX, tileY);
    }

    //----------------------------------------------------------------------
    /**
     * Creates a path from the ghost home (Blinky section) -> outside ghost home
     *
     * @return
     */
    private ArrayList<Vector2D> createPath() {
        //Final method can not be overridden
        ArrayList<Vector2D> path = new ArrayList<>();
        path.add(new Vector2D(12 * Tile.TILE_WIDTH, 17 * Tile.TILE_HEIGHT + scaledNum(3)));
        path.add(new Vector2D(14 * Tile.TILE_WIDTH, 17 * Tile.TILE_HEIGHT + scaledNum(3)));
        path.add(new Vector2D(14 * Tile.TILE_WIDTH, 15 * Tile.TILE_HEIGHT + scaledNum(3)));
//        path.trimToSize();  //Trims the capacity of this ArrayList
        return path;
    }

    private float distance(Vector2D v1, Vector2D v2) {
        float dx = v2.x - v1.x;
        float dy = v2.y - v1.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private Vector2D interpolate(Vector2D v1, Vector2D v2, float weight,
            Vector2D positionDst) {
        positionDst.x = (v1.x * weight) + (v2.x * (1f - weight));
        positionDst.y = (v1.y * weight) + (v2.y * (1f - weight));
        return positionDst;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Assets.inky.update(deltaTime);
//        System.out.println("INKY.Y: "+pixel.y);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        Tile target = getTarget(state);
        g.fillRect((int) target.bounds.topLeft.x, (int) target.bounds.topLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
        //Draw offset tile (2 tiles from pacman)
        Tile twoTile = tiles[twoTiles.y][twoTiles.x];
        g.setColor(color);
        g.fillRect((int) twoTile.bounds.topLeft.x, (int) twoTile.bounds.topLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//        g.drawRect(inkyScatter.x, inkyScatter.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);

        //Draw line from blinky -> Target
//        g.setColor(color);
//        g.drawLine(blinky.pixel.x, blinky.pixel.y,
//                (int) (blinky.pixel.x + length.x), (int) (blinky.pixel.y + length.y));
//        System.out.println(pos);
//        g.drawImage(Assets.inky.getImage(), (int) (position.x - Blinky.GHOST_WIDTH / 2),
//                (int) (position.y - Blinky.GHOST_HEIGHT / 2),
//                Enemy.GHOST_WIDTH, Enemy.GHOST_HEIGHT, null);
        //------------------------------------------
        g.setColor(Color.YELLOW);
        g.fillRect(points[0].x, points[0].y, scaledNum(1), scaledNum(1));
        g.fillRect(points[1].x, points[1].y, scaledNum(1), scaledNum(1));
        g.fillRect(points[2].x, points[2].y, scaledNum(1), scaledNum(1));

        g.drawImage(Assets.inky.getImage(), (int) (pixel.x - Blinky.GHOST_WIDTH / 2),
                (int) (pixel.y - Blinky.GHOST_HEIGHT / 2),
                Enemy.GHOST_WIDTH, Enemy.GHOST_HEIGHT, null);
    }

    private void testFollowPath() {
        //ATTEMP 1
//                Vector2D last = path.get(currentPointIndex);
//                Vector2D next = path.get(currentPointIndex + 1);
//                distanceTraveled = distance(position, last);
//                System.out.println("----------------------------------------");
////                System.out.println("pos: " + position);
////                System.out.println("las: " + last);
//                System.out.println("distance trav: " + distanceTraveled);
//
//                distanceTraveled += 1f;
//
//                if (distanceTraveled > distanceBetweenCurrentPoints) {
//                    currentPointIndex++;
//                    distanceTraveled -= distanceBetweenCurrentPoints;
//                    System.out.println("cuurentPointIndex: " + currentPointIndex);
//                    System.out.println("distanceTraved: " + distanceTraveled);
//                } else {
//                    System.out.println("interpolate");
//                    float weight = distanceTraveled / distanceBetweenCurrentPoints;
//                    System.out.println("weight: " + weight);
//                    position = interpolate(last, next, weight, position);
//                    System.out.println("position: " + position);
//                }
    }

    private void timeToLeave() {
        //ATTEMPT 2
//                System.out.println("pixel.y: "+pixel.y);
//                System.out.println("points[0].y: "+points[0].y);
//                for (int i = 0; i < points.length; i++) {
//
//                }
        if (travel == 0) {
            //Go to point 1
            if (pixel.y != points[0].y) {
                if (pixel.y < points[0].y) {
                    pixel.y++;
                } else {
                    pixel.y--;
                }
            } else {
                travel += 1;
            }
        } else if (travel == 1) {
            //Go to point 2
            if (pixel.x != points[1].x) {
                if (pixel.x < points[1].x) {
                    pixel.x++;
                } else {
                    pixel.x--;
                }
            } else {
                travel += 1;
            }
        } else if (travel == 2) {
            if (pixel.y != points[2].y) {
                if (pixel.y < points[2].y) {
                    pixel.y++;
                } else {
                    pixel.y--;
                }
            } else {
                //TRAVEL IS COMPLETE
                System.out.println("setting state");
            }
        }
    }

    private void timeToHover() {
//                if(pixel.y < scaledNum(136)){
//                    switchDir();
//                }
//                if(pixel.y > scaledNum(143)){
//                    switchDir();
//                }
    }
}
