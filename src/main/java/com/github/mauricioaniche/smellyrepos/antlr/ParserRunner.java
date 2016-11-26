package com.github.mauricioaniche.smellyrepos.antlr;

import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.github.mauricioaniche.smellyrepos.antlr.JavaParser.CompilationUnitContext;

public class ParserRunner {

	private JavaBaseListener listener;
	
	public ParserRunner(JavaBaseListener listener) {
		this.listener = listener;
	}
	
	public void run(InputStream f) {
		try {
			CharStream input;
			input = new ANTLRInputStream(f);
			
			JavaLexer lex = new JavaLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lex);
			JavaParser parser = new JavaParser(tokens);
			CompilationUnitContext r = parser.compilationUnit();
			
			new ParseTreeWalker().walk(listener, r);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
