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

import common.Helper;
import common.Vector2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * A Particle represents an individual sprite that can move independently.
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class Particle extends DynamicGameObject {

    protected Point centerPoint;
    protected float width, height;
    //Particles lifespan in ms
    protected float age;

    //Range between 0.0 - 1.0 (ressistanse on x axis)
    protected float dampening;
    //Rotation variables
    protected float rotation;
    protected float rotationVel;
    protected float rotationDamp;
    //Scale variables
    protected float scale;  //or do I only scale one axis 0.0 -> scaleMax
    protected float scaleVel;
    protected float scaleAcc;
    protected float scaleMax;

    protected Color color;
    private Color initColor;
    private Color finalColor;
    protected float fadeAge;    //color will fade from init-> final after fadeAge

    protected float startTime;
    protected boolean dead;
    private AffineTransform trans;

    public Particle(float x, float y, float width, float height,
            float age, float damp,
            float initRot, float initRotVel, float initRotDamp,
            float initScale, float initScaleVel, float initScaleAcc,
            float initScalemax) {
        super(x, y, width, height);
        //Width of our pixel (5)
        this.width = width;
        this.height = height;
        centerPoint = new Point();
        calculateCenter();

        initColor = new Color(0xff_ffffff);
        finalColor = new Color(0xff_ffffff);
        this.age = age;
        dampening = damp;//between 0.0 -> 1.0

        this.rotation = initRot;
        this.rotationVel = initRotVel;
        this.rotationDamp = initRotDamp;

        this.scale = initScale;
        this.scaleVel = initScaleVel;
        this.scaleAcc = initScaleAcc;
        this.scaleMax = initScalemax;

        startTime = System.currentTimeMillis();
        dead = false;

        trans = new AffineTransform();
//        printInfo();
    }

    public Particle(float x, float y, float width, float height,
            float age, float damp,
            float initRot, float initRotVel, float initRotDamp,
            float initScale, float initScaleVel, float initScaleAcc,
            float initScalemax,
            Color initColor, Color finalColor, float fadeAge) {
        this(x, y, width, height,
                age, damp,
                initRot, initRotVel, initRotDamp,
                initScale, initScaleVel, initScaleAcc,
                initScalemax);
//        this.color = color;
        this.initColor = initColor;
        this.finalColor = finalColor;
        this.fadeAge = fadeAge;
    }

    private void printInfo() {
        System.out.println(position);
        System.out.println(velocity);
        System.out.println(acceleration);
        System.out.println("width: " + width);
        System.out.println("height: " + height);
        System.out.println(color);
    }

    public void mousePressed(MouseEvent e) {
//        position.x = e.getX();
//        position.y = e.getY();
//        System.out.println("Pressed");
    }

    public void mouseReleased(MouseEvent e) {
//        position.x = e.getX();
//        position.y = e.getY();
//        System.out.println("Released");

//        startTime = System.currentTimeMillis();
//        age = 3000;//ms
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

    public void applyForce(Vector2D force) {
        this.acceleration.add(force);
    }

    private void updatePos(float deltaTime) {
        /* 
         vel (m/s) gravitiy (m/s), deltaTime in seconds(s), position(m)
         Everysecond we update the velocity
         */
        velocity.mult(dampening);

        //Update velocities position by applying gravity (change in vel = accel)
        velocity.x += acceleration.x * deltaTime;
        velocity.y += acceleration.y * deltaTime;
//        System.out.println(velocity);

        //Update the particles position
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        //Update collision bounds
        bounds.topLeft.x = position.x;
        bounds.topLeft.y = position.y;

        //Reset forces
        acceleration.mult(0);
    }

    private void updateRot(float deltaTime) {
        rotation *= rotationDamp;
        rotation += rotationVel * deltaTime;
//        System.out.println("rotation: " + rotation);
    }

    private void updateScale(float deltaTime) {
        scaleVel += scaleAcc * deltaTime;
        scale += scaleVel * deltaTime;
        scale = Helper.Clamp(scale, 0.0f, scaleMax);
//        System.out.println("scale = " + scale);
    }

    private void updateCol(float deltaTime) {
        if ((age > fadeAge) && (fadeAge != 0)) {
            color = initColor;
//            System.out.println("first col");
        } else {
            //interpolate color value
//            System.out.println("blending colors");
            float initC = (float) (age / fadeAge);
            float finalC = 1.0f - initC;

            float red = (initColor.getRed() / 255f * initC) + (finalColor.getRed() / 255f * finalC);
            float green = (initColor.getGreen() / 255f * initC) + (finalColor.getGreen() / 255f * finalC);
            float blue = (initColor.getBlue() / 255f * initC) + (finalColor.getBlue() / 255f * finalC);
            float alpha = (initColor.getAlpha() / 255f * initC) + (finalColor.getAlpha() / 255f * finalC);

//            System.out.println("red: " + red);
//            System.out.println("green = " + green);
//            System.out.println("blue = " + blue + "\n");
            color = new Color(red, green, blue, alpha);
        }
        //Set color of sprite here
    }

    private void calculateCenter() {
        centerPoint.x = (int) (position.x + width / 2);
        centerPoint.y = (int) (position.y + height / 2);
    }

    private void scaleAndRotate(Graphics2D g) {
        AffineTransform old = g.getTransform();
//        AffineTransform trans = new AffineTransform();
        trans.setToIdentity();
        trans.translate(centerPoint.x, centerPoint.y);
        trans.scale(scale, scale);
        trans.rotate(Math.toRadians(rotation));
        trans.translate(-centerPoint.x, -centerPoint.y);
        g.setTransform(trans);

//        g.fillRect((int) position.x, (int) position.y, (int) width, (int) height);
        g.fillOval((int) position.x, (int) position.y, (int) width, (int) height);
        g.setTransform(old);
    }

    @Override
    void gameUpdate(float deltaTime) {
        //dt = 0.016 (60fps) dt = 0.032 (30fps)
//        System.out.println("deltaTime: "+deltaTime);
        age -= deltaTime;  //in ms
        if (age < 0) {
            age = 0;
            dead = true;
            return;
        }
//        System.out.println("Age: "+age);

        updatePos(deltaTime);
        updateRot(deltaTime);
        updateScale(deltaTime);
        updateCol(deltaTime);
        calculateCenter();
    }

    @Override
    void gameRender(Graphics2D g) {
        if (age < 0) {
            age = 0;
            return;
        }
        /*
         g.scale(x, y);
         g.rotate(rot);
         g.translate(x, y);
         */
        g.setColor(color);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        scaleAndRotate(g);
    }

}
