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

package org.mevenide.properties.writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * interface for writing properties files.
 * @author  <a href="mailto:ca206216@tiscali.cz">Milos Kleint</a>
 */
public interface IPropertiesWriter
{
    /**
     * writes the properties to outputstream, considering the currentcontent if not null andthe implementation
     * is capable.
     * @param props properties to write.
     * @throws IOException if anything goes wrong.
     */
    void marshall(OutputStream output, Properties props, InputStream currentContent) throws IOException;
}
