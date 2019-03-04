/**
 * generated by Xtext
 */
package org.eclipse.xtext.testlanguages.fileAware.formatting2;

import java.util.Arrays;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.formatting2.AbstractFormatter2;
import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.eclipse.xtext.formatting2.IHiddenRegionFormatter;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.testlanguages.fileAware.fileAware.Element;
import org.eclipse.xtext.testlanguages.fileAware.fileAware.FileAwarePackage;
import org.eclipse.xtext.testlanguages.fileAware.fileAware.Import;
import org.eclipse.xtext.testlanguages.fileAware.fileAware.PackageDeclaration;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class FileAwareTestLanguageFormatter extends AbstractFormatter2 {
  protected void _format(final PackageDeclaration pkg, @Extension final IFormattableDocument document) {
    final Procedure1<IHiddenRegionFormatter> _function = (IHiddenRegionFormatter it) -> {
      it.setNewLines(2);
    };
    document.append(this.textRegionExtensions.regionFor(pkg).feature(FileAwarePackage.Literals.PACKAGE_DECLARATION__NAME), _function);
    EList<Import> _imports = pkg.getImports();
    for (final Import imp : _imports) {
      {
        document.<Import>format(imp);
        final Procedure1<IHiddenRegionFormatter> _function_1 = (IHiddenRegionFormatter it) -> {
          int _xifexpression = (int) 0;
          Import _last = IterableExtensions.<Import>last(pkg.getImports());
          boolean _tripleEquals = (imp == _last);
          if (_tripleEquals) {
            _xifexpression = 2;
          } else {
            _xifexpression = 1;
          }
          it.setNewLines(_xifexpression);
        };
        document.<Import>append(imp, _function_1);
      }
    }
    EList<Element> _contents = pkg.getContents();
    for (final Element element : _contents) {
      document.<Element>format(element);
    }
  }
  
  protected void _format(final Element element, @Extension final IFormattableDocument document) {
    EList<Element> _contents = element.getContents();
    for (final Element _element : _contents) {
      document.<Element>format(_element);
    }
  }
  
  public void format(final Object element, final IFormattableDocument document) {
    if (element instanceof XtextResource) {
      _format((XtextResource)element, document);
      return;
    } else if (element instanceof Element) {
      _format((Element)element, document);
      return;
    } else if (element instanceof PackageDeclaration) {
      _format((PackageDeclaration)element, document);
      return;
    } else if (element instanceof EObject) {
      _format((EObject)element, document);
      return;
    } else if (element == null) {
      _format((Void)null, document);
      return;
    } else if (element != null) {
      _format(element, document);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(element, document).toString());
    }
  }
}
