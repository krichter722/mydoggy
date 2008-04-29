package org.noos.xing.mydoggy.plaf.persistence.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Stack;

public class XMLWriter {
    protected final static Object SEEN_NOTHING = new Object();
    protected final static Object SEEN_ELEMENT = new Object();
    protected final static Object SEEN_DATA = new Object();

    protected int elementLevel = 0;
    protected Writer output;
    protected final Attributes EMPTY_ATTS = new AttributesImpl();

    protected Object state = SEEN_NOTHING;
    protected Stack<Object> stateStack;
    protected int indentStep = 0;
    protected int depth = 0;


    public XMLWriter(Writer writer) {
        init(writer);
    }

    public void reset() {
        depth = 0;
        state = SEEN_NOTHING;
        stateStack = new Stack<Object>();
        elementLevel = 0;
    }

    public void flush() throws IOException {
        output.flush();
    }

    public void setOutput(Writer writer) {
        if (writer == null) {
            output = new OutputStreamWriter(System.out);
        } else {
            output = writer;
        }
    }

    public Writer getOutput() {
        return output;
    }

    public void startDocument() throws SAXException {
        reset();
        write("<?xml version=\"1.0\" standalone=\"yes\"?>\n\n");
    }

    public void endDocument() throws SAXException {
        write('\n');
        try {
            flush();
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    public void startElement(String elementName, Attributes atts) throws SAXException {
        stateStack.push(SEEN_ELEMENT);

        state = SEEN_NOTHING;
        if (depth > 0)
            characters("\n");
        doIndent();

        elementLevel++;
        write('<');
        writeName(elementName);
        writeAttributes(atts);
        write('>');

        depth++;
    }

    public void endElement(String elementName) throws SAXException {
        depth--;
        if (state == SEEN_ELEMENT) {
            characters("\n");
            doIndent();
        }

        write("</");
        writeName(elementName);
        write('>');
        if (elementLevel == 1) {
            write('\n');
        }
        elementLevel--;

        state = stateStack.pop();
    }

    public void characters(char ch[], int start, int len) throws SAXException {
        state = SEEN_DATA;
        writeEsc(ch, start, len, false);
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        writeEsc(ch, start, length, false);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        write("<?");
        write(target);
        write(' ');
        write(data);
        write("?>");
        if (elementLevel < 1) {
            write('\n');
        }
    }

    public void dataElement(String elementName, Attributes atts) throws SAXException {
        state = SEEN_ELEMENT;
        if (depth > 0)
            characters("\n");
        doIndent();
        state = SEEN_ELEMENT;

        write('<');
        writeName(elementName);
        writeAttributes(atts);
        write("/>");
    }

    public void startElement(String localName) throws SAXException {
        startElement(localName, EMPTY_ATTS);
    }

    public void dataElement(String elementName, Attributes atts, String content) throws SAXException {
        startElement(elementName, atts);
        characters(content);
        endElement(elementName);
    }

    public void characters(String data) throws SAXException {
        if (data == null)
            return;

        char ch[] = data.toCharArray();
        characters(ch, 0, ch.length);
    }

    public int getIndentStep() {
        return indentStep;
    }

    public void setIndentStep(int indentStep) {
        this.indentStep = indentStep;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void cdata(String s) throws SAXException {
        state = SEEN_ELEMENT;
        if (depth > 0)
            characters("\n");
        doIndent();
        state = SEEN_ELEMENT;

        write("<![CDATA[");
        writeCDATAEsc(s.toCharArray(), 0, s.length());
        write("]]>");
    }


    
    protected void init(Writer writer) {
        setOutput(writer);
        reset();
        setIndentStep(4);
    }

    protected void write(char c) throws SAXException {
        try {
            output.write(c);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    protected void write(String s) throws SAXException {
        try {
            output.write(s);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    protected void writeAttributes(Attributes atts) throws SAXException {
        int len = atts.getLength();
        for (int i = 0; i < len; i++) {
            write(' ');
            writeName(atts.getLocalName(i));
            write("=\"");

            char ch[] = atts.getValue(i).toCharArray();
            writeEsc(ch, 0, ch.length, true);

            write('"');
        }
    }

    protected void writeEsc(char ch[], int start, int length, boolean isAttVal) throws SAXException {
        for (int i = start; i < start + length; i++) {
            switch(ch[i]) {
                case '<' :
                    write("&lt;");
                    break;
                case '>' :
                    write("&gt;");
                    break;
/*
                case '\'' :
                    write("&apos;");
                    break;
*/
                case '\"' :
                    if (isAttVal) {
                        write("&quot;");
                    } else {
                        write('\"');
                    }
                    break;
                case '&' :
                    write("&amp;");
                    break;
                default :
                    if (ch[i] > '\u007f') {
                        write("&#");
                        write(Integer.toString(ch[i]));
                        write(';');
                    } else {
                        write(ch[i]);
                    }
                    break;
            }
        }
    }

    protected void writeCDATAEsc(char ch[], int start, int length) throws SAXException {
        for (int i = start; i < start + length; i++) {
            if (ch[i] > '\u007f') {
                write("&#");
                write(Integer.toString(ch[i]));
                write(';');
            } else {
                write(ch[i]);
            }
        }
    }

    protected void writeName(String elementName) throws SAXException {
        write(elementName);
    }

    protected void doIndent() throws SAXException {
        if (indentStep > 0 && depth > 0) {
            int n = indentStep * depth;
            char ch[] = new char[n];
            for (int i = 0; i < n; i++) {
                ch[i] = ' ';
            }
            characters(ch, 0, n);
        }
    }

    public void close() throws IOException {
        flush();
        output.close();
    }


}
