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

package org.apache.uima.textmarker.ide.ui.wizards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.textmarker.engine.TextMarkerEngine;
import org.apache.uima.textmarker.ide.core.TextMarkerNature;
import org.apache.uima.textmarker.ide.core.builder.TextMarkerProjectUtils;
import org.apache.uima.textmarker.ide.ui.TextMarkerImages;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.internal.ui.util.CoreUtility;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.BuildpathsBlock;
import org.eclipse.dltk.ui.wizards.ProjectWizard;
import org.eclipse.dltk.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class TextMarkerProjectCreationWizard extends ProjectWizard {

  public static final String ID_WIZARD = "org.apache.uima.textmarker.ide.ui.wizards.TextMarkerProjectWizard"; //$NON-NLS-1$

  private ProjectWizardFirstPage fFirstPage;

  private ProjectWizardSecondPage fSecondPage;

  private IConfigurationElement fConfigElement;

  public TextMarkerProjectCreationWizard() {
    setDefaultPageImageDescriptor(TextMarkerImages.DESC_WIZBAN_PROJECT_CREATION);
    setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
    setWindowTitle(TextMarkerWizardMessages.ProjectCreationWizard_title);
  }

  @Override
  public void addPages() {
    super.addPages();
    fFirstPage = new TextMarkerProjectWizardFirstPage();

    fFirstPage.setTitle(TextMarkerWizardMessages.ProjectCreationWizardFirstPage_title);
    fFirstPage.setDescription(TextMarkerWizardMessages.ProjectCreationWizardFirstPage_description);
    addPage(fFirstPage);
    fSecondPage = new TextMarkerProjectWizardSecondPage(fFirstPage);
    addPage(fSecondPage);
  }

  @Override
  protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
    super.finishPage(monitor);
    createProject(monitor);
  }

  public void createProject(IProgressMonitor monitor) throws CoreException {
    IProject project = fSecondPage.getScriptProject().getProject();
    IFolder folder = project.getFolder(TextMarkerProjectUtils.getDefaultInputLocation());
    if (!folder.exists()) {
      CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
    }
    folder = project.getFolder(TextMarkerProjectUtils.getDefaultOutputLocation());
    if (!folder.exists()) {
      CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
    }
    folder = project.getFolder(TextMarkerProjectUtils.getDefaultTestLocation());
    if (!folder.exists()) {
      CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
    }
    IFolder descFolder = project.getFolder(TextMarkerProjectUtils.getDefaultDescriptorLocation());
    if (!descFolder.exists()) {
      CoreUtility.createFolder(descFolder, true, true, new SubProgressMonitor(monitor, 1));
    }
    IFolder srcFolder = project.getFolder(TextMarkerProjectUtils.getDefaultScriptLocation());
    if (!srcFolder.exists()) {
      CoreUtility.createFolder(srcFolder, true, true, new SubProgressMonitor(monitor, 1));
    }
    IFolder rsrcFolder = project.getFolder(TextMarkerProjectUtils.getDefaultResourcesLocation());
    if (!rsrcFolder.exists()) {
      CoreUtility.createFolder(rsrcFolder, true, true, new SubProgressMonitor(monitor, 1));
    }

    IFolder utilsFolder = descFolder.getFolder("utils");
    if (!utilsFolder.exists()) {
      CoreUtility.createFolder(utilsFolder, true, true, new SubProgressMonitor(monitor, 1));
    }

    List<BPListElement> buildpathEntries = new ArrayList<BPListElement>();
    for (IBuildpathEntry buildpathEntry : fSecondPage.getRawBuildPath()) {
      BPListElement createFromExisting = BPListElement.createFromExisting(buildpathEntry,
              fSecondPage.getScriptProject());
      if (createFromExisting.getBuildpathEntry().getEntryKind() != IBuildpathEntry.BPE_SOURCE) {
        buildpathEntries.add(createFromExisting);
      }
    }
    IBuildpathEntry newSourceEntry = DLTKCore.newSourceEntry(srcFolder.getFullPath());
    buildpathEntries.add(BPListElement.createFromExisting(newSourceEntry,
            fSecondPage.getScriptProject()));

    BuildpathsBlock.flush(buildpathEntries, fSecondPage.getScriptProject(), monitor);
    copyDescriptors(descFolder);

    TextMarkerProjectUtils.addProjectDataPath(project, descFolder);

    descFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);

  }

  public static void copyDescriptors(IFolder descFolder) {
    File descDir = descFolder.getLocation().toFile();
    File utilsDir = new File(descFolder.getLocation().toFile(), "utils/");
    copy(descDir, "BasicTypeSystem.xml");
    copy(descDir, "BasicEngine.xml");
    copy(descDir, "InternalTypeSystem.xml");
    
    copy(utilsDir, "Modifier.xml");
    copy(utilsDir, "AnnotationWriter.xml");
    copy(utilsDir, "StyleMapCreator.xml");
    copy(utilsDir, "XMIWriter.xml");
    copy(utilsDir, "SourceDocumentInformation.xml");
    copy(utilsDir, "PlainTextAnnotator.xml");
    copy(utilsDir, "PlainTextTypeSystem.xml");
    copy(utilsDir, "HtmlAnnotator.xml");
    copy(utilsDir, "HtmlTypeSystem.xml");
    copy(utilsDir, "HtmlConverter.xml");
  }

  private static void copy(File dir, String fileName) {
    InputStream in = null;
    OutputStream out = null;
    in = TextMarkerEngine.class.getResourceAsStream(fileName);
    try {
      out = new FileOutputStream(new File(dir, fileName));
    } catch (FileNotFoundException e) {
      System.err.println(e);
    }
    if (in != null && out != null) {
      copy(in, out);
    }

  }

  static void copy(InputStream fis, OutputStream fos) {
    try {
      byte[] buffer = new byte[0xFFFF];
      for (int len; (len = fis.read(buffer)) != -1;)
        fos.write(buffer, 0, len);
    } catch (IOException e) {
      System.err.println(e);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          System.err.println(e);
        }
      }
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          System.err.println(e);
        }
      }
    }
  }

  @Override
  public boolean performFinish() {
    boolean res = super.performFinish();
    if (res) {
      BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
      selectAndReveal(fSecondPage.getScriptProject().getProject());
    }
    return res;
  }

  public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
    fConfigElement = cfig;
  }

  @Override
  public boolean performCancel() {
    return super.performCancel();
  }

  @Override
  public String getScriptNature() {
    return TextMarkerNature.NATURE_ID;
  }
}