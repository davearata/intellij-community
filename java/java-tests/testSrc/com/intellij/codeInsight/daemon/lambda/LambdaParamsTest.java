/*
 * Copyright 2000-2012 JetBrains s.r.o.
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
package com.intellij.codeInsight.daemon.lambda;

import com.intellij.codeInsight.daemon.LightDaemonAnalyzerTestCase;
import org.jetbrains.annotations.NonNls;

public class LambdaParamsTest extends LightDaemonAnalyzerTestCase {
  @NonNls static final String BASE_PATH = "/codeInsight/daemonCodeAnalyzer/lambda/params";

  public void testFormalParams() throws Exception {
    doTest();
  }
  
  public void testInferredParams() throws Exception {
    doTest();
  }

  public void testMethodApplicability() throws Exception {
    doTest();
  }

  public void testIncompatibleTypes() throws Exception {
    doTest();
  }

  public void testRaw() throws Exception {
    doTest();
  }

  public void testFormalParamsWithWildcards() throws Exception {
    doTest();
  }

  private void doTest() throws Exception {
    doTest(BASE_PATH + "/" + getTestName(false) + ".java", false, false);
  }
}
