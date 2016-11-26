package com.github.mauricioaniche.smellyrepos;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.github.mauricioaniche.smellyrepos.antlr.ParserRunner;
import com.github.mauricioaniche.smellyrepos.listeners.ClassInfo;
import com.github.mauricioaniche.smellyrepos.listeners.DaoMethods;

public class SmellyRepoDetector {

	private static Logger log = Logger.getLogger(SmellyRepoDetector.class);
	private Set<String> enumerators;
	private Map<String, Set<String>> subtypes;
	private JavaFilesFinder files;
	private Map<String, SmellyRepo> repos;
	private PrintStream ps;
	
	public SmellyRepoDetector(PrintStream ps) {
		this.ps = ps;
		enumerators = new HashSet<String>();
		subtypes = new HashMap<String, Set<String>>();
		this.repos = new HashMap<String, SmellyRepo>();
	}
	
	public void execute(String projectPath) {
		files = new JavaFilesFinder(projectPath);
		
		extractTypeInfo();
		detectSmells();
		printDetailedReport();
		printSummary();
		
	}

	private void printSummary() {
		int problematicMethods = repos.values().stream().mapToInt(x -> x.getNonProblematic().size()).sum();
		int okMethods = repos.values().stream().mapToInt(x -> x.getProblematic().size()).sum();
		int totalMethods = problematicMethods + okMethods;
		double pct = (double) problematicMethods / (double) totalMethods;
		
		ps.println("-------");
		ps.println("Summary");
		ps.println("-------");
		ps.println("Total repos        : " + repos.size());
		ps.println("Total methods      : " + totalMethods);
		ps.println("Problematic methods: " + problematicMethods + "(" + pct + ")");
		
	}

	private void printDetailedReport() {
		for(SmellyRepo sr : repos.values()) {
			if(sr.getProblematic().isEmpty()) continue;
			
			ps.println(sr.getFileName());
			for(String problematicMethod : sr.getProblematic()) {
				ps.println("- " + problematicMethod);
			}
			ps.println();
		}
	}

	private void detectSmells() {
		for(File f : files.getAllDaoFiles()) {
			try {
				DaoMethods allMethods = new DaoMethods(enumerators, subtypes);
				new ParserRunner(allMethods).run(new FileInputStream(f));
				
				repos.put(f.getCanonicalPath(), new SmellyRepo(f.getCanonicalPath(), allMethods.getProblematicOnes(), allMethods.getRightOnes()));
			} catch (Exception e) {
				System.err.println("problem in " + f.getPath());
				e.printStackTrace();
			}
		}
	}

	private void extractTypeInfo() {
		for(File f : files.getAllProductionFiles()) {
			try {
				log.info("Extracting type from " + f.getCanonicalPath());
				ClassInfo info = new ClassInfo();
				new ParserRunner(info).run(new FileInputStream(f));
				
				if(info.isEnum()) enumerators.add(info.getName());
				if(info.isSubtypeOrImplementsInterface()) {
					subtypes.put(info.getName(), new HashSet<String>());
					for(String interfaceName : info.subtypeAndInterfaces()) {
						subtypes.get(info.getName()).add(interfaceName);
					}
				}
			} catch (Exception e) {
				System.err.println("problem in " + f.getPath());
				e.printStackTrace();
			}
		}
	}

	
}
