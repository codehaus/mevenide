/*
 * Copyright (c) 2006 Bryan Kate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package org.codehaus.mevenide.idea.configuration;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.InvalidDataException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Collection;

import org.jdom.Element;
import org.jdom.Attribute;


/**
 * A JDOM externalizer that handles primitives, Strings, and some data structures. The data structures must be of type
 * Map or Collection, must be parameterized (generic), and the parameters must be primitives, Strings, or other
 * supported collections.
 *
 * todo: allow collections to take JDOMExternalizable objects
 *
 * @author bkate
 */
public class PluginJDOMExternalizer {

    // constants for creating and parsing XML elements
    private static final String MAP_ELEMENT = "map";
    private static final String COLLECTION_ELEMENT = "collection";
    private static final String VALUE_ELEMENT = "value";
    private static final String KEY_ELEMENT = "key";
    private static final String ENTRY_ELEMENT = "entry";
    private static final String VALUE_ATTRIBUTE = "value";
    private static final String TYPE_ATTRIBUTE = "type";

    private static final Logger LOG = Logger.getInstance("#org.codehaus.mevenide.idea.configuration.PluginJDOMExternalizer");


    /** {@inheritDoc} */
    public static void writeExternal(Object data, Element parentNode) throws WriteExternalException {

        if (parentNode == null) {
            return;
        }

        Field[] fields = data.getClass().getFields();

        for(int i = 0; i < fields.length; i++) {

            // operate on the fields of the class being written
            Field field = fields[i];

            if (field.getName().indexOf('$') >= 0) {
                continue;
            }

            int modifiers = field.getModifiers();
            String value = null;

            // make sure it is a public and non-static field
            if (((modifiers & Modifier.PUBLIC) == 0) || ((modifiers & Modifier.STATIC) != 0)) {
                continue;
            }

            // class might be non-public
            field.setAccessible(true);

            Class type = field.getType();

            Element element = new Element("option");
            element.setAttribute("name", field.getName());

            try {

                // handling primitives and Strings is easy
                if (type.isPrimitive() || type.equals(String.class)) {

                    value = writeBasicValue(field.get(data));

                    if (value != null) {
                        element.setAttribute("value", value);
                    }
                }
                else if (isSupportedCollection(type)) {

                    // check if this field is a collection that is typed as containing basic values
                    if (!isCollectionWritable(field.getGenericType())) {
                        LOG.debug("Cannot write field '" + field.getName() + "': invalid Collection.");
                        continue;
                    }

                    Element valueElement = new Element("value");

                    writeCollection(field.get(data), valueElement);
                    element.addContent(valueElement);
                }
                else if (JDOMExternalizable.class.isAssignableFrom(type)) {

                    // the field is not primitive, but it is externalizable
                    JDOMExternalizable domValue = (JDOMExternalizable)field.get(data);

                    if (domValue != null) {

                        // recurse into the child externalization
                        Element valueElement = new Element("value");

                        domValue.writeExternal(valueElement);
                        element.addContent(valueElement);
                    }
                }
                else {
                    LOG.debug("Wrong field type: " + type);
                    continue;
                }
            }
            catch (IllegalAccessException e) {
                continue;
            }

            parentNode.addContent(element);
        }
    }


