package com.github.mauricioaniche.smellyrepos;

import java.util.Set;

public class SmellyRepo {

	private String fileName;
	private Set<String> problematic;
	private Set<String> nonProblematic;
	public SmellyRepo(String fileName, Set<String> problematic, Set<String> nonProblematic) {
		this.fileName = fileName;
		this.problematic = problematic;
		this.nonProblematic = nonProblematic;
	}
	public String getFileName() {
		return fileName;
	}
	public Set<String> getProblematic() {
		return problematic;
	}
	public Set<String> getNonProblematic() {
		return nonProblematic;
	}

	
	
	
}
