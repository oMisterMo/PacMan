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
import java.util.List;

/**
 * 22-May-2018, 02:29:30.
 *
 * @author Mohammed Ibrahim
 */
public class Blinky extends Enemy {

    public Blinky(int id, Tile[][] tiles, Pacman pacman, List<Point> allDots,
            List<Point> allEnergizers, int x, int y) {
        super(id, tiles, pacman, allDots, allEnergizers, x, y);
        color = new Color(255, 0, 0, 255);
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Assets.blinky.update(deltaTime);
//        System.out.println("BLINKY.Y = "+pixel.y);
    }

    @Override
    public Tile getTarget(int mode) {
        return super.getTarget(mode);
    }
}
