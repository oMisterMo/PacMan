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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class implements the A* path finding algorithm and uses it to find the
 * shortest path given two tiles within the Pacman world.
 *
 * @version 0.1.0
 * @author Mohammed Ibrahim
 */
public class Astar {

    private World world;

    /**
     * Default constructor.
     *
     * @param world reference to Pacmans world
     */
    public Astar(World world) {
        this.world = world;
    }

    /**
     * Gets the distance between two nodes using the Diagonal distance. If your
     * map allows diagonal movement you need a different heuristic. The
     * Manhattan distance for (4 east, 4 north) will be 8 X D.
     *
     * D = 14, D2 = 10
     *
     * @param nodaA initial node
     * @param nodeB goal node
     * @return the cost of going from any location to nearby way points
     */
    private int getDistance(Tile nodaA, Tile nodeB, int nothingHere) {
        int dx = Math.abs(nodaA.grid.x - nodeB.grid.x);
        int dy = Math.abs(nodaA.grid.y - nodeB.grid.y);

        //D * (dx + dy) + (D2 - 2 * D) * min(dx, dy) (same thing)
        if (dx > dy) {
            return 14 * (dx - dy) + 10 * dy;
        }
        return 14 * (dy - dx) + 10 * dx;
    }

    /**
     * Gets the distance between two nodes using the Manhattan distance. This is
     * useful when you can move UP, DOWN, LEFT and RIGHT only. The standard
     * heuristic for a square grid is the Manhattan distance. Look at your cost
     * function and find the minimum cost D for moving from one space to an
     * adjacent space. In the simple case, you can set D to be 1.
     *
     * D = 10
     *
     * @param nodaA initial node
     * @param nodeB goal node
     * @return the cost of going from any location to nearby way points
     */
    private int getDistance(Tile nodaA, Tile nodeB) {
        int dx = Math.abs(nodaA.grid.x - nodeB.grid.x);
        int dy = Math.abs(nodaA.grid.y - nodeB.grid.y);
        //D * (dx + dy)
        return 10 * (dy + dx);
    }

    /**
     * Performs the A* algorithm. The function will return the shortest path
     * from the start node to the end node as a list and null if a path is not
     * found.
     *
     * @param start the start node
     * @param goal the end node
     * @return the shortest path from start to end
     */
    public List<Tile> aStarSearch(Tile start, Tile goal) {
        boolean found = false;
        int total = 0;
        List<Tile> openSet = new ArrayList<>();
        Set<Tile> closedSet = new HashSet<>();
        openSet.add(start);
        while (openSet.size() > 0) {
            //1
            Tile current = openSet.get(0);
            for (int i = 1; i < openSet.size(); i++) {
                Tile tile = openSet.get(i);
                if (tile.fCost() < current.fCost()
                        || tile.fCost() == current.fCost()
                        && tile.hCost < current.hCost) {
                    current = tile;
                }
            }
            //2
            openSet.remove(current);
            closedSet.add(current);
            //3 path has been found
            if (current == goal) {
                found = true;
                System.out.println("Total steps: " + total);
                return retracePath(start, goal);
//                return found;
            }
            //4
            List<Tile> adjacent;
            adjacent = world.getAdjacentLegal(current);
            for (Tile neighbour : adjacent) {
                if (neighbour.isBlocked() || closedSet.contains(neighbour)) {
//                    System.out.println(neighbour +": "+ neighbour.isBlocked());
//                    System.out.println("continue");
                    continue;
                }
                //5
                int newMoventCostToNeighbour = current.gCost + getDistance(current, neighbour);
                if (newMoventCostToNeighbour < neighbour.gCost || !openSet.contains(neighbour)) {
                    neighbour.gCost = newMoventCostToNeighbour;
                    neighbour.hCost = getDistance(neighbour, goal);
                    neighbour.parent = current;

                    if (!openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    }
                }
            }
            total += 1;
        }
        System.out.println("Returning null, couldn't find a path!");
        System.out.println("Total steps: " + total);
        return null;
    }

    private List<Tile> retracePath(Tile start, Tile end) {
        /* A* Helper function */
//        System.out.println("Retracing path...");
        List<Tile> path = new ArrayList<>();
        Tile currentNode = end;
        while (currentNode != start) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }
        //reverse path
        Collections.reverse(path);
        //print tiles
//        for (Tile c : path) {
//            System.out.println(c);
//        }
        return path;
    }
}
