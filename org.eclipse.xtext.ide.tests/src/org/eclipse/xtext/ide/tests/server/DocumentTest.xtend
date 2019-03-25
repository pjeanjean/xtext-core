/*******************************************************************************
 * Copyright (c) 2016 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ide.tests.server

import org.eclipse.lsp4j.Position
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextEdit
import org.eclipse.xtext.ide.server.Document
import org.junit.Test

import static org.junit.Assert.*
import static org.junit.Assume.*

/**
 * @author efftinge - Initial contribution and API
 */
class DocumentTest {
    
    @Test def void testOffSet() {
        new Document(1, '''
            hello world
            foo
            bar
        '''.normalize) => [
            assertEquals(0, getOffSet(position(0,0)))
            assertEquals(11, getOffSet(position(0,11)))
            try {
                getOffSet(position(0, 12))
                fail()
            } catch (IndexOutOfBoundsException e) {
                //expected
            }
            assertEquals(12, getOffSet(position(1,0)))
            assertEquals(13, getOffSet(position(1,1)))
            assertEquals(14, getOffSet(position(1,2)))
            assertEquals(16, getOffSet(position(2,0)))
            assertEquals(19, getOffSet(position(2,3)))
        ]
    }
    
    @Test def void testOffSet_empty() {
        new Document(1, "") => [
            assertEquals(0, getOffSet(position(0,0)))
            try {
                getOffSet(position(0, 12))
                fail()
            } catch (IndexOutOfBoundsException e) {
                //expected
            }
        ]
    }
    
    @Test def void testUpdate_01() {
        new Document(1, '''
            hello world
            foo
            bar
        '''.normalize) => [
            assertEquals('''
                hello world
                bar
            '''.normalize, applyChanges(#[
                change(position(1,0), position(2,0), "")
            ]).contents)
        ]
    }
    
    @Test def void testUpdate_02() {
        new Document(1, '''
            hello world
            foo
            bar
        '''.normalize) => [
            assertEquals('''
                hello world
                future
                bar
            '''.normalize, applyChanges(#[
                change(position(1,1), position(1,3), "uture")
            ]).contents)
        ]
    }
    
    @Test def void testUpdate_03() {
        new Document(1, '''
            hello world
            foo
            bar'''.normalize) => [
            assertEquals('', applyChanges(#[
                change(position(0,0), position(2,3), "")
            ]).contents)
        ]
    }
    
    @Test def void testUpdate_nonIncrementalChange() {
        new Document(1, '''
            hello world
            foo
            bar'''.normalize) => [
            assertEquals(' foo ', applyChanges(#[
                change(null, null, " foo ")
            ]).contents)
        ]
    }
    
    @Test(expected=IndexOutOfBoundsException) def void testGetLineContent_negative() {
        new Document(1, '').getLineContent(-1);
    }

    @Test(expected=IndexOutOfBoundsException) def void testGetLineContent_exceeds() {
        new Document(1, '''
        aaa
        bbb
        ccc''').getLineContent(3);
    }

    @Test def void testGetLineContent_empty() {
        assertEquals('', new Document(1, '').getLineContent(0));
    }

    @Test def void testGetLineContent() {
    	assumeFalse(System.getProperty("os.name").startsWith("Windows")) // XXX newline issue
        assertEquals('bbb', new Document(1, '''
        aaa
        bbb
        ccc''').getLineContent(1));
    }

    @Test def void testGetLineCount_empty() {
        assertEquals(1, new Document(1, '').lineCount);
    }

    @Test def void testGetLineCount_single() {
        assertEquals(1, new Document(1, 'aaa bbb ccc').lineCount);
    }

    @Test def void testGetLineCount_multi() {
        assertEquals(3, new Document(1, '''
        aaa
        bbb
        ccc''').lineCount);
    }

    private def change(Position startPos, Position endPos, String newText) {
        new TextEdit => [
              if (startPos !== null) {
                  range = new Range => [
                      start = startPos
                      end = endPos
                  ]
              }
              it.newText = newText
            ]
    }
    
    private def normalize(CharSequence s) {
        return s.toString.replaceAll("\r", "")
    }
    
    private def position(int l, int c) {
        new Position => [line=l character=c]
    }
    
}