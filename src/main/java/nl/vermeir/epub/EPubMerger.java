package nl.vermeir.epub;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by jan on 09/12/14.
 */
public class EPubMerger {

    public static final char QUOTE = '”';

    public boolean shouldMerge(String firstLine) {
        String strippedLine = firstLine.trim();
        String endOfLine = "";
        if (strippedLine.length()>30) {
            endOfLine = strippedLine.substring(strippedLine.length() - 20);
        }
        if (!strippedLine.endsWith("</p>")) {
            return false;
        } else if (strippedLine.endsWith(". </p>")) {
            return false;
        } else if (strippedLine.endsWith("></p>")) {
            return false;
        } else if (strippedLine.endsWith(QUOTE + " </p>")) {
            return false;
        }
        return true;
    }

    public String merge(String line1, String line2) {
        int endOfLine1 = line1.indexOf("</p>");
        int startOfLine2 = line2.indexOf(">") + 1;
        return line1.substring(0,endOfLine1) + " " + line2.substring(startOfLine2);
    }

    public void mergeLines(Book book) throws Exception {
        List<Resource> contents = book.getContents();
        for (int c=2; c<contents.size();c++){
            Resource resource = contents.get(c);
            String text = new String(resource.getData());
            String[] lines = text.split("\n");
            StringBuilder mergedLines = new StringBuilder();
            String lastLine = lines[0];
            for (int i = 1; i < lines.length; i++) {
                String newLine = lines[i];
                if (shouldMerge(lastLine)) {
                    lastLine = merge(lastLine, newLine);
                } else {
                    mergedLines.append(lastLine).append("\n");
                    lastLine = newLine;
                }
            }
            resource.setData(mergedLines.toString().getBytes());
            contents.set(c, resource);
        }
    }

    private String constructOutputFileName(String inputFileName) {
        String parts[] = inputFileName.split(File.separator);
        return "data/" + parts[parts.length-1];
    }

    public void cleanUpBook(String inputFileName) {
        System.out.println("Processing: " + inputFileName);
        try {
            Book book = new EpubReader().readEpub(new FileInputStream(inputFileName));
            EPubMerger merger = new EPubMerger();
            merger.mergeLines(book);
            File out = new File(constructOutputFileName(inputFileName));
            FileOutputStream fos = new FileOutputStream(out);
            EpubWriter epubWriter = new EpubWriter();
            epubWriter.write(book, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanUpDirectory(String directoryName) throws  Exception {
        File[] files = new File(directoryName).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.toString().endsWith(".epub");
            }
        });
        for (File file:files) {
            cleanUpBook(file.getAbsolutePath());
        }
    }
}