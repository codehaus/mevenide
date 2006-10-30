/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 * a netbeans settings for global options related to repository indexing and manamegent..
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenIndexSettings {
    public static final String PROP_INDEX_FREQ = "indexUpdateFrequency"; //NOI18N
    public static final String PROP_LAST_INDEX_UPDATE = "lastIndexUpdate"; //NOI18N
    public static final String PROP_COLLECTED = "collectedReposAsStrings"; //NOI18N
    public static final String PROP_SNAPSHOTS = "includeSnapshots"; //NOI18N
    
    public static final int FREQ_ONCE_WEEK = 0;
    public static final int FREQ_ONCE_DAY = 1;
    public static final int FREQ_STARTUP = 2;
    public static final int FREQ_NEVER = 3;
    
    private static final MavenIndexSettings INSTANCE = new MavenIndexSettings();
    
    private MavenIndexSettings() {
    }
    
    private final Preferences getPreferences() {
        return NbPreferences.forModule(MavenIndexSettings.class);
    }
    
    private final List<String> getStringList(String key) {
        Preferences pref = getPreferences();
        int count = 0;
        String val = pref.get(key + "." + count, null);
        List<String> toRet = new ArrayList<String>();
        while (val != null) {
            toRet.add(val);
            count = count + 1;
            val = pref.get(key + "." + count, null);
        }
        return toRet;
    }
    
    private final void setStringList(String basekey, List<String> list) {
        assert list != null;
        Preferences pref = getPreferences();
        int count = 0;
        String key = basekey + "." + count;
        String val = pref.get(key, null);
        Iterator<String> it = list.iterator();
        while (val != null || it.hasNext()) {
            if (it.hasNext()) {
                pref.put(key, it.next());
            } else {
                pref.remove(key);
            }
            count = count + 1;
            key = basekey + "." + count;
            val = pref.get(key, null);
        }
    }
    
    public static MavenIndexSettings getDefault() {
        return INSTANCE;
    }
    
    public void setIndexUpdateFrequency(int fr) {
        getPreferences().putInt(PROP_INDEX_FREQ, fr);
    }
    
    public int getIndexUpdateFrequency() {
        return getPreferences().getInt(PROP_INDEX_FREQ, FREQ_ONCE_WEEK);
    }

    public Date getLastIndexUpdate() {
        return new Date(getPreferences().getLong(PROP_LAST_INDEX_UPDATE, 0));
    }
    
    public void setLastIndexUpdate(Date date) {
        getPreferences().putLong(PROP_LAST_INDEX_UPDATE, date.getTime());
    }

    public  void setCollectedRepositories(List<String> repos) {
        setStringList(PROP_COLLECTED, repos);
    }
    
    public void setCollectedReposAsStrings(String[] repos) {
        setCollectedRepositories(Arrays.asList(repos));
    }
    
    public String[] getCollectedReposAsStrings() {
        List<String> str = getCollectedRepositories();
        return str.toArray(new String[str.size()]);
    }
    
    public List<String> getCollectedRepositories() {
        return getStringList(PROP_COLLECTED);
    }

    public boolean isIncludeSnapshots() {
        return getPreferences().getBoolean(PROP_SNAPSHOTS, true);
    }

    public void setIncludeSnapshots(boolean includeSnapshots) {
        getPreferences().putBoolean(PROP_SNAPSHOTS, includeSnapshots);
    }
    
}
