/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.idea;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Loads UTF-8 resources from resource bundles.
 *
 * <p>This is an enhancement over the standard java.util.ResourceBundle class which allows providing
 * additional properties to the message (for inline place holders) as well as supporting
 * hierarchical bundles.</p>
 *
 * @author Arik
 */
public final class Res {
    private static final Object LOCK = new Object();
    private static final Log LOG = LogFactory.getLog(Res.class);
    private static final String UNKNOWN_KEY = "(unknown resource key)";

    private static final Map<String, Res> cache = Collections.synchronizedMap(new HashMap<String, Res>(
            10));

    private final String packageName;
    private final String bundleName;
    private final Res parent;
    private final ResourceBundle bundle;
    private static final String DEFAULT_BUNDLE_NAME = "res";

    private Res(final String pPackageName,
                final String pBundleName,
                final Res pParent,
                final ResourceBundle pBundle) {
        if (pPackageName != null && pPackageName.trim().length() == 0)
            packageName = null;
        else
            packageName = pPackageName;
        bundleName = pBundleName;
        bundle = pBundle;
        parent = pParent;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getName() {
        return buildBundleName(packageName, bundleName);
    }

    public Res getParent() {
        return parent;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public String getResource(final String pResourceName) {
        final String res;
        if (packageName != null) {
            String lPackageName = packageName.replace('.', '/');
            if (!lPackageName.endsWith("/"))
                lPackageName = lPackageName + "/";
            res = lPackageName + pResourceName;
        }
        else
            res = pResourceName;

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final InputStream is = cl.getResourceAsStream(res);
        if (is == null)
            throw new MissingResourceException(
                    "Can't find resource '" + res + "'", "", "");

        try {
            return IOUtils.toString(is, "UTF-8");
        }
        catch (IOException e) {
            final MissingResourceException exc = new MissingResourceException(
                    "Error reading resource '" + res + "'", "", "");
            throw (MissingResourceException) exc.initCause(e);
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }

    public String get(final String pKey, final Object... pArguments) {
        if (pKey == null) {
            LOG.warn("null key has been passed for bundle '" + bundleName + "'");
            return UNKNOWN_KEY;
        }

        try {
            String msg = bundle.getString(pKey);
            if (msg == null || msg.trim().length() == 0)
                return "";
            else {
                msg = StringUtils.replace(msg, "${", "'${'");
                msg = StringUtils.replace(msg, "'", "''");
                return MessageFormat.format(msg, pArguments);
            }
        }
        catch (MissingResourceException e) {
            if (parent == null)
                throw e;
            return parent.get(pKey, pArguments);
        }
    }

    public Map getAll() {
        final Enumeration<String> keys = bundle.getKeys();
        final Map<String, String> all = new HashMap<String, String>();

        while (keys.hasMoreElements()) {
            final String key = keys.nextElement();
            all.put(key, get(key));
        }

        return all;
    }

    public static Res getInstance(final Class pClass) {
        return getInstance(pClass, DEFAULT_BUNDLE_NAME);
    }

    public static Res getInstance(final Class pClass, final String pBundleName) {
        return getInstance(pClass.getPackage().getName(), pBundleName);
    }

    public static Res getInstance(final String pPackageName) {
        return getInstance(pPackageName, DEFAULT_BUNDLE_NAME);
    }

    public static Res getInstance(final String pPackageName,
                                  final String pBundleName) {

        synchronized (LOCK) {
            return getRes(pPackageName, pBundleName);
        }
    }

    static Res getRes(final String pPackageName,
                      final String pBundleName) {


        //find the ResourceBundle for the given base bundleName
        String packageName = pPackageName;
        String baseName;
        baseName = buildBundleName(packageName, pBundleName);

        //if we have already searched for this res, return the cached instance
        final Res cachedRes = cache.get(baseName);
        if (cachedRes != null)
            return cachedRes;

        //otherwise, start by searching for a resource bundle
        ResourceBundle bundle = null;
        while (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle(baseName);
            }
            catch (MissingResourceException e) {
                if (packageName == null || packageName.trim().length() == 0)
                    break;

                final int lastDotIndex = packageName.lastIndexOf('.');
                if (lastDotIndex < 0) {
                    packageName = null;
                    baseName = pBundleName;
                }
                else {
                    packageName = packageName.substring(0, lastDotIndex);
                    baseName = packageName + "." + pBundleName;
                }
            }
        }

        if (bundle == null) {
            throw new MissingResourceException(
                    "Can't find bundle for base bundleName '" + baseName + "'",
                    baseName,
                    "");
        }

        Res parent = null;
        if (packageName != null && packageName.trim().length() > 0) {
            int lastDotIndex = packageName.lastIndexOf('.');

            //we iterate while 'lastDotIndex' is > 0 and not >= 0. Since if it is 0
            //there's no point to keep searcing anyway, and we subtract 1 from it
            //so we better not do that.
            while (parent == null && lastDotIndex > 0) {
                final String parentPackageName = pPackageName.substring(0, lastDotIndex);
                try {
                    parent = getInstance(parentPackageName, pBundleName);
                }
                catch (MissingResourceException e) {
                    lastDotIndex = pPackageName.lastIndexOf('.', lastDotIndex - 1);
                }
            }
        }

        final Res res = new Res(packageName, pBundleName, parent, bundle);

        //cache the new Res
        cache.put(baseName, res);

        return res;
    }

    protected static String buildBundleName(final String pPackageName,
                                            final String pBundleName) {
        final String baseName;
        if (pPackageName == null || pPackageName.trim().length() == 0)
            baseName = pBundleName;
        else
            baseName = pPackageName + "." + pBundleName;
        return baseName;
    }
}
