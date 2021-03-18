package greatreviews.grw.utilities.implementations;

import greatreviews.grw.utilities.interfaces.LambdaTarget;
import greatreviews.grw.utilities.interfaces.Scraper;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Optional;


@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class ScraperImpl implements Scraper {
    Document currentPage;


    @Override
    public void getPage(String url) {
        /*
         *
         * this implementation is for demo purposes
         * TODO: implement error handling saying page is invalid if Jsoup is unable to connect.
         * */

        if (!url.startsWith("http")) {
                    url = "https://" + url;
        }
        try{
            this.currentPage = Jsoup.connect(url).timeout(5 * 1000).get();
        } catch (Exception e){
            System.out.println("The following exception was thrown from Jsoup  :: ");
            System.out.println(e.getMessage());
        }

    }

    @Override
    public String getFirstElementTextByTag(String tagName) {
       return getFirstSafely(args -> {
           return currentPage.getElementsByTag(args[0]);
       },new String[]{tagName}).map(Element::text).orElseGet(() -> "");
    }



    @Override
    public String getCurrentPageTitle() {
       return getFirstSafely(args -> {
           return currentPage.getElementsByTag(args[0]);
       },new String[]{"title"}).map(Element::text).orElseGet(()-> "");
    }

    @Override
    public String getCurrentPageDescription() {
        String result = "";
        var targetAttributes = new String[]{
                "description",
                "Description",
                "DESCRIPTION"
        };

        result = getFirstSafely(args -> currentPage.getElementsByAttributeValueMatching("name",args[0]) ,targetAttributes)
        .map(element -> element.attributes().get("content")).orElseGet(() -> {

                    return  getFirstSafely(args -> currentPage.getElementsByAttributeValueMatching("name",args[1]) ,targetAttributes)
                            .map(element -> element.attributes().get("content")).orElseGet(()->{

                                return getFirstSafely(args -> currentPage.getElementsByAttributeValueMatching("name",args[2]) ,targetAttributes)
                                        .map(element -> element.attributes().get("content")).orElseGet(()-> "");
                            });
                });

        return result;
    }

    @Override
    public String getElementAttributeByAnotherAttribute(String tagName, String searchByAttribute, String searchByAttributeValue, String targetAttribute) {
        String result = "";

        if(this.currentPage != null){
            Elements elementsByAttributeValueMatching = this.currentPage.getElementsByAttributeValueMatching(searchByAttribute, searchByAttributeValue);
            if(!elementsByAttributeValueMatching.isEmpty()){
                Element element = elementsByAttributeValueMatching.get(0);

                if(element.attributes().hasKey(targetAttribute)){
                    String targetAttribValue = element.attributes().get(targetAttribute);
                    result = targetAttribValue;

                    }
                }

            }


        return result;
    }

    // helper function to safely get the first element by
    // passing a callback that contains arbitrary function from Document
    // and passing the function arguments as an array of strings
    private Optional<Element> getFirstSafely(LambdaTarget function, String[] args){

        Optional<Element> targetElement = Optional.empty();

        if(currentPage != null){
            Elements scrapedElements = function.inCheck(args);
//                    currentPage.getElementsByTag(tagName);
            if (!scrapedElements.isEmpty()) {
                targetElement = Optional.of(scrapedElements.get(0));
            }
        }

        return targetElement;
    }



}
