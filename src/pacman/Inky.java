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
import java.util.List;

/**
 * 29-Jun-2018, 19:17:00.
 *
 * @author Mo
 */
public class Inky extends Enemy {

    private final Enemy blinky;
    public Point twoTiles = new Point();
    Vector2D length = new Vector2D();
    Vector2D blinkyPixel = new Vector2D();
    Vector2D offsetPixel = new Vector2D();

    public Inky(Tile[][] tiles, Pacman pacman, List<Point> allDots,
            List<Point> allEnergizers, int x, int y, int id, Enemy blinky) {
        super(tiles, pacman, allDots, allEnergizers, x, y, id);

        this.blinky = blinky;
        color = new Color(0, 255, 255, 255);
    }

    @Override
    public Tile getTarget(int mode) {
        switch (mode) {
            case Enemy.STATE_SCATTER:
                return tiles[inkyScatter.y][inkyScatter.x];
        }
        int x, y, offset;
        x = pacman.pacmanTile.x;
        y = pacman.pacmanTile.y;
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

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Assets.inky.update(deltaTime);
//        System.out.println(blinky.ghostTile);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        Tile target = getTarget(state);
        g.fillRect((int) target.bounds.lowerLeft.x, (int) target.bounds.lowerLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
        //Draw offset tile (2 tiles from pacman)
        Tile twoTile = tiles[twoTiles.y][twoTiles.x];
        g.setColor(color);
        g.fillRect((int) twoTile.bounds.lowerLeft.x, (int) twoTile.bounds.lowerLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//        g.drawRect(inkyScatter.x, inkyScatter.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);

        //Draw line from blinky -> Target
//        g.setColor(color);
//        g.drawLine(blinky.pixel.x, blinky.pixel.y,
//                (int) (blinky.pixel.x + length.x), (int) (blinky.pixel.y + length.y));
    }
}
