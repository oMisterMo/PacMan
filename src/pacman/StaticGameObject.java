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
import java.awt.Graphics2D;
//import java.awt.Rectangle;
import common.Rectangle;

/**
 * The class represents static game objects which does not move once created.
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class StaticGameObject extends GameObject {

    protected Vector2D position;
//    protected Rectangle.Float bounds;
    protected Rectangle bounds;

    /**
     * Constructs a new object at the given location
     *
     * The x, y location represent the top left hand corner of the object
     *
     * @param x the x position
     * @param y the y position
     * @param width the width of the object
     * @param height the height of the object
     */
    public StaticGameObject(float x, float y, float width, float height) {
        this.position = new Vector2D(x, y);
        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    void gameUpdate(float deltaTime) {
    }

    @Override
    void gameRender(Graphics2D g) {
    }

}
