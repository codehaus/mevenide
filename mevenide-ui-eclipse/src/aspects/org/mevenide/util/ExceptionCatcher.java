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
 */
package org.mevenide.util;

import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public privileged aspect ExceptionCatcher {
	
	interface Loggable {}
	
	declare parents : org.mevenide..* implements Loggable;
	
	private Map loggers = new HashMap();
	
	void around(Throwable ex, Loggable loggable): 
			call(void Exception.printStackTrace()) 
			&& !within(ExceptionCatcher)
			&& target(ex) && this(loggable){
		Log logger = getLogger(loggable);
		logger.error(ex.getStackTrace());
	}
	
	private Log getLogger(Loggable loggable) {
		try {
			Class clazz = loggable.getClass();
			if ( loggers.containsKey(clazz) ) {
				return (Log) loggers.get(clazz);
			}
			Log logger = LogFactory.getLog(clazz);
			loggers.put(clazz, logger);
			return logger;
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
			return null;
		}
	}
}
