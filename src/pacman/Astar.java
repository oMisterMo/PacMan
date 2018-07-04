package pacman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 12-Jun-2018, 18:34:20.
 *
 * @author Mo
 */
public class Astar {

    private World world;

    public Astar(World world) {
        this.world = world;
    }

    /**
     * When you can move diagonally
     *
     * @param nodaA
     * @param nodeB
     * @return
     */
    private int getDistance(Tile nodaA, Tile nodeB, int nothingHere) {
        int dx = Math.abs(nodaA.grid.x - nodeB.grid.x);
        int dy = Math.abs(nodaA.grid.y - nodeB.grid.y);

        //D * (dx + dy) + (D2 - 2 * D) * min(dx, dy) (same thing)
        if (dx > dy) {
            return 14 * dy + 10 * (dx - dy);
        }
        return 14 * dx + 10 * (dy - dx);
    }

    /**
     * When you can move UP, DOWN, LEFT, RIGHT (Manhattan distance)
     *
     * @param nodaA
     * @param goal
     * @return
     */
    private int getDistance(Tile nodaA, Tile goal) {
        int dx = Math.abs(nodaA.grid.x - goal.grid.x);
        int dy = Math.abs(nodaA.grid.y - goal.grid.y);
        //D * (dx + dy)
        return 10 * (dy + dx);
    }

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

    public List<Tile> retracePath(Tile start, Tile end) {
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
