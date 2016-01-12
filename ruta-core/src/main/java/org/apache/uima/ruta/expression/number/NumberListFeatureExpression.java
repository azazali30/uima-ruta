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

package org.apache.uima.ruta.expression.number;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.DoubleArrayFS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.FloatArrayFS;
import org.apache.uima.cas.IntArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.ruta.RutaStream;
import org.apache.uima.ruta.UIMAConstants;
import org.apache.uima.ruta.expression.feature.FeatureExpression;
import org.apache.uima.ruta.rule.MatchContext;

/**
 * An expression referring to numbers stored in a feature.
 *
 */
public class NumberListFeatureExpression extends AbstractNumberListExpression {

  private FeatureExpression fe;

  public NumberListFeatureExpression(FeatureExpression fe) {
    super();
    this.fe = fe;
  }
  
  @Override
  public List<Number> getList(MatchContext context, RutaStream stream) {
    AnnotationFS annotation = context.getAnnotation();
    Type type = fe.getTypeExpr(context, stream).getType(context, stream);
    Feature feature = fe.getFeature(context, stream);
    if(feature == null || !feature.getRange().isArray() || !StringUtils.equals(feature.getRange().getName(), UIMAConstants.TYPE_BOOLEAN)) {
      // throw runtime exception?
      return Collections.emptyList();
    }
    List<AnnotationFS> list = getTargetAnnotation(annotation, type, stream);
    Collection<AnnotationFS> featureAnnotations = fe.getFeatureAnnotations(list, stream, context,
            false);
    List<Number> result = new ArrayList<>();

    for (AnnotationFS each : featureAnnotations) {
      FeatureStructure featureValue = each.getFeatureValue(feature);
      if(featureValue instanceof IntArrayFS) {
        IntArrayFS array = (IntArrayFS) featureValue;
        for (int i = 0; i < array.size(); i++) {
          Integer b = array.get(i);
          result.add(b);
        }
      } else if(featureValue instanceof DoubleArrayFS) {
        DoubleArrayFS array = (DoubleArrayFS) featureValue;
        for (int i = 0; i < array.size(); i++) {
          Double b = array.get(i);
          result.add(b);
        }
      } else if(featureValue instanceof FloatArrayFS) {
        FloatArrayFS array = (FloatArrayFS) featureValue;
        for (int i = 0; i < array.size(); i++) {
          Float b = array.get(i);
          result.add(b);
        }
      }
    }
    
    return result;
  }

  public FeatureExpression getFeatureExpression() {
    return fe;
  }

  public void setFeatureExpression(FeatureExpression fe) {
    this.fe = fe;
  }



  
  

}
