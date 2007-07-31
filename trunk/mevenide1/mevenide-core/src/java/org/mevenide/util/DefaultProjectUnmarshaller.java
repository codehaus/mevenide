package org.mevenide.util;

/*
 * ==================================================================== The
 * Apache Software License, Version 1.1
 * 
 * Copyright (c) 2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: "This product includes software
 * developed by the Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself, if and
 * wherever such third-party acknowledgments normally appear.
 * 
 * 4. The names "Apache" and "Apache Software Foundation" and "Apache Maven"
 * must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact
 * apache@apache.org.
 * 
 * 5. Products derived from this software may not be called "Apache", "Apache
 * Maven", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE APACHE
 * SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the Apache Software Foundation. For more information on the Apache
 * Software Foundation, please see <http://www.apache.org/>.
 * 
 * ====================================================================
 */
import java.io.IOException;
import java.io.Reader;
import org.apache.maven.project.Branch;
import org.apache.maven.project.Build;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.License;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;
import org.apache.maven.project.Resource;
import org.apache.maven.project.SourceModification;
import org.apache.maven.project.UnitTest;
import org.apache.maven.project.Version;
import org.mevenide.context.IProjectUnmarshaller;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author Jason van Zyl
 * @version $Id: DefaultProjectUnmarshaller.java,v 1.10 2004/05/09 20:16:15
 *          gdodinet Exp $
 */
public class DefaultProjectUnmarshaller implements IProjectUnmarshaller {

