/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ide.server.rename

import org.eclipse.lsp4j.RenameParams
import org.eclipse.lsp4j.WorkspaceEdit
import org.eclipse.xtext.ide.server.ILanguageServerAccess
import org.eclipse.xtext.ide.server.WorkspaceManager
import org.eclipse.xtext.util.CancelIndicator
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * @author koehnlein - Initial contribution and API
 * @since 2.13
 * @deprecated use {IRenameService2} instead.
 */
@Deprecated
interface IRenameService {
	
    @Deprecated
	def WorkspaceEdit rename(WorkspaceManager workspaceManager, RenameParams renameParams, CancelIndicator cancelIndicator)
}

/**
 * Service called for rename refactoring. 
 * 
 * @author koehnlein - Initial contribution and API
 * @since 2.18
 */
interface IRenameService2 {
	
	def WorkspaceEdit rename(Options options)
	
	@Accessors
	class Options {
		ILanguageServerAccess languageServerAccess
		RenameParams renameParams
		CancelIndicator cancelIndicator
	}
}
