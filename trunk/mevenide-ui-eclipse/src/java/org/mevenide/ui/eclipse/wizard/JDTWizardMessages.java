/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */

package org.mevenide.ui.eclipse.wizard;

import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * Provides a wrapper around org.eclipse.jdt.internal.ui.wizards.NewWizardMessages.
 * NewWizardMessages is an internal class of the JDT. This class wraps it so that
 * MevenIDE is isolated from its changes. The 3.1RC1 release introduced changes
 * to NewWizardeMessages that breaks the current MevenIDE wizard code. This class
 * provides the new API while dynamically adapting to the NewWizardMessages using
 * the version of the JDT plugin as a hint on how to grab the real messages.
 */
public class JDTWizardMessages {
   private static final String NewWizardMessages = "org.eclipse.jdt.internal.ui.wizards.NewWizardMessages"; //$NON-NLS-1$

   public static String JavaProjectWizardSecondPage_error_title;
   public static String JavaProjectWizardSecondPage_error_message;
   public static String JavaProjectWizardFirstPage_directory_message;
   public static String NewPackageWizardPage_error_InvalidPackageName;
   public static String NewPackageWizardPage_warning_DiscouragedPackageName;
   
   static {
      try {
         Class clazz = Class.forName(NewWizardMessages);
         Method method = findGetStringMethod(clazz);

         if (method != null) {
            JavaProjectWizardSecondPage_error_title = getValue(method, "JavaProjectWizardSecondPage.error.title"); //$NON-NLS-1$
            JavaProjectWizardSecondPage_error_message = getValue(method, "JavaProjectWizardSecondPage.error.message"); //$NON-NLS-1$
            JavaProjectWizardFirstPage_directory_message = getValue(method, "JavaProjectWizardFirstPage.directory.message"); //$NON-NLS-1$
            NewPackageWizardPage_error_InvalidPackageName = getValue(method, "NewPackageWizardPage.error.InvalidPackageName"); //$NON-NLS-1$
            NewPackageWizardPage_warning_DiscouragedPackageName = getValue(method, "NewPackageWizardPage.warning.DiscouragedPackageName"); //$NON-NLS-1$
         } else {
            JavaProjectWizardSecondPage_error_title = getValue(clazz, "JavaProjectWizardSecondPage_error_title"); //$NON-NLS-1$
            JavaProjectWizardSecondPage_error_message = getValue(clazz, "JavaProjectWizardSecondPage_error_message"); //$NON-NLS-1$
            JavaProjectWizardFirstPage_directory_message = getValue(clazz, "JavaProjectWizardFirstPage_directory_message"); //$NON-NLS-1$
            NewPackageWizardPage_error_InvalidPackageName = getValue(clazz, "NewPackageWizardPage_error_InvalidPackageName"); //$NON-NLS-1$
            NewPackageWizardPage_warning_DiscouragedPackageName = getValue(clazz, "NewPackageWizardPage_warning_DiscouragedPackageName"); //$NON-NLS-1$
         }
      } catch (ClassNotFoundException e) {
         final String message = MessageFormat.format("Unable to locate the class {0}.", new String[] {NewWizardMessages});
         IStatus status = new Status(IStatus.ERROR, Mevenide.PLUGIN_ID, 0, message, e);
         Mevenide.getInstance().getLog().log(status);
      }
   }

   /**
    * Retrieves the value of a static string field from the provided class.
    * @param clazz The class that contains the field.
    * @param name The name of the field.
    * @return The value of the field. 
    */
   private static final String getValue(Class clazz, String name) {
      String result = "";

      try {
         result = (String)clazz.getField(name).get(null);
      } catch (Throwable e) {
         final String message = MessageFormat.format("Unable to retrieve the value of {0} from {1}.", new String[] {name, clazz.getName()});
         IStatus status = new Status(IStatus.ERROR, Mevenide.PLUGIN_ID, 0, message, e);
         Mevenide.getInstance().getLog().log(status);
      }

      return result;
   }

   /**
    * Retrieves the value of a static string field by calling a method on the provided class.
    * @param method The method to invoke.
    * @param name The argument to the method.
    * @return The value of the field.
    */
   private static final String getValue(Method method, String name) {
      String result = "";

      try {
         result = (String)method.invoke(null, new String[] {name});
      } catch (Throwable e) {
         final String message = MessageFormat.format("Unable to retrieve the value of {0} from {1}.", new String[] {name, method.getDeclaringClass().getName()});
         IStatus status = new Status(IStatus.ERROR, Mevenide.PLUGIN_ID, 0, message, e);
         Mevenide.getInstance().getLog().log(status);
      }

      return result;
   }

   /**
    * Returns a reference to the getString method
    * that takes a single string parameter.
    * @param clazz the class to test
    * @return a reference to the getString method.
    */
   private static final Method findGetStringMethod(Class clazz) {
      Method result = null;

      try {
         result = clazz.getMethod("getString", new Class[] {String.class});
      } catch (NoSuchMethodException e) {
         // ignored, just return null
      }

      return result;
   }

}
