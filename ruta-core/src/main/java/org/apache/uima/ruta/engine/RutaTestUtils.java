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

package org.apache.uima.ruta.engine;

import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.ruta.seed.DefaultSeeder;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.CasIOUtils;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

public class RutaTestUtils {

  public static final boolean DEBUG_MODE = isDebugging();

  private static boolean isDebugging() {

    Pattern debugPattern = Pattern.compile("-Xdebug|jdwp");
    for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
      if (debugPattern.matcher(arg).find()) {
        return true;
      }
    }
    return false;
  }

  public static class TestFeature {
    public String name;

    public String description;

    public String range;

    public TestFeature(String name, String description, String range) {
      super();
      this.name = name;
      this.description = description;
      this.range = range;
    }
  }

  public static final String TYPE = "org.apache.uima.T";

  public static CAS process(String ruleFileName, String textFileName, int amount)
          throws URISyntaxException, IOException, InvalidXMLException,
          ResourceInitializationException, AnalysisEngineProcessException,
          ResourceConfigurationException {
    return process(ruleFileName, textFileName, amount, false, false, null, null, null, null);
  }

  public static CAS process(String ruleFileName, String textFileName,
          Map<String, Object> parameters, int amount) throws URISyntaxException, IOException,
          InvalidXMLException, ResourceInitializationException, AnalysisEngineProcessException,
          ResourceConfigurationException {
    return process(ruleFileName, textFileName, parameters, amount, null, null, null, null);
  }

  public static CAS process(String ruleFileName, String textFileName, int amount,
          boolean dynamicAnchoring, boolean simpleGreedyForComposed,
          Map<String, String> complexTypes, String resourceDirName) throws URISyntaxException,
          IOException, InvalidXMLException, ResourceInitializationException,
          AnalysisEngineProcessException, ResourceConfigurationException {
    return process(ruleFileName, textFileName, amount, dynamicAnchoring, simpleGreedyForComposed,
            complexTypes, null, resourceDirName, null);
  }

  public static CAS process(String ruleFileName, String textFileName, int amount,
          boolean dynamicAnchoring, boolean simpleGreedyForComposed,
          Map<String, String> complexTypes, Map<String, List<TestFeature>> features,
          String resourceDirName) throws URISyntaxException, IOException, InvalidXMLException,
          ResourceInitializationException, AnalysisEngineProcessException,
          ResourceConfigurationException {
    return process(ruleFileName, textFileName, amount, dynamicAnchoring, simpleGreedyForComposed,
            complexTypes, features, resourceDirName, null);
  }

  public static CAS process(String ruleFileName, String textFileName, int amount,
          boolean dynamicAnchoring, boolean simpleGreedyForComposed,
          Map<String, String> complexTypes, Map<String, List<TestFeature>> features,
          String resourceDirName, CAS cas) throws URISyntaxException, IOException,
          InvalidXMLException, ResourceInitializationException, AnalysisEngineProcessException,
          ResourceConfigurationException {
    final Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put(RutaEngine.PARAM_DYNAMIC_ANCHORING, dynamicAnchoring);
    parameters.put(RutaEngine.PARAM_SIMPLE_GREEDY_FOR_COMPOSED, simpleGreedyForComposed);

    return process(ruleFileName, textFileName, parameters, amount, complexTypes, features,
            resourceDirName, cas);
  }

  public static CAS process(String ruleFileName, String textFileName,
          Map<String, Object> parameters, int amount, Map<String, String> complexTypes,
          Map<String, List<TestFeature>> features, String resourceDirName, CAS cas)
          throws URISyntaxException, IOException, InvalidXMLException,
          ResourceInitializationException, AnalysisEngineProcessException,
          ResourceConfigurationException {
    URL ruleURL = RutaTestUtils.class.getClassLoader().getResource(ruleFileName);
    File ruleFile = new File(ruleURL.toURI());
    URL textURL = RutaTestUtils.class.getClassLoader().getResource(textFileName);
    File textFile = new File(textURL.toURI());
    File resourceFile = null;
    if (resourceDirName != null) {
      URL resourceURL = RutaTestUtils.class.getClassLoader().getResource(resourceDirName);
      resourceFile = new File(resourceURL.toURI());
    }
    URL url = RutaEngine.class.getClassLoader().getResource("BasicEngine.xml");
    if (url == null) {
      url = RutaTestUtils.class.getClassLoader().getResource("org/apache/uima/ruta/TestEngine.xml");
    }
    if (url == null) {
      url = RutaTestUtils.class.getResource("/org/apache/uima/ruta/engine/BasicEngine.xml");
    }
    XMLInputSource in = new XMLInputSource(url);
    ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
    AnalysisEngineDescription aed = (AnalysisEngineDescription) specifier;

    TypeSystemDescription basicTypeSystem = aed.getAnalysisEngineMetaData().getTypeSystem();
    addTestTypes(basicTypeSystem);
    addAdditionalTypes(complexTypes, features, basicTypeSystem);

    Collection<TypeSystemDescription> tsds = new ArrayList<TypeSystemDescription>();
    tsds.add(basicTypeSystem);
    TypeSystemDescription mergeTypeSystems = CasCreationUtils.mergeTypeSystems(tsds);
    aed.getAnalysisEngineMetaData().setTypeSystem(mergeTypeSystems);

    AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
    ae.setConfigParameterValue(RutaEngine.PARAM_SCRIPT_PATHS,
            new String[] { ruleFile.getParentFile().getPath() });
    String name = ruleFile.getName();
    if (name.endsWith(RutaEngine.SCRIPT_FILE_EXTENSION)) {
      name = name.substring(0, name.length() - 5);
    }

    ae.setConfigParameterValue(RutaEngine.PARAM_DICT_REMOVE_WS, false);

    for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
      ae.setConfigParameterValue(parameter.getKey(), parameter.getValue());
    }

    ae.setConfigParameterValue(RutaEngine.PARAM_MAIN_SCRIPT, name);

    if (resourceFile != null) {
      ae.setConfigParameterValue(RutaEngine.PARAM_RESOURCE_PATHS,
              new String[] { resourceFile.getPath() });
    }

    ae.reconfigure();
    if (cas == null) {
      cas = ae.newCAS();
      cas.setDocumentText(FileUtils.file2String(textFile, "UTF-8"));
    }
    ae.process(cas);

    ae.destroy();
    return cas;
  }

  /**
   * Helper to get the test type, e.g. org.apache.uima.T1, org.apache.uima.T2, ...
   * 
   * @param cas
   *          - The CAS object containing the type system
   * @param i
   *          - typeId, converted to {@link #TYPE} + i
   * @return the test type object with the given counter
   */
  public static Type getTestType(CAS cas, int i) {
    if (cas == null)
      return null;
    return cas.getTypeSystem().getType(TYPE + i);
  }

  public static CAS getCAS(String document)
          throws ResourceInitializationException, IOException, InvalidXMLException, SAXException {
    return getCAS(document, null, null);
  }

  public static CAS getCAS(String document, Map<String, String> complexTypes,
          Map<String, List<TestFeature>> features)
          throws ResourceInitializationException, IOException, InvalidXMLException, SAXException {
    return getCAS(document, complexTypes, features, false);
  }

  public static CAS getCAS(String document, Map<String, String> complexTypes,
          Map<String, List<TestFeature>> features, boolean storeTypeSystem)
          throws ResourceInitializationException, IOException, InvalidXMLException, SAXException {
    URL url = RutaEngine.class.getClassLoader().getResource("BasicEngine.xml");
    if (url == null) {
      url = RutaTestUtils.class.getClassLoader()
              .getResource("org/apache/uima/ruta/BasicEngine.xml");
    }
    if (url == null) {
      url = RutaTestUtils.class.getResource("BasicEngine.xml");
    }
    XMLInputSource in = new XMLInputSource(url);
    ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
    AnalysisEngineDescription aed = (AnalysisEngineDescription) specifier;
    TypeSystemDescription basicTypeSystem = aed.getAnalysisEngineMetaData().getTypeSystem();
    addTestTypes(basicTypeSystem);
    addAdditionalTypes(complexTypes, features, basicTypeSystem);
    Collection<TypeSystemDescription> tsds = new ArrayList<TypeSystemDescription>();
    tsds.add(basicTypeSystem);
    TypeSystemDescription mergeTypeSystems = CasCreationUtils.mergeTypeSystems(tsds);

    if (storeTypeSystem) {
      try (OutputStream os = new FileOutputStream("TypeSystem.xml")) {
        mergeTypeSystems.toXML(os);
      }
    }

    aed.getAnalysisEngineMetaData().setTypeSystem(mergeTypeSystems);
    AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
    CAS cas = ae.newCAS();
    cas.setDocumentText(document);
    return cas;
  }

  public static void addTestTypes(TypeSystemDescription typeSystemDescription) {
    for (int i = 1; i <= 50; i++) {
      typeSystemDescription.addType("org.apache.uima.T" + i, "Type for Testing",
              "uima.tcas.Annotation");
    }
  }

  private static void addAdditionalTypes(Map<String, String> complexTypes,
          Map<String, List<TestFeature>> features, TypeSystemDescription typeSystemDescription) {
    if (complexTypes != null) {
      Set<Entry<String, String>> entrySet = complexTypes.entrySet();
      for (Entry<String, String> entry : entrySet) {
        String name = entry.getKey();
        TypeDescription addType = typeSystemDescription.addType(name, "Type for Testing",
                entry.getValue());
        if (features != null) {
          List<TestFeature> list = features.get(name);
          for (TestFeature f : list) {
            addType.addFeature(f.name, f.description, f.range);
          }
        }
      }
    }
  }

  public static void printAnnotations(CAS cas, int typeId) {
    Type t = getTestType(cas, typeId);
    AnnotationIndex<AnnotationFS> ai = cas.getAnnotationIndex(t);
    for (AnnotationFS annotationFS : ai) {
      System.out.println(annotationFS.getCoveredText());
    }
  }

  /**
   * Helper for common assertion in JUnit tests
   * 
   * @param cas
   *          - The CAS object containing the type system
   * @param typeId
   *          - typeId, converted to {@link #TYPE} + i
   * @param expectedCnt
   *          - the expected amount of annotations
   * @param expecteds
   *          - the exprected covered texts
   */
  public static void assertAnnotationsEquals(CAS cas, int typeId, int expectedCnt,
          String... expecteds) {
    Type t = getTestType(cas, typeId);
    Collection<AnnotationFS> select = CasUtil.select(cas, t);
    if (select.size() != expectedCnt) {
      throw new RuntimeException("size of expected annotations (" + expectedCnt
              + ") does not match with actual size (" + select.size() + ").");
    }
    if (expecteds.length > 0) {
      Iterator<AnnotationFS> iterator = select.iterator();
      for (String expected : expecteds) {
        String actual = iterator.next().getCoveredText();
        if (!actual.equals(expected)) {
          throw new RuntimeException(
                  "expected text (" + expected + ") does not match with actual (" + actual + ").");
        }
      }
    }
  }

  /**
   * Helper to run Ruta on a tests script
   * 
   * @param testClass
   *          - the class of the unit test
   * @return the annotated {@link CAS}
   */
  public static CAS processTestScript(Class<?> testClass) {
    String name = testClass.getSimpleName();
    String namespace = testClass.getPackage().getName().replaceAll("\\.", "/");

    CAS cas = null;
    try {
      cas = RutaTestUtils.process(namespace + "/" + name + RutaEngine.SCRIPT_FILE_EXTENSION,
              namespace + "/" + name + ".txt", 50);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return cas;
  }

  public static CAS processTestScriptWithDefaultSeeder(Class<?> testClass) throws Exception {
    String name = testClass.getSimpleName();
    String namespace = testClass.getPackage().getName().replaceAll("\\.", "/");
    String scriptPath = namespace + "/" + name + RutaEngine.SCRIPT_FILE_EXTENSION;
    String textPath = namespace + "/" + name + ".txt";

    Map<String, Object> params = new LinkedHashMap<>();
    params.put(RutaEngine.PARAM_SEEDERS, new String[] { DefaultSeeder.class.getName() });

    return RutaTestUtils.process(scriptPath, textPath, params, 50);
  }

  public static void storeCas(CAS cas, String name) {
    File file = new File("input/" + name + ".xmi");
    file.getParentFile().mkdirs();
    try (OutputStream fos = new FileOutputStream(file)) {
      CasIOUtils.save(cas, fos, SerialFormat.XMI);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static void storeTypeSystem() {
    storeTypeSystem(Collections.emptyMap(), Collections.emptyMap());
  }

  public static void storeTypeSystem(Map<String, String> complexTypes,
          Map<String, List<TestFeature>> features) {

    File tsFile = new File("TypeSystem.xml");

    try {

      TypeSystemDescription typeSystemDescription = createTypeSystemDescription();
      addTestTypes(typeSystemDescription);
      addAdditionalTypes(complexTypes, features, typeSystemDescription);
      try (OutputStream os = new FileOutputStream(tsFile)) {
        typeSystemDescription.toXML(os);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static Map<String, Object> getDebugParams() {
    Map<String, Object> params = new LinkedHashMap<>();
    params.put(RutaEngine.PARAM_DEBUG, true);
    params.put(RutaEngine.PARAM_DEBUG_WITH_MATCHES, true);
    params.put(RutaEngine.PARAM_CREATED_BY, true);
    return params;
  }

}
