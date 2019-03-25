/**
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.ide.tests.testlanguage;

import org.eclipse.xtext.ide.tests.testlanguage.TestLanguageStandaloneSetupGenerated;

/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
@SuppressWarnings("all")
public class TestLanguageStandaloneSetup extends TestLanguageStandaloneSetupGenerated {
  public static void doSetup() {
    new TestLanguageStandaloneSetup().createInjectorAndDoEMFRegistration();
  }
}