    /** {@inheritDoc} */
    public static void readExternal(Object data, Element parentNode) throws InvalidDataException {

        if (parentNode == null) {
            return;
        }

        for (Iterator i = parentNode.getChildren("option").iterator(); i.hasNext();) {

            Element e = (Element)i.next();

            String fieldName = e.getAttributeValue("name");

            if (fieldName == null) {
                throw new InvalidDataException();
            }

            try {

                Field field = data.getClass().getField(fieldName);
                Class type = field.getType();

                int modifiers = field.getModifiers();

                if ((modifiers & Modifier.PUBLIC) == 0 || (modifiers & Modifier.STATIC) != 0 || (modifiers & Modifier.FINAL) != 0) {
                    continue;
                }

                // class might be non-public
                field.setAccessible(true);

                // read in a primitive or string
                if (type.isPrimitive() || type.equals(String.class)) {

                    String value = e.getAttributeValue("value");

                    if (value != null) {

                        try {
                            field.set(data, createBasicObject(field.getType(), value));
                        }
                        catch(IllegalAccessException iae) {
                            throw new InvalidDataException();
                        }
                    }
                }
                else if (isSupportedCollection(type)) {

                    // parse a collection
                    if (!isCollectionWritable(field.getGenericType())) {
                        LOG.debug("Cannot read field '" + field.getName() + "': invalid Collection.");
                        continue;
                    }

                    Element valueElement = e.getChild("value");

                    if (valueElement != null) {

                        Object collection = parseCollection(valueElement);

                        try {
                            field.set(data, collection);
                        }
                        catch(IllegalAccessException iae) {
                            throw new InvalidDataException();
                        }
                    }
                }
                else if (JDOMExternalizable.class.isAssignableFrom(type)) {

                    // read in another externalized object
                    JDOMExternalizable object = null;

                    for (Iterator j = e.getChildren("value").iterator(); j.hasNext();) {

                        Element el = (Element)j.next();

                        object = (JDOMExternalizable)type.newInstance();
                        object.readExternal(el);
                    }

                    field.set(data, object);
                }
                else {
                    throw new InvalidDataException("wrong type: " + type);
                }
            }
            catch (NoSuchFieldException ex) {
                continue;
            }
            catch (SecurityException ex) {
                throw new InvalidDataException();
            }
            catch (IllegalAccessException ex) {
                ex.printStackTrace();
                throw new InvalidDataException();
            }
            catch (InstantiationException ex) {
                throw new InvalidDataException();
            }
        }
    }


    /**
     * Converts a 'basic' value (primitive or String) to a string that can be written in XML.
     *
     * @param obj The object to be written.
     *
     * @return A String representing the object being written.
     */
    private static String writeBasicValue(Object obj) {

        String value = null;

        if (obj instanceof Byte) {
            value = Byte.toString((Byte)obj);
        }
        else if (obj instanceof Short) {
            value = Short.toString((Short)obj);
        }
        else if (obj instanceof Integer) {
            value = Integer.toString((Integer)obj);
        }
        else if (obj instanceof Long) {
            value = Long.toString((Long)obj);
        }
        else if (obj instanceof Float) {
            value = Float.toString((Float)obj);
        }
        else if (obj instanceof Double) {
            value = Double.toString((Double)obj);
        }
        else if (obj instanceof Character) {
            value = "" + (Character)obj;
        }
        else if (obj instanceof Boolean) {
            value = Boolean.toString((Boolean)obj);
        }
        else if (obj instanceof String) {
            value = (String)obj;
        }

        return value;
    }


    /**
     * A method that creates a basic obejct from a Class type and a String value.
     *
     * @param type The fully qualified class name that the object should be.
     * @param value The value to set into the object.
     *
     * @return An object that is of the type specified that contains the given value.
     *
     * @throws InvalidDataException Thrown when the object cannot be created as requested.
     */
    private static Object createBasicObject(String type, String value) throws InvalidDataException {

        Class typeClass = null;

        try {
            typeClass = Class.forName(type);
        }
        catch(ClassNotFoundException cnf) {
            throw new InvalidDataException();
        }

        return createBasicObject(typeClass, value);
    }


