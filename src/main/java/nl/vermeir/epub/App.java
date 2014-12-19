package nl.vermeir.epub;

public class App
{
    public static void main( String[] args ) throws Exception {
        if (args.length<1) {
            System.err.println("Usage: java nl.vermeir.epub.App <filename> or java nl.vermeir.epub.App <foldername>");
            System.exit(-1);
        }
        String fileOrDirectoryName = args[0];
        EPubMerger ePubMerger = new EPubMerger();
        ePubMerger.cleanUp(fileOrDirectoryName);
    }

}
