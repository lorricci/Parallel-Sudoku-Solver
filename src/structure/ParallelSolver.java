package structure;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class ParallelSolver extends RecursiveTask<Integer> {
	private int solSpace;
	private static final int SEQ_CUTOFF = 10;
	private static final long serialVersionUID = 1L;
	private ArrayList<int[]> emptyCells;
	private boolean[][] rowContent;
	private boolean[][] colContent;
	private boolean[][] subgridContent;
	private int n;
	private int index;
	private boolean[][] rowCopy;
	private boolean[][] colCopy;
	private boolean[][] subgridCopy;
	
	
	public ParallelSolver(ArrayList<int[]> emptyCells, boolean[][] rowContent, boolean[][] colContent, boolean[][] subgridContent, int index) {
		super();
		this.emptyCells = emptyCells;
		this.rowContent = rowContent;
		this.colContent = colContent;
		this.subgridContent = subgridContent;
		this.n = emptyCells.size();
		this.index = index;
		this.solSpace = 0;
	}

	public static boolean[][] deepCopy(boolean[][] original) {
	    if (original == null) {
	        return null;
	    }
	    final boolean[][] result = new boolean[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        result[i] = original[i].clone();
	    }
	    return result;
	}
	
	private void setValue(int x, int y, int value) {
		rowCopy = deepCopy(rowContent);
		colCopy = deepCopy(colContent);
		subgridCopy = deepCopy(subgridContent);
		rowCopy[x][value] = false;
		colCopy[y][value] = false;
		subgridCopy[SubGrid.getUnivocalIndex(x, y)][value] = false;
	}
	
	private void setValue(int[] coord, int value) {
		setValue(coord[0], coord[1], value);
	}
	
	
	private void setValueDirectly(int[] coord, int value) {
		rowContent[coord[0]][value] = false;
		colContent[coord[1]][value] = false;
		subgridContent[SubGrid.getUnivocalIndex(coord[0], coord[1])][value] = false;
	}
	
	private void resetValue(int x, int y, int value) {
		rowContent[x][value] = true;
		colContent[y][value] = true;
		subgridContent[SubGrid.getUnivocalIndex(x, y)][value] = true;
	}

	private void resetValue(int[] coord, int value) {
		resetValue(coord[0], coord[1], value);
	}
	
	private boolean isValid(int[] coord, int val) {
		return isNotInRow(coord[0], val) &&
			isNotInColumn(coord[1], val) &&
			isNotInSubgrid(coord[0], coord[1], val);
	}
	public boolean isNotInRow(int x, int val) {
		return rowContent[x][val];
	}

	public boolean isNotInColumn(int y, int val) {
		return colContent[y][val];
	}

	public boolean isNotInSubgrid(int x, int y, int val) {
		return subgridContent[SubGrid.getUnivocalIndex(x, y)][val];
	}

	@Override
	protected Integer compute() {
		if (index == n) {
			//System.out.println(this);
			return 1;
		}
		if (index >= ParallelSolver.SEQ_CUTOFF) {
			solveDirectly(n, index);
			return solSpace;
		}
		int[] current = this.emptyCells.get(index);
		ArrayList<ParallelSolver> solveArr = new ArrayList<>();
		for (int val = 1; val < 10; val++) {
			if (isValid(current, val)) {
				setValue(current, val);
				ParallelSolver solve = new ParallelSolver(emptyCells,
														  rowCopy, 
														  colCopy,
														  subgridCopy,
														  index + 1);
				solve.fork();
				solveArr.add(solve);
			}
		}
		solSpace += solveArr.stream().map(x -> x.join()).reduce(0, (a,b) -> a + b);
		return solSpace;
	}
	
	
	public void solveDirectly(int n, int index) {
		if (index == n) {
			solSpace++;
			return;
		}
		int[] current = this.emptyCells.get(index);
		for (int val = 1; val < 10; val++) {
			if (isValid(current, val)) {
				setValueDirectly(current, val);
				this.solveDirectly(n, index + 1);
				resetValue(current, val);
			}
		}
	}
}
