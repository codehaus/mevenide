/*
 *  Copyright 2008 mkleint.
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

package org.netbeans.modules.maven.embedder;

import java.io.File;
import java.util.Properties;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.path.DefaultPathTranslator;
import org.apache.maven.project.path.PathTranslator;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import hidden.org.codehaus.plexus.util.introspection.ReflectionValueExtractor;

/**
 * a stripped down version of plugin parameter expression evaluator
 * (PluginParameterExpressionEvaluator)
 * @author mkleint
 */
public class NBPluginParameterExpressionEvaluator 
    implements ExpressionEvaluator
{
    private final PathTranslator pathTranslator;

    private final MavenProject project;

    private final String basedir;

    private final Properties properties;
    
    private Settings settings;

    public NBPluginParameterExpressionEvaluator( 
                                               MavenProject project, 
                                               Settings settings,
                                               Properties properties )
    {
        this.pathTranslator = new DefaultPathTranslator();
        this.properties = properties;
        this.project = project;
        this.settings = settings;

        String basedir = null;

        if ( project != null )
        {
            File projectFile = project.getBasedir();

            // this should always be the case for non-super POM instances...
            if ( projectFile != null )
            {
                basedir = projectFile.getAbsolutePath();
            }
        }

        if ( basedir == null )
        {
            basedir = System.getProperty( "user.dir" );
        }

        this.basedir = basedir;
    }


    public Object evaluate( String expr )
        throws ExpressionEvaluationException
    {
        Object value = null;

        if ( expr == null )
        {
            return null;
        }

        String expression = stripTokens( expr );
        if ( expression.equals( expr ) )
        {
            int index = expr.indexOf( "${" );
            if ( index >= 0 )
            {
                int lastIndex = expr.indexOf( "}", index );
                if ( lastIndex >= 0 )
                {
                    String retVal = expr.substring( 0, index );

                    if ( ( index > 0 ) && ( expr.charAt( index - 1 ) == '$' ) )
                    {
                        retVal += expr.substring( index + 1, lastIndex + 1 );
                    }
                    else
                    {
                        Object subResult = evaluate( expr.substring( index, lastIndex + 1 ) );

                        if ( subResult != null )
                        {
                            retVal += subResult;
                        }
                        else
                        {
                            retVal += "$" + expr.substring( index + 1, lastIndex + 1 );
                        }
                    }

                    retVal += evaluate( expr.substring( lastIndex + 1 ) );
                    return retVal;
                }
            }

            // Was not an expression
            if ( expression.indexOf( "$$" ) > -1 )
            {
                return expression.replaceAll( "\\$\\$", "\\$" );
            }
            else
            {
                return expression;
            }
        }

//        if ( "localRepository".equals( expression ) )
//        {
//            value = context.getLocalRepository();
//        }
//        else if ( "session".equals( expression ) )
//        {
//            value = context;
//        }
//        else if ( "reactorProjects".equals( expression ) )
//        {
//            value = context.getSortedProjects();
//        }
//        else if ( "reports".equals( expression ) )
//        {
//            value = context.getReports();
//        }
//        else if ("mojoExecution".equals(expression))
//        {
//        	value = mojoExecution;
//        }
        else if ( "project".equals( expression ) )
        {
            value = project;
        }
        else if ( "executedProject".equals( expression ) )
        {
            value = project.getExecutionProject();
        }
        else if ( expression.startsWith( "project" ) )
        {
            try
            {
                int pathSeparator = expression.indexOf( "/" );

                if ( pathSeparator > 0 )
                {
                    String pathExpression = expression.substring( 0, pathSeparator );
                    value = ReflectionValueExtractor.evaluate( pathExpression, project );
                    value = value + expression.substring( pathSeparator );
                }
                else
                {
                    value = ReflectionValueExtractor.evaluate( expression.substring( 1 ), project );
                }
            }
            catch ( Exception e )
            {
                // TODO: don't catch exception
                throw new ExpressionEvaluationException( "Error evaluating plugin parameter expression: " + expression,
                                                         e );
            }
        }
//        else if ( expression.equals( "mojo" ) )
//        {
//            value = mojoExecution;
//        }
//        else if ( expression.startsWith( "mojo" ) )
//        {
//            try
//            {
//                int pathSeparator = expression.indexOf( "/" );
//
//                if ( pathSeparator > 0 )
//                {
//                    String pathExpression = expression.substring( 1, pathSeparator );
//                    value = ReflectionValueExtractor.evaluate( pathExpression, mojoExecution );
//                    value = value + expression.substring( pathSeparator );
//                }
//                else
//                {
//                    value = ReflectionValueExtractor.evaluate( expression.substring( 1 ), mojoExecution );
//                }
//            }
//            catch ( Exception e )
//            {
//                // TODO: don't catch exception
//                throw new ExpressionEvaluationException( "Error evaluating plugin parameter expression: " + expression,
//                                                         e );
//            }
//        }
//        else if ( expression.equals( "plugin" ) )
//        {
//            value = mojoDescriptor.getPluginDescriptor();
//        }
//        else if ( expression.startsWith( "plugin" ) )
//        {
//            try
//            {
//                int pathSeparator = expression.indexOf( "/" );
//
//                PluginDescriptor pluginDescriptor = mojoDescriptor.getPluginDescriptor();
//
//                if ( pathSeparator > 0 )
//                {
//                    String pathExpression = expression.substring( 1, pathSeparator );
//                    value = ReflectionValueExtractor.evaluate( pathExpression, pluginDescriptor );
//                    value = value + expression.substring( pathSeparator );
//                }
//                else
//                {
//                    value = ReflectionValueExtractor.evaluate( expression.substring( 1 ), pluginDescriptor );
//                }
//            }
//            catch ( Exception e )
//            {
//                // TODO: don't catch exception
//                throw new ExpressionEvaluationException( "Error evaluating plugin parameter expression: " + expression,
//                                                         e );
//            }
//        }
        else if ( "settings".equals( expression ) )
        {
            value = settings;
        }
        else if ( expression.startsWith( "settings" ) )
        {
            try
            {
                int pathSeparator = expression.indexOf( "/" );

                if ( pathSeparator > 0 )
                {
                    String pathExpression = expression.substring( 1, pathSeparator );
                    value = ReflectionValueExtractor.evaluate( pathExpression, settings );
                    value = value + expression.substring( pathSeparator );
                }
                else
                {
                    value = ReflectionValueExtractor.evaluate( expression.substring( 1 ), settings );
                }
            }
            catch ( Exception e )
            {
                // TODO: don't catch exception
                throw new ExpressionEvaluationException( "Error evaluating plugin parameter expression: " + expression,
                                                         e );
            }
        }
        else if ( "basedir".equals( expression ) )
        {
            value = basedir;
        }
        else if ( expression.startsWith( "basedir" ) )
        {
            int pathSeparator = expression.indexOf( "/" );

            if ( pathSeparator > 0 )
            {
                value = basedir + expression.substring( pathSeparator );
            }
            else
            {
//                logger.error( "Got expression '" + expression + "' that was not recognised" );
            }
        }

        if ( value == null )
        {
            // The CLI should win for defining properties

            if ( ( value == null ) && ( properties != null ) )
            {
                // We will attempt to get nab a system property as a way to specify a
                // parameter to a plugins. My particular case here is allowing the surefire
                // plugin to run a single test so I want to specify that class on the cli
                // as a parameter.

                value = properties.getProperty( expression );
            }

            if ( ( value == null ) && ( ( project != null ) && ( project.getProperties() != null ) ) )
            {
                value = project.getProperties().getProperty( expression );
            }

        }

        if ( value instanceof String )
        {
            // TODO: without #, this could just be an evaluate call...

            String val = (String) value;

            int exprStartDelimiter = val.indexOf( "${" );

            if ( exprStartDelimiter >= 0 )
            {
                if ( exprStartDelimiter > 0 )
                {
                    value = val.substring( 0, exprStartDelimiter ) + evaluate( val.substring( exprStartDelimiter ) );
                }
                else
                {
                    value = evaluate( val.substring( exprStartDelimiter ) );
                }
            }
        }

        return value;
    }

    private String stripTokens( String expr )
    {
        if ( expr.startsWith( "${" ) && ( expr.indexOf( "}" ) == expr.length() - 1 ) )
        {
            expr = expr.substring( 2, expr.length() - 1 );
        }
        return expr;
    }

    public File alignToBaseDirectory( File file )
    {
        return new File( pathTranslator.alignToBaseDirectory( file.getPath(), new File(basedir) ) );
    }


}
