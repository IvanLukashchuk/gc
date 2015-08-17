package com.spdukraine.pitchbook.googleclone.services;

import com.spdukraine.pitchbook.googleclone.crawler.GoggleCloneCrawler;
import com.spdukraine.pitchbook.googleclone.dtos.SearchResult;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleCloneService {

    private static final Logger log = LogManager.getLogger(GoogleCloneService.class);

    @Autowired
    private Directory index;
    @Autowired
    private Analyzer analyzer;
    @Value("${crawlStorageFolder}")
    public String crawlStorageFolder;
    @Value("${numberOfCrawlers}")
    public String numberOfCrawlers;

    public void index(String url, int maxDepth) throws Exception {
        log.info("Indexing started");

        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setMaxDepthOfCrawling(maxDepth);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(index, config);

        CrawlController crawlController = getCrawlController(crawlConfig);
        crawlController.setCustomData(indexWriter);
        crawlController.addSeed(url);
        crawlController.start(GoggleCloneCrawler.class, Integer.valueOf(numberOfCrawlers));
        indexWriter.close();

        log.info("Indexing finished");
    }

    private CrawlController getCrawlController(CrawlConfig crawlConfig) throws Exception {
        crawlConfig.setCrawlStorageFolder(crawlStorageFolder);
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        return new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
    }

    public List<SearchResult> search(String query, int from, int count) throws IOException, ParseException {

        Query q = new QueryParser("content", analyzer).parse(query);

        int hitsPerPage = from + count;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
        searcher.search(q, collector);

        ScoreDoc[] hits = collector.topDocs(from, count).scoreDocs;
        log.debug("Found {} hits.", hits.length);
        List<SearchResult> results = collectResults(searcher, hits);
        reader.close();
        return results;
    }

    private List<SearchResult> collectResults(IndexSearcher searcher, ScoreDoc[] hits) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        for(ScoreDoc scoreDoc : hits) {
            Document doc = searcher.doc(scoreDoc.doc);
            SearchResult searchResult =  new SearchResult();
            searchResult.setUrl(doc.get("url"));
            searchResult.setTitle(doc.get("title"));
            searchResult.setContent(doc.get("content"));
            results.add(searchResult);
        }
        return results;
    }


    public String getCrawlStorageFolder() {
        return crawlStorageFolder;
    }

    public void setCrawlStorageFolder(String crawlStorageFolder) {
        this.crawlStorageFolder = crawlStorageFolder;
    }

    public String getNumberOfCrawlers() {
        return numberOfCrawlers;
    }

    public void setNumberOfCrawlers(String numberOfCrawlers) {
        this.numberOfCrawlers = numberOfCrawlers;
    }
}
