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

package org.apache.uima.ruta.expression.annotation;

import java.util.List;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.ruta.RutaStream;
import org.apache.uima.ruta.block.RutaBlock;
import org.apache.uima.ruta.rule.MatchContext;

/**
 * An expression referring to an annotation stored in a list by its index.
 *
 */
public class AnnotationListVariableIndexExpression extends AbstractAnnotationExpression {

  private String var;

  private int index;

  public AnnotationListVariableIndexExpression(String var, int index) {
    super();
    this.var = var;
    this.index = index;
  }

  @Override
  public AnnotationFS getAnnotation(MatchContext context, RutaStream stream) {
    RutaBlock parent = context.getParent();
    @SuppressWarnings("unchecked")
    List<AnnotationFS> list = parent.getEnvironment().getVariableValue(var, List.class, stream);
    if (list != null && index >= 0 && index < list.size()) {
      return list.get(index);
    }
    return null;
  }

  public String getVar() {
    return var;
  }

}
