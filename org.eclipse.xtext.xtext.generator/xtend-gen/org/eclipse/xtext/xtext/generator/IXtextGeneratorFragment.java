/**
 * Copyright (c) 2015, 2017 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.xtext.generator;

import org.eclipse.xtext.xtext.generator.IGuiceAwareGeneratorComponent;
import org.eclipse.xtext.xtext.generator.Issues;

/**
 * A fragment that contributes to the {@link XtextGenerator}. Use {@link AbstractXtextGeneratorFragment}
 * for implementing new fragments.
 */
@SuppressWarnings("all")
public interface IXtextGeneratorFragment extends IGuiceAwareGeneratorComponent {
  public abstract void checkConfiguration(final Issues issues);
  
  public abstract void generate();
}
