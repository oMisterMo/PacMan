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
package common;

import java.awt.image.BufferedImage;

/**
 * The Animation class holds a list of BufferedImages and the meta data
 * associated with the current Animation.
 *
 * 14/03/2016.
 *
 * @author Mohammed Ibrahim
 */
public class Animation {

    private BufferedImage[] frames;    //Sprite array of current animation
    private int currentFrame;

    private long startTime;
    private long delay;
    private boolean playedOnce; //Some animation are only played once

    public Animation() {
        frames = null;
    }

    /**
     * Determines which image in the array to return
     *
     * @param deltaTime time since last tick
     */
    public void update(float deltaTime) {
        long elapsed = (System.nanoTime() - startTime) / 1000000;

        if (elapsed > delay) {
            currentFrame++;
//            currentFrame += delay * deltaTime;
            startTime = System.nanoTime();
        }
        //Resets frame count after full play through
        if (currentFrame >= frames.length) {
            currentFrame = 0;
            playedOnce = true;
        }
    }

    public BufferedImage getImage() {
        return frames[currentFrame];
    }

    /**
     * Assign a bitmap array to frames
     *
     * @param frames The frame that's updated
     */
    public void setFrames(BufferedImage[] frames) {
        this.frames = frames;
        currentFrame = 0;
        startTime = System.nanoTime();
    }

    public void setDelay(long del) {
        delay = del;
    }

    private int getFrame() {
        return currentFrame;
    }

    public boolean playedOnce() {
        return playedOnce;
    }

    public void resetAnimation() {
        currentFrame = 0;
    }
}
