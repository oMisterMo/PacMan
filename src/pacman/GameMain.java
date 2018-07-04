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

import javax.swing.JFrame;

/**
 * Main Class
 *
 * 06-Apr-2018, 21:36:36.
 *
 * @author Mo
 */
public class GameMain {

    public static void main(String[] args) {
        JFrame window = new JFrame("Pac-Man");
        GamePanel game = new GamePanel();
        
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        window.add(game);
        window.pack();
//        window.setLocation(70, 50);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setVisible(true);

        window.setAlwaysOnTop(true);
    }

}
