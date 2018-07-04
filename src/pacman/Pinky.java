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
import java.util.List;
import static pacman.Enemy.state;

/**
 * 28-Jun-2018, 19:31:05.
 *
 * @author Mo
 */
public class Pinky extends Enemy {

    public Pinky(Tile[][] tiles, Pacman pacman, List<Point> allDots,
            List<Point> allEnergizers, int x, int y, int id) {
        super(tiles, pacman, allDots, allEnergizers, x, y, id);

        color = new Color(255, 185, 255, 255);
    }

    @Override
    public Tile getTarget(int mode) {
//        return super.getTarget(mode);
//        System.out.println("PINKKKY!!");
//        System.out.println("pacman dir: "+pacmanDir);
//        System.out.println("-------->" + pacman.pacmanDir + "<--------");
        switch (mode) {
//            case Enemy.STATE_CHASE:
//                return tiles[pacmanTile.y][pacmanTile.x];
            case Enemy.STATE_SCATTER:
                return tiles[pinkyScatter.y][pinkyScatter.x];
        }
        int x, y, offset;
        x = pacman.pacmanTile.x;
        y = pacman.pacmanTile.y;
        offset = 4;
        switch (pacman.recentDir) {
            case UP:
                //If it is the same logic as the classic:
                //get 4 tiles up, 4 tiles left
                //due to a subtle error in the logic code that calculates Pinky's offset from Pac-Man
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
        return tiles[y][x];
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Assets.pinky.update(deltaTime);
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        Tile target = getTarget(state);
        g.fillRect((int) target.bounds.lowerLeft.x, (int) target.bounds.lowerLeft.y,
                Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
//        g.drawRect(inkyScatter.x, inkyScatter.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
    }
}
