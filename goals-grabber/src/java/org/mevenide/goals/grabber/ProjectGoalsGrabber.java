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
package org.mevenide.goals.grabber;

import java.io.FileReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**  
 * read custom goals declared in maven.xml
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ProjectGoalsGrabber.java 4 sept. 2003 Exp gdodinet 
 * 
 */
public class ProjectGoalsGrabber extends AbstractGoalsGrabber {
    private String mavenXmlFile;
	
    public ProjectGoalsGrabber() { }

    public String getName() {
        return IGoalsGrabber.ORIGIN_PROJECT;
    }

    public void refresh() throws Exception {
        super.refresh();
    	if ( mavenXmlFile == null) {
    		throw new Exception("maven.xml file hasnot been set. Unable to refresh goals.");
    	}
    	parseMavenXml();
    }

	private void parseMavenXml() throws Exception {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = factory.newPullParser();
		FileReader reader = null;
		try {
			reader = new FileReader(mavenXmlFile);
			parser.setInput( reader );
	
			int eventType = parser.getEventType();
	
			while ( eventType != XmlPullParser.END_DOCUMENT )
			{
				if ( eventType == XmlPullParser.START_TAG )
				{
					if ( parser.getName().equals("goal")) {
						String fullyQualifiedName = parser.getAttributeValue(null, "name");
						String prereqs = parser.getAttributeValue(null, "prereqs");
						String description = parser.getAttributeValue(null, "description");
						registerGoal(fullyQualifiedName, description+">"+prereqs);
					}
				}
				eventType = parser.next();
			}
		}
		finally {
		    if ( reader != null ) {
		        reader.close();
		    }
		}
	}

    public String getMavenXmlFile() {
        return mavenXmlFile;
    }

    public void setMavenXmlFile(String mavenXml) {
        this.mavenXmlFile = mavenXml;
    }

}
