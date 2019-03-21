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

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Main Class
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class GameMain {

    private static Assets assets;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            loadImages();

            /*ATTEMPT 1*/
            //Load when ready
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    createAndShowGUI();
//                }
//            });

            /*ATTEMPT 2*/
            createAndShowGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadImages() throws IOException {
//        Assets.loadImages();
        assets = new Assets();
//        assets.loadImages();
    }

    private static void createAndShowGUI() {
        JFrame window = new JFrame("Pac-Man");
        GamePanel game = new GamePanel();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(game);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setAlwaysOnTop(true);
    }
}
