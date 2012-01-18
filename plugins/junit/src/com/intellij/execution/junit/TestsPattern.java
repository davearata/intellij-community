/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * User: anna
 * Date: 11-Jun-2010
 */
package com.intellij.execution.junit;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.configurations.*;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.util.Function;
import com.intellij.util.FunctionUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestsPattern extends TestObject {
  public TestsPattern(final Project project,
                      final JUnitConfiguration configuration,
                      RunnerSettings runnerSettings,
                      ConfigurationPerRunnerSettings configurationSettings) {
    super(project, configuration, runnerSettings, configurationSettings);
  }

  @Override
  protected void initialize() throws ExecutionException {
    super.initialize();
    final JUnitConfiguration.Data data = myConfiguration.getPersistentData();
    final Project project = myConfiguration.getProject();
    boolean isJUnit4 = false;
    final ArrayList<String> classNames = new ArrayList<String>();
    final Set<Module> modules = new HashSet<Module>();
    for (String className : data.getPatterns()) {
      final PsiClass psiClass = JavaExecutionUtil.findMainClass(project, className, GlobalSearchScope.allScope(project));
      if (psiClass != null && JUnitUtil.isTestClass(psiClass)) {
        classNames.add(className);
        modules.add(ModuleUtil.findModuleForPsiElement(psiClass));
        if (JUnitUtil.isJUnit4TestClass(psiClass)) {
          isJUnit4 = true;
        }
      }
    }
    final String jreHome = myConfiguration.isAlternativeJrePathEnabled() ? myConfiguration.getAlternativeJrePath() : null;

    Module module = myConfiguration.getConfigurationModule().getModule();
    if (module == null && modules.size() == 1 && modules.iterator().next() != null) {
       module = modules.iterator().next();
    }

    if (module != null) {
      JavaParametersUtil.configureModule(module, myJavaParameters, JavaParameters.JDK_AND_CLASSES_AND_TESTS, jreHome);
    } else {
      JavaParametersUtil.configureProject(project, myJavaParameters, JavaParameters.JDK_AND_CLASSES_AND_TESTS, jreHome);
    }
    addClassesListToJavaParameters(classNames, StringUtil.isEmpty(data.METHOD_NAME) ? FunctionUtil.<String>id() : new Function<String, String>() {
      @Override
      public String fun(String className) {
        return className + "," + data.METHOD_NAME;
      }
    }, "", true, isJUnit4);
  }

  @Override
  public String suggestActionName() {
    final String configurationName = myConfiguration.getName();
    if (!myConfiguration.isGeneratedName()) {
    }
    return "'" + configurationName + "'"; //todo
  }

  @Nullable
  @Override
  public RefactoringElementListener getListener(PsiElement element, JUnitConfiguration configuration) {
    return null;
  }

  @Override
  public boolean isConfiguredByElement(JUnitConfiguration configuration,
                                       PsiClass testClass,
                                       PsiMethod testMethod,
                                       PsiPackage testPackage) {
    if (testMethod != null && Comparing.strEqual(testMethod.getName(), configuration.getPersistentData().METHOD_NAME)) {
      return true;
    }
    return false;
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    final JUnitConfiguration.Data data = myConfiguration.getPersistentData();
    final Set<String> patterns = data.getPatterns();
    if (patterns.isEmpty()) {
      throw new RuntimeConfigurationWarning("No pattern selected");
    }
    final GlobalSearchScope searchScope = GlobalSearchScope.allScope(myConfiguration.getProject());
    for (String pattern : patterns) {
      final PsiClass psiClass = JavaExecutionUtil.findMainClass(myConfiguration.getProject(), pattern, searchScope);
      if (psiClass == null) {
        throw new RuntimeConfigurationWarning("Class " + pattern + " not found");
      }
      if (!JUnitUtil.isTestClass(psiClass)) {
        throw new RuntimeConfigurationWarning("Class " + pattern + " not a test");
      }
    }
  }
}
