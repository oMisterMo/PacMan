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

import common.Circle;
import common.OverlapTester;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

/**
 * The orange ghost is nicknamed "Clyde" and is characterized as one who is
 * pokey. Clyde is the last ghost to leave the pen and tends to separate himself
 * from the other ghosts by shying away from Pac-Man and doing his own thing
 * when he isn't patrolling his corner of the maze. In chase mode, Clyde's
 * target differs based on his proximity to Pac-Man. When more than eight tiles
 * away, he uses Pac-Man's tile as his target (shown as the yellow target
 * above). If Clyde is closer than eight tiles away, he switches to his scatter
 * mode target instead, and starts heading for his corner until he is far enough
 * away to target Pac-Man again.
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class Clyde extends Enemy {

    private Circle scatterBounds;

    /**
     * Initialises Clyde.
     *
     * @param tiles reference to world
     * @param pacman reference to Pacman
     * @param allDots list of all dots
     * @param allEnergizers list of all energizers
     * @param x initial x index
     * @param y initial y index
     */
    public Clyde(Tile[][] tiles, Pacman pacman, List<Point> allDots,
            List<Point> allEnergizers, int x, int y) {
        super(Tile.CLYDE, tiles, pacman, allDots, allEnergizers, x, y);
        color = new Color(255, 185, 80, 255);

        //Create a circle 8 tiles wide around pacman
        scatterBounds = new Circle(pacman.pixel.x,
                pacman.pixel.y, 8 * Tile.TILE_WIDTH);
        System.out.println("clyde constructor finsihed...");
    }

    @Override
    public Tile getTarget(int mode) {
        switch (mode) {
            case Enemy.STATE_SCATTER:
                return tiles[clydeScatter.y][clydeScatter.x];
        }
        //If clyde is 8 tiles from pacman -> go hide in scatter corner
        if (OverlapTester.pointInCircle(scatterBounds, pixel.x, pixel.y)) {
            return tiles[clydeScatter.y][clydeScatter.x];
        }
        //Otherwise chase pacman
        return tiles[pacman.pacmanTile.y][pacman.pacmanTile.x];
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Assets.clyde.update(deltaTime);
        scatterBounds.center.set(pacman.pixel.x, pacman.pixel.y);
    }

    @Override
    public void draw(Graphics2D g) {

    }
}
