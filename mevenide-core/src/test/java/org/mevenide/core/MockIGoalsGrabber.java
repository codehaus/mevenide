/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.core;

/**
 * 
 * generated by MockCreator (http://www.abstrakt.de/mockcreator.html)
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: MockIGoalsGrabber.java 20 avr. 2003 21:03:4513:34:35 Exp gdodinet 
 * 
 */
public class MockIGoalsGrabber extends de.abstrakt.mock.MockObject implements org.mevenide.IGoalsGrabber
{
	private java.lang.Object	_createMavenXmlFileDummyValue;
	private java.lang.Boolean	_createMavenXmlFileExpectFlag;
	private java.lang.Object	_getDescriptionDummyValue;
	private java.lang.Boolean	_getDescriptionExpectFlag;
	private java.lang.Object	_getGoalsDummyValue;
	private java.lang.Boolean	_getGoalsExpectFlag;
	private java.lang.Object	_getPluginsDummyValue;
	private java.lang.Boolean	_getPluginsExpectFlag;
	private java.lang.Object	_loadDummyValue;
	private java.lang.Boolean	_loadExpectFlag;
	private java.lang.Object	_load_StringDummyValue;
	private java.lang.Boolean	_load_StringExpectFlag;

	public java.lang.String getDescription( java.lang.String plugin, java.lang.String goal )
	{
		Object returnValue = null;
		if( _getDescriptionExpectFlag != null && _getDescriptionExpectFlag.booleanValue() )
		{
			returnValue = addActualManyToExpectationList( new Object[] { "getDescription", plugin, goal } );
		}
		else
		{
			returnValue = _getDescriptionDummyValue;
		}
		if( returnValue instanceof RuntimeException )
		{
			throw (RuntimeException)returnValue;
		}
		if( returnValue instanceof Error )
		{
			throw (Error)returnValue;
		}
		return (java.lang.String)returnValue;
	}

	public java.util.Collection getGoals( java.lang.String plugin )
	{
		Object returnValue = null;
		if( _getGoalsExpectFlag != null && _getGoalsExpectFlag.booleanValue() )
		{
			returnValue = addActualManyToExpectationList( new Object[] { "getGoals", plugin } );
		}
		else
		{
			returnValue = _getGoalsDummyValue;
		}
		if( returnValue instanceof RuntimeException )
		{
			throw (RuntimeException)returnValue;
		}
		if( returnValue instanceof Error )
		{
			throw (Error)returnValue;
		}
		return (java.util.Collection)returnValue;
	}

	public java.util.Collection getPlugins()
	{
		Object returnValue = null;
		if( _getPluginsExpectFlag != null && _getPluginsExpectFlag.booleanValue() )
		{
			returnValue = addActualManyToExpectationList( new Object[] { "getPlugins" } );
		}
		else
		{
			returnValue = _getPluginsDummyValue;
		}
		if( returnValue instanceof RuntimeException )
		{
			throw (RuntimeException)returnValue;
		}
		if( returnValue instanceof Error )
		{
			throw (Error)returnValue;
		}
		return (java.util.Collection)returnValue;
	}

	public void createMavenXmlFile( java.lang.String effectiveDirectory, java.lang.String output )
	{
		Object returnValue = null;
		if( _createMavenXmlFileExpectFlag != null && _createMavenXmlFileExpectFlag.booleanValue() )
		{
			returnValue = addActualManyToExpectationList( new Object[] { "createMavenXmlFile", effectiveDirectory, output } );
		}
		else
		{
			returnValue = _createMavenXmlFileDummyValue;
		}
		if( returnValue instanceof RuntimeException )
		{
			throw (RuntimeException)returnValue;
		}
		if( returnValue instanceof Error )
		{
			throw (Error)returnValue;
		}
	}

	public void expectCreateMavenXmlFile( java.lang.String effectiveDirectory, java.lang.String output )
	{
		_createMavenXmlFileExpectFlag = checkDummy( _createMavenXmlFileExpectFlag, true );
		addExpectedMethodCall( new Object[] { "createMavenXmlFile", effectiveDirectory, output } );
	}

	public void expectCreateMavenXmlFile( java.lang.String effectiveDirectory, java.lang.String output, Throwable throwable )
	{
		_createMavenXmlFileExpectFlag = checkDummy( _createMavenXmlFileExpectFlag, true );
		addExpectedMethodCall( new Object[] { "createMavenXmlFile", effectiveDirectory, output }, throwable );
	}

	public void expectGetDescription( java.lang.String plugin, java.lang.String goal, Throwable throwable )
	{
		_getDescriptionExpectFlag = checkDummy( _getDescriptionExpectFlag, true );
		addExpectedMethodCall( new Object[] { "getDescription", plugin, goal }, throwable );
	}

	public void expectGetDescription( java.lang.String plugin, java.lang.String goal, java.lang.String returnValue )
	{
		_getDescriptionExpectFlag = checkDummy( _getDescriptionExpectFlag, true );
		addExpectedMethodCall( new Object[] { "getDescription", plugin, goal }, returnValue );
	}

