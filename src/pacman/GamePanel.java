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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import pacman.World.WorldListener;

/**
 * 06-Apr-2018, 21:37:27.
 *
 * @author Mo
 */
public class GamePanel extends JPanel implements Runnable {
    public static final int scale = 10;
    
    public static final int GAME_WIDTH = 224 * scale;
    public static final int GAME_HEIGHT = 288 * scale;
//    public static final int GAME_WIDTH = 224;   //Original size
//    public static final int GAME_HEIGHT = 288;
    //Game States
    public static final int GAME_READY = 0;
    public static final int GAME_RUNNING = 1;
    public static final int GAME_GAMEOVER = 2;
//    public static int state = GAME_READY;
    public static int state = GAME_RUNNING;

    private boolean running = false;
    private Thread thread;
    private BufferedImage image;
    private Graphics2D g;

    private Input input;

    private final int FPS = 60;
    private long averageFPS;

    private final World world;
    private final WorldListener worldListener;

    private Font smallFont, largeFont;

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(GAME_WIDTH - 10, GAME_HEIGHT - 10));
        setFocusable(true);
        //System.out.println(requestFocusInWindow());
        requestFocus(); //-> platform dependant

        initInput();
        Assets.loadImages();

        worldListener = new WorldListener() {
            @Override
            public void fire() {
            }

            @Override
            public void enemySpawn() {
            }

            @Override
            public void enemyDie() {
            }

            @Override
            public void playerSpawn() {
            }

            @Override
            public void playerHurt() {
            }

            @Override
            public void playerDie() {
            }

            @Override
            public void loadNextWave() {
            }

            @Override
            public void sayPraise() {
//                Assets.sayPraise.play(1);
            }

        };
        world = new World(worldListener);

        smallFont = new Font("Courier new", Font.PLAIN, 20);
        largeFont = new Font("Courier new", Font.PLAIN, 85);
    }

    private void initInput() {
        input = new Input();
        addKeyListener(input);
//        addKeyListener(new TAdapter());
        MAdapter lis = new GamePanel.MAdapter();
        addMouseListener(lis);
        addMouseMotionListener(lis);
    }

    //METHODS
    /**
     * Is called after the JPanel has been added to the JFrame component.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        long startTime;
        long timeTaken;
        long frameCount = 0;
        long totalTime = 0;
        long waitTime;
        long targetTime = 1000 / FPS;
        int counter = 0;    //can delete, counts negative waitTime

        running = true;
        image = new BufferedImage(GAME_WIDTH, GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        //Set correct font & size
        g.setFont(largeFont);
        //Enable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        //GAME LOOP
        while (running) {
            startTime = System.nanoTime();

            handleInput();
            gameUpdate(1f / FPS);
            Input.updateLastKey();
            gameRender(g);
            gameDraw();

            //How long it took to run
            timeTaken = (System.nanoTime() - startTime) / 1000000;
            //              16ms - targetTime
            waitTime = targetTime - timeTaken;

            //System.out.println(timeTaken);
            if (waitTime < 0) {
                //I get a negative value at the beg
                System.out.println(counter++ + ": NEGATIVE: " + waitTime);
                System.out.println("targetTime = " + targetTime);
                System.out.println("timeTaken = " + timeTaken + "\n");
            }

            try {
                //System.out.println("Sleeping for: " + waitTime);
                //thread.sleep(waitTime);
                Thread.sleep(waitTime);
            } catch (Exception ex) {

            }
            totalTime += System.nanoTime() - startTime;
            frameCount++;

            //If the current frame == 60  we calculate the average frame count
            if (frameCount >= FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
//                System.out.println("Average fps: " + averageFPS);
            }
        }

    }

    private void handleInput() {
        switch (state) {
            case GAME_RUNNING:
                world.handleKeyEvents();
                break;
            case GAME_GAMEOVER:

                break;
        }
    }

    /* ********************* UPDATE & RENDER ************************* */
    private void gameUpdate(float deltaTime) {
        switch (state) {
            case GAME_READY:
                break;
            case GAME_RUNNING:
                world.gameUpdate(deltaTime);
                break;
            case GAME_GAMEOVER:
                break;
        }
    }

    private void gameRender(Graphics2D g) {
        //Clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        switch (state) {
            case GAME_READY:
                break;
            case GAME_RUNNING:
                world.gameRender(g);
                break;
            case GAME_GAMEOVER:
                break;
        }
    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    /**
     * *************** EVENT HANDERLERS ***********************
     */
    //Handle Input ** Inner Class
    private class TAdapter extends KeyAdapter {

        //When a key is pressed, let the CRAFT class deal with it.
        @Override
        public void keyPressed(KeyEvent e) {
            //Handle player from world movement
//            player.keyPressed(e);

//            world.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //ship.keyReleased(e);
//            player.keyReleased(e);
        }
    }

    private class MAdapter implements MouseListener, MouseMotionListener {
        /* CLICK LISTENER */

        @Override
        public void mouseClicked(MouseEvent e) {
//            System.out.println("CLICKED");
            //Clicked in one position
        }

        @Override
        public void mousePressed(MouseEvent e) {
//            System.out.println("PRESSED");
        }

        @Override
        public void mouseReleased(MouseEvent e) {
//            System.out.println("RELEASED");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //System.out.println("ENTERED");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //System.out.println("EXITED");
        }

        /* MOTION LISTENER */
        @Override
        public void mouseDragged(MouseEvent e) {
//            System.out.println("DRAGGED");
            //Clicked and held
        }

        @Override
        public void mouseMoved(MouseEvent e) {
//            System.out.println("MOVED");
            //All movement
        }

    }
}
