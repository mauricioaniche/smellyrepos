package com.github.mauricioaniche.smellyrepos.listeners;

import com.github.mauricioaniche.smellyrepos.antlr.JavaParser.FormalParameterListContext;
import com.github.mauricioaniche.smellyrepos.antlr.JavaParser.MethodDeclarationContext;


public class FullMethodName {

	
	public static String fullMethodName(String name, FormalParameterListContext parameters) {
		return name + "/" + (parameters == null ? "0" : (parameters.formalParameter().size() + typesIn(parameters)));
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

	public static String fullMethodName(MethodDeclarationContext ctx) {
		return FullMethodName.fullMethodName(ctx.Identifier().getText(), ctx.formalParameters().formalParameterList());
	}

}