    /**
     * A method that creates a basic obejct from a Class type and a String value.
     *
     * @param typeClass The Class of the returned object.
     * @param value The value to set into the object.
     *
     * @return An object that is of the type specified that contains the given value.
     *
     * @throws InvalidDataException Thrown when the object cannot be created as requested.
     */
    private static Object createBasicObject(Class typeClass, String value) throws InvalidDataException {

        try {

            if (typeClass.equals(byte.class)) {
                return Byte.parseByte(value);
            }
            else if (typeClass.equals(short.class)) {
                return Short.parseShort(value);
            }
            else if (typeClass.equals(int.class)) {
                return Integer.parseInt(value);
            }
            else if (typeClass.equals(long.class)) {
                return Long.parseLong(value);
            }
            else if (typeClass.equals(float.class)) {
                return Float.parseFloat(value);
            }
            else if (typeClass.equals(double.class)) {
                return Double.parseDouble(value);
            }
            else if (typeClass.equals(char.class)) {

                if (value.length() != 1) {
                    throw new InvalidDataException();
                }

                return new Character(value.charAt(0));
            }
            else if (typeClass.equals(boolean.class)) {
                return Boolean.parseBoolean(value);
            }
            else if (typeClass.equals(String.class)) {
                return value;
            }
            else {
                throw new InvalidDataException();
            }
        }
        catch (Exception e) {
            throw new InvalidDataException();
        }
    }


    /**
     * Writes a collection as a hierarchy of DOM Elements into the given element.
     *
     * @param collection The collection to write (Map or Collection).
     * @param container The DOM element that contains the data structure.
     */
    private static void writeCollection(Object collection, Element container) {

        Element collectionRoot = new Element("structure");
        collectionRoot.setAttribute(TYPE_ATTRIBUTE, collection.getClass().getName());

        if (collection instanceof Map) {

            // take care of the map case
            collectionRoot.setName(MAP_ELEMENT);

            Map map = (Map)collection;

            // get the key/value paris from the map and store them
            for (Object key : map.keySet()) {

                Object val = map.get(key);

                Element entryElement = new Element(ENTRY_ELEMENT);
                Element keyElement = new Element(KEY_ELEMENT);

                writeCollectionDataElement(key, keyElement);
                writeCollectionDataElement(val, entryElement);

                entryElement.addContent(keyElement);
                collectionRoot.addContent(entryElement);
            }
        }
        else if (collection instanceof Collection) {

            // the structure is a collection
            collectionRoot.setName(COLLECTION_ELEMENT);

            // get each entry and store it as XML
            for (Object obj : ((Collection)collection).toArray()) {

                Element entryElement = new Element(ENTRY_ELEMENT);

                writeCollectionDataElement(obj, entryElement);

                collectionRoot.addContent(entryElement);
            }
        }

        container.addContent(collectionRoot);
    }


    /**
     * Parses a data structure DOM Element into a Java Object.
     *
     * @param valueElement The "value" element that contains the collection.
     *
     * @return The data structure as a supported Map or Collection.
     *
     * @throws InvalidDataException Thrown if the XML cannot be parsed into an Object.
     */
    private static Object parseCollection(Element valueElement) throws InvalidDataException {

        Object ret = null;

        Element collElement = null;

        // see if the element is a map
        collElement = (Element)valueElement.getChild(MAP_ELEMENT);

        if (collElement == null) {

            // see if the element is a collection
            collElement = (Element)valueElement.getChild(COLLECTION_ELEMENT);
        }

        // the element is neither a Map nor a Collection
        if (collElement == null) {
            throw new InvalidDataException();
        }

        // get the structure type
        Attribute typeAttr = collElement.getAttribute(TYPE_ATTRIBUTE);

        if (typeAttr == null) {
            throw new InvalidDataException();
        }

        String type = typeAttr.getValue();

        if (type == null) {
            throw new InvalidDataException();
        }

        try {

            // make an instance of the structure
            ret = Class.forName(type).newInstance();
        }
        catch(ClassNotFoundException cnf) {
            throw new InvalidDataException();
        }
        catch(InstantiationException ie) {
            throw new InvalidDataException();
        }
        catch(IllegalAccessException iae) {
            throw new InvalidDataException();
        }

        // try to get all the data entries for the structure
        for (Element entry : (List<Element>)collElement.getChildren(ENTRY_ELEMENT)) {

            Object entryData = null;

            Element entryValueElement = entry.getChild(VALUE_ELEMENT);

            if (entryValueElement != null) {

                // the entry is another collection
                entryData = parseCollection(entryValueElement);
            }
            else {

                // the entry is a primitive or String
                Attribute entryTypeAttr = entry.getAttribute(TYPE_ATTRIBUTE);
                Attribute entryValueAttr = entry.getAttribute(VALUE_ATTRIBUTE);

                if ((entryTypeAttr == null) || (entryValueAttr == null)) {
                    throw new InvalidDataException();
                }

                entryData = createBasicObject(entryTypeAttr.getValue(), entryValueAttr.getValue());
            }

            // if the structure is a Map, we need to get key info and store the data differently
            if (collElement.getName().equals(MAP_ELEMENT)) {

                // get the key element
                Element keyElement = entry.getChild(KEY_ELEMENT);

                if (keyElement == null) {
                    throw new InvalidDataException();
                }

                Object keyData = null;

                Element keyValueElement = keyElement.getChild(VALUE_ELEMENT);

                if (keyValueElement != null) {

                    // key is another collection
                    keyData = parseCollection(entryValueElement);
                }
                else {

                    // key is a primitive or String
                    Attribute keyTypeAttr = keyElement.getAttribute(TYPE_ATTRIBUTE);
                    Attribute keyValueAttr = keyElement.getAttribute(VALUE_ATTRIBUTE);

                    if ((keyTypeAttr == null) || (keyValueAttr == null)) {
                        throw new InvalidDataException();
                    }

                    keyData = createBasicObject(keyTypeAttr.getValue(), keyValueAttr.getValue());
                }

                // store the key/value pair in the Map
                ((Map)ret).put(keyData, entryData);
            }
            else if (collElement.getName().equals(COLLECTION_ELEMENT)) {

                // store the entry into the Collection
                ((Collection)ret).add(entryData);
            }
        }

        return ret;
    }


