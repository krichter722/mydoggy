package org.noos.xing.mydoggy.tutorialset.doc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.Properties;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TutorialToDoc {
    Properties patterns;


    public TutorialToDoc() {
        try {
            patterns = new Properties();
            patterns.load(this.getClass().getClassLoader().getResourceAsStream("xdocregex.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String parse(String fileName) {
        File tutorialFile = new File(fileName);
        if (!tutorialFile.exists())
            throw new IllegalArgumentException("File not found!");

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            FileInputStream fis = new FileInputStream(tutorialFile);
            fis.getChannel().transferTo(0, fis.getChannel().size(), Channels.newChannel(stream));
            fis.close();

            return applyFilters(stream.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected String applyFilters(String source) {
        if (source == null)
            return null;

        for (Object key : patterns.keySet()) {
            source = source.replaceAll((String) key , (String) patterns.get(key));
        }

        return source;
    }



    public static void main(String[] args) {
        TutorialToDoc tutorialToDoc = new TutorialToDoc();
        System.out.println(tutorialToDoc.parse("/home/angelo/Projects/java/sourceforge/mydoggy/mydoggy/mydoggy-examples/src/main/java/org/noos/xing/mydoggy/tutorialset/TutorialSet5.java"));
    }



}
