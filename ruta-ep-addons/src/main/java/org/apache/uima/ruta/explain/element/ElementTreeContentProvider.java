/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/

package org.apache.uima.ruta.explain.element;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.ruta.explain.tree.IExplainTreeNode;
import org.apache.uima.ruta.explain.tree.InlinedRootNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ElementTreeContentProvider implements ITreeContentProvider {

  @Override
  public Object[] getChildren(Object parentElement) {
    List<Object> result = new ArrayList<Object>();
    IExplainTreeNode debugNode = (IExplainTreeNode) parentElement;
    for (IExplainTreeNode each : debugNode.getChildren()) {
      if (!(each instanceof InlinedRootNode)) {
        result.add(each);
      }
    }
    return result.toArray();
  }

  @Override
  public Object getParent(Object element) {
    if (element instanceof IExplainTreeNode) {
      return ((IExplainTreeNode) element).getParent();
    }
    return null;

  }

  @Override
  public Object[] getElements(Object element) {
    return getChildren(element);
  }

  @Override
  public boolean hasChildren(Object parentElement) {
    if (parentElement instanceof IExplainTreeNode) {
      IExplainTreeNode debugNode = (IExplainTreeNode) parentElement;
      return debugNode.hasChildren();
    }
    return false;
  }

  @Override
  public void dispose() {
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
  }

}
