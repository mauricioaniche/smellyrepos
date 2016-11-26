package com.github.mauricioaniche.smellyrepos;

import java.io.IOException;

public class Runner {

	public static void main(String[] args) throws IOException {
		
		String projectPath = args[0];
		
		new SmellyRepoDetector(System.out).execute(projectPath);
	}
}
