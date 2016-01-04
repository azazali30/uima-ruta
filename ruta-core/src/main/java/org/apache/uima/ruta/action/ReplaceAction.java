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

package org.apache.uima.ruta.action;

import java.util.List;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.ruta.RutaStream;
import org.apache.uima.ruta.expression.string.IStringExpression;
import org.apache.uima.ruta.rule.MatchContext;
import org.apache.uima.ruta.rule.RuleElement;
import org.apache.uima.ruta.rule.RuleMatch;
import org.apache.uima.ruta.type.RutaBasic;
import org.apache.uima.ruta.visitor.InferenceCrowd;

public class ReplaceAction extends AbstractRutaAction {

  public IStringExpression getReplacement() {
    return replacement;
  }

  private final IStringExpression replacement;

  public ReplaceAction(IStringExpression replacement) {
    super();
    this.replacement = replacement;
  }

  @Override
  public void execute(MatchContext context, RutaStream stream, InferenceCrowd crowd) {
    RuleMatch match = context.getRuleMatch();
    RuleElement element = context.getElement();
    List<AnnotationFS> matchedAnnotations = match.getMatchedAnnotations(null,
            element.getContainer());
    for (AnnotationFS matchedAnnotation : matchedAnnotations) {
      List<RutaBasic> annotationsInWindow = stream.getBasicsInWindow(matchedAnnotation);
      boolean replaced = false;
      for (RutaBasic basic : annotationsInWindow) {
        element.getParent();
        basic.setReplacement(replaced ? "" : replacement.getStringValue(context, stream));
        replaced = true;
      }
    }

  }

}
