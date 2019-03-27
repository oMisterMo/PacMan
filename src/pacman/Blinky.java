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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

/**
 * The red ghost's character is aptly described as that of a shadow and is
 * best-known as "Blinky". Blinky seems to always be the first of the ghosts to
 * track Pac-Man down in the maze. He is by far the most aggressive of the four
 * and will doggedly pursue Pac-Man once behind him. Of all the ghosts'
 * targeting schemes for chase mode, Blinky's is the most simple and direct,
 * using Pac-Man's current tile as his target.
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class Blinky extends Enemy {

    /**
     * Initialises Blinky.
     *
     * @param tiles reference to world
     * @param pacman reference to Pacman
     * @param allDots list of all dots
     * @param allEnergizers list of all energizers
     * @param x initial x index
     * @param y initial y index
     */
    public Blinky(Tile[][] tiles, Pacman pacman, List<Point> allDots,
            List<Point> allEnergizers, int x, int y) {
        super(Tile.BLINKY, tiles, pacman, allDots, allEnergizers, x, y);
        color = new Color(255, 0, 0, 255);
    }

    @Override
    public Tile getTarget(int mode) {
        switch (mode) {
//            case Enemy.STATE_CHASE:
//                return tiles[pacmanTile.y][pacmanTile.x];
            case Enemy.STATE_SCATTER:
                return tiles[blinkyScatter.y][blinkyScatter.x];
        }
        return tiles[pacman.pacmanTile.y][pacman.pacmanTile.x];
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Assets.blinky.update(deltaTime);
//        System.out.println("BLINKY.Y = "+pixel.y);
    }

    @Override
    public void draw(Graphics2D g) {

    }
}
