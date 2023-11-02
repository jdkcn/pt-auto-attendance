package org.jdkcn.apps.pt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParseUtils {

    private HtmlParseUtils() {}

    public static String parseAttendResult(String content) {
        Document htmlDocument = Jsoup.parse(content);
        Elements textElements = htmlDocument.select("#outer .embedded .text");
        if (textElements.isEmpty()) {
            textElements = htmlDocument.select("#outer .embedded li");
        }
        if (textElements.isEmpty()) {
            textElements = htmlDocument.select("#outer .embedded");
        }
        if (textElements.isEmpty()) {
            return content;
        } else {
            Element firstElement = textElements.first();
            return firstElement == null ? content : firstElement.text();
        }
    }
}
