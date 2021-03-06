/*******************************************************************************
 * Copyright (c) 2015, 2018 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xtext.wizard;

public enum Scope {
	COMPILE("compile", "compile"),
	TESTCOMPILE("test", "testCompile"),
	TESTRUNTIME("test", "testRuntime"),
	PROVIDED("provided", "providedCompile");

	private String mavenNotation;
	private String gradleNotation;

	private Scope(String mavenNotation, String gradleNotation) {
		this.mavenNotation = mavenNotation;
		this.gradleNotation = gradleNotation;
	}

	public String getMavenNotation() {
		return mavenNotation;
	}

	public String getGradleNotation() {
		return gradleNotation;
	}
}
