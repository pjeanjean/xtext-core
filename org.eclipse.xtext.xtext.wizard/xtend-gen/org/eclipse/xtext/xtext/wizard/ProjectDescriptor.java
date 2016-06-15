/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.xtext.wizard;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.util.JavaVersion;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xtext.wizard.AbstractFile;
import org.eclipse.xtext.xtext.wizard.BinaryFile;
import org.eclipse.xtext.xtext.wizard.EPackageInfo;
import org.eclipse.xtext.xtext.wizard.Ecore2XtextConfiguration;
import org.eclipse.xtext.xtext.wizard.ExternalDependency;
import org.eclipse.xtext.xtext.wizard.GradleBuildFile;
import org.eclipse.xtext.xtext.wizard.Outlet;
import org.eclipse.xtext.xtext.wizard.ParentProjectDescriptor;
import org.eclipse.xtext.xtext.wizard.PlainTextFile;
import org.eclipse.xtext.xtext.wizard.PomFile;
import org.eclipse.xtext.xtext.wizard.ProjectLayout;
import org.eclipse.xtext.xtext.wizard.SourceLayout;
import org.eclipse.xtext.xtext.wizard.TextFile;
import org.eclipse.xtext.xtext.wizard.WizardConfiguration;

@FinalFieldsConstructor
@Accessors
@SuppressWarnings("all")
public abstract class ProjectDescriptor {
  private final WizardConfiguration config;
  
  private boolean enabled;
  
  public String getName() {
    String _baseName = this.config.getBaseName();
    String _nameQualifier = this.getNameQualifier();
    return (_baseName + _nameQualifier);
  }
  
  public abstract String getNameQualifier();
  
  public String getLocation() {
    String _xifexpression = null;
    ProjectLayout _projectLayout = this.config.getProjectLayout();
    boolean _equals = Objects.equal(_projectLayout, ProjectLayout.FLAT);
    if (_equals) {
      String _rootLocation = this.config.getRootLocation();
      String _plus = (_rootLocation + "/");
      String _name = this.getName();
      _xifexpression = (_plus + _name);
    } else {
      ParentProjectDescriptor _parentProject = this.config.getParentProject();
      String _location = _parentProject.getLocation();
      String _plus_1 = (_location + "/");
      String _name_1 = this.getName();
      _xifexpression = (_plus_1 + _name_1);
    }
    return _xifexpression;
  }
  
  public Set<? extends ProjectDescriptor> getUpstreamProjects() {
    return CollectionLiterals.<ProjectDescriptor>emptySet();
  }
  
  public Set<String> getSourceFolders() {
    final Function1<Outlet, String> _function = (Outlet it) -> {
      return this.sourceFolder(it);
    };
    List<String> _map = ListExtensions.<Outlet, String>map(Collections.<Outlet>unmodifiableList(CollectionLiterals.<Outlet>newArrayList(Outlet.MAIN_JAVA, Outlet.MAIN_RESOURCES, Outlet.MAIN_SRC_GEN, Outlet.MAIN_XTEND_GEN)), _function);
    return IterableExtensions.<String>toSet(_map);
  }
  
  public Iterable<? extends AbstractFile> getFiles() {
    final List<TextFile> files = CollectionLiterals.<TextFile>newArrayList();
    boolean _isEclipsePluginProject = this.isEclipsePluginProject();
    if (_isEclipsePluginProject) {
      String _manifest = this.manifest();
      PlainTextFile _file = this.file(Outlet.META_INF, "MANIFEST.MF", _manifest);
      files.add(_file);
      CharSequence _buildProperties = this.buildProperties();
      PlainTextFile _file_1 = this.file(Outlet.ROOT, "build.properties", _buildProperties);
      files.add(_file_1);
    }
    boolean _isEclipseFeatureProject = this.isEclipseFeatureProject();
    if (_isEclipseFeatureProject) {
      CharSequence _buildProperties_1 = this.buildProperties();
      PlainTextFile _file_2 = this.file(Outlet.ROOT, "build.properties", _buildProperties_1);
      files.add(_file_2);
    }
    if ((this.config.needsGradleBuild() && this.isPartOfGradleBuild())) {
      GradleBuildFile _buildGradle = this.buildGradle();
      files.add(_buildGradle);
    }
    if ((this.config.needsMavenBuild() && this.isPartOfMavenBuild())) {
      PomFile _pom = this.pom();
      files.add(_pom);
    }
    return files;
  }
  
  public abstract boolean isPartOfGradleBuild();
  
  public abstract boolean isPartOfMavenBuild();
  
