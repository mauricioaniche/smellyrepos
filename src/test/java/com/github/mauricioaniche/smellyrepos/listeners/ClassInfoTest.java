package com.github.mauricioaniche.smellyrepos.listeners;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.mauricioaniche.smellyrepos.antlr.ParserRunner;
import com.github.mauricioaniche.smellyrepos.listeners.ClassInfo;

public class ClassInfoTest {

	private ClassInfo classInfo;
	@Before
	public void setUp() {
		classInfo = new ClassInfo();
	}
	
	@Test
	public void shouldIdentifyIfItIsAEnum() {
		String dao = 
				  "enum PaymentInfo {"
				+ "A,B,C;"
				+ "}";
		
		new ParserRunner(classInfo).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertTrue(classInfo.isEnum());
		Assert.assertEquals("PaymentInfo",classInfo.getName());
	}
	
	@Test
	public void shouldIdentifyIfClassHasNoInterfaceImplementations() {
		String dao = 
				  "class Payment {"
				+ "public void x() {}"
				+ "}";
		
		new ParserRunner(classInfo).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertFalse(classInfo.isEnum());
		Assert.assertFalse(classInfo.isSubtypeOrImplementsInterface());
		Assert.assertEquals("Payment", classInfo.getName());
	}
	
	@Test
	public void shouldIdentifyInterfacesImplemented() {
		String dao = 
				"class Payment implements A {"
						+ "public void x() {}"
						+ "}";
		
		new ParserRunner(classInfo).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertTrue(classInfo.isSubtypeOrImplementsInterface());
		Assert.assertEquals("Payment", classInfo.getName());
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("A"));
	}
	@Test
	public void shouldIdentifyInheritanceImplemented() {
		String dao = 
				"class Payment extends A {"
						+ "public void x() {}"
						+ "}";
		
		new ParserRunner(classInfo).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertTrue(classInfo.isSubtypeOrImplementsInterface());
		Assert.assertEquals("Payment", classInfo.getName());
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("A"));
	}
	
	@Test
	public void shouldIdentifyMultipleInterfacesImplemented() {
		String dao = 
				"class Payment implements A,B,C {"
						+ "public void x() {}"
						+ "}";
		
		new ParserRunner(classInfo).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertTrue(classInfo.isSubtypeOrImplementsInterface());
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("A"));
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("B"));
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("C"));
	}
	@Test
	public void shouldIdentifyMultipleInterfacesImplementedAndInheritance() {
		String dao = 
				"class Payment extends Father implements A,B,C {"
						+ "public void x() {}"
						+ "}";
		
		new ParserRunner(classInfo).run(new ByteArrayInputStream(dao.getBytes()));
		
		Assert.assertTrue(classInfo.isSubtypeOrImplementsInterface());
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("A"));
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("Father"));
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("B"));
		Assert.assertTrue(classInfo.subtypeAndInterfaces().contains("C"));
	}
}
