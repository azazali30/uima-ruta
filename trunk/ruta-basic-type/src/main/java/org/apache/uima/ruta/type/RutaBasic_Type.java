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
package org.apache.uima.ruta.type;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * Updated by JCasGen Thu Jul 12 10:42:33 CEST 2012
 * 
 * @generated
 */
public class RutaBasic_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected FSGenerator getFSGenerator() {
    return fsGenerator;
  }

  /** @generated */
  @SuppressWarnings("rawtypes")
  private final FSGenerator fsGenerator = new FSGenerator() {
    public FeatureStructure createFS(int addr, CASImpl cas) {
      if (RutaBasic_Type.this.useExistingInstance) {
        // Return eq fs instance if already created
        FeatureStructure fs = RutaBasic_Type.this.jcas.getJfsFromCaddr(addr);
        if (null == fs) {
          fs = new RutaBasic(addr, RutaBasic_Type.this);
          RutaBasic_Type.this.jcas.putJfsFromCaddr(addr, fs);
          return fs;
        }
        return fs;
      } else
        return new RutaBasic(addr, RutaBasic_Type.this);
    }
  };

  /** @generated */
  public final static int typeIndexID = RutaBasic.typeIndexID;

  /**
   * @generated
   * @modifiable
   */
  public final static boolean featOkTst = JCasRegistry
          .getFeatOkTst("org.apache.uima.ruta.type.RutaBasic");

  /** @generated */
  final Feature casFeat_replacement;

  /** @generated */
  final int casFeatCode_replacement;

  /** @generated */
  public String getReplacement(int addr) {
    if (featOkTst && casFeat_replacement == null)
      jcas.throwFeatMissing("replacement", "org.apache.uima.ruta.type.RutaBasic");
    return ll_cas.ll_getStringValue(addr, casFeatCode_replacement);
  }

  /** @generated */
  public void setReplacement(int addr, String v) {
    if (featOkTst && casFeat_replacement == null)
      jcas.throwFeatMissing("replacement", "org.apache.uima.ruta.type.RutaBasic");
    ll_cas.ll_setStringValue(addr, casFeatCode_replacement, v);
  }

  /**
   * initialize variables to correspond with Cas Type and Features
   * 
   * @generated
   */
  public RutaBasic_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());

    casFeat_replacement = jcas.getRequiredFeatureDE(casType, "replacement", "uima.cas.String",
            featOkTst);
    casFeatCode_replacement = (null == casFeat_replacement) ? JCas.INVALID_FEATURE_CODE
            : ((FeatureImpl) casFeat_replacement).getCode();

  }
}
