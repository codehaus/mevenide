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
package org.mevenide.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: PostGoal.java 21 avr. 2003 10:52:5713:34:35 Exp gdodinet 
 * 
 */
public class PostGoal {
	
	private PostGoal() {
	}

    public static void create(File mavenXml) {
				Document doc = new Document();
		Namespace gdfact =
			Namespace.getNamespace(
				"g",
				"http://oss.gdfact.com/2003/maven/get-goals");
		Namespace jellyCore = Namespace.getNamespace("j", "jelly:core");

		Element root = new Element("project");
		root.addNamespaceDeclaration(gdfact);
		root.addNamespaceDeclaration(jellyCore);

		Comment authent =
			new Comment(
				" Generated by Eclipse plugin for Maven. please dont edit. "
					+ new Date()
					+ " ");
		root.addContent(authent);

		Element postGoal = new Element("postGoal");
		postGoal.setAttribute("name", "eclipse:get-goals");
		root.addContent(postGoal);

		Element set = new Element("set", jellyCore);
		set.setAttribute("var", "output");
		set.setAttribute(
			"value",
			new File(mavenXml.getParentFile(), "maven-goals.xml")
				.getAbsolutePath());
		postGoal.addContent(set);

		Element storeGoals = new Element("store-goals", gdfact);
		storeGoals.setAttribute("attr", "output");
		storeGoals.addContent("${output}");
		postGoal.addContent(storeGoals);

		doc.setRootElement(root);

		try {
			JDomOutputter.output(doc, mavenXml);
		} 
        catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static boolean validate(File mavenXml, String output) {
		if (!mavenXml.exists()) {
			return false;
		}
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(mavenXml);
			Element projectRoot = doc.getRootElement();
			List postGoals = projectRoot.getChildren("postGoal");
			for (int i = 0; i < postGoals.size(); i++) {
				Element postGoal = ((Element) postGoals.get(i));
				ResourceBundle rb = ResourceBundle.getBundle("mevenide");
                if (postGoal
					.getAttribute("name")
					.equals(rb.getString("goals.grabber.name"))) {
					return postGoal.getText().equals(output);
				}
			}
			return false;
		} 
        catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
