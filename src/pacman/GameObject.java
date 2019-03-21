/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman;

import java.awt.Graphics2D;

/**
 * Represents any object which is drawn to the screen.
 *
 * Possible subclasses: -> Tile -> Particle -> Player -> Square
 *
 * ALT + SHIFT + F = auto re-factor
 *
 * 16/05/16
 *
 * @author Mohammed Ibrahim
 */
public abstract class GameObject {

    /**
     * Updates the current game object
     *
     * @param deltaTime time since last frame
     */
    abstract void gameUpdate(float deltaTime);

    /**
     * Draws the screen
     *
     * @param g graphics object
     */
    abstract void gameRender(Graphics2D g);
}
