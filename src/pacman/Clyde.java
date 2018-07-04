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

import common.Circle;
import common.OverlapTester;
import common.Vector2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

/**
 * 29-Jun-2018, 19:17:05.
 *
 * @author Mo
 */
public class Clyde extends Enemy {

    private Circle scatterBounds;

    public Clyde(Tile[][] tiles, Pacman pacman, List<Point> allDots,
            List<Point> allEnergizers, int x, int y, int id) {
        super(tiles, pacman, allDots, allEnergizers, x, y, id);
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
        g.setColor(color);
        if (OverlapTester.pointInCircle(scatterBounds, pixel.x, pixel.y)) {
            g.setColor(Color.RED);
        }
        g.drawOval((int) (scatterBounds.center.x - scatterBounds.radius),
                (int) (scatterBounds.center.y - scatterBounds.radius),
                (int) scatterBounds.radius * 2, (int) scatterBounds.radius * 2);
    }
}
