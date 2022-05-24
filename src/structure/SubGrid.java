package structure;

import java.util.HashSet;
import java.util.Set;

public class SubGrid {
	public static Set<Integer> getContent(Sudoku sudoku, int indX, int indY) {
		int origX = flat(indX);
		int origY = flat(indY);
		int[][] grid = sudoku.getGrid();
		Set<Integer> contSet = new HashSet<Integer>();
		for (int x = origX; x < origX + 3; x++) {
			for (int y = origY; y < origY + 3; y++) {
				contSet.add(grid[x][y]);
			}
		}
		contSet.remove(0);
		return contSet;
	}
	
	public static int getUnivocalIndex(int x, int y) {
		return (flat(x)) + (flat(y) / 3);
	}

	private static int flat(int x) {
		return ((int)(x / 3)) * 3;
	}
}
