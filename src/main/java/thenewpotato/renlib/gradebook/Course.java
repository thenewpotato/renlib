package thenewpotato.renlib.gradebook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Course {

    public Document html;

    public Course(String html) {
        this.html = Jsoup.parse(html);
    }

    public ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        Elements tables = html.select("body > table");
        if (tables == null) {
            return null;
        }
        for (int i = 1; i < tables.size(); i++) {
            if (i % 2 == 0) {
                // body
                categories.get(categories.size() - 1).body = tables.get(i);
            } else {
                // header
                categories.add(new Category(tables.get(i), null));
            }
        }
        return categories;
    }

    public Float getNumberAverage() {
        try {
            Element font = html.select("body > table").last().select("tr").last().select("td:nth-child(2) > b:nth-child(1) > font:nth-child(1)").first();
            return Float.parseFloat(font.text());
        } catch (NullPointerException e){
            return null;
        }
    }

    public String getCourseName() {
        try {
            Element b = html.select("body > table").first().select("tr").last().select("td:nth-child(1) > div:nth-child(1) > font:nth-child(1) > b:nth-child(1)").first();
            return b.text();
        } catch (NullPointerException e){
            return null;
        }
    }

    public String getLetterAverage() {
        try {
            Element font = html.select("body > table").last().select("tr").last().select("td:nth-child(3) > b:nth-child(1) > font:nth-child(1)").first();
            return font.text();
        } catch (NullPointerException e){
            return null;
        }
    }

}
