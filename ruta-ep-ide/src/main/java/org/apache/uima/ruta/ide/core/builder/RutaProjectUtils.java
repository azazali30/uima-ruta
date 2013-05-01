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

package org.apache.uima.ruta.ide.core.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.ruta.engine.RutaEngine;
import org.apache.uima.ruta.ide.RutaIdePlugin;
import org.apache.uima.ruta.ide.core.RutaNature;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;

public class RutaProjectUtils {

  public static final String JAVANATURE = "org.eclipse.jdt.core.javanature";
  private static final String CDE_DATA_PATH = "CDEdataPath";

  public static IPath getEngineDescriptorPath(IPath scriptPath, IProject project) {
    String elementName = getModuleName(scriptPath);
    IPath descPackagePath = getDescriptorPackagePath(scriptPath, project);
    IPath result = descPackagePath.append(elementName + "Engine.xml");
    return result;
  }

  public static IPath getTypeSystemDescriptorPath(IPath scriptPath, IProject project) {
    String elementName = getModuleName(scriptPath);
    IPath descPackagePath = getDescriptorPackagePath(scriptPath.makeAbsolute(), project);
    IPath result = descPackagePath.append(elementName + "TypeSystem.xml");
    return result;
  }

  public static IPath getDescriptorRootPath(IProject project) {
    IPath projectPath = project.getLocation();
    IPath descPath = projectPath.append(getDefaultDescriptorLocation());
    return descPath;
  }

  public static List<IFolder> getAllScriptFolders(IScriptProject proj) throws CoreException {
    List<IFolder> result = new ArrayList<IFolder>();
    result.addAll(getScriptFolders(proj));
    result.addAll(getReferencedScriptFolders(proj));
    return result;
  }

  public static List<IFolder> getReferencedScriptFolders(IScriptProject proj) throws CoreException {
    return getReferencedScriptFolders(proj, new HashSet<IProject>());
  }
  
  public static List<IFolder> getReferencedScriptFolders(IScriptProject proj, Collection<IProject> visited) throws CoreException {
    List<IFolder> result = new ArrayList<IFolder>();
    IProject[] referencedProjects = proj.getProject().getReferencedProjects();
    for (IProject eachProject : referencedProjects) {
      if(!visited.contains(eachProject)) {
        IScriptProject scriptProject = DLTKCore.create(eachProject);
        result.addAll(RutaProjectUtils.getScriptFolders(scriptProject));
        visited.add(eachProject);
        result.addAll(getReferencedScriptFolders(scriptProject, visited));
      }
    }
    return result;
  }

  public static List<IFolder> getScriptFolders(IScriptProject proj) {
    List<IFolder> result = new ArrayList<IFolder>();
    IScriptFolder[] scriptFolders = null;
    try {
      scriptFolders = proj.getScriptFolders();
    } catch (ModelException e) {
      // referring to a non-ruta project
      // RutaIdePlugin.error(e);
    }
    if (scriptFolders != null) {
      for (IScriptFolder eachScriptFolder : scriptFolders) {
        IModelElement parent = eachScriptFolder.getParent();
        IResource resource = parent.getResource();
        if (parent != null && resource != null && resource instanceof IFolder) {
          if (!result.contains(resource)) {
            result.add((IFolder) resource);
          }
        }
      }
    }
    return result;
  }

  public static List<IFolder> getAllDescriptorFolders(IProject proj) throws CoreException {
    List<IFolder> result = new ArrayList<IFolder>();
    result.addAll(getDescriptorFolders(proj));
    result.addAll(getReferencedDescriptorFolders(proj));
    return result;
  }

  public static List<IFolder> getReferencedDescriptorFolders(IProject proj) throws CoreException {
    return getReferencedDescriptorFolders(proj, new HashSet<IProject>());
  }
  
  public static List<IFolder> getReferencedDescriptorFolders(IProject proj, Collection<IProject> visited) throws CoreException {
    List<IFolder> result = new ArrayList<IFolder>();
    Collection<IProject> referencedProjects = getReferencedProjects(proj, new HashSet<IProject>());
    for (IProject eachProject : referencedProjects) {
      if(!visited.contains(eachProject)) {
        result.addAll(RutaProjectUtils.getDescriptorFolders(eachProject));
        visited.add(eachProject);
        result.addAll(getReferencedDescriptorFolders(eachProject, visited));
      }
    }
    return result;
  }

