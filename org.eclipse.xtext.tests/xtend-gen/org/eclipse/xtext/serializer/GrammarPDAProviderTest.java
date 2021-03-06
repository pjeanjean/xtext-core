/**
 * Copyright (c) 2015, 2016 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.serializer;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.grammaranalysis.impl.GrammarElementTitleSwitch;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.analysis.IGrammarPDAProvider;
import org.eclipse.xtext.serializer.analysis.ISerState;
import org.eclipse.xtext.serializer.analysis.SerializationContextMap;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.tests.XtextInjectorProvider;
import org.eclipse.xtext.util.EmfFormatter;
import org.eclipse.xtext.util.formallang.NfaUtil;
import org.eclipse.xtext.util.formallang.Pda;
import org.eclipse.xtext.util.formallang.PdaListFormatter;
import org.eclipse.xtext.util.formallang.PdaToDot;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
@RunWith(XtextRunner.class)
@InjectWith(XtextInjectorProvider.class)
@SuppressWarnings("all")
public class GrammarPDAProviderTest {
  @Inject
  private IGrammarPDAProvider pdaProvider;
  
  @Inject
  private ParseHelper<Grammar> parser;
  
  @Inject
  private ValidationTestHelper validator;
  
  @Test
  public void testUnassignedAction() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: {Action};");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> {Action}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Action} -> stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testAssignedAction() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: {Foo} {Action.feat=current};");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> {Foo}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Action.feat=} -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Foo} -> {Action.feat=}");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testAssignedTerminalRuleCall() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: name=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> name=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("name=ID -> stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testAssignedEObjectRuleCall() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: call=Called;");
    _builder.newLine();
    _builder.append("Called: name=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> call=Called");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("call=Called -> stop");
    _builder_1.newLine();
    _builder_1.append("Called:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> name=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("name=ID -> stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testAssignedDatatypeRuleCall() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: call=Called;");
    _builder.newLine();
    _builder.append("Called: \"foo\";");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> call=Called");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("call=Called -> stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testUnassignedCalledAction() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: D1 | D2;");
    _builder.newLine();
    _builder.append("D1: \'d1\' A;");
    _builder.newLine();
    _builder.append("D2: \'d2\' A;");
    _builder.newLine();
    _builder.append("A: f1=ID {A.l=current} f2=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> >>D1, >>D2");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'d1\' -> >>A");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'d2\' -> >>A");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<A -> <<D1");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<A -> <<D2");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<D1 -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<D2 -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>A -> f1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>A -> f1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>D1 -> \'d1\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>D2 -> \'d2\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f1=ID -> {A.l=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f2=ID -> <<A, <<A");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{A.l=} -> f2=ID");
    _builder_1.newLine();
    _builder_1.append("D1:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> \'d1\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'d1\' -> >>A");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<A -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>A -> f1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f1=ID -> {A.l=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f2=ID -> <<A");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{A.l=} -> f2=ID");
    _builder_1.newLine();
    _builder_1.append("D2:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> \'d2\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'d2\' -> >>A");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<A -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>A -> f1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f1=ID -> {A.l=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f2=ID -> <<A");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{A.l=} -> f2=ID");
    _builder_1.newLine();
    _builder_1.append("A:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> f1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f1=ID -> {A.l=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f2=ID -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{A.l=} -> f2=ID");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testUnassignedFragmentRuleCall() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: Called;");
    _builder.newLine();
    _builder.append("fragment Called returns Abstract: name=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> >>Called");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<Called -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>Called -> name=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("name=ID -> <<Called");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testUnassignedDatatypeRule() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: val=ID Called;");
    _builder.newLine();
    _builder.append("Called: \'kw1\';");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> val=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("Called -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("val=ID -> Called");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testUnassignedTerminalRule() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: val=ID Called;");
    _builder.newLine();
    _builder.append("terminal Called: \'kw1\';");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> val=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("Called -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("val=ID -> Called");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testUnassignedParserRuleCall() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: Called;");
    _builder.newLine();
    _builder.append("Called returns Sub: name=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> >>Called");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<Called -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>Called -> name=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("name=ID -> <<Called");
    _builder_1.newLine();
    _builder_1.append("Called:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> name=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("name=ID -> stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testUnassignedWildcardFragmentRuleCall() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: Called;");
    _builder.newLine();
    _builder.append("fragment Called*: name=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> >>Called");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<Called -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>Called -> name=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("name=ID -> <<Called");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testGroup() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: {Rule} \'a\' \'b\' \'c\';");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> {Rule}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'a\' -> \'b\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'b\' -> \'c\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'c\' -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Rule} -> \'a\'");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testAlternative() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: {Rule} (\'a\' | \'b\' | \'c\');");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> {Rule}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'a\' -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'b\' -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'c\' -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Rule} -> \'a\', \'b\', \'c\'");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testUnorderedGroup() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: {Rule} (\'a\' & \'b\' & \'c\');");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> {Rule}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'a\' -> \'a\', \'b\', \'c\', stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'b\' -> \'a\', \'b\', \'c\', stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'c\' -> \'a\', \'b\', \'c\', stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Rule} -> \'a\', \'b\', \'c\'");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testUnorderedGroup2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: {Rule} (\'a\' & \'b\'? & \'c\'* & \'d\'+);");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> {Rule}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'a\' -> \'a\', \'b\', \'c\', \'d\', stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'b\' -> \'a\', \'b\', \'c\', \'d\', stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'c\' -> \'a\', \'b\', \'c\', \'d\', stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'d\' -> \'a\', \'b\', \'c\', \'d\', stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Rule} -> \'a\', \'b\', \'c\', \'d\'");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testTwoAssignedEObjectRuleCalls() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Rule: foo1=Sub foo2=Sub; Sub: id=\'id\';");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Rule:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> foo1=Sub");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("foo1=Sub -> foo2=Sub");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("foo2=Sub -> stop");
    _builder_1.newLine();
    _builder_1.append("Sub:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> id=\'id\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("id=\'id\' -> stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testRecursion() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Recursion: val=ID | \'(\' Recursion \')\';");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Recursion:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> \'(\', val=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'(\' -> >>Recursion");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\')\' -> <<Recursion, stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<Recursion -> \')\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>Recursion -> \'(\', val=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("val=ID -> <<Recursion, stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testExpression1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Exp: \'kw\' Addit; Addit returns Exp: Prim ({Add.left=current} \'+\' right=Prim)*; Prim returns Exp: {Val} val=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Exp:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> \'kw\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'+\' -> right=Prim");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'kw\' -> >>Addit");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<Addit -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<Prim -> <<Addit, {Add.left=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>Addit -> >>Prim");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>Prim -> {Val}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("right=Prim -> <<Addit, {Add.left=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("val=ID -> <<Prim");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Add.left=} -> \'+\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Val} -> val=ID");
    _builder_1.newLine();
    _builder_1.append("Addit:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> >>Prim");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'+\' -> right=Prim");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<Prim -> stop, {Add.left=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>Prim -> {Val}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("right=Prim -> stop, {Add.left=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("val=ID -> <<Prim");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Add.left=} -> \'+\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Val} -> val=ID");
    _builder_1.newLine();
    _builder_1.append("Prim:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> {Val}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("val=ID -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Val} -> val=ID");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testActionAlternative() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Greeting: \'(\' Greeting \')\' {Foo.child=current} | val=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("Greeting:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> \'(\', val=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'(\' -> >>Greeting");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\')\' -> {Foo.child=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<Greeting -> \')\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>Greeting -> \'(\', val=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("val=ID -> <<Greeting, stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{Foo.child=} -> <<Greeting, stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  @Ignore
  public void testFragmentWithAction() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("R: f1=ID F; fragment F returns R: {A.prev=current} f2=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("R:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> f1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<F -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>F -> {A.prev=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f1=ID -> >>F");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f2=ID -> <<F");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{A.prev=} -> f2=ID");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  @Ignore
  public void testFragmentWithAction2() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("R: \'kw1a\' f1=ID \'kw1b\' F; fragment F returns R: \'kw2a\' {A.prev=current} \'kw2b\' f2=ID \'kw2c\';");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("R:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> f1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<F -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>F -> {A.prev=}");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f1=ID -> >>F");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f2=ID -> <<F");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("{A.prev=} -> f2=ID");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testParameter1() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("M: \"kw1\" s=S<true> | \"kw2\" s=S<false>;");
    _builder.newLine();
    _builder.append("S <P>: <P> v1=ID | <!P> v2=ID;  ");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("M:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> \'kw1\', \'kw2\'");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'kw1\' -> (s=S|)");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("\'kw2\' -> (|s=S)");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("(s=S|) -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("(|s=S) -> stop");
    _builder_1.newLine();
    _builder_1.append("S<P>:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> v1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("v1=ID -> stop");
    _builder_1.newLine();
    _builder_1.append("S:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> v2=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("v2=ID -> stop");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testDoubleFragment() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("R: F1 F2;");
    _builder.newLine();
    _builder.append("fragment F1: f1=ID;  ");
    _builder.newLine();
    _builder.append("fragment F2: f2=ID;  ");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("R:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> >>F1");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<F1 -> >>F2");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<F2 -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>F1 -> f1=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>F2 -> f2=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f1=ID -> <<F1");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f2=ID -> <<F2");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testFragmentLoop() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("R: F+;");
    _builder.newLine();
    _builder.append("fragment F: f+=ID;  ");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("R:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> >>F");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<F -> >>F, stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>F -> f+=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f+=ID -> <<F");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testParameterizedDoubleDelegation() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("R: F<true> | F<false>;");
    _builder.newLine();
    _builder.append("fragment F<X>: f+=ID;");
    _builder.newLine();
    final String actual = this.toPda(_builder);
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("R:");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("start -> >>F, >>F");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<F -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("<<F -> stop");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>F -> f+=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append(">>F -> f+=ID");
    _builder_1.newLine();
    _builder_1.append("\t");
    _builder_1.append("f+=ID -> <<F, <<F");
    _builder_1.newLine();
    final String expected = _builder_1.toString();
    Assert.assertEquals(expected, actual);
  }
  
  private String toPda(final CharSequence rulesText) {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("grammar org.eclipse.xtext.serializer.GrammarPDAProviderTestLanguage with org.eclipse.xtext.common.Terminals");
      _builder.newLine();
      _builder.newLine();
      _builder.append("generate GrammarPDAProviderTest \"http://www.eclipse.org/2010/tmf/xtext/GrammarPDAProviderTestLanguage\"");
      _builder.newLine();
      _builder.newLine();
      _builder.append(rulesText);
      _builder.newLineIfNotEmpty();
      final Grammar grammar = this.parser.parse(_builder);
      this.validator.assertNoErrors(grammar);
      final SerializationContextMap<Pda<ISerState, RuleCall>> pdas = this.pdaProvider.getGrammarPDAs(grammar);
      final Consumer<SerializationContextMap.Entry<Pda<ISerState, RuleCall>>> _function = (SerializationContextMap.Entry<Pda<ISerState, RuleCall>> it) -> {
        this.assertNoLeakedGrammarElements(grammar, it.getValue());
      };
      pdas.values().forEach(_function);
      final Function1<SerializationContextMap.Entry<Pda<ISerState, RuleCall>>, List<ISerializationContext>> _function_1 = (SerializationContextMap.Entry<Pda<ISerState, RuleCall>> it) -> {
        return it.getContexts();
      };
      final Function1<ISerializationContext, String> _function_2 = (ISerializationContext it) -> {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append(it);
        _builder_1.append(":");
        _builder_1.newLineIfNotEmpty();
        _builder_1.append("\t");
        String _listString = this.toListString(pdas.get(it));
        _builder_1.append(_listString, "\t");
        _builder_1.newLineIfNotEmpty();
        return _builder_1.toString();
      };
      return IterableExtensions.join(ListExtensions.<ISerializationContext, String>map(IterableExtensions.<ISerializationContext>sort(Iterables.<ISerializationContext>concat(ListExtensions.<SerializationContextMap.Entry<Pda<ISerState, RuleCall>>, List<ISerializationContext>>map(pdas.values(), _function_1))), _function_2));
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void assertNoLeakedGrammarElements(final Grammar grammar, final Pda<ISerState, RuleCall> pda) {
    final Function1<ISerState, AbstractElement> _function = (ISerState it) -> {
      return it.getGrammarElement();
    };
    Iterable<AbstractElement> _filterNull = IterableExtensions.<AbstractElement>filterNull(IterableExtensions.<ISerState, AbstractElement>map(new NfaUtil().<ISerState>collect(pda), _function));
    for (final AbstractElement ele : _filterNull) {
      {
        final Grammar actual = GrammarUtil.getGrammar(ele);
        if ((actual != grammar)) {
          String _objPath = EmfFormatter.objPath(ele);
          String _plus = ("Element " + _objPath);
          String _plus_1 = (_plus + " leaked!");
          Assert.fail(_plus_1);
        }
      }
    }
  }
  
  protected void toDot(final Pda<ISerState, RuleCall> pda, final String name) {
    try {
      final String test = Thread.currentThread().getStackTrace()[6].getMethodName();
      new PdaToDot<Object, Object>().draw(pda, (((("dot2/" + test) + "_") + name) + ".pdf"), "-T pdf");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private String toListString(final Pda<ISerState, RuleCall> pda) {
    final GrammarElementTitleSwitch ts = new GrammarElementTitleSwitch().showAssignments().hideCardinality().showQualified();
    final PdaListFormatter<ISerState, RuleCall> formatter = new PdaListFormatter<ISerState, RuleCall>();
    final Function<ISerState, String> _function = (ISerState it) -> {
      String _switchResult = null;
      ISerState.SerStateType _type = it.getType();
      if (_type != null) {
        switch (_type) {
          case START:
            _switchResult = "start";
            break;
          case STOP:
            _switchResult = "stop";
            break;
          default:
            _switchResult = ts.apply(it.getGrammarElement());
            break;
        }
      } else {
        _switchResult = ts.apply(it.getGrammarElement());
      }
      return _switchResult;
    };
    formatter.setStateFormatter(_function);
    formatter.setStackitemFormatter(new GrammarElementTitleSwitch().showAssignments().hideCardinality());
    formatter.sortFollowers();
    String _format = formatter.format(pda);
    return (_format + "\n");
  }
}
