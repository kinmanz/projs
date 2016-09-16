package com.w6.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.w6.data.Article;
import com.w6.data.Event;
import com.w6.nlp.Parser;
import com.w6.nlp.MySolrClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import java.text.DateFormatSymbols;
import java.util.Locale;

@Controller
public class EndpointController {
    protected static final String INPUT_VIEW = "input";
    protected static final String W6_VIEW = "w6";
    protected static final String UPLOAD_VIEW = "upload";
    protected static final String QUERY_VIEW = "query";
    protected static final String DOCUMENTS_BY_EVENT_VIEW = "articlesOfEvent";
    protected static final String REPORT_VIEW = "try";
//    protected static final String REPORT_VIEW = "report";

    protected MySolrClient solrClient = new MySolrClient();
    
    
    private static final Gson gson = new GsonBuilder().create();
    
    @RequestMapping(value = "post", method = RequestMethod.POST)
    public ModelAndView post(
            @RequestParam("sourse") String sourse,
            @RequestParam("title") String title,           
            @RequestParam("text") String text
            ) throws IOException, SolrServerException
    {
        Article article = new Article(new Long(-1), sourse, text, title, 
                "",
                -1
        );
        article.response = gson.toJson(new Parser().generateResponse(article));
        solrClient.uploadDataToSolr(article);
        return parse(article.id);
    }

    @RequestMapping(value = "parse", method = RequestMethod.GET)
    public ModelAndView parse(@RequestParam("id") Long docId) throws IOException
    {
        Article text;
        try { 
            text = solrClient.getDocumentById(docId);
            if (text == null) {
                return new ModelAndView(W6_VIEW);
            }
            
            ModelAndView modelAndView = new ModelAndView(W6_VIEW);
            modelAndView.addObject("article", gson.toJson(solrClient.getDocumentById(docId)));
            modelAndView.addObject("events", gson.toJson(solrClient.getEvents()));
            modelAndView.addObject("id", docId);
            
            return modelAndView;
        } catch (SolrServerException ex) {
            Logger.getLogger(EndpointController.class.getName()).log(Level.SEVERE, null, ex);
            return new ModelAndView(W6_VIEW);
        }
        
    }
    
    @RequestMapping(value = "/update/event", method = RequestMethod.POST)
    public ModelAndView  updateEvent(
            @RequestParam("eventId") String id,    
            @RequestParam("eventTitle") String title ,                
            @RequestParam("eventDate") String date,
            @RequestParam("eventDesc") String description,
            @RequestParam("eventReg") String region,
            @RequestParam("eventCountry") String country
    ) throws IOException
    {
        Event event = new Event(Long.parseLong(id), date, title, description, region, country);
        try {
            solrClient.updateEventInSolr(event);
        } catch (SolrServerException ex) {
            Logger.getLogger(EndpointController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return displayDocumentsByEvent(Long.parseLong(id));
    }
    
    @RequestMapping(value = "parse", method = RequestMethod.POST)
    public ModelAndView update(
            @RequestParam("id") long docId,    
            @RequestParam("event_select") long eventId,                
            @RequestParam("title") String title,
            @RequestParam("date") String date,
            @RequestParam("region") String region,
            @RequestParam("country") String country            
    ) throws IOException, SolrServerException
    {
        if (eventId == -1)
        {
            Event event = new Event(
                    -1,
                    date,
                    title,
                    "Please provide description",
                    region,
                    country                    
            );
            eventId = solrClient.uploadEventToSolr(event);
        }
        
        Article document = solrClient.getDocumentById(docId);
        document.eventId = eventId;
        solrClient.uploadDataToSolr(document);
        return parse();
        
    }
    
    @RequestMapping(value = "/input", method = RequestMethod.GET)
    public String displayInput() 
    {
        return INPUT_VIEW;
    }
    
    @RequestMapping(value = "/events/view", method = RequestMethod.GET)
    public ModelAndView displayDocumentsByEvent(@RequestParam("id") long docId) 
            throws IOException
    {
        ModelAndView modelAndView = new ModelAndView(DOCUMENTS_BY_EVENT_VIEW);
        try {
           modelAndView.addObject("event", gson.toJson(solrClient.getEventById(docId))); 
           modelAndView.addObject("docList", gson.toJson(solrClient.getArticlesByEventId(docId)));
        } catch (SolrServerException e) {
             Logger.getLogger(EndpointController.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return modelAndView;
    }
    
    @RequestMapping(value = "view", method = RequestMethod.GET)
    public ModelAndView parse() throws IOException
    {
        ArrayList<Article> text;
        try { 
            text = solrClient.getDocuments();
            ModelAndView modelAndView = new ModelAndView(QUERY_VIEW);
            modelAndView.addObject("response", gson.toJson(text));
            return modelAndView;
        } catch (SolrServerException ex) {
            Logger.getLogger(EndpointController.class.getName()).log(Level.SEVERE, null, ex);
            return new ModelAndView(W6_VIEW);
        }
    }
    
    @RequestMapping(value = "report", method = RequestMethod.GET)
    public ModelAndView report(@RequestParam("month") String month) throws IOException, SolrServerException
    {
        ModelAndView modelAndView = new ModelAndView(REPORT_VIEW);
//        ArrayList<Event> eventsInRange = solrClient.getEventsInRange(month.concat("-01"), month.concat("-31"));
       
        ArrayList<Event> eventsInRange = DummySolrClient.getEvents();
        modelAndView.addObject("events", eventsInRange);
        
        String[] dateNames = month.split("-");
        String yearNumber = dateNames[0];
        String monthString = new DateFormatSymbols(Locale.ENGLISH).getMonths()[Integer.valueOf(dateNames[1])-1];
        
        for (Event event: eventsInRange)
        {
            break;
            event.setSourses(
                    solrClient.getArticlesByEventId(event.id)
                            .stream()
                            .map(article -> article.sourse)
                            .toArray(size -> new String[size])
            );
        }
            
        modelAndView.addObject("year", yearNumber);
        modelAndView.addObject("month", monthString);
        return modelAndView;
    }  
    
}
