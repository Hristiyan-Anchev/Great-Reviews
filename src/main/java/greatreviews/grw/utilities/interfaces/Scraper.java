package greatreviews.grw.utilities.interfaces;

public interface Scraper {

     void getPage(String url);

     String getFirstElementTextByTag(String tagName);
     String getCurrentPageTitle();
     String getCurrentPageDescription();

}
