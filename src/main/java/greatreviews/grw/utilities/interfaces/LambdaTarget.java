package greatreviews.grw.utilities.interfaces;

import org.jsoup.select.Elements;

@FunctionalInterface
public interface LambdaTarget {

    Elements inCheck(String ...args);
}
