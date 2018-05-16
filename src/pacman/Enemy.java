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

/**
 * 13-Apr-2018, 00:11:54.
 *
 * @author Mo
 */
public class Enemy {

    public final static int STATE_CHASE = 0;
    public final static int STATE_SCATTER = 1;
    public final static int STATE_EVADE = 2;
    public static int state = STATE_SCATTER;

}
