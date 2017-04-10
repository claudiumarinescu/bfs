import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 * Proiectarea Algoritmilor
 * Lab 3:  BFS
 * Task 1: Help me escape!
 */

enum CellType {
	FREE, OBSTACLE, EXIT
}

class Cell {
	int x;
	int y;
	CellType type;
}

class Room {

	/* Dimension of the matrix corresponding to the map of the room */
	int dim;

	/* The map of the room */
	CellType[][] map;

	/* Starting point from where you should calculate the minimum distance
	 * to an exit point on the map */
	Cell startPoint = new Cell();

	/**
	 * Read and parse the data from the input file
	 * @param filename
	 */
	void loadRoomFromFile(String filename) {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(filename));

			dim = sc.nextInt();
			map = new CellType[dim][];
			for (int row = 0; row < dim; row++) {
				map[row] = new CellType[dim];
			}

			for (int row = 0; row < dim; row++) {
				for (int col = 0; col < dim; col++) {
					map[row][col] = CellType.OBSTACLE;
					int val = sc.nextInt();
					if (val == 0) {
						map[row][col] = CellType.FREE;
					} else if (val == 2) {
						map[row][col] = CellType.EXIT;
					}
				}
			}
			startPoint.x = sc.nextInt() - 1;
			startPoint.y = sc.nextInt() - 1;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (sc != null) {
					sc.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}

public class P1 {

	Room room;

	/* Holds the optimal path to an exit on the map */
	List<Cell> solution;

	/* Auxiliary matrix to hold the min distances between the starting point
	 * and the current point on the map */
	int[][] distances;

	/* Queue used in the BFS algorithm */
	Queue<Cell> q;

	P1(String loadFile) {
		room = new Room();
		room.loadRoomFromFile(loadFile);

		solution = new ArrayList<>();
		distances = new int[room.dim][];
		for (int row = 0; row < room.dim; row++) {
			distances[row] = new int[room.dim];
		}
		for (int row = 0; row < room.dim; row++) {
			for (int col = 0; col < room.dim; col++) {
				distances[row][col] = Integer.MAX_VALUE;
			}
		}
		q = new LinkedList<>();
	}
	
	/*
	 * Return the neighbours of Cell c that are also
	 * valid choices for next move
	 */
	List<Cell> getNeighbours(Cell c) {
		List<Cell> n = new ArrayList<Cell>();
		
		if (c.x > 0) {
			if (room.map[c.x - 1][c.y] != CellType.OBSTACLE) {
				Cell temp = new Cell();
				temp.x = c.x - 1;
				temp.y = c.y;
				temp.type = room.map[c.x - 1][c.y];
				n.add(temp);
			}
		}
		if (c.x < room.dim - 1) {
			if (room.map[c.x + 1][c.y] != CellType.OBSTACLE) {
				Cell temp = new Cell();
				temp.x = c.x + 1;
				temp.y = c.y;
				temp.type = room.map[c.x + 1][c.y];
				n.add(temp);
			}
		}
		if (c.y < room.dim - 1) {
			if (room.map[c.x][c.y + 1] != CellType.OBSTACLE) {
				Cell temp = new Cell();
				temp.x = c.x;
				temp.y = c.y + 1;
				temp.type = room.map[c.x][c.y + 1];
				n.add(temp);
			}
		}
		if (c.y > 0) {
			if (room.map[c.x][c.y - 1] != CellType.OBSTACLE) {
				Cell temp = new Cell();
				temp.x = c.x;
				temp.y = c.y - 1;
				temp.type = room.map[c.x][c.y - 1];
				n.add(temp);
			}
		}
		
		return n;
	}

	/**
	 * Find the minimum path in terms of cell walked between a starting point
	 * and an exit on the map of the Room
	 */
	void findMinPathValue() {
		//TODO
		// apply the breadth first search algorithm
		// use the Room to handle the map of the room and the starting point
		
		List<Cell> visited = new ArrayList<Cell>();
		distances[room.startPoint.x][room.startPoint.y] = 0;
		q.add(room.startPoint);
		visited.add(room.startPoint);
		
		while (!q.isEmpty()) {
			Cell p = q.poll();
			List<Cell> children = getNeighbours(p);
			if (p.type == CellType.EXIT) {
				solution.add(p);
				rebuildMinPath(p);
				return;
			}
			
			int found = 0;
			for (Cell t : children) {
				found = 0;
				for (Cell v : visited) {
					if ((v.x == t.x) && (v.y == t.y)) {
						found = 1;
						break;
					}
				}
				if (found == 0) {
					distances[t.x][t.y] = distances[p.x][p.y] + 1;
					visited.add(t);
					q.add(t);
				}
			}
		}
	}

	void printDistances() {
		for (int i=0; i<room.dim; i++) {
			for (int j=0; j<room.dim; j++) {
				if (distances[i][j] == Integer.MAX_VALUE) {
					System.out.print("inf ");
				}
				else
					System.out.print(distances[i][j] + "   ");
			}
			System.out.println();
		}
		System.out.println("\n");
	}
	
	void rebuildMinPath(Cell p) {
		//TODO
		// using the distances matrix, rebuild the optimal path between the
		// starting point and the found exit
		// save the path in solution
		// CORNER CASE 1: what if there is no path to any of the exits?
		
		if (distances[p.x][p.y] == 0) {
			return;
		}
		
		List<Cell> c = getNeighbours(p);
		for (Cell t : c) {
			if ((distances[t.x][t.y] + 1) == distances[p.x][p.y]) {
				solution.add(t);
				rebuildMinPath(t);
				break;
			}
		}
		
	}

	void afisare(int n) {
		if (n == solution.size()) {
			return;
		}
		afisare(n+1);
		Cell t = solution.get(n);
		System.out.print("(" + (t.x+1) + ", " + (t.y+1) + ") ");
	}
	
	
	void showMinPath() {
		
		findMinPathValue();
		
		//TODO
		// print the path
		if (solution.isEmpty()) {
			System.out.println("No solution found :(");
		}
		else {
			afisare(0);
			System.out.println();
		}
		
	}

	public static void main(String[] argv) {
		// three tests to pass
		String[] testFiles = { "room1.in", "room2.in", "room3.in" };

		//String[] testFiles = { "room3.in" };
		P1[] p = new P1[testFiles.length];
		for (int test = 0; test < testFiles.length; test++) {
			System.out.print("Test " + (test + 1) + ": ");
			p[test] = new P1(testFiles[test]);
			p[test].showMinPath();
		}
	}
}