    /**
     * Writes a Collection data element.
     *
     * @param data The data being written.
     * @param element The element being formed.
     */
    private static void writeCollectionDataElement(Object data, Element element) {

        if (isSupportedCollection(data.getClass())) {

            // the data is another collection, we need to handle this with a child value element
            Element valueElement = new Element(VALUE_ELEMENT);

            writeCollection(data, valueElement);

            element.addContent(valueElement);
        }
        else {

            // no child value needed, just use attributes to store the data type and value
            element.setAttribute(TYPE_ATTRIBUTE, data.getClass().getName());
            element.setAttribute(VALUE_ATTRIBUTE, writeBasicValue(data));
        }
    }


    /**
     * Determines if the type of a data structure is supported by this JDOMExternalizer.
     *
     * @param c The Class of the data structure being persisted.
     *
     * @return True if the type is supported, false otherwise.
     */
    private static boolean isSupportedCollection(Class c) {

        if (Map.class.isAssignableFrom(c)) {
            return true;
        }
        else if (Collection.class.isAssignableFrom(c)) {
            return true;
        }

        return false;
    }


    /**
     * Determines if a data structure is of the right parameterized type to be externalized by this JDOMExternalizer.
     *
     * @param type The Type of the field being persisted. It is assumed that the Type was already verified as being supported.
     *
     * @return True if the structure can be persisted, false otherwise.
     */
    private static boolean isCollectionWritable(Type type) {

        // test to make sure it is a parameterized collection, so we can determine now if it is externalizable
        if ((type == null) || !(type instanceof ParameterizedType)) {
            return false;
        }

        ParameterizedType genType = (ParameterizedType)type;

        // get the parameterized types for this collection
        for (Type t : genType.getActualTypeArguments()) {

            if (t instanceof Class) {

                // the type is a concrete implementation
                Class clazz = (Class)t;

                // it has to be primitive or a String
                if (!clazz.isPrimitive() && !String.class.isAssignableFrom(clazz)) {
                    return false;
                }
            }
            else if (t instanceof ParameterizedType) {

                // it is another parameterized type.
                // make sure the type that is parameterized is a collection, otherwise we do not support it
                if (!isSupportedCollection((Class)genType.getRawType())) {
                    return false;
                }

                // recurse to find out if everything is supported
                if (!isCollectionWritable(t)) {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        return true;
    }

}

