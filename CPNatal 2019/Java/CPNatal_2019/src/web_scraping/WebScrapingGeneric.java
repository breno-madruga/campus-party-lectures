package web_scraping;

// Required imports
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The WebScrapingGeneric class is a generic and abstract class that crawl the page links and
 * extract a particular content of page. It allows to save the extracted content in a CSV file.
 */
public abstract class WebScrapingGeneric {

    /**
     * The getPage method gets the HTML source from an URL.
     * @param url - a String object that represents an URL.
     * @return Document object - if the connection was successful, the Document object of the requested page will be
     *      returned. Otherwise, it will be returned null.
     */
    private static Document getPage(String url){
        Document page = null;
        try {
            // Get the HTML source from url.
            page = Jsoup.connect(url).get();
        } catch (IOException e){
            e.printStackTrace();
        }
        return page;
    }

    /**
     * The getLinks method gets the links from the page, based on a CSS Locator and an URL.
     * @param url - a String object that represents an URL.
     * @param css_locator - a String object that represents a CSS Locator.
     * @return List object - if the URL's page exists, the links from the given CSS Locator will be obtained.
     *         Otherwise, it will be returned null.
     */
    protected List<String> getLinks(String url, String css_locator){
        List<String> links_result = null;

        // Get the HTML source from url.
        Document page = getPage(url);

        if(page != null) {
            // Get the needed HTML links.
            Elements links = page.select(css_locator);

            // Create the list of links.
            links_result = new ArrayList<>();

            // For each link, get the absolute url.
            for (Element link : links)
                links_result.add(link.attr("abs:href"));
        }
        return links_result;
    }

    /**
     * The getLinks method gets the links with their paginations, based on the CSS Locators and page URL.
     * @param url - a String object that represents an URL.
     * @param css_locator - a String object that represents a CSS Locator.
     * @param css_pagination - a String object that represents a CSS Locator of pagination.
     * @return List object - if the URL's page exists, the links from the given CSS Locators will be obtained.
     *         Otherwise, it will be returned null.
     */
    protected List<String> getLinks(String url, String css_locator, String css_pagination){
        // Get the links from the url and css_locator.
        List<String> links_result = getLinks(url, css_locator);

        if(links_result != null) {
            // For each link, get it with its pagination.
            for (String link : new ArrayList<>(links_result)) {
                List<String> links_pagination = getLinks(link, css_pagination);
                if(links_pagination != null && !links_result.containsAll(links_pagination)){
                    links_result.remove(link);
                    links_result.addAll(links_pagination);
                }
            }
        }
        return links_result;
    }

    /**
     * The getData method obtains the page's data.
     * @param url - a String object that represents an URL.
     * @return String object - if the URL's page exists, the page's data will be obtained.
     *         Otherwise, it will be returned null.
     */
    protected String getData(String url){
        // Get the HTML source from url.
        Document page = getPage(url);

        if(page != null){
            // Instance the object that represents the page content.
            StringBuilder data = new StringBuilder();

            // Include the data in StringBuilder object.
            getDataFields(data, page);

            return data.toString();
        }
        return null;
    }

    /**
     * The getDataFields method is an abstract method that allows the subclasses of WebScrapingGeneric to define what
     * the page data will be extracted.
     * @param sb - a StringBuilder object that will store the extracted content from the HTML page.
     * @param page - a Document object that represents the HTML page.
     */
    protected abstract void getDataFields(StringBuilder sb, Document page);

    /**
     * The saveDataToCSV method enables to store the extracted data in a specific CSV file.
     * @param file_csv - a String object that represents the absolute path of the CSV file.
     * @param data - a List object that contains the content that will be stored in the CSV file.
     */
    protected void saveDataToCSV(String file_csv, List<String> data){
        try {
            File file = new File(file_csv);
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            for(String line : data) {
                bw.write(line);
                bw.newLine();
                bw.flush();
            }
            bw.close();
            fw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}