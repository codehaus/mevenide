package org.mevenide.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.mevenide.util.DeprecationWarning;
import org.apache.maven.project.Build;
import org.apache.maven.project.Contributor;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Developer;
import org.apache.maven.project.MailingList;
import org.apache.maven.project.Organization;
import org.apache.maven.project.Project;
import org.apache.maven.project.Repository;
import org.apache.maven.project.Resource;
import org.apache.maven.project.UnitTest;
import org.apache.maven.project.Version;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public class DefaultProjectUnmarshaller
{
    public Project parse( Reader reader )
        throws Exception
    {
        Project project = new Project();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput( reader );

        int eventType = parser.getEventType();

        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( parser.getName().equals( "extend" ) )
                {
                    project.setExtend( parser.nextText() );
                }
                else if ( parser.getName().equals( "pomVersion" ) )
                {
                    project.setPomVersion( parser.nextText() );
                }
                else if ( parser.getName().equals( "groupId" ) )
                {
                    project.setGroupId( parser.nextText() );
                }
                else if ( parser.getName().equals( "artifactId" ) )
                {
                    project.setArtifactId( parser.nextText() );
                }
                else if ( parser.getName().equals( "id" ) )
                {
                    DeprecationWarning.warn( "<project/> element: You should be using groupId / artifactId" );
                    project.setId( parser.nextText() );
                }
                else if ( parser.getName().equals( "name" ) )
                {
                    project.setName( parser.nextText() );
                }
                else if ( parser.getName().equals( "currentVersion" ) )
                {
                    project.setCurrentVersion( parser.nextText() );
                }
                else if ( parser.getName().equals( "inceptionYear" ) )
                {
                    project.setInceptionYear( parser.nextText() );
                }
                else if ( parser.getName().equals( "package" ) )
                {
                    project.setPackage( parser.nextText() );
                }
                else if ( parser.getName().equals( "gumpRepositoryId" ) )
                {
                    project.setGumpRepositoryId( parser.nextText() );
                }
                else if ( parser.getName().equals( "description" ) )
                {
                    project.setDescription( parser.nextText() );
                }
                else if ( parser.getName().equals( "shortDescription" ) )
                {
                    project.setShortDescription( parser.nextText() );
                }
                else if ( parser.getName().equals( "url" ) )
                {
                    project.setUrl( parser.nextText() );
                }
                else if ( parser.getName().equals( "issueTrackingUrl" ) )
                {
                    project.setIssueTrackingUrl( parser.nextText() );
                }
                else if ( parser.getName().equals( "siteAddress" ) )
                {
                    project.setSiteAddress( parser.nextText() );
                }
                else if ( parser.getName().equals( "siteDirectory" ) )
                {
                    project.setSiteDirectory( parser.nextText() );
                }
                else if ( parser.getName().equals( "distributionDirectory" ) )
                {
                    project.setDistributionDirectory( parser.nextText() );
                }
                else if ( parser.getName().equals( "organization" ) )
                {
                    project.setOrganization( new Organization() );

                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "name" ) )
                        {
                            project.getOrganization().setName( parser.nextText() );
                        }
                        else if ( parser.getName().equals( "url" ) )
                        {
                            project.getOrganization().setUrl( parser.nextText() );

                        }
                        else if ( parser.getName().equals( "logo" ) )
                        {
                            project.getOrganization().setLogo( parser.nextText() );

                        }
                        else
                        {
                            parser.nextText();
                        }
                    }
                }
                else if ( parser.getName().equals( "repository" ) )
                {
                    project.setRepository( new Repository() );

                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "connection" ) )
                        {
                            project.getRepository().setConnection( parser.nextText() );
                        }
                        else if ( parser.getName().equals( "developerConnection" ) )
                        {
                            project.getRepository().setConnection( parser.nextText() );
                        }
                        else if ( parser.getName().equals( "url" ) )
                        {
                            project.getRepository().setUrl( parser.nextText() );
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }
                }
                else if ( parser.getName().equals( "versions" ) )
                {
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "version" ) )
                        {
                            Version v = new Version();
                            project.addVersion( v );

                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "id" ) )
                                {
                                    String id = parser.nextText();
                                    v.setId( id );
                                }
                                else if ( parser.getName().equals( "name" ) )
                                {
                                    v.setName( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "tag" ) )
                                {
                                    v.setTag( parser.nextText() );
                                }
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }
                }
                else if ( parser.getName().equals( "mailingLists" ) )
                {
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "mailingList" ) )
                        {
                            MailingList ml = new MailingList();
                            project.addMailingList( ml );

                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "name" ) )
                                {
                                    ml.setName( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "subscribe" ) )
                                {
                                    ml.setSubscribe( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "unsubscribe" ) )
                                {
                                    ml.setUnsubscribe( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "archive" ) )
                                {
                                    ml.setArchive( parser.nextText() );
                                }
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }

                }
                else if ( parser.getName().equals( "developers" ) )
                {
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "developer" ) )
                        {
                            Developer d = new Developer();
                            project.addDeveloper( d );

                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "id" ) )
                                {
                                    d.setId( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "name" ) )
                                {
                                    d.setName( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "email" ) )
                                {
                                    d.setEmail( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "organization" ) )
                                {
                                    d.setOrganization( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "roles" ) )
                                {
                                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                                    {
                                        if ( parser.getName().equals( "role" ) )
                                        {
                                            d.addRole( parser.nextText() );
                                        }
                                        else
                                        {
                                            parser.nextText();
                                        }
                                    }
                                }
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }
                }
                else if ( parser.getName().equals( "contributors" ) )
                {
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "contributor" ) )
                        {
                            Contributor c = new Contributor();
                            project.addContributor( c );

                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "name" ) )
                                {
                                    c.setName( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "email" ) )
                                {
                                    c.setEmail( parser.nextText() );
                                }
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }
                }
                else if ( parser.getName().equals( "dependencies" ) )
                {
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "dependency" ) )
                        {
                            Dependency d = new Dependency();
                            project.addDependency( d );

                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "groupId" ) )
                                {
                                    d.setGroupId( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "artifactId" ) )
                                {
                                    d.setArtifactId( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "id" ) )
                                {
                                    String id = parser.nextText();
                                    DeprecationWarning.warn(
                                        "You should be using groupId / artifactId on the Dependency - " + id );
                                    d.setId( id );
                                }
                                else if ( parser.getName().equals( "version" ) )
                                {
                                    d.setVersion( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "type" ) )
                                {
                                    d.setType( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "url" ) )
                                {
                                    d.setUrl( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "artifact" ) )
                                {
                                    d.setJar( parser.nextText() );
                                }
                                else if ( parser.getName().equals( "jar" ) )
                                {
                                   String msg = "You should be using <artifact> tag " +
                                              "instead of <jar> for Dependency: " + 
                                              d.getId(); 
                                   DeprecationWarning.warn( msg );                                   
                                   d.setJar( parser.nextText() );
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
                                else if ( parser.getName().equals( "properties" ) ) 
                                {
                                    //Supporting current dependency properties declaration
                                    
									//String str = parser.nextText();
									//ByteArrayInputStream bais 
									//   = new ByteArrayInputStream( str.getBytes() );
									//Properties properties = new Properties();
									//properties.load( bais );
									//d.setProperties( properties );
									
									//gdodinet
									Properties props = new Properties();
									while ( parser.nextTag() == XmlPullParser.START_TAG) {
										String pname = parser.getName();
										
										String pvalue = parser.nextText();
										String combinedProperty = pname + ":" + pvalue;
										
										d.addProperty(combinedProperty);
									
										//crappy trick to initialize properly the map.. 
										//shouldnot be necessary. however if i dont do 
										//that it doesnot seem to work
										//anyway this is only a temp solution (see above)
										d.resolvedProperties().put(pname, pvalue);
										
									}
                                 }
                                
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }
                }
                else if ( parser.getName().equals( "build" ) )
                {
                    Build b = new Build();
                    project.setBuild( b );

                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "nagEmailAddress" ) )
                        {
                            b.setNagEmailAddress( parser.nextText() );
                        }
                        else if ( parser.getName().equals( "sourceDirectory" ) )
                        {
                            b.setSourceDirectory( parser.nextText() );
                        }
						else if ( parser.getName().equals( "aspectSourceDirectory" ) )
						{
							b.setAspectSourceDirectory( parser.nextText() );
						}
						else if ( parser.getName().equals( "integrationUnitTestSourceDirectory" ) )
						{
							b.setIntegrationUnitTestSourceDirectory( parser.nextText() );
						}
                        else if ( parser.getName().equals( "unitTestSourceDirectory" ) )
                        {
                            b.setUnitTestSourceDirectory( parser.nextText() );
                        }
                        else if ( parser.getName().equals( "unitTest" ) )
                        {
                            UnitTest ut = new UnitTest();
                            b.setUnitTest( ut );

                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "includes" ) )
                                {
                                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                                    {
                                        if ( parser.getName().equals( "include" ) )
                                        {
                                            ut.addInclude( parser.nextText() );
                                        }
                                        else
                                        {
                                            parser.nextText();
                                        }
                                    }

                                }
                                else if ( parser.getName().equals( "excludes" ) )
                                {
                                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                                    {
                                        if ( parser.getName().equals( "exclude" ) )
                                        {
                                            ut.addExclude( parser.nextText() );
                                        }
                                        else
                                        {
                                            parser.nextText();
                                        }
                                    }
                                }
                                else if ( parser.getName().equals( "resources" ) )
                                {
                                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                                    {
                                        if ( parser.getName().equals( "resource" ) )
                                        {
                                            ut.addResource( unmarshalResource( parser ) );
                                        }
                                        else
                                        {
                                            parser.nextText();
                                        }
                                    }
                                }
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else if ( parser.getName().equals( "resources" ) )
                        {
                            while ( parser.nextTag() == XmlPullParser.START_TAG )
                            {
                                if ( parser.getName().equals( "resource" ) )
                                {
                                    b.addResource( unmarshalResource( parser ) );
                                }
                                else
                                {
                                    parser.nextText();
                                }
                            }
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }

                }
                else if ( parser.getName().equals( "reports" ) )
                {
                    while ( parser.nextTag() == XmlPullParser.START_TAG )
                    {
                        if ( parser.getName().equals( "report" ) )
                        {
                            project.addReport( parser.nextText() );
                        }
                        else
                        {
                            parser.nextText();
                        }
                    }
                }
            }
            
            eventType = parser.next();
        }

        return project;
    }

    protected Resource unmarshalResource( XmlPullParser parser ) throws XmlPullParserException, IOException
    {
        Resource r = new Resource();

        while ( parser.nextTag() == XmlPullParser.START_TAG )
        {
            if ( parser.getName().equals( "directory" ) )
            {
                r.setDirectory( parser.nextText() );
            }
            else if ( parser.getName().equals( "targetPath" ) )
            {
                r.setTargetPath( parser.nextText() );
            }
            else if ( parser.getName().equals( "includes" ) )
            {
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( parser.getName().equals( "include" ) )
                    {
                        r.addInclude( parser.nextText() );
                    }
                    else
                    {
                        parser.nextText();
                    }
                }

            }
            else if ( parser.getName().equals( "excludes" ) )
            {
                while ( parser.nextTag() == XmlPullParser.START_TAG )
                {
                    if ( parser.getName().equals( "exclude" ) )
                    {
                        r.addExclude( parser.nextText() );
                    }
                    else
                    {
                        parser.nextText();
                    }
                }
            }
            else
            {
                parser.nextText();
            }
        }
        return r;
    }
}
