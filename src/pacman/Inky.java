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

import common.Vector2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

/**
 * The light-blue ghost is nicknamed "Inky" and his character is described as
 * one who is bashful. Sometimes he chases Pac-Man aggressively like Blinky;
 * other times he jumps ahead of Pac-Man as Pinky would. He might even wander
 * off like Clyde on occasion!
 *
 * Inky uses the most complex targeting scheme of the four ghosts in chase mode.
 * He needs Pac-Man's current tile/orientation and Blinky's current tile to
 * calculate his final target. To envision Inky's target, imagine an
 * intermediate offset two tiles away from Pac-Man's tile in the direction
 * Pac-Man is moving, then draw a line from Blinky's tile to that offset. Now
 * double the line length by extending the line out just as far again, and you
 * will have Inky's target tile as shown above.
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class Inky extends Enemy {

    private final Enemy blinky;
    private Point twoTiles = new Point();
    Vector2D length = new Vector2D();
    Vector2D blinkyPixel = new Vector2D();
    Vector2D offsetPixel = new Vector2D();

    /**
     * Initialises Inky.
     *
     * @param tiles reference to world
     * @param pacman reference to Pacman
     * @param allDots list of all dots
     * @param allEnergizers list of all energizers
     * @param x initial x index
     * @param y initial y index
     * @param blinky reference to Blinky
     */
    public Inky(Tile[][] tiles, Pacman pacman, List<Point> allDots,
            List<Point> allEnergizers, int x, int y, Enemy blinky) {
        super(Tile.INKY, tiles, pacman, allDots, allEnergizers, x, y);

        this.blinky = blinky;
        color = new Color(0, 255, 255, 255);
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

        return pixelToTile(tileX, tileY);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Assets.inky.update(deltaTime);
//        System.out.println("INKY.Y: "+pixel.y);
    }

    @Override
    public void draw(Graphics2D g) {

    }
}
