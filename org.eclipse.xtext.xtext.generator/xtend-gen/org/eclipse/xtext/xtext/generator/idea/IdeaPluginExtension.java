/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.xtext.generator.idea;

import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class IdeaPluginExtension {
  public Iterable<AbstractRule> getAllNonTerminalRules(final Grammar grammar) {
    final Function1<AbstractRule, Boolean> _function = (AbstractRule it) -> {
      return Boolean.valueOf((!(it instanceof TerminalRule)));
    };
    return IterableExtensions.<AbstractRule>filter(GrammarUtil.allRules(grammar), _function);
  }
  
  public String getSimpleName(final Grammar grammar) {
    return GrammarUtil.getSimpleName(grammar);
  }
  
  public String getPackageName(final Grammar grammar) {
    return GrammarUtil.getNamespace(grammar);
  }
  
  public String getLanguageID(final Grammar grammar) {
    return grammar.getName();
  }
}
