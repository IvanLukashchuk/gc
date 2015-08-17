package com.spdukraine.pitchbook.googleclone.mvc;

import com.spdukraine.pitchbook.googleclone.dtos.SearchResult;
import com.spdukraine.pitchbook.googleclone.services.GoogleCloneService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GoogleCloneController {

    private static final Logger log = LogManager.getLogger(GoogleCloneController.class);

    @Autowired
    private GoogleCloneService googleCloneService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(){
        return "index";
    }

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public String index(@RequestParam("q") String url, @RequestParam("depth") int depth){
        try {
            googleCloneService.index(url, depth);
        } catch (Exception e) {
            log.error(e);
        }
        return "index";
    }

    @RequestMapping(value = "/search")
    public String search(@RequestParam("q") String query, @RequestParam(name = "from", defaultValue = "0") int from, @RequestParam(name = "count", defaultValue = "10") int count, Model model){
        try {
            query = new String(query.getBytes("ISO8859-1"), "UTF-8");
            model.addAttribute("query", query);
            List<SearchResult> searchResults = googleCloneService.search(query, from, count);
            model.addAttribute("results", searchResults);
        } catch (Exception e) {
            log.error(e);
        }
        return "search";
    }

    @RequestMapping(value = "/")
    public String rootSearch(){
        return "search";
    }

}
