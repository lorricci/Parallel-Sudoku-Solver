package structure;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SudokuParser {

	public static Sudoku generateSudoku(String path) {
		int[][] grid = new int[9][9];
		ArrayList<int[]> empty = new ArrayList<>();
		boolean[][] rowContent = new boolean[9][10];
		boolean[][] colContent = new boolean[9][10];
		boolean[][] subgridContent = new boolean[9][10];
		init(rowContent);
		init(colContent);
		init(subgridContent);
		int row = 0;
		int col = 0;
		try(BufferedReader br = Files.newBufferedReader(Paths.get(path))){
			while(br.ready()){
				char ch = (char) br.read();
				switch (ch) {
					case '.':
						grid[row][col] = 0; 
						empty.add(new int[] {row, col});
						col++;
						break;
					case '\r': 
						break;
					case '\n':
						row++;
						col = 0;
						break;
					default :
						int value = Integer.parseInt(ch + "");
						grid[row][col] = value;
						rowContent[row][value] = false;
						colContent[col][value] = false;
						subgridContent[SubGrid.getUnivocalIndex(row, col)][value] = false;
						col++;
				}
			}
		} catch (NoSuchFileException e) {
			System.out.println("File not found.");
			System.exit(-1);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new Sudoku(grid, empty, rowContent, colContent, subgridContent);
	}
	
	public static void init(boolean[][] matrix) {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 10; y++) {
				matrix[x][y] = true;
			}
		}
	}
}