  public abstract boolean isEclipsePluginProject();
  
  public boolean isEclipseFeatureProject() {
    return false;
  }
  
  public CharSequence buildProperties() {
    StringConcatenation _builder = new StringConcatenation();
    Set<String> _sourceFolders = this.getSourceFolders();
    final Function1<String, String> _function = (String it) -> {
      return (it + "/");
    };
    Iterable<String> _map = IterableExtensions.<String, String>map(_sourceFolders, _function);
    String _buildPropertiesEntry = this.buildPropertiesEntry("source..", _map);
    _builder.append(_buildPropertiesEntry, "");
    _builder.newLineIfNotEmpty();
    Set<String> _binIncludes = this.getBinIncludes();
    String _buildPropertiesEntry_1 = this.buildPropertiesEntry("bin.includes", _binIncludes);
    _builder.append(_buildPropertiesEntry_1, "");
    _builder.newLineIfNotEmpty();
    Set<String> _developmentBundles = this.getDevelopmentBundles();
    String _buildPropertiesEntry_2 = this.buildPropertiesEntry("additional.bundles", _developmentBundles);
    _builder.append(_buildPropertiesEntry_2, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public Set<String> getBinIncludes() {
    StringConcatenation _builder = new StringConcatenation();
    String _sourceFolder = this.sourceFolder(Outlet.META_INF);
    _builder.append(_sourceFolder, "");
    _builder.append("/");
    return CollectionLiterals.<String>newLinkedHashSet(".", _builder.toString());
  }
  
  public Set<String> getDevelopmentBundles() {
    return CollectionLiterals.<String>emptySet();
  }
  
  private String buildPropertiesEntry(final String key, final Iterable<String> value) {
    String _xblockexpression = null;
    {
      boolean _isEmpty = IterableExtensions.isEmpty(value);
      if (_isEmpty) {
        return "";
      }
      final String assignment = (key + " = ");
      int _length = assignment.length();
      final String indent = Strings.repeat(" ", _length);
      String _join = IterableExtensions.join(value, (",\\\n" + indent));
      _xblockexpression = (assignment + _join);
    }
    return _xblockexpression;
  }
  
  public String manifest() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Manifest-Version: 1.0");
    _builder.newLine();
    _builder.append("Bundle-ManifestVersion: 2");
    _builder.newLine();
    _builder.append("Bundle-Name: ");
    String _name = this.getName();
    _builder.append(_name, "");
    _builder.newLineIfNotEmpty();
    _builder.append("Bundle-Vendor: My Company");
    _builder.newLine();
    _builder.append("Bundle-Version: 1.0.0.qualifier");
    _builder.newLine();
    _builder.append("Bundle-SymbolicName: ");
    String _name_1 = this.getName();
    _builder.append(_name_1, "");
    _builder.append("; singleton:=true");
    _builder.newLineIfNotEmpty();
    {
      Object _activatorClassName = this.getActivatorClassName();
      boolean _notEquals = (!Objects.equal(_activatorClassName, null));
      if (_notEquals) {
        _builder.append("Bundle-Activator: ");
        Object _activatorClassName_1 = this.getActivatorClassName();
        _builder.append(_activatorClassName_1, "");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("Bundle-ActivationPolicy: lazy");
    _builder.newLine();
    Set<String> _requiredBundles = this.getRequiredBundles();
    String _manifestEntry = this.manifestEntry("Require-Bundle", _requiredBundles);
    _builder.append(_manifestEntry, "");
    _builder.newLineIfNotEmpty();
    Set<String> _importedPackages = this.getImportedPackages();
    String _manifestEntry_1 = this.manifestEntry("Import-Package", _importedPackages);
    _builder.append(_manifestEntry_1, "");
    _builder.newLineIfNotEmpty();
    _builder.append("Bundle-RequiredExecutionEnvironment: ");
    String _bree = this.getBree();
    _builder.append(_bree, "");
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }
  
  public String getBree() {
    JavaVersion _javaVersion = this.config.getJavaVersion();
    return _javaVersion.getBree();
  }
  
  private String manifestEntry(final String key, final Iterable<String> value) {
    boolean _isEmpty = IterableExtensions.isEmpty(value);
    if (_isEmpty) {
      return "";
    }
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(key, "");
    _builder.append(": ");
    String _join = IterableExtensions.join(value, ",\n ");
    _builder.append(_join, "");
    return _builder.toString();
  }
  
  public Set<String> getRequiredBundles() {
    LinkedHashSet<String> _xblockexpression = null;
    {
      final LinkedHashSet<String> bundles = CollectionLiterals.<String>newLinkedHashSet();
      Set<? extends ProjectDescriptor> _upstreamProjects = this.getUpstreamProjects();
      final Function1<ProjectDescriptor, String> _function = (ProjectDescriptor it) -> {
        return it.getName();
      };
      Iterable<String> _map = IterableExtensions.map(_upstreamProjects, _function);
      Iterables.<String>addAll(bundles, _map);
      Set<ExternalDependency> _externalDependencies = this.getExternalDependencies();
      final Function1<ExternalDependency, ExternalDependency.P2Coordinates> _function_1 = (ExternalDependency it) -> {
        return it.getP2();
      };
      Iterable<ExternalDependency.P2Coordinates> _map_1 = IterableExtensions.<ExternalDependency, ExternalDependency.P2Coordinates>map(_externalDependencies, _function_1);
      final Function1<ExternalDependency.P2Coordinates, Boolean> _function_2 = (ExternalDependency.P2Coordinates it) -> {
        String _bundleId = it.getBundleId();
        return Boolean.valueOf((!Objects.equal(_bundleId, null)));
      };
      Iterable<ExternalDependency.P2Coordinates> _filter = IterableExtensions.<ExternalDependency.P2Coordinates>filter(_map_1, _function_2);
      final Function1<ExternalDependency.P2Coordinates, String> _function_3 = (ExternalDependency.P2Coordinates it) -> {
        String _bundleId = it.getBundleId();
        String _xifexpression = null;
        String _version = it.getVersion();
        boolean _equals = Objects.equal(_version, null);
        if (_equals) {
          _xifexpression = "";
        } else {
          String _version_1 = it.getVersion();
          String _plus = (";bundle-version=\"" + _version_1);
          _xifexpression = (_plus + "\"");
        }
        return (_bundleId + _xifexpression);
      };
      Iterable<String> _map_2 = IterableExtensions.<ExternalDependency.P2Coordinates, String>map(_filter, _function_3);
      Iterables.<String>addAll(bundles, _map_2);
      _xblockexpression = bundles;
    }
    return _xblockexpression;
  }
  
  public Set<String> getImportedPackages() {
    Set<ExternalDependency> _externalDependencies = this.getExternalDependencies();
    final Function1<ExternalDependency, Set<String>> _function = (ExternalDependency it) -> {
      ExternalDependency.P2Coordinates _p2 = it.getP2();
      return _p2.getPackages();
    };
    Iterable<Set<String>> _map = IterableExtensions.<ExternalDependency, Set<String>>map(_externalDependencies, _function);
    Iterable<String> _flatten = Iterables.<String>concat(_map);
    return IterableExtensions.<String>toSet(_flatten);
  }
  
  public Set<ExternalDependency> getExternalDependencies() {
    final LinkedHashSet<ExternalDependency> deps = CollectionLiterals.<ExternalDependency>newLinkedHashSet();
    Ecore2XtextConfiguration _ecore2Xtext = this.config.getEcore2Xtext();
    Set<EPackageInfo> _ePackageInfos = _ecore2Xtext.getEPackageInfos();
    for (final EPackageInfo ePackage : _ePackageInfos) {
      String _bundleID = ePackage.getBundleID();
      ExternalDependency _createBundleDependency = ExternalDependency.createBundleDependency(_bundleID);
      deps.add(_createBundleDependency);
    }
    return deps;
  }
  
  public Object getActivatorClassName() {
    return null;
  }
  
  public GradleBuildFile buildGradle() {
    return new GradleBuildFile(this);
  }
  
  public PomFile pom() {
    return new PomFile(this);
  }
  
  public String sourceFolder(final Outlet outlet) {
    SourceLayout _sourceLayout = this.config.getSourceLayout();
    return _sourceLayout.getPathFor(outlet);
  }
  
  protected PlainTextFile file(final Outlet outlet, final String relativePath, final CharSequence content) {
    return new PlainTextFile(outlet, relativePath, this, content);
  }
  
  protected PlainTextFile file(final Outlet outlet, final String relativePath, final CharSequence content, final boolean executable) {
    return new PlainTextFile(outlet, relativePath, this, content, executable);
  }
  
  protected BinaryFile binaryFile(final Outlet outlet, final String relativePath, final URL url) {
    return new BinaryFile(outlet, relativePath, this, false, url);
  }
  
  public ProjectDescriptor(final WizardConfiguration config) {
    super();
    this.config = config;
  }
  
  @Pure
  public WizardConfiguration getConfig() {
    return this.config;
  }
  
  @Pure
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }
}