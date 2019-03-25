/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.xtext.wizard;

import org.eclipse.xtext.xtext.wizard.WizardConfiguration;

@SuppressWarnings("all")
public interface ProjectsCreator {
  public abstract void createProjects(final WizardConfiguration config);
}
