/* ==========================================================================
 * Copyright 2007 Mevenide Team
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


package org.codehaus.mevenide.netbeans;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.spi.java.project.classpath.ProjectClassPathModifierImplementation;
import org.netbeans.spi.project.LookupMerger;
import org.openide.util.Lookup;

/**
 * TODO: The idea of having a LookupMerger for this class is not 100% semantically correct.
 * The original add/remove methods return values have different meaning than here.
 * if true is returned from impls means everything is done no further processing necessary, 
 * false means not relevant, try another impl or fallback to default.
 * a proper solution would be to create our own api that would reflect this difference.
 * @author mkleint
 */
public class CPModifierLookupMerger implements LookupMerger<ProjectClassPathModifierImplementation>{
    
    private CPExtender fallback;
    private Extender instance;
    
    /** Creates a new instance of CPExtenderLookupMerger */
    public CPModifierLookupMerger(CPExtender fallbck) {
        fallback = fallbck;
        assert fallback != null;
    }
    
    public Class<ProjectClassPathModifierImplementation> getMergeableClass() {
        return ProjectClassPathModifierImplementation.class;
    }

    public synchronized ProjectClassPathModifierImplementation merge(Lookup lookup) {
        if (instance == null) {
            instance =  new Extender();
        }
        instance.setLookup(lookup);
        return instance;
    }

    private class Extender extends ProjectClassPathModifierImplementation {
        
        private Lookup context;
        
        private Extender() {
            this.context = context;
        }
        private void setLookup(Lookup context) {
            this.context = context;
        }
    
        private Object retVal(String methodName, ProjectClassPathModifierImplementation impl, 
                              Class<?>[] paramTypes,
                              Object... params) throws IOException {
            try {
                Method meth = impl.getClass().getMethod(methodName, paramTypes);
                meth.setAccessible(true);
                return meth.invoke(impl, params);
            } catch (InvocationTargetException x) {
                if (x.getCause() instanceof IOException) {
                    throw (IOException)x.getCause();
                }
                //JDK16 can replace with new IOException(x.getCause());
                IOException ex = new IOException(x.getCause().getMessage());
                ex.initCause(x.getCause());
                throw ex;
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionError("Cannot use reflection on " + impl + " method:" + methodName);
            }
        }

        protected SourceGroup[] getExtensibleSourceGroups() {
            Collection<? extends ProjectClassPathModifierImplementation> list = context.lookupAll(ProjectClassPathModifierImplementation.class);
            Collection<SourceGroup> sg = new HashSet<SourceGroup>();
            for (ProjectClassPathModifierImplementation ext : list) {
                try {
                    SourceGroup[] sgs = (SourceGroup[])retVal("getExtensibleSourceGroups", ext, new Class<?>[0]);
                    sg.addAll(Arrays.asList(sgs));
                } catch (IOException e) {
                    //should not happen at all.
                }
            }
            sg.addAll(Arrays.asList(fallback.getExtensibleSourceGroups()));
            return sg.toArray(new SourceGroup[sg.size()]);
        }

        protected String[] getExtensibleClassPathTypes(SourceGroup arg0) {
            Collection<? extends ProjectClassPathModifierImplementation> list = context.lookupAll(ProjectClassPathModifierImplementation.class);
            Collection<String> retVal = new HashSet<String>();
            for (ProjectClassPathModifierImplementation ext : list) {
                try {
                    String[] ret = (String[])retVal("getExtensibleClassPathTypes", ext,
                            new Class<?>[] {SourceGroup.class}, arg0 );
                    retVal.addAll(Arrays.asList(ret));
                } catch (IOException e) {
                    //should not happen at all.
                }
            }
            retVal.addAll(Arrays.asList(fallback.getExtensibleClassPathTypes(arg0)));
            return retVal.toArray(new String[retVal.size()]);
        }