    public Project parse(Reader reader) throws Exception {
        Project project = new Project();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance("org.xmlpull.mxp1.MXParserFactory", 
                                    									Thread.currentThread().getContextClassLoader().getClass());
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(reader);
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("extend")) {
                    project.setExtend(parser.nextText());
                }
                else if (parser.getName().equals("pomVersion")) {
                    project.setPomVersion(parser.nextText());
                }
                else if (parser.getName().equals("groupId")) {
                    project.setGroupId(parser.nextText());
                }
                else if (parser.getName().equals("artifactId")) {
                    project.setArtifactId(parser.nextText());
                }
                else if (parser.getName().equals("id")) {
                    DeprecationWarning.warn("<project/> element: You should be using groupId / artifactId");
                    project.setId(parser.nextText());
                }
                else if (parser.getName().equals("name")) {
                    project.setName(parser.nextText());
                }
                else if (parser.getName().equals("logo")) {
                    project.setLogo(parser.nextText());
                }
                else if (parser.getName().equals("currentVersion")) {
                    project.setCurrentVersion(parser.nextText());
                }
                else if (parser.getName().equals("inceptionYear")) {
                    project.setInceptionYear(parser.nextText());
                }
                else if (parser.getName().equals("package")) {
                    project.setPackage(parser.nextText());
                }
                else if (parser.getName().equals("gumpRepositoryId")) {
                    project.setGumpRepositoryId(parser.nextText());
                }
                else if (parser.getName().equals("description")) {
                    project.setDescription(parser.nextText());
                }
                else if (parser.getName().equals("shortDescription")) {
                    project.setShortDescription(parser.nextText());
                }
                else if (parser.getName().equals("url")) {
                    project.setUrl(parser.nextText());
                }
                else if (parser.getName().equals("issueTrackingUrl")) {
                    project.setIssueTrackingUrl(parser.nextText());
                }
                else if (parser.getName().equals("siteAddress")) {
                    project.setSiteAddress(parser.nextText());
                }
                else if (parser.getName().equals("siteDirectory")) {
                    project.setSiteDirectory(parser.nextText());
                }
                else if (parser.getName().equals("distributionSite")) {
                    project.setDistributionSite(parser.nextText());
                }
                else if (parser.getName().equals("distributionDirectory")) {
                    project.setDistributionDirectory(parser.nextText());
                }
                else if (parser.getName().equals("organization")) {
                    project.setOrganization(new Organization());
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("name")) {
                            project.getOrganization().setName(parser.nextText());
                        }
                        else if (parser.getName().equals("url")) {
                            project.getOrganization().setUrl(parser.nextText());
                        }
                        else if (parser.getName().equals("logo")) {
                            project.getOrganization().setLogo(parser.nextText());
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("repository")) {
                    project.setRepository(new Repository());
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("connection")) {
                            project.getRepository().setConnection(parser.nextText());
                        }
                        else if (parser.getName().equals("developerConnection")) {
                            project.getRepository().setDeveloperConnection(parser.nextText());
                        }
                        else if (parser.getName().equals("url")) {
                            project.getRepository().setUrl(parser.nextText());
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("versions")) {
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("version")) {
                            Version v = new Version();
                            project.addVersion(v);
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("id")) {
                                    String id = parser.nextText();
                                    v.setId(id);
                                }
                                else if (parser.getName().equals("name")) {
                                    v.setName(parser.nextText());
                                }
                                else if (parser.getName().equals("tag")) {
                                    v.setTag(parser.nextText());
                                }
                                else {
                                    parser.nextText();
                                }
                            }
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("branches")) {
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("branch")) {
                            Branch b = new Branch();
                            project.addBranch(b);
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("tag")) {
                                    b.setTag(parser.nextText());
                                }
                                else {
                                    parser.nextText();
                                }
                            }
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("licenses")) {
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("license")) {
                            License l = new License();
                            project.addLicense(l);
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("name")) {
                                    l.setName(parser.nextText());
                                }
                                else if (parser.getName().equals("distribution")) {
                                    l.setDistribution(parser.nextText());
                                }
                                else if (parser.getName().equals("url")) {
                                    l.setUrl(parser.nextText());
                                }
                                else if (parser.getName().equals("comments")) {
                                    l.setComments(parser.nextText());
                                }
                                else {
                                    parser.nextText();
                                }
                            }
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("mailingLists")) {
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("mailingList")) {
                            MailingList ml = new MailingList();
                            project.addMailingList(ml);
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("name")) {
                                    ml.setName(parser.nextText());
                                }
                                else if (parser.getName().equals("subscribe")) {
                                    ml.setSubscribe(parser.nextText());
                                }
                                else if (parser.getName().equals("unsubscribe")) {
                                    ml.setUnsubscribe(parser.nextText());
                                }
                                else if (parser.getName().equals("archive")) {
                                    ml.setArchive(parser.nextText());
                                }
                                else {
                                    parser.nextText();
                                }
                            }
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("developers")) {
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("developer")) {
                            Developer d = new Developer();
                            project.addDeveloper(d);
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("id")) {
                                    d.setId(parser.nextText());
                                }
                                else {
                                    unmarshallContributorDetails(parser, d);
                                }
                            }
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("contributors")) {
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("contributor")) {
                            Contributor c = new Contributor();
                            project.addContributor(c);
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                unmarshallContributorDetails(parser, c);
                            }
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("dependencies")) {
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("dependency")) {
                            Dependency d = new Dependency();
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("groupId")) {
                                    d.setGroupId(parser.nextText());
                                }
                                else if (parser.getName().equals("artifactId")) {
                                    d.setArtifactId(parser.nextText());
                                }
                                else if (parser.getName().equals("id")) {
                                    String id = parser.nextText();
                                    DeprecationWarning.warn("You should be using groupId / artifactId on the Dependency - " + id);
                                    d.setId(id);
                                }
                                else if (parser.getName().equals("version")) {
                                    d.setVersion(parser.nextText());
                                }
                                else if (parser.getName().equals("type")) {
                                    d.setType(parser.nextText());
                                }
                                else if (parser.getName().equals("url")) {
                                    d.setUrl(parser.nextText());
                                }
                                else if (parser.getName().equals("artifact")) {
                                    d.setJar(parser.nextText());
                                }
                                else if (parser.getName().equals("jar")) {
                                    d.setJar(parser.nextText());
                                    String msg = "You should be using <artifact> tag " + "instead of <jar> for Dependency: "
                                            + d.getJar();
                                    DeprecationWarning.warn(msg);
                                }
                                // E X P E R I M E N T A L
                                // 
                                // istead of
                                //  <properties>
                                //    <war.bundle>true</war.bundle>
                                //  </properties>
                                //
                                //  which is not very XML'ish enayway
                                //  we will have:
                                //  <properties>
                                //     war.bundle=true
                                //     foo=baa
                                //  </properties>
                                //
                                // This is has two advantages:
                                // a) very simple to implement
                                // b) simpler to use for end user
                                //
                                //
                                //
                                else if (parser.getName().equals("properties")) {
                                    //Supporting current dependency properties
                                    // declaration
                                    //String str = parser.nextText();
                                    //ByteArrayInputStream bais
                                    //   = new ByteArrayInputStream(
                                    // str.getBytes() );
                                    //Properties properties = new Properties();
                                    //properties.load( bais );
                                    //d.setProperties( properties );
                                    //gdodinet
                                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                                        String pname = parser.getName();
                                        String pvalue = parser.nextText();
                                        d.addProperty(pname, pvalue);
                                        //crappy trick to initialize properly
                                        // the map..
                                        //shouldnot be necessary. however if i
                                        // dont do
                                        //that it doesnot seem to work
                                        //anyway this is only a temp solution
                                        // (see above)
                                        d.getProperties().put(pname, pvalue);
                                    }
                                }
                                else {
                                    parser.nextText();
                                }
                            }
                            d.setId(d.getGroupId() +":" +d.getArtifactId());
                            project.addDependency(d);
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("build")) {
                    Build b = new Build();
                    project.setBuild(b);
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("nagEmailAddress")) {
                            b.setNagEmailAddress(parser.nextText());
                        }
                        else if (parser.getName().equals("sourceDirectory")) {
                            b.setSourceDirectory(parser.nextText());
                        }
                        else if (parser.getName().equals("sourceModifications")) {
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("sourceModification")) {
                                    SourceModification sourceModification = new SourceModification();
                                    b.addSourceModification(sourceModification);
                                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                                        if (parser.getName().equals("className")) {
                                            sourceModification.setClassName(parser.nextText());
                                        }
                                        else if (parser.getName().equals("includes")) {
                                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                                if (parser.getName().equals("include")) {
                                                    sourceModification.addInclude(parser.nextText());
                                                }
                                                else {
                                                    parser.nextText();
                                                }
                                            }
                                        }
                                        else if (parser.getName().equals("excludes")) {
                                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                                if (parser.getName().equals("exclude")) {
                                                    sourceModification.addExclude(parser.nextText());
                                                }
                                                else {
                                                    parser.nextText();
                                                }
                                            }
                                        }
                                        else {
                                            parser.nextText();
                                        }
                                    }
                                }
                                else {
                                    parser.nextText();
                                }
                            }
                        }
                        else if (parser.getName().equals("aspectSourceDirectory")) {
                            b.setAspectSourceDirectory(parser.nextText());
                        }
                        else if (parser.getName().equals("integrationUnitTestSourceDirectory")) {
                            b.setIntegrationUnitTestSourceDirectory(parser.nextText());
                        }
                        else if (parser.getName().equals("unitTestSourceDirectory")) {
                            b.setUnitTestSourceDirectory(parser.nextText());
                        }
                        else if (parser.getName().equals("unitTest")) {
                            UnitTest ut = new UnitTest();
                            b.setUnitTest(ut);
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("includes")) {
                                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                                        if (parser.getName().equals("include")) {
                                            ut.addInclude(parser.nextText());
                                        }
                                        else {
                                            parser.nextText();
                                        }
                                    }
                                }
                                else if (parser.getName().equals("excludes")) {
                                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                                        if (parser.getName().equals("exclude")) {
                                            ut.addExclude(parser.nextText());
                                        }
                                        else {
                                            parser.nextText();
                                        }
                                    }
                                }
                                else if (parser.getName().equals("resources")) {
                                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                                        if (parser.getName().equals("resource")) {
                                            ut.addResource(unmarshalResource(parser));
                                        }
                                        else {
                                            parser.nextText();
                                        }
                                    }
                                }
                                else {
                                    parser.nextText();
                                }
                            }
                        }
                        else if (parser.getName().equals("resources")) {
                            while (parser.nextTag() == XmlPullParser.START_TAG) {
                                if (parser.getName().equals("resource")) {
                                    b.addResource(unmarshalResource(parser));
                                }
                                else {
                                    parser.nextText();
                                }
                            }
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
                else if (parser.getName().equals("reports")) {
                    while (parser.nextTag() == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("report")) {
                            project.addReport(parser.nextText());
                        }
                        else {
                            parser.nextText();
                        }
                    }
                }
            }
            eventType = parser.next();
        }
        return project;
    }

    private void unmarshallContributorDetails(XmlPullParser parser, Contributor c) throws XmlPullParserException, IOException {
        if (parser.getName().equals("name")) {
            c.setName(parser.nextText());
        }
        else if (parser.getName().equals("email")) {
            c.setEmail(parser.nextText());
        }
        else if (parser.getName().equals("organization")) {
            c.setOrganization(parser.nextText());
        }
        else if (parser.getName().equals("url")) {
            c.setUrl(parser.nextText());
        }
        else if (parser.getName().equals("timezone")) {
            c.setTimezone(parser.nextText());
        }
        else if (parser.getName().equals("roles")) {
            while (parser.nextTag() == XmlPullParser.START_TAG) {
                if (parser.getName().equals("role")) {
                    c.addRole(parser.nextText());
                }
                else {
                    parser.nextText();
                }
            }
        }
        else {
            parser.nextText();
        }
    }

    protected Resource unmarshalResource(XmlPullParser parser) throws XmlPullParserException, IOException {
        Resource r = new Resource();
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            if (parser.getName().equals("directory")) {
                r.setDirectory(parser.nextText());
            }
            else if (parser.getName().equals("targetPath")) {
                r.setTargetPath(parser.nextText());
            }
            else if (parser.getName().equals("filtering")) {
                r.setFiltering(Boolean.valueOf(parser.nextText()).booleanValue());
            }
            else if (parser.getName().equals("includes")) {
                while (parser.nextTag() == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("include")) {
                        r.addInclude(parser.nextText());
                    }
                    else {
                        parser.nextText();
                    }
                }
            }
            else if (parser.getName().equals("excludes")) {
                while (parser.nextTag() == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("exclude")) {
                        r.addExclude(parser.nextText());
                    }
                    else {
                        parser.nextText();
                    }
                }
            }
            else {
                parser.nextText();
            }
        }
        return r;
    }
}