package com.github.mauricioaniche.smellyrepos.listeners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ParserRuleContext;

import com.github.mauricioaniche.smellyrepos.antlr.JavaBaseListener;
import com.github.mauricioaniche.smellyrepos.antlr.JavaParser;
import com.github.mauricioaniche.smellyrepos.antlr.JavaParser.FormalParameterContext;
import com.github.mauricioaniche.smellyrepos.antlr.JavaParser.GenericInterfaceMethodDeclarationContext;
import com.github.mauricioaniche.smellyrepos.antlr.JavaParser.MethodDeclarationContext;

public class DaoMethods extends JavaBaseListener {

	private Stack<String> classes;
	private Set<String> rightOnes;
	private Set<String> problematicOnes;
	private String lastModifier;
	private List<String> primitivesList;
	private Set<String> enumerators;
	private Map<String, Set<String>> subtypes;
	private boolean inner;
	
	private String genericType = null;
	private Pattern pattern;
	
	public DaoMethods(Set<String> enumerators, Map<String, Set<String>> subtypes, String regex) {
		this.enumerators = enumerators;
		this.subtypes = subtypes;
		this.pattern = Pattern.compile(regex);
		
		rightOnes = new HashSet<String>();
		problematicOnes = new HashSet<String>();
		classes = new Stack<String>();
		
		primitivesList = new ArrayList<String>();
		primitivesList.add("int");
		primitivesList.add("boolean");
		primitivesList.add("double");
		primitivesList.add("long");
		primitivesList.add("char");
		primitivesList.add("float");
		primitivesList.add("byte");
		primitivesList.add("short");
		primitivesList.add("Integer");
		primitivesList.add("Byte");
		primitivesList.add("Short");
		primitivesList.add("Double");
		primitivesList.add("Boolean");
		primitivesList.add("Long");
		primitivesList.add("Character");
		
		primitivesList.add("String");
		primitivesList.add("BigDecimal");
		primitivesList.add("Calendar");
		primitivesList.add("Date");
	}

	public Set<String> getRightOnes() {
		return rightOnes;
	}
	
	public Set<String> getProblematicOnes() {
		return problematicOnes;
	}

	@Override public void enterModifier(JavaParser.ModifierContext ctx) { 
		if(notOnMainClass()) return;
		lastModifier = ctx.getText();
	}

	@Override public void enterGenericMethodDeclaration(JavaParser.GenericMethodDeclarationContext ctx) {
		genericType = ctx.typeParameters().getText();
	}
	
	@Override public void exitGenericMethodDeclaration(JavaParser.GenericMethodDeclarationContext ctx) {
		genericType = null;
	}
	
	public void enterGenericInterfaceMethodDeclaration(GenericInterfaceMethodDeclarationContext ctx) {
		System.out.println("OI " + ctx.getText());
		super.enterGenericInterfaceMethodDeclaration(ctx);
	}
	
	@Override public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
		if(notOnMainClass()) return;
		if(notPublic()) return;
		
		String typeAsInTheCode;
		if(ctx.type()==null) typeAsInTheCode = "void";
		else typeAsInTheCode = ctx.type().getText();
		String returnType = removeGenerics(typeAsInTheCode);
		
		String methodName = FullMethodName.fullMethodName(ctx.Identifier().getText(), ctx.formalParameters().formalParameterList(), typeAsInTheCode);
		
		if(typeMatches(returnType) || parameterIsFromType(ctx) ||
				allParametersArePrimitives(ctx, returnType) ||
				isPrimitive(returnType) || isEnum(returnType) || 
				isSubtypeOrInterface(returnType) || isGenericWithManyTypes(returnType) ||
				isDTO(returnType) || genericTypeIsTheSameOfType()) {
			rightOnes.add(methodName);
		} else {
			problematicOnes.add(methodName);
		}
		
		super.enterMethodDeclaration(ctx);
	}

	private boolean genericTypeIsTheSameOfType() {
		if(genericType == null) return false;
		
		Pattern p = Pattern.compile("<(.*)(super|extends)(.*)>");
		Matcher m = p.matcher(genericType);
		m.matches();
		
		String cleanedGenericType = m.group(3);
		return typeMatches(cleanedGenericType);
	}

	private boolean notOnMainClass() {
		return classes.size()!=1 || inner;
	}

	private boolean allParametersArePrimitives(MethodDeclarationContext ctx, String returnType) {
		if(ctx.formalParameters().formalParameterList() == null) return false;

		for(FormalParameterContext param : ctx.formalParameters().formalParameterList().formalParameter()) {
			String parameterType = removeGenerics(param.type().getText());
			if(!primitivesList.contains(parameterType)) {
				return false;
			}
		}
		return true && returnIsVoid(returnType);
	}

	private boolean returnIsVoid(String returnType) {
		return "void".equals(returnType);
	}

	private boolean isDTO(String returnType) {
		return returnType.toLowerCase().contains(clazzWithoutDao().toLowerCase());
	}

	private String clazzWithoutDao() {
		Matcher matcher = pattern.matcher(clazz().toLowerCase());
		matcher.matches();
		return matcher.group(1);
	}

	private boolean isGenericWithManyTypes(String returnType) {
		return returnType.contains(",");
	}

	private String clazz() {
		return classes.peek();
	}

	private boolean isSubtypeOrInterface(String returnType) {
		Set<String> interfacesImplemented = subtypes.get(returnType);
		if(interfacesImplemented == null) return false;
		return interfacesImplemented.contains(clazzWithoutDao());
	}

	private boolean isEnum(String returnType) {
		return enumerators.contains(returnType);
	}

	private boolean isPrimitive(String returnType) {
		return primitivesList.contains(returnType);
	}

	@Override public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
		classes.add(ctx.Identifier().getText());
	}
	
	public void enterEveryRule(ParserRuleContext ctx) { 
//		System.out.println("aquii " + ctx.getText() + ctx.getClass().getName());
	}
	
	@Override public void enterClassCreatorRest(JavaParser.ClassCreatorRestContext ctx) {
		this.inner = true;
	}
	
	@Override public void exitClassCreatorRest(JavaParser.ClassCreatorRestContext ctx) { 
		this.inner = false;
	}
	
	@Override public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
		classes.pop();
	}


	private boolean typeMatches(String type) {
		return type.toLowerCase().equals(clazzWithoutDao());
	}
	
	private boolean parameterIsFromType(MethodDeclarationContext ctx) {
		if(ctx.formalParameters().formalParameterList() == null) return false;

		for(FormalParameterContext param : ctx.formalParameters().formalParameterList().formalParameter()) {
			String parameterType = removeGenerics(param.type().getText());
			if(typeMatches(parameterType)) return true;
		}
		return false;
	}

	private boolean notPublic() {
		return !"public".equals(lastModifier);
	}

	private String removeGenerics(String text) {
		int start = text.indexOf("<");
		int end = text.indexOf(">");
		if(start>0) return text.substring(start+1, end);
		return text;
	}
	
}
