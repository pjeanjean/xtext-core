/**
 * Copyright (c) 2014, 2016 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.service;

import com.google.common.base.Objects;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.xtext.service.OperationCanceledError;
import org.eclipse.xtext.util.CancelIndicator;

/**
 * A facade for managing and working with cancellation exceptions of different platforms.
 * 
 * @author Sven Efftinge - Initial contribution and API
 * 
 * @since 2.8
 */
@SuppressWarnings("all")
public class OperationCanceledManager {
  protected RuntimeException getPlatformOperationCanceledException(final Throwable t) {
    RuntimeException _switchResult = null;
    boolean _matched = false;
    if (t instanceof OperationCanceledException) {
      _matched=true;
      _switchResult = ((RuntimeException)t);
    }
    if (!_matched) {
      if (t instanceof RuntimeException) {
        String _name = ((RuntimeException)t).getClass().getName();
        boolean _equals = Objects.equal(_name, "com.intellij.openapi.progress.ProcessCanceledException");
        if (_equals) {
          _matched=true;
          _switchResult = ((RuntimeException)t);
        }
      }
    }
    if (!_matched) {
      if (t instanceof OperationCanceledError) {
        _matched=true;
        _switchResult = ((OperationCanceledError)t).getWrapped();
      }
    }
    if (!_matched) {
      _switchResult = null;
    }
    return _switchResult;
  }
  
  public boolean isOperationCanceledException(final Throwable t) {
    RuntimeException _platformOperationCanceledException = this.getPlatformOperationCanceledException(t);
    return (_platformOperationCanceledException != null);
  }
  
  /**
   * Rethrows OperationCanceledErrors and wraps platform specific OperationCanceledExceptions. Does nothing for any other type of Throwable.
   */
  public void propagateAsErrorIfCancelException(final Throwable t) {
    if ((t instanceof OperationCanceledError)) {
      throw ((OperationCanceledError)t);
    }
    final RuntimeException opCanceledException = this.getPlatformOperationCanceledException(t);
    if ((opCanceledException != null)) {
      throw new OperationCanceledError(opCanceledException);
    }
  }
  
  /**
   * Rethrows platform specific OperationCanceledExceptions and unwraps OperationCanceledErrors. Does nothing for any other type of Throwable.
   */
  public void propagateIfCancelException(final Throwable t) {
    final RuntimeException cancelException = this.getPlatformOperationCanceledException(t);
    if ((cancelException != null)) {
      throw cancelException;
    }
  }
  
  protected Error asWrappingOperationCanceledException(final Throwable throwable) {
    if ((throwable instanceof OperationCanceledError)) {
      return ((Error)throwable);
    }
    final RuntimeException platform = this.getPlatformOperationCanceledException(throwable);
    if ((platform != null)) {
      return new OperationCanceledError(platform);
    }
    return null;
  }
  
  public void throwOperationCanceledException() {
    throw this.asWrappingOperationCanceledException(this.getPlatformSpecificOperationCanceledException());
  }
  
  protected RuntimeException getPlatformSpecificOperationCanceledException() {
    return new OperationCanceledException();
  }
  
  public void checkCanceled(final CancelIndicator indicator) {
    if (((indicator != null) && indicator.isCanceled())) {
      this.throwOperationCanceledException();
    }
  }
}
