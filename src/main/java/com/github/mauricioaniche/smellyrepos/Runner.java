package com.github.mauricioaniche.smellyrepos;

import java.io.IOException;
import java.io.PrintStream;

public class Runner {

	public static void main(String[] args) throws IOException {
		
		if(args.length < 2) {
			System.out.println("You should pass <project path> <repo regex> <output file>");
			System.exit(-1);
		}
		
		String projectPath = args[0];
		String regex = args[1];
		
		PrintStream ps = System.out;
		if(args.length == 3) ps = new PrintStream(args[2]);
		
		new SmellyRepoDetector(ps, regex).execute(projectPath);
	}
}
