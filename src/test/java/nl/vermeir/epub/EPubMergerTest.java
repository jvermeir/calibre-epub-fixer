package nl.vermeir.epub;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.Test;

import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EPubMergerTest {
    private EPubMerger merger = new EPubMerger();

    @Test
    public void testDontMergeIfLineEndsInDotParagraph() {
        String firstLine = "<p class=\"calibre1\">the feral library that trailed it bled through in all the wrong places. Ritter recognized the archivist. </p>";
        assertFalse(merger.shouldMerge(firstLine));
    }

    @Test
    public void testDontMergeIfLineDoesNotEndInParagraph() {
        String firstLine = "<title>A Cost-Benefit Analysis of the Proposed Trade-Offs for the Overhaul of the Barricade</title>";
        assertFalse(merger.shouldMerge(firstLine));
    }

    @Test
    public void testDontMergeIfLineEndsInSomeTagAndAParagraph(){
        String firstLine = "<p class=\"calibre1\"> <i class=\"calibre4\">This short story was acquired and edited for Tor.com by consulting editor Ann VanderMeer. </i></p>";
        assertFalse(merger.shouldMerge(firstLine));
    }

    @Test
    public void testDontMergeIfLineEndsInAQuote() {
        String firstLine = "<p class=\"calibre1\"> <i class=\"calibre4\">This short st" + EPubMerger.QUOTE + " </p>";
        assertFalse(merger.shouldMerge(firstLine));
    }

    @Test
    public void testMergeIfLineEndsInParagraphAndOtherRulesFail() {
        String firstLine = "    <p class=\"calibre1\">The barricade ran the length of the frontier. It was transparent and still when calm, but the section</p>";
        assertTrue(merger.shouldMerge(firstLine));
    }

    @Test
    public void testCalibreTagsAreRemovedFromMergedLine() {
        String line1 = "<p class=\"calibre1\">The barricade ran the length of the frontier. It was transparent and still when calm, but the section</p>";
        String line2 = "<p class=\"calibre1\">before Ritter shimmered. Once coiled as though in tangled skeins, Turbulence now splattered like</p>";
        String mergedLines = merger.merge(line1, line2);
        assertTrue(mergedLines.contains("section before"));
        assertEquals(0, mergedLines.lastIndexOf("<p class=\"calibre1\""));
    }

    @Test
    public void testSplitLinesWorks() throws Exception {
        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(new FileInputStream("data/in/test.epub"));
        Resource resource = book.getContents().get(1);
        String text = new String(resource.getData());
        EPubMerger merger = new EPubMerger();
        String[] lines = text.split("\n");
        assertEquals(498, lines.length);
    }

}
