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

package org.apache.uima.ruta.ide.core.extensions;

import java.util.Set;

import org.apache.uima.ruta.ide.core.codeassist.CompletionOnKeywordArgumentOrFunctionArgument;
import org.apache.uima.ruta.ide.core.codeassist.CompletionOnKeywordOrFunction;
import org.apache.uima.ruta.ide.core.codeassist.RutaCompletionEngine;
import org.apache.uima.ruta.ide.core.codeassist.RutaCompletionParser;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.core.CompletionRequestor;

public interface ICompletionExtension {
  boolean visit(Expression s, RutaCompletionParser parser, int position);

  boolean visit(Statement s, RutaCompletionParser parser, int position);

  void completeOnKeywordOrFunction(CompletionOnKeywordOrFunction key, ASTNode astNodeParent,
          RutaCompletionEngine engine);

  void completeOnKeywordArgumentsOne(String name,
          CompletionOnKeywordArgumentOrFunctionArgument compl, Set methodNames, Statement st,
          RutaCompletionEngine tmCompletionEngine);

  void setRequestor(CompletionRequestor requestor);
}
