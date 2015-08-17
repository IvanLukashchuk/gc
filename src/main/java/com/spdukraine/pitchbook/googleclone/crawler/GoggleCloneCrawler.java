package com.spdukraine.pitchbook.googleclone.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.regex.Pattern;

public class GoggleCloneCrawler extends WebCrawler{

    private static final Logger log = LogManager.getLogger(GoggleCloneCrawler.class);

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches();
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        log.info("URL: {}", url);
        if (page.getParseData() instanceof HtmlParseData) {
            IndexWriter indexWriter = (IndexWriter) getMyController().getCustomData();
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String title = htmlParseData.getTitle();
            log.debug("Links: {}, Content length: {}", htmlParseData.getOutgoingUrls().size(), text.length());
            try {
                addDocument(indexWriter, url, title, text);
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private void addDocument(IndexWriter indexWriter,  String url, String title, String content) throws IOException {
        Document doc = new Document();
        doc.add(new StringField("url", url, Field.Store.YES));
        doc.add(new StringField("title", title, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        indexWriter.addDocument(doc);
    }
}