        protected boolean addLibraries(Library[] arg0, SourceGroup arg1,
                                       String arg2) throws IOException,
                                                           UnsupportedOperationException {
            Collection<? extends ProjectClassPathModifierImplementation> list = context.lookupAll(ProjectClassPathModifierImplementation.class);
            for (ProjectClassPathModifierImplementation ext : list) {
                Boolean ret = (Boolean)retVal("addLibraries", ext, 
                        new Class<?>[] { new Library[0].getClass(), SourceGroup.class, String.class}, arg0, arg1, arg2);
                if (ret.booleanValue()) {
                    return ret.booleanValue();
                }
            }
            return fallback.addLibraries(arg0, arg1, arg2);
        }

        protected boolean removeLibraries(Library[] arg0, SourceGroup arg1,
                                          String arg2) throws IOException,
                                                              UnsupportedOperationException {
            Collection<? extends ProjectClassPathModifierImplementation> list = context.lookupAll(ProjectClassPathModifierImplementation.class);
            for (ProjectClassPathModifierImplementation ext : list) {
                Boolean ret = (Boolean)retVal("removeLibraries", ext, 
                        new Class<?>[] { new Library[0].getClass(), SourceGroup.class, String.class}, arg0, arg1, arg2);
                if (ret.booleanValue()) {
                    return ret.booleanValue();
                }
            }
            return fallback.removeLibraries(arg0, arg1, arg2);
        }

        protected boolean addRoots(URL[] arg0, SourceGroup arg1, String arg2) throws IOException,
                                                                                     UnsupportedOperationException {
            Collection<? extends ProjectClassPathModifierImplementation> list = context.lookupAll(ProjectClassPathModifierImplementation.class);
            for (ProjectClassPathModifierImplementation ext : list) {
                Boolean ret = (Boolean)retVal("addRoots", ext, 
                        new Class<?>[] { new URL[0].getClass(), SourceGroup.class, String.class}, arg0, arg1, arg2);
                if (ret.booleanValue()) {
                    return ret.booleanValue();
                }
            }
            return fallback.addRoots(arg0, arg1, arg2);
        }

        protected boolean removeRoots(URL[] arg0, SourceGroup arg1, String arg2) throws IOException,
                                                                                        UnsupportedOperationException {
            Collection<? extends ProjectClassPathModifierImplementation> list = context.lookupAll(ProjectClassPathModifierImplementation.class);
            for (ProjectClassPathModifierImplementation ext : list) {
                Boolean ret = (Boolean)retVal("removeRoots", ext, 
                        new Class<?>[] { new URL[0].getClass(), SourceGroup.class, String.class}, arg0, arg1, arg2);
                if (ret.booleanValue()) {
                    return ret.booleanValue();
                }
            }
            return fallback.removeRoots(arg0, arg1, arg2);
        }

        protected boolean addAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                          SourceGroup arg2, String arg3) throws IOException,
                                                                                UnsupportedOperationException {
            Collection<? extends ProjectClassPathModifierImplementation> list = context.lookupAll(ProjectClassPathModifierImplementation.class);
            for (ProjectClassPathModifierImplementation ext : list) {
                Boolean ret = (Boolean)retVal("addAntArtifacts", ext, 
                        new Class<?>[] { new AntArtifact[0].getClass(), new URI[0].getClass(), SourceGroup.class, String.class}, arg0, arg1, arg2, arg3);
                if (ret.booleanValue()) {
                    return ret.booleanValue();
                }
            }
            return fallback.addAntArtifacts(arg0, arg1, arg2, arg3);
        }

        protected boolean removeAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                             SourceGroup arg2, String arg3) throws IOException,
                                                                                   UnsupportedOperationException {
            Collection<? extends ProjectClassPathModifierImplementation> list = context.lookupAll(ProjectClassPathModifierImplementation.class);
            for (ProjectClassPathModifierImplementation ext : list) {
                Boolean ret = (Boolean)retVal("addAntArtifacts", ext, 
                        new Class<?>[] { new AntArtifact[0].getClass(), new URI[0].getClass(), SourceGroup.class, String.class}, arg0, arg1, arg2, arg3);
                if (ret.booleanValue()) {
                    return ret.booleanValue();
                }
            }
            return fallback.addAntArtifacts(arg0, arg1, arg2, arg3);
        }

    }
    
}
