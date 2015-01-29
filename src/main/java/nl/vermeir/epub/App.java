package nl.vermeir.epub;

import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java nl.vermeir.epub.App <filename> or java nl.vermeir.epub.App <foldername> [createdSinceDate (ddmmyyyy)>");
            System.exit(-1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Date createSince = sdf.parse("01011970");
        if (args.length == 2) {
            createSince = sdf.parse(args[1]);
        }
        String fileOrDirectoryName = args[0];
        EPubMerger ePubMerger = new EPubMerger();
        ePubMerger.cleanUp(fileOrDirectoryName, createSince.getTime());
    }

}
