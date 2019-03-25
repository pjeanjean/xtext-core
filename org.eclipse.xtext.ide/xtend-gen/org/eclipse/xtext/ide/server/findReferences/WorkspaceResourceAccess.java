/**
 * Copyright (c) 2016, 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.ide.server.findReferences;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.findReferences.IReferenceFinder;
import org.eclipse.xtext.ide.server.Document;
import org.eclipse.xtext.ide.server.WorkspaceManager;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function2;

/**
 * @author kosyakov - Initial contribution and API
 * @since 2.11
 */
@FinalFieldsConstructor
@SuppressWarnings("all")
public class WorkspaceResourceAccess implements IReferenceFinder.IResourceAccess {
  private final WorkspaceManager workspaceManager;
  
  @Override
  public <R extends Object> R readOnly(final URI targetURI, final IUnitOfWork<R, ResourceSet> work) {
    final Function2<Document, XtextResource, R> _function = (Document document, XtextResource resource) -> {
      try {
        if ((resource == null)) {
          return null;
        }
        return work.exec(resource.getResourceSet());
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    return this.workspaceManager.<R>doRead(targetURI, _function);
  }
  
  public WorkspaceResourceAccess(final WorkspaceManager workspaceManager) {
    super();
    this.workspaceManager = workspaceManager;
  }
}
