package nl.vermeir.epub;

public class App
{
    public static void main( String[] args ) throws Exception {
        EPubMerger ePubMerger = new EPubMerger();
//        ePubMerger.cleanUpBook("data/in/test.epub");
        ePubMerger.cleanUpDirectory("/tmp/copy");
    }
}
