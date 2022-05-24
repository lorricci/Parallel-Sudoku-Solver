package structure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;

/**
 * Struttura la griglia generale del sudoku. Contiene i metodi di controllo di riempimento delle celle 
 * e di calcolo dello spazio di soluzione.
 *
 */
public class Sudoku {
	static final ForkJoinPool pool = new ForkJoinPool();
	
	/**
	 * Spazio delle soluzioni dell'intero sudoku.
	 */
	private int legalSolutionSpace = 0;
	
	/**
	 * Griglia 9x9 del sudoku.
	 */
	private int mainGrid[][];
	
	/**
	 * Insieme di celle vuote. Ottimizza la ricerca delle celle vuote
	 * nel Sudoku senza controllare tutta la griglia ad ogni step.
	 */
	private ArrayList<int[]> emptyCells;

	/**
	 * Matrice che mantiene i numeri che sono presenti in ogni riga.
	 * Esempio: se rowContent[0][3] = false allora alla riga 0 è 
	 * presente il valore 3
	 */
	private boolean[][] rowContent;
	
	/**
	 * Matrice che mantiene i numeri che sono presenti in ogni colonna.
	 */
	private boolean[][] colContent;
	
	/**
	 * Matrice che mantiene i numeri che sono presenti in ogni sottogriglia.
	 */
	private boolean[][] subgridContent;
	
		
	/**
	 * Crea un nuovo sudoku con la griglia passata in input e calcola lo spazio di
	 * soluzione totale dalla configurazione iniziale.
	 * @param grid Griglia di gioco.
	 */
	public Sudoku(int[][] grid, ArrayList<int[]> emptyCells, boolean[][] rowContent, boolean[][] colContent, boolean[][] subgridContent) {
		this.mainGrid = grid;
		this.emptyCells = emptyCells;
		this.rowContent = rowContent;
		this.colContent = colContent;
		this.subgridContent = subgridContent;
		Collections.reverse(emptyCells);
	}
	
	/**
	 * Immette il valore nella griglia principale e modifica i valori
	 * delle varie matrici.
	 * @param x riga
	 * @param y colonna
	 * @param value valore da immettere
	 */
	private void setValue(int x, int y, int value) {
		mainGrid[x][y] = value;
		rowContent[x][value] = false;
		colContent[y][value] = false;
		subgridContent[SubGrid.getUnivocalIndex(x, y)][value] = false;
	}
	/**
	 * {@inheritDoc}
	 * @param coord coordinate della cella
	 * @param value valore da immettere
	 */
	private void setValue(int[] coord, int value) {
		setValue(coord[0], coord[1], value);
	}
	
	/**
	 * Resetta il valore della cella
	 * @param x riga
	 * @param y colonna
	 * @param value valore immesso in precedenza
	 */
	private void resetValue(int x, int y, int value) {
		rowContent[x][value] = true;
		colContent[y][value] = true;
		subgridContent[SubGrid.getUnivocalIndex(x, y)][value] = true;
	}

	private void resetValue(int[] coord, int value) {
		resetValue(coord[0], coord[1], value);
	}

	@Override
	public String toString() {
		String display = "╔═══════════╦═══════════╦═══════════╗\n";
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				display += x == 0 ? "║ " : (x % 3) == 0  ? " ║ " : " │ ";					
				display += mainGrid[x][y];
			}
			display += " ║" +  "\n";
			if (y % 3 == 2 && y != 8) {
				display += "╠═══════════╬═══════════╬═══════════╣" + "\n";
			}
		}
		display += "╚═══════════╩═══════════╩═══════════╝";
		return display;
	}
	
	/**
	 * Restituisce la griglia del Sudoku.
	 * @return Griglia attuale del sudoku.
	 */
	public int[][] getGrid() {
		return mainGrid;
	}

	public int getLegalSolutionSpace() {
		return legalSolutionSpace;
	}
	
	public void parallelSolve() {
		ParallelSolver solve = new ParallelSolver(emptyCells, rowContent, colContent, subgridContent, 0);
		this.legalSolutionSpace = pool.invoke(solve);
	}
	
	public void solve(int n, int index) {
		if (index == n) {
			//System.out.println(this);
			legalSolutionSpace++;
			return;
		}
		int[] current = this.emptyCells.get(index);
		for (int val = 1; val < 10; val++) {
			if (isValid(current, val)) {
				setValue(current, val);
				this.solve(n, index + 1);
				resetValue(current, val);
			}
		}
	}
	
	public void solve() {
		solve(emptyCells.size(), 0);
	}

	/**
	 * Verifica se un valore si può immettere in una cella
	 * @param coord coordinate della cella
	 * @param val valore da immetere
	 * @return booleano che rappresenta se il valore è valido o no
	 */
	private boolean isValid(int[] coord, int val) {
		return isNotInRow(coord[0], val) &&
			isNotInColumn(coord[1], val) &&
			isNotInSubgrid(coord[0], coord[1], val);
	}
	
	/**
	 * Verifica se il valore è presente nella riga
	 * @param x riga
	 * @param val valore
	 * @return leggi sopra
	 */
	public boolean isNotInRow(int x, int val) {
		return rowContent[x][val];
	}
	
	/**
	 * Verifica se il valore è presente nella colonna
	 * @param y colonna
	 * @param val valore
	 * @return questo campo è inutile per i booleani
	 */
	public boolean isNotInColumn(int y, int val) {
		return colContent[y][val];
	}
	
	/**
	 * Verifica se il valore è presente nella sottogriglia
	 * @param x riga
	 * @param y colonna
	 * @param val valore
	 * @return XD
	 */
	public boolean isNotInSubgrid(int x, int y, int val) {
		return subgridContent[SubGrid.getUnivocalIndex(x, y)][val];
	}
	
	/**
	 * Trova tutte le soluzioni legali e non, data una configurazione del sudoku
	 * @return soluzioni totali
	 */
	public BigInteger getSolutionSpace() {
		BigInteger solSpace = BigInteger.valueOf(1);
		for (int[] x : emptyCells) {
			int c = 0;
			for (int val = 0; val < 10; val++) {
				if (isValid(x, val)) {
					c++;
				}
			}
			solSpace = solSpace.multiply(BigInteger.valueOf(c));
		}
		return solSpace;
	}
	
	/**
	 * Calcola il rapporto tra celle vuote e celle totali
	 * @return rapporto tra celle vuote e quelle totali
	 */
	public float emptyRatio() {
		return emptyCells.size() / 81f;
	}
	/**
	 * Calcola la percentuale approssimata di celle vuote
	 * @return percentuale di celle vuote
	 */
	public int emptyPercentage() {
		return (int) Math.floor(emptyRatio() * 100);
	}
}
