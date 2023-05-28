import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String baseUrl = "https://quotes.toscrape.com/";
        List<Quote> quoteList = new ArrayList<>();

        Document doc = Jsoup.connect(baseUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                .get();

        Elements nextElements =  doc.select(".next");

        while(!nextElements.isEmpty()){


            Elements quoteElements = doc.select(".quote");

            for(Element quoteElement : quoteElements){
                Quote quote = new Quote();

                String text = quoteElement.select(".text").first().text().replace("“", "") .replace("”", "");
                String author = quoteElement.select(".author").first().text();

                List<String> tags = new ArrayList<>();
                for(Element tag : quoteElements.select("tags")){
                    tags.add(tag.text());
                }

                quote.setText(text);
                quote.setAuthor(author);
                quote.setTags(String.join(", ", tags));

                quoteList.add(quote);
            }

            Element nextElement = nextElements.first();

            String relativeUrl = nextElement.getElementsByTag("a").first().attr("href");

            String completeUrl = baseUrl + relativeUrl;

            doc = Jsoup.connect(completeUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36")
                    .get();

            nextElements = doc.select(".next");
        }

        File csvFile = new File("output.csv");
        try(PrintWriter printWriter = new PrintWriter(csvFile, StandardCharsets.UTF_8)){
            printWriter.write('\ufeff');

            for(Quote quote : quoteList){
                List<String> row = new ArrayList<>();
                row.add("\"" + quote.getText() + "\"");
                row.add("\"" +quote.getAuthor() + "\"");
                row.add("\"" +quote.getTags() + "\"");

                printWriter.println(String.join(",", row));
            }
        }



    }
}