  private static Collection<IProject> getReferencedProjects(IProject proj, Collection<IProject> visited) throws CoreException {
    Collection<IProject> result = new HashSet<IProject>();
    IProject[] referencedProjects = proj.getReferencedProjects();
    result.addAll(Arrays.asList(referencedProjects));
    IProjectNature nature = proj.getNature(JAVANATURE);
    if(nature != null) {
      JavaProject javaProject = (JavaProject) JavaCore.create(proj);
      IClasspathEntry[] resolvedClasspath = javaProject.getResolvedClasspath();
      for (IClasspathEntry eachCPE : resolvedClasspath) {
        if(eachCPE.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
         IProject project = getProject(eachCPE.getPath());
         result.add(project);
        }
      }
    }
    return result;
  }

  public static List<IFolder> getDescriptorFolders(IProject proj) throws CoreException {
    List<IFolder> result = new ArrayList<IFolder>();
    IProjectNature javaNature = proj.getNature(JAVANATURE);
    if (javaNature != null) {
      IJavaProject javaProject = JavaCore.create(proj);
      IPath readOutputLocation = javaProject.readOutputLocation();
      IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(readOutputLocation);
      result.add(folder);
    }
    IProjectNature pearNature = proj.getNature("org.apache.uima.pear.UimaNature");
    if (pearNature != null) {
      IFolder findElement = proj.getFolder("desc");
      if (findElement != null) {
        result.add((IFolder) findElement);
      }
    }
    IProjectNature rutaNature = proj.getNature(RutaNature.NATURE_ID);
    if (rutaNature != null) {
      IFolder findElement = proj.getFolder(getDefaultDescriptorLocation());
      if (findElement != null) {
        result.add((IFolder) findElement);
      }
    }
    return result;
  }

  public static List<String> getFolderLocations(List<IFolder> folders) {
    List<String> result = new ArrayList<String>();
    if (folders == null)
      return result;
    for (IFolder each : folders) {
      String portableString = each.getLocation().toPortableString();
      result.add(portableString);
    }
    return result;
  }

  public static IPath getScriptRootPath(IProject project) {
    IPath projectPath = project.getLocation();
    IPath descPath = projectPath.append(getDefaultScriptLocation());
    return descPath;
  }

  public static IPath getDescriptorPackagePath(IPath scriptPath, IProject project) {
    IPath projectPath = project.getLocation().makeAbsolute();
    IPath packagePath = scriptPath.removeLastSegments(1);
    IPath relativePackagePath;
    relativePackagePath = packagePath.makeRelativeTo(projectPath).removeFirstSegments(1);
    IPath descPackagePath = projectPath.append(getDefaultDescriptorLocation());
    descPackagePath = descPackagePath.append(relativePackagePath);
    return descPackagePath;
  }

  public static String getModuleName(IPath path) {
    String result = path.lastSegment();
    int lastIndexOf = result.lastIndexOf(RutaEngine.SCRIPT_FILE_EXTENSION);
    if (lastIndexOf != -1) {
      result = result.substring(0, lastIndexOf);
    }
    return result;
  }

  public static void addProjectDataPath(IProject project, IFolder folder) throws CoreException {
    String dataPath = project.getPersistentProperty((new QualifiedName("", CDE_DATA_PATH)));
    if (dataPath == null) {
      dataPath = "";
    }
    String addon = folder.getLocation().toPortableString();
    if (!StringUtils.isEmpty(dataPath)) {
      dataPath += File.pathSeparator;
    }
    dataPath += addon;
    project.setPersistentProperty(new QualifiedName("", CDE_DATA_PATH), dataPath);
  }

  public static void removeProjectDataPath(IProject project, IFolder folder) throws CoreException {
    String dataPath = project.getPersistentProperty((new QualifiedName("", CDE_DATA_PATH)));
    if (dataPath == null) {
      return;
    }
    String path = folder.getLocation().toPortableString();
    if (!StringUtils.isEmpty(dataPath)) {
      dataPath.replaceAll(path, "");
      dataPath.replaceAll(File.pathSeparator + File.pathSeparator, "");
    }
    project.setPersistentProperty(new QualifiedName("", CDE_DATA_PATH), dataPath);
  }

  public static IProject getProject(IPath path) {
    IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
    if (member instanceof IProject) {
      IProject p = (IProject) member;
      return p;
    }
    return null;
  }
  
  
  public static String getDefaultInputLocation() {
    return "input";
  }

  public static String getDefaultOutputLocation() {
    return "output";
  }

  public static String getDefaultTestLocation() {
    return "test";
  }

  public static String getDefaultScriptLocation() {
    return "script";
  }

  public static String getDefaultResourcesLocation() {
    return "resources";
  }

  public static String getDefaultDescriptorLocation() {
    return "descriptor";
  }

  public static String getDefaultTempTestLocation() {
    return "temp";
  }
}
