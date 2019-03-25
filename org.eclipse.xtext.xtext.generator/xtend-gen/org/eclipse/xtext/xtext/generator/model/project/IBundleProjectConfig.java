/**
 * Copyright (c) 2015, 2017 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.xtext.generator.model.project;

import org.eclipse.xtext.xtext.generator.model.ManifestAccess;
import org.eclipse.xtext.xtext.generator.model.PluginXmlAccess;
import org.eclipse.xtext.xtext.generator.model.project.ISubProjectConfig;

/**
 * Configuration of subprojects that can be used as Eclipse bundles.
 * 
 * @noimplement This interface should not be implemented by clients.
 */
@SuppressWarnings("all")
public interface IBundleProjectConfig extends ISubProjectConfig {
  public abstract ManifestAccess getManifest();
  
  public abstract PluginXmlAccess getPluginXml();
}
