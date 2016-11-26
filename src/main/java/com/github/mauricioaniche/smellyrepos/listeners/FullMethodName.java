package com.github.mauricioaniche.smellyrepos.listeners;

import com.github.mauricioaniche.smellyrepos.antlr.JavaParser.FormalParameterListContext;


public class FullMethodName {

	
	public static String fullMethodName(String name, FormalParameterListContext parameters, String returnType) {
		return name + "/" + (parameters == null ? "0" : (parameters.formalParameter().size() + typesIn(parameters))) + ":" + returnType;
	}

	private static String typesIn(FormalParameterListContext parameters) {
		StringBuilder types = new StringBuilder();
		types.append("[");
		
		for(int i = 0; i < parameters.formalParameter().size(); i++) {
			String type = parameters.formalParameter().get(i).type().getText();
			types.append(type + ",");
		}
		
		return types.substring(0, types.length() - 1) + "]";
	}

}
