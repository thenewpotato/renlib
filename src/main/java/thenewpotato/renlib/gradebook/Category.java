package thenewpotato.renlib.gradebook;

import org.joda.time.format.DateTimeFormat;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Category {

    Element header;
    Element body;

    public Category(Element header, Element body) {
        this.header = header;
        this.body = body;
    }

    public String name() {
        if (header == null) {
            return null;
        }
        Element font = header.select("tr:nth-child(2) > td:nth-child(1) > b:nth-child(1) > font:nth-child(1)").first();
        return font.text();
    }

    public Float weight() {
        if (header == null) {
            return null;
        }
        Element font = header.select("tr:nth-child(2) > td:nth-child(3) > b:nth-child(1) > font:nth-child(1)").first();
        String weight = font.text().substring(font.text().indexOf("=") + 2);
        return Float.parseFloat(weight);
    }

    public Float average() {
        if (body == null) {
            return null;
        }
        try {
            Element font = body.select("tr").last().select("td:nth-child(2) > p:nth-child(1) > b:nth-child(1) > font:nth-child(1)").first();
            return Float.parseFloat(font.text());
        } catch (NullPointerException e) {
            Elements trs = body.select("tr");
            Element font = trs.get(trs.size() - 3).select("td:nth-child(2) > p:nth-child(1) > b:nth-child(1) > font:nth-child(1)").first();
            return Float.parseFloat(font.text());
        }
    }

    public ArrayList<Entry> entries() {
        if (body == null) {
            return null;
        }
        int factor;
        if (body.select("tr").last().select("td:nth-child(1) > b") != null) {
            factor = 3;
        } else{
            factor = 1;
        }
        ArrayList<Entry> entries = new ArrayList<>();
        Elements trs = body.select("tr");
        for (int i = 1; i < trs.size() - factor; i++) {
            entries.add(new Entry());
            Elements tds = trs.get(i).select("td");
            for (int l = 0; l < tds.size(); l++) {
                switch (l) {
                    case 0:
                        entries.get(entries.size() - 1).name = tds.get(l).select("font").first().text();
                        break;
                    case 1:
                        entries.get(entries.size() - 1).points = Float.parseFloat(tds.get(l).select("font").first().text());
                        break;
                    case 2:
                        entries.get(entries.size() - 1).maxPoints = Float.parseFloat(tds.get(l).select("font").first().text());
                        break;
                    case 3:
                        entries.get(entries.size() - 1).average = Float.parseFloat(tds.get(l).select("font").first().text());
                        break;
                    case 4:
                        entries.get(entries.size() - 1).status = tds.get(l).select("font").first().text();
                        break;
                    case 5:
                        entries.get(entries.size() - 1).date = DateTimeFormat.forPattern(Entry.DATE_PATTERN).parseLocalDate(tds.get(l).select("font").first().text());
                        break;
                    case 6:
                        entries.get(entries.size() - 1).curve = Float.parseFloat(tds.get(l).select("font").first().text());
                        break;
                    case 7:
                        entries.get(entries.size() - 1).bonus = Float.parseFloat(tds.get(l).select("font").first().text());
                        break;
                    case 8:
                        // penalty
                        break;
                    case 9:
                        // weight;
                        break;
                    case 10:
                        entries.get(entries.size() - 1).note = tds.get(l).select("font").first().text();
                        break;
                }
            }

        }
        return entries;
    }

}
