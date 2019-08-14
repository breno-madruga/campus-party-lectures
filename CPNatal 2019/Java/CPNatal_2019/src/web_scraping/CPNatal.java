package web_scraping;

// Required imports
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * The CPNatal class represents the case study performed with the CPNatal2 HTML page. It accessed the URLs of
 * "Workshop" and "Conference" events. In addition, it stored the data within a CSV file. This class is subclass of
 * WebScrapingGeneric.
 */
public class CPNatal extends WebScrapingGeneric {

    /**
     * The getDataFields method was defined to extract the data of CPNatal's HTML page.
     * @param sb - a StringBuilder object that will store the extracted content from the HTML page.
     * @param page - a Document object that represents the HTML page.
     */
    protected void getDataFields(StringBuilder sb, Document page){
        // Extract the data.
        Element type = page.selectFirst("div.left > ul.filter-tab-menu > li:nth-of-type(3) > a");
        Element title = page.selectFirst("div.blue-box > span.title");
        Element start_date = page.selectFirst("div.metadata > span:nth-of-type(1)");
        Element end_date = page.selectFirst("div.metadata > span:nth-of-type(2)");
        Element location = page.selectFirst("div.metadata > span:nth-of-type(3)");
        Element description = page.selectFirst("div[class='ident-25'] > p");

        // Include the data with the separator in StringBuilder object, except the description.
        sb.append(type == null ? "|" : type.text().concat("|"));
        sb.append(title == null ? "|" : title.text().concat("|"));
        sb.append(start_date == null ? "|" : start_date.text().concat("|"));
        sb.append(end_date == null ? "|" : end_date.text().concat("|"));
        sb.append(location == null ? "|" : location.text().concat("|"));
        sb.append(description == null ? "|" : description.text().replaceAll(";", ","));
    }

    /**
     * The main method. It represents the starting point for project execution.
     */
    public static void main(String[] args){
        // Create CPNatal object to extract the data.
        CPNatal cpNatal = new CPNatal();

        // Get the links of events' categories with their paginations.
        List<String> links = cpNatal.getLinks("https://campuse.ro/events/campus-party-natal-2019/",
                "div[class='small-12 medium-6 large-3 columns pink-bg'] > a",
                "span.step-links > span.list > a");

        // Get the links of events (Workshops and Conferences).
        List<String> links_events = new ArrayList<>();
        for(String link : links)
            links_events.addAll(cpNatal.getLinks(link, "div.header > a"));

        // Get the data.
        List<String> data = new ArrayList<>();
        data.add("Type|Title|Start Date|End Date|Location|Description"); // header
        for(String url: links_events)
            data.add(cpNatal.getData(url));

        // Store data in a CSV file.
        cpNatal.saveDataToCSV("data_cp_natal_2019_java.csv", data);
    }
}