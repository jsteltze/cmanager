package cmanager.xml;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;

public class Element {

    private String elementName = null;
    private String body = null;
    private final List<Element> children = new ArrayList<>();
    private final List<XmlAttribute> attributes = new ArrayList<>();

    public Element() {}

    public Element(String name) {
        setName(name);
    }

    public Element(String name, String body) {
        setName(name);
        setBody(body);
    }

    public Element(String name, Double body) {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }

    public Element(String name, Integer body) {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }

    public Element(String name, Boolean body) {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }

    public void setName(String name) {
        elementName = name;
    }

    public String getName() {
        return elementName;
    }

    public boolean is(String name) {
        return this.elementName.equals(name);
    }

    public boolean attrIs(String attr, String is) {
        for (final XmlAttribute attribute : attributes) {
            if (attribute.is(attr, is)) {
                return true;
            }
        }
        return false;
    }

    public void add(Element child) {
        children.add(child);
    }

    public List<Element> getChildren() {
        return children;
    }

    public Element getChild(String name) {
        for (final Element element : children) {
            if (element.is(name)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Get all the children with the given name.
     *
     * @param name To tag name to filter for.
     * @return The children matching the given tag name.
     */
    public List<Element> getChildren(String name) {
        final List<Element> matching = new ArrayList<>();
        for (final Element element : children) {
            if (element.is(name)) {
                matching.add(element);
            }
        }
        return matching;
    }

    public void add(XmlAttribute attribute) {
        attributes.add(attribute);
    }

    public List<XmlAttribute> getAttributes() {
        return attributes;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUnescapedBody() {
        return body == null ? null : StringEscapeUtils.unescapeXml(body);
    }

    public Double getBodyDouble() {
        return Double.valueOf(body);
    }

    public Integer getBodyInteger() {
        return Integer.valueOf(body);
    }

    public Boolean getBodyBoolean() {
        return Boolean.valueOf(body);
    }

    public static class XmlAttribute {

        private String name;
        private String value = null;

        public XmlAttribute(String name) {
            this.name = name;
        }

        public XmlAttribute(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public XmlAttribute(String name, Double value) {
            this.name = name;
            this.value = value != null ? value.toString() : null;
        }

        public XmlAttribute(String name, Boolean value) {
            this.name = name;
            this.value = value == null ? null : value ? "True" : "False";
        }

        public XmlAttribute(String name, Integer value) {
            this.name = name;
            this.value = value != null ? value.toString() : null;
        }

        public String getName() {
            return name;
        }

        public boolean is(String name) {
            return this.name.equals(name);
        }

        public boolean is(String name, String value) {
            return this.name.equals(name) && this.value.equals(value);
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public Double getValueDouble() {
            return Double.valueOf(value);
        }

        public Integer getValueInteger() {
            return Integer.valueOf(value);
        }
    }
}