	public void expectGetGoals( java.lang.String plugin, Throwable throwable )
	{
		_getGoalsExpectFlag = checkDummy( _getGoalsExpectFlag, true );
		addExpectedMethodCall( new Object[] { "getGoals", plugin }, throwable );
	}

	public void expectGetGoals( java.lang.String plugin, java.util.Collection returnValue )
	{
		_getGoalsExpectFlag = checkDummy( _getGoalsExpectFlag, true );
		addExpectedMethodCall( new Object[] { "getGoals", plugin }, returnValue );
	}

	public void expectGetPlugins( Throwable throwable )
	{
		_getPluginsExpectFlag = checkDummy( _getPluginsExpectFlag, true );
		addExpectedMethodCall( new Object[] { "getPlugins" }, throwable );
	}

	public void expectGetPlugins( java.util.Collection returnValue )
	{
		_getPluginsExpectFlag = checkDummy( _getPluginsExpectFlag, true );
		addExpectedMethodCall( new Object[] { "getPlugins" }, returnValue );
	}

	public void expectLoad( Throwable throwable )
	{
		_loadExpectFlag = checkDummy( _loadExpectFlag, true );
		addExpectedMethodCall( new Object[] { "load" }, throwable );
	}

	public void expectLoad( java.lang.String xmlGoals )
	{
		_load_StringExpectFlag = checkDummy( _load_StringExpectFlag, true );
		addExpectedMethodCall( new Object[] { "load", xmlGoals } );
	}

	public void expectLoad( java.lang.String xmlGoals, Throwable throwable )
	{
		_load_StringExpectFlag = checkDummy( _load_StringExpectFlag, true );
		addExpectedMethodCall( new Object[] { "load", xmlGoals }, throwable );
	}

	public void expectLoad()
	{
		_loadExpectFlag = checkDummy( _loadExpectFlag, true );
		addExpectedMethodCall( new Object[] { "load" } );
	}

	public void load( java.lang.String xmlGoals )
	{
		Object returnValue = null;
		if( _load_StringExpectFlag != null && _load_StringExpectFlag.booleanValue() )
		{
			returnValue = addActualManyToExpectationList( new Object[] { "load", xmlGoals } );
		}
		else
		{
			returnValue = _load_StringDummyValue;
		}
		if( returnValue instanceof RuntimeException )
		{
			throw (RuntimeException)returnValue;
		}
		if( returnValue instanceof Error )
		{
			throw (Error)returnValue;
		}
	}

	public void load() throws java.lang.Exception
	{
		Object returnValue = null;
		if( _loadExpectFlag != null && _loadExpectFlag.booleanValue() )
		{
			returnValue = addActualManyToExpectationList( new Object[] { "load" } );
		}
		else
		{
			returnValue = _loadDummyValue;
		}
		if( returnValue instanceof java.lang.Exception )
		{
			throw (java.lang.Exception)returnValue;
		}
		if( returnValue instanceof RuntimeException )
		{
			throw (RuntimeException)returnValue;
		}
		if( returnValue instanceof Error )
		{
			throw (Error)returnValue;
		}
	}

	public void setCreateMavenXmlFileDummy( Throwable throwable )
	{
		_createMavenXmlFileExpectFlag = checkDummy( _createMavenXmlFileExpectFlag, false );
		_createMavenXmlFileDummyValue = throwable;
	}

	public void setGetDescriptionDummy( Throwable throwable )
	{
		_getDescriptionExpectFlag = checkDummy( _getDescriptionExpectFlag, false );
		_getDescriptionDummyValue = throwable;
	}

	public void setGetDescriptionDummy( java.lang.String returnValue )
	{
		_getDescriptionExpectFlag = checkDummy( _getDescriptionExpectFlag, false );
		_getDescriptionDummyValue = returnValue;
	}

	public void setGetGoalsDummy( Throwable throwable )
	{
		_getGoalsExpectFlag = checkDummy( _getGoalsExpectFlag, false );
		_getGoalsDummyValue = throwable;
	}

	public void setGetGoalsDummy( java.util.Collection returnValue )
	{
		_getGoalsExpectFlag = checkDummy( _getGoalsExpectFlag, false );
		_getGoalsDummyValue = returnValue;
	}

	public void setGetPluginsDummy( Throwable throwable )
	{
		_getPluginsExpectFlag = checkDummy( _getPluginsExpectFlag, false );
		_getPluginsDummyValue = throwable;
	}

	public void setGetPluginsDummy( java.util.Collection returnValue )
	{
		_getPluginsExpectFlag = checkDummy( _getPluginsExpectFlag, false );
		_getPluginsDummyValue = returnValue;
	}

	public void setLoadDummy( Throwable throwable )
	{
		_loadExpectFlag = checkDummy( _loadExpectFlag, false );
		_loadDummyValue = throwable;
	}

	public void setLoad_StringDummy( Throwable throwable )
	{
		_load_StringExpectFlag = checkDummy( _load_StringExpectFlag, false );
		_load_StringDummyValue = throwable;
	}
}