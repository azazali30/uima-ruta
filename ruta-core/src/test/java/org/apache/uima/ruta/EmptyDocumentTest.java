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

package org.apache.uima.ruta;

import static org.junit.Assert.assertEquals;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.SelectFSs;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.ruta.engine.Ruta;
import org.apache.uima.ruta.engine.RutaEngine;
import org.apache.uima.ruta.engine.RutaTestUtils;
import org.apache.uima.ruta.rule.RuleInference1Test;
import org.apache.uima.ruta.type.RutaBasic;
import org.junit.Assert;
import org.junit.Test;

public class EmptyDocumentTest {

  @Test
  public void test() throws Exception {
    String name = RuleInference1Test.class.getSimpleName();
    String namespace1 = RuleInference1Test.class.getPackage().getName().replaceAll("\\.", "/");
    String namespace2 = EmptyDocumentTest.class.getPackage().getName().replaceAll("\\.", "/");
    CAS cas = RutaTestUtils.process(namespace1 + "/" + name + RutaEngine.SCRIPT_FILE_EXTENSION,
            namespace2 + "/" + "EmptyDocumentTest.txt", 50);

    AnnotationIndex<AnnotationFS> ai = null;
    FSIterator<AnnotationFS> iterator = null;

    ai = cas.getAnnotationIndex();
    assertEquals(2, ai.size());
    iterator = ai.iterator();
    assertEquals("", iterator.next().getCoveredText());

    if (cas != null) {
      cas.release();
    }

  }

  @Test
  public void test2() throws Exception {
    CAS cas = RutaTestUtils.getCAS("");
    Ruta.apply(cas, "Document{IS(uima.tcas.DocumentAnnotation) -> T1};");
    RutaTestUtils.assertAnnotationsEquals(cas, 1, 1, "");
  }

  @Test
  public void testSpaceWithInvalidAnnotation() throws Exception {
    CAS cas = RutaTestUtils.getCAS(" ");
    AnnotationFS annotation = cas.createAnnotation(cas.getAnnotationType(), -1, 2);
    cas.addFsToIndexes(annotation);
    Ruta.apply(cas, "Document;");

    SelectFSs<RutaBasic> select = cas.select(RutaBasic.class);
    Assert.assertEquals(1, select.count());
    RutaBasic rutaBasic = select.findAny().get();
    Assert.assertEquals(0, rutaBasic.getBegin());
    Assert.assertEquals(1, rutaBasic.getEnd());
  }

}
