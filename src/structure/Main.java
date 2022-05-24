package structure;

import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;

public class Main {
	@Option(name="-t", aliases="--time", usage="Print execution time.")
	private boolean time;
	
	@Option(name="-s", aliases="--sequential", usage="Execute in sequential mode.")
	private boolean sequential;
	
	@Option(name="-f", aliases="--file", usage="Fully qualified path and name of file.", required=true)
	private String fileName;
	
	private void doMain(final String[] arguments) throws IOException {
		final CmdLineParser parser = new CmdLineParser(this);
		if (arguments.length < 1) {
			parser.printUsage(System.out);
			System.exit(-1);
		}
		try {
			parser.parseArgument(arguments);
		} catch (CmdLineException clEx) {
			System.out.println("ERROR: Unable to parse command-line options: " + clEx);
			System.exit(-1);
		}
		
		Sudoku sudoku = SudokuParser.generateSudoku(fileName);
		long start = System.currentTimeMillis();
		
		if (sequential) {
			sudoku.solve();
		} else {
			sudoku.parallelSolve();			
		}
		
		long stop = System.currentTimeMillis();
		System.out.println("Soluzioni: " + sudoku.getLegalSolutionSpace());
		System.out.println("Spazio totale soluzioni: " + sudoku.getSolutionSpace().toString());
		System.out.println("Percentuale di celle vuote: " + sudoku.emptyPercentage() + "%");
		if (time) {
			System.out.println(String.format("Tempo: %s", stop - start) + " ms");			
		}
	}
	
	public static void main(String[] args) throws Exception {
		final Main instance = new Main();
		try {
			instance.doMain(args);
		} catch (IOException ioEx) {
			System.out.println("ERROR: I/O Exception encountered: " + ioEx);
		}
	}
}
