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

package org.apache.uima.ruta.rule.quantifier;

import org.apache.uima.cas.CAS;
import org.apache.uima.ruta.engine.Ruta;
import org.apache.uima.ruta.engine.RutaTestUtils;
import org.junit.Test;

public class MinMaxQuantifierTest {

  @Test
  public void test() throws Exception {
    String document = "A, A. B, B, B";
    String script = "";
    script += "(CW{-PARTOF(TruePositive)} (COMMA CW)[2,5]){-> TruePositive};\n";
    script += "TruePositive{-> T1};\n";

    CAS cas = RutaTestUtils.getCAS(document);
    Ruta.apply(cas, script);

    RutaTestUtils.assertAnnotationsEquals(cas, 1, 1, "B, B, B");
  }

  @Test
  public void testMinMaxOnComposedWithAnchor() throws Exception {
    String document = "1 2 3 4 5 6 7 8 9 10";
    String script = "";
    script += "(NUM @NUM NUM NUM)[2,2]{-> T1};\n";
    script += "(NUM NUM @NUM NUM)[2,2]{-> T2};\n";

    CAS cas = RutaTestUtils.getCAS(document);
    Ruta.apply(cas, script);

//    if (RutaTestUtils.DEBUG_MODE) {
//      RutaTestUtils.storeTypeSystem();
//      RutaTestUtils.storeCas(cas, "testMinMaxOnComposedWithAnchor");
//    }

    RutaTestUtils.assertAnnotationsEquals(cas, 1, 3, "1 2 3 4 5 6 7 8", "2 3 4 5 6 7 8 9",
            "3 4 5 6 7 8 9 10");
    RutaTestUtils.assertAnnotationsEquals(cas, 2, 3, "1 2 3 4 5 6 7 8", "2 3 4 5 6 7 8 9",
            "3 4 5 6 7 8 9 10");
  }
}
