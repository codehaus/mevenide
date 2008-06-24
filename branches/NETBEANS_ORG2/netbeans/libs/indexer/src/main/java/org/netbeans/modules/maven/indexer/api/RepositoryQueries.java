/*
 *  Copyright 2008 Mevenide Team.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.netbeans.modules.maven.indexer.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.netbeans.modules.maven.indexer.spi.ArchetypeQueries;
import org.netbeans.modules.maven.indexer.spi.BaseQueries;
import org.netbeans.modules.maven.indexer.spi.ChecksumQueries;
import org.netbeans.modules.maven.indexer.spi.ClassesQuery;
import org.netbeans.modules.maven.indexer.spi.DependencyInfoQueries;
import org.netbeans.modules.maven.indexer.spi.GenericFindQuery;
import org.netbeans.modules.maven.indexer.spi.RepositoryIndexerImplementation;
import org.openide.util.Exceptions;

/**
 *
 * @author mkleint
 */
public final class RepositoryQueries {

    /**
     * 
     * @param repos
     * @return
     */
    public static Set<String> getGroups(RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        final Set<String> toRet = new TreeSet<String>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                BaseQueries bq = impl.getCapabilityLookup().lookup(BaseQueries.class);
                assert bq != null : "All RepositoryIndexerImplementation need to define BaseQueries:" + impl.getType() + " : " + impl.getClass();
                toRet.addAll(bq.getGroups(rps));
            }
        }
        return toRet;
    }
    
    /**
     * 
     * @param prefix
     * @param repos
     * @return
     */
    public static Set<String> filterGroupIds(String prefix, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        Set<String> toRet = new TreeSet<String>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                BaseQueries bq = impl.getCapabilityLookup().lookup(BaseQueries.class);
                assert bq != null : "All RepositoryIndexerImplementation need to define BaseQueries:" + impl.getType() + " : " + impl.getClass();
                toRet.addAll(bq.filterGroupIds(prefix, rps));
            }
        }
        return toRet;
    }

    public static List<NBVersionInfo> getRecords(String groupId, String artifactId, String version, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                BaseQueries bq = impl.getCapabilityLookup().lookup(BaseQueries.class);
                assert bq != null : "All RepositoryIndexerImplementation need to define BaseQueries:" + impl.getType() + " : " + impl.getClass();
                toRet.addAll(bq.getRecords(groupId, artifactId, version, rps));
            }
        }
        return toRet;
    }

    /**
     * 
     * @param groupId
     * @param repos
     * @return
     */
    public static Set<String> getArtifacts(String groupId, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        Set<String> toRet = new TreeSet<String>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                BaseQueries bq = impl.getCapabilityLookup().lookup(BaseQueries.class);
                assert bq != null : "All RepositoryIndexerImplementation need to define BaseQueries:" + impl.getType() + " : " + impl.getClass();
                toRet.addAll(bq.getArtifacts(groupId, rps));
            }
        }
        return toRet;
    }

    public static List<NBVersionInfo> getVersions(String groupId, String artifactId, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                BaseQueries bq = impl.getCapabilityLookup().lookup(BaseQueries.class);
                assert bq != null : "All RepositoryIndexerImplementation need to define BaseQueries:" + impl.getType() + " : " + impl.getClass();
                toRet.addAll(bq.getVersions(groupId, artifactId, rps));
            }
        }
        return toRet;
    }

    public static List<NBGroupInfo> findDependencyUsage(String groupId, String artifactId, String version, RepositoryInfo... repos) {
        //tempmaps
        Map<String, NBGroupInfo> groupMap = new HashMap<String, NBGroupInfo>();
        Map<String, NBArtifactInfo> artifactMap = new HashMap<String, NBArtifactInfo>();
        List<NBGroupInfo> groupInfos = new ArrayList<NBGroupInfo>();
        
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                DependencyInfoQueries dq = impl.getCapabilityLookup().lookup(DependencyInfoQueries.class);
                if (dq != null) {
                    convertToNBGroupInfo(dq.findDependencyUsage(groupId, artifactId, version, rps),
                            groupMap, artifactMap, groupInfos);
                }
            }
        }
        
        return groupInfos;
    }
    
    private static void convertToNBGroupInfo(Collection<NBVersionInfo> artifactInfos, 
                                      Map<String, NBGroupInfo> groupMap, 
                                      Map<String, NBArtifactInfo> artifactMap,
                                      List<NBGroupInfo> groupInfos) {
        for (NBVersionInfo ai : artifactInfos) {
            String groupId = ai.getGroupId();
            String artId = ai.getArtifactId();

            NBGroupInfo ug = groupMap.get(groupId);
            if (ug == null) {
                ug = new NBGroupInfo(groupId);
                groupInfos.add(ug);
                groupMap.put(groupId, ug);
            }
            NBArtifactInfo ua = artifactMap.get(artId);
            if (ua == null) {
                ua = new NBArtifactInfo(artId);
                ug.addArtifactInfo(ua);
                artifactMap.put(artId, ua);
            }
            ua.addVersionInfo(ai);
        }
    }
    

    public static List<NBVersionInfo> findByMD5(File file, RepositoryInfo... repos) {
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        try {
            String calculateChecksum = RepositoryUtil.calculateMD5Checksum(file);
            return findByMD5(calculateChecksum, repos);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return toRet;
        
    }

    public static List<NBVersionInfo> findByMD5(String md5, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                ChecksumQueries chq = impl.getCapabilityLookup().lookup(ChecksumQueries.class);
                if (chq != null) {
                    toRet.addAll(chq.findByMD5(md5, rps));
                }
            }
        }
        return toRet;
    }
    
    public static List<NBVersionInfo> findBySHA1(File file, RepositoryInfo... repos) {
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        try {
            String calculateChecksum = RepositoryUtil.calculateSHA1Checksum(file);
            return findBySHA1(calculateChecksum, repos);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return toRet;
        
    }

    public static List<NBVersionInfo> findBySHA1(String sha1, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                ChecksumQueries chq = impl.getCapabilityLookup().lookup(ChecksumQueries.class);
                if (chq != null) {
                    toRet.addAll(chq.findBySHA1(sha1, rps));
                }
            }
        }
        return toRet;
    }
    
    
    public static List<NBVersionInfo> findVersionsByClass(final String className, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                ClassesQuery chq = impl.getCapabilityLookup().lookup(ClassesQuery.class);
                if (chq != null) {
                    toRet.addAll(chq.findVersionsByClass(className, rps));
                }
            }
        }
        return toRet;
    }

    public static List<NBVersionInfo> findArchetypes(RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                ArchetypeQueries aq = impl.getCapabilityLookup().lookup(ArchetypeQueries.class);
                if (aq != null) {
                    toRet.addAll(aq.findArchetypes(rps));
                }
            }
        }
        return toRet;
    }
    
    /**
     * 
     * @param groupId
     * @param prefix
     * @param repos
     * @return
     */
    public static Set<String> filterPluginArtifactIds(String groupId, String prefix, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        Set<String> toRet = new TreeSet<String>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                BaseQueries bq = impl.getCapabilityLookup().lookup(BaseQueries.class);
                assert bq != null : "All RepositoryIndexerImplementation need to define BaseQueries:" + impl.getType() + " : " + impl.getClass();
                toRet.addAll(bq.filterPluginArtifactIds(groupId, prefix, rps));
            }
        }
        return toRet;
    }

    /**
     * 
     * @param prefix
     * @param repos
     * @return
     */
    public static Set<String> filterPluginGroupIds(String prefix, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        Set<String> toRet = new TreeSet<String>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                BaseQueries bq = impl.getCapabilityLookup().lookup(BaseQueries.class);
                assert bq != null : "All RepositoryIndexerImplementation need to define BaseQueries:" + impl.getType() + " : " + impl.getClass();
                toRet.addAll(bq.filterPluginGroupIds(prefix, rps));
            }
        }
        return toRet;
    }
    
    public static List<NBVersionInfo> find(List<QueryField> fields, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        List<NBVersionInfo> toRet = new ArrayList<NBVersionInfo>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                GenericFindQuery gfq = impl.getCapabilityLookup().lookup(GenericFindQuery.class);
                if (gfq != null) {
                    toRet.addAll(gfq.find(fields, rps));
                }
            }
        }
        return toRet;
    }
    

    /**
     * 
     * @param groupId
     * @param prefix
     * @param repos
     * @return
     */
    public static Set<String> filterArtifactIdForGroupId(String groupId, String prefix, RepositoryInfo... repos) {
        Collection<List<RepositoryInfo>> all = splitReposByType(repos);
        Set<String> toRet = new TreeSet<String>();
        for (List<RepositoryInfo> rps : all) {
            RepositoryIndexerImplementation impl = RepositoryIndexer.findImplementation(rps.get(0));
            if (impl != null) {
                BaseQueries bq = impl.getCapabilityLookup().lookup(BaseQueries.class);
                assert bq != null : "All RepositoryIndexerImplementation need to define BaseQueries:" + impl.getType() + " : " + impl.getClass();
                toRet.addAll(bq.filterArtifactIdForGroupId(groupId, prefix, rps));
            }
        }
        return toRet;
    }

    private static Collection<List<RepositoryInfo>> splitReposByType(RepositoryInfo[] repos) {
        if (repos == null || repos.length == 0) {
            repos = RepositoryPreferences.getInstance().getRepositoryInfos().toArray(new RepositoryInfo[0]);
        }
        Map<String, List<RepositoryInfo>> toRet = new HashMap<String, List<RepositoryInfo>>();
        for (RepositoryInfo info : repos) {
            String type = info.getType();
            List<RepositoryInfo> list = toRet.get(type);
            if (list == null) {
                list = new ArrayList<RepositoryInfo>();
                toRet.put(type, list);
            }
            list.add(info);
        }
        return toRet.values();
    }

}
