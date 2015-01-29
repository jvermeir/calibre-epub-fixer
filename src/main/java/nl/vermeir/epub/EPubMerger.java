package nl.vermeir.epub;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class EPubMerger {

    public static final char QUOTE = '”';

    public boolean shouldMerge(String currentText) {
        String strippedLine = currentText.trim();
        if (!strippedLine.endsWith("</p>")) {
            return false;
        } else if (strippedLine.endsWith(". </p>")) {
            return false;
        } else if (strippedLine.endsWith("></p>")) {
            return false;
        } else if (strippedLine.endsWith(QUOTE + " </p>")) {
            return false;
        } else if (strippedLine.endsWith("\" </p>")) {
            return false;
        }
        return true;
    }

    public String merge(String line1, String line2) {
        int endOfLine1 = line1.indexOf("</p>");
        int startOfLine2 = line2.indexOf(">") + 1;
        return line1.substring(0, endOfLine1) + " " + line2.substring(startOfLine2);
    }

    public void mergeLines(Book book) throws Exception {
        List<Resource> contents = book.getContents();
        for (int contentItem = 0; contentItem < contents.size(); contentItem++) {
            Resource resource = contents.get(contentItem);
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
            contents.set(contentItem, resource);
        }
    }

    public void cleanUpBook(String inputFileName) {
        System.out.print("Processing: " + inputFileName);
        try {
            FileInputStream inputStream = new FileInputStream(inputFileName);
            Book book = new EpubReader().readEpub(inputStream);
            inputStream.close();
            EPubMerger merger = new EPubMerger();
            merger.mergeLines(book);
            File out = new File(inputFileName);
            FileOutputStream fos = new FileOutputStream(out);
            EpubWriter epubWriter = new EpubWriter();
            epubWriter.write(book, fos);
            System.out.println("->" + out.getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanUpDirectory(String directoryName, final long modifiedSince) throws Exception {
        Collection<File> files = getEpubFilesSince(directoryName, modifiedSince);
        for (File file : files) {
            cleanUpBook(file.getAbsolutePath());
        }
    }

    public void cleanUp(String fileOrDirectoryName, long modifiedSince) throws Exception {
        File f = new File(fileOrDirectoryName);
        if (f.exists() && f.isDirectory()) {
            cleanUpDirectory(fileOrDirectoryName, modifiedSince);
        } else {
            cleanUpBook(fileOrDirectoryName);
        }
    }

    public void cleanUp(String fileOrDirectoryName) throws Exception {
        cleanUp(fileOrDirectoryName, 0);
    }

    public Collection<File> getEpubFilesSince(String rootDir, long timeStamp) {
        final String[] epubFiles = {"epub"};
        Collection<File> files = FileUtils.listFiles(new File(rootDir), epubFiles, true);
        Collection<File> newFiles = new ArrayList<File>();
        for (File file : files) {
            if (file.lastModified()>=timeStamp) {
                newFiles.add(file);
            }
        }
        return newFiles;
    }

}