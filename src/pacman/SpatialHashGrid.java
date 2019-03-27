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

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>SpatialHashGrid</code> class provides an implementation to the
 * broad phase collision detection.
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class SpatialHashGrid {

    private List<StaticGameObject>[] dynamicCells;
    private List<StaticGameObject>[] staticCells;
    private int cellsPerRow;
    private int cellsPerCol;
    private float cellSize;
    private int[] cellIds = new int[4]; //Cell an object is currently contained in
    private List<StaticGameObject> foundObjects;
//    List<DynamicGameObject> dynamicCells;

    /**
     * Constructs a new SpatialHasGrid with enough cells to fill the entire
     * screen based on the arguments provided.
     *
     * A good place to start is having {@link #cellSize cellSize} five times
     * bigger than the biggest object in the scene.
     *
     * @param worldWidth the width of the world
     * @param worldHeight the height of the world
     * @param cellSize the size of each cell
     */
    @SuppressWarnings("unchecked")
    public SpatialHashGrid(float worldWidth, float worldHeight, float cellSize) {
        this.cellSize = cellSize;
        this.cellsPerRow = (int) Math.ceil(worldWidth / cellSize);
        this.cellsPerCol = (int) Math.ceil(worldHeight / cellSize);
        int numOfCells = cellsPerRow * cellsPerCol;

        dynamicCells = new List[numOfCells];
        staticCells = new List[numOfCells];
        for (int i = 0; i < numOfCells; i++) {
            //Assumption that a single cell will not contain more than 10 gameObjects at once
            dynamicCells[i] = new ArrayList<>(10);
            staticCells[i] = new ArrayList<>(10);
        }
        foundObjects = new ArrayList<>(10);

//        //Debuggin
        System.out.println("*********************************");
        System.out.println("cells size: " + cellSize);          //250
        System.out.println("cells per row: " + cellsPerRow);    //6
        System.out.println("cells per col: " + cellsPerCol);    //3
        System.out.println("num of cells: " + numOfCells);      //18
        System.out.println("*********************************");

    }

    /**
     * Inserts a game object into the {@link #staticCells staticCells} list.
     * There are
     * {@link #cellsPerRow cellsPerRow} * {@link #cellsPerCol cellsPerCol} total
     * cells. Each cell contains a static and dynamic list.
     *
     * @param obj game object to insert
     */
    public void insertStaticObject(StaticGameObject obj) {
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i <= 3 && (cellId = cellIds[i++]) != -1) {
            staticCells[cellId].add(obj);
        }
    }

    /**
     * Inserts a game object into the {@link #dynamicCells dynamicCells} list.
     *
     * @param obj dynamic game object to insert
     */
    public void insertDynamicObject(StaticGameObject obj) {
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i <= 3 && (cellId = cellIds[i++]) != -1) {
            dynamicCells[cellId].add(obj);
        }
    }

    /**
     * Removes a game object from from either the static or dynamic list.
     *
     * @param obj game object to remove
     */
    public void removeObject(StaticGameObject obj) {
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;
        while (i <= 3 && (cellId = cellIds[i++]) != -1) {
            dynamicCells[cellId].remove(obj);
            staticCells[cellId].remove(obj);
        }
    }

    /**
     * Clear all dynamic cell lists, must be called each frame before we
     * reinsert the dynamic objects.
     */
    public void clearDynamicCells() {
        int len = dynamicCells.length;
        for (int i = 0; i < len; i++) {
            dynamicCells[i].clear();
        }
    }

    /**
     * Given a game object, will return a list of game objects contained in the
     * same cell.
     *
     * @param obj the object whose cell to search
     * @return a list of neighbouring objects in same cell as obj
     */
    public List<StaticGameObject> getPotentialColliders(StaticGameObject obj) {
        foundObjects.clear();
        int[] cellIds = getCellIds(obj);
        int i = 0;
        int cellId = -1;

        while (i <= 3 && (cellId = cellIds[i++]) != -1) {
            //go through dynamic list
            int len = dynamicCells[cellId].size();
            for (int j = 0; j < len; j++) {
                StaticGameObject collider = dynamicCells[cellId].get(j);
                if (!foundObjects.contains(collider)) {
                    foundObjects.add(collider);
                }
            }

            //num of objects at cellid
            len = staticCells[cellId].size();
            for (int j = 0; j < len; j++) {
                StaticGameObject collider = staticCells[cellId].get(j);
                if (!foundObjects.contains(collider)) {
                    foundObjects.add(collider);
                }
            }
        }
        return foundObjects;
    }

    /**
     * Gets the cell id's that a given game object is contained in. Cells are id
     * from 0 to
     * ({@link #cellsPerRow cellsPerRow} * {@link #cellsPerCol cellsPerCol}).
     * The first cell starts at (0,0) to (cellW, cellH).
     *
     * @param obj the object to examine
     * @return a list of cell id's that obj overlaps
     */
    public int[] getCellIds(StaticGameObject obj) {
        int x1 = (int) Math.floor(obj.bounds.topLeft.x / cellSize);
        int y1 = (int) Math.floor(obj.bounds.topLeft.y / cellSize);
        int x2 = (int) Math.floor((obj.bounds.topLeft.x + obj.bounds.width) / cellSize);
        int y2 = (int) Math.floor((obj.bounds.topLeft.y + obj.bounds.height) / cellSize);
//        System.out.println("width: "+obj.bounds.width);
//        System.out.println("x1 = " + x1);
//        System.out.println("y2 = " + y2);
//        System.out.println("x2 = " + x2);
//        System.out.println("y2 = " + y2);

        //Single cell
        if (x1 == x2 && y1 == y2) {
            if (x1 >= 0 && x1 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol) {
                cellIds[0] = x1 + y1 * cellsPerRow;
                cellIds[1] = -1;
                cellIds[2] = -1;
                cellIds[3] = -1;
            } else {
                System.out.println("DEBUG ME, WTF AM I GETTING CALLED??");
                cellIds[0] = -1;
                cellIds[1] = -1;
                cellIds[2] = -1;
                cellIds[3] = -1;
            }
        } else if (x1 == x2) {
            //Two cell (vertical)
            int i = 0;
            if (x1 >= 0 && x1 < cellsPerRow) {
                if (y1 >= 0 && y1 < cellsPerCol) {
                    cellIds[i++] = x1 + y1 * cellsPerRow;
                }
                if (y2 >= 0 && y2 < cellsPerCol) {
                    cellIds[i++] = x1 + y2 * cellsPerRow;
                }
            }
            while (i <= 3) {
                cellIds[i++] = -1;
            }
        } else if (y1 == y2) {
            //Two cell (horizontal)
//            System.out.println("HORIZONTAL CELLS");
            int i = 0;
            if (y1 >= 0 && y1 < cellsPerCol) {
                if (x1 >= 0 && x1 < cellsPerRow) {
                    cellIds[i++] = x1 + y1 * cellsPerRow;
                }
                if (x2 >= 0 && x2 < cellsPerRow) {
                    cellIds[i++] = x2 + y1 * cellsPerRow;
                }
            }
            while (i <= 3) {
                cellIds[i++] = -1;
            }
        } else {
            //Four cell
            int i = 0;
            int y1CellsPerRow = y1 * cellsPerRow;
            int y2CellsPerRow = y2 * cellsPerRow;
            if (x1 >= 0 && x1 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol) {
                cellIds[i++] = x1 + y1CellsPerRow;
            }
            if (x2 >= 0 && x2 < cellsPerRow && y1 >= 0 && y1 < cellsPerCol) {
                cellIds[i++] = x2 + y1CellsPerRow;
            }
            if (x2 >= 0 && x2 < cellsPerRow && y2 >= 0 && y2 < cellsPerCol) {
                cellIds[i++] = x2 + y2CellsPerRow;
            }
            if (x1 >= 0 && x1 < cellsPerRow && y2 >= 0 && y2 < cellsPerCol) {
                cellIds[i++] = x1 + y2CellsPerRow;
            }
            while (i <= 3) {
                cellIds[i++] = -1;
            }
        }
        return cellIds;
    }

    /**
     * Gets the size of each cell.
     *
     * @return the cell size
     */
    public float getCellSize() {
        return cellSize;
    }

    /**
     * Debugging, do not need.
     */
    public void printInfo() {
        System.out.println("No of static cells: " + staticCells.length);
        System.out.println("No of dynamic cells: " + dynamicCells.length);
        int n = 10;
        System.out.println("Static cell " + n + " contains: " + staticCells[n].size()
                + " objects");
        System.out.println("Dynaimic cell " + n + " contains: " + dynamicCells[n].size()
                + " objects");
    }
}
