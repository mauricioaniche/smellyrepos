package com.github.mauricioaniche.smellyrepos.listeners;

import java.util.HashSet;
import java.util.Set;

import com.github.mauricioaniche.smellyrepos.antlr.JavaBaseListener;
import com.github.mauricioaniche.smellyrepos.antlr.JavaParser;
import com.github.mauricioaniche.smellyrepos.antlr.JavaParser.TypeContext;

public class ClassInfo extends JavaBaseListener {

	private boolean isEnum;
	private String name;
	private Set<String> subtypes;
	private boolean hasSubtype;
	
	public ClassInfo() {
		subtypes = new HashSet<String>();
	}
	
	@Override public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
		if(name!=null) return;
		name = ctx.Identifier().getText();
		
		if(ctx.typeList()!=null) {
			hasSubtype = true;
			for(TypeContext type : ctx.typeList().type()) {
				subtypes.add(type.getText());
			}
		}
		
		if(ctx.type()!=null) {
			hasSubtype = true;
			subtypes.add(ctx.type().getText());
		}
	}

	@Override public void enterEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
		if(name!=null) return;
		
		isEnum = true;
		name = ctx.Identifier().getText();
	}
	
	public boolean isEnum() {
		return isEnum;
	}
	
	public boolean isSubtypeOrImplementsInterface() {
		return hasSubtype;
	}
	
	public Set<String> subtypeAndInterfaces() {
		return subtypes;
	}

	public String getName() {
		return name;
	}
}
