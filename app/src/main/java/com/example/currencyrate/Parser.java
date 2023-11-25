package com.example.currencyrate;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    public List<Map<String, String>> parseData(String xmlData) {
        List<Map<String, String>> currencyList = new ArrayList<>();

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(xmlData));

            boolean inItemTag = false;
            Map<String, String> currencyData = null;

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = parser.getEventType();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("item")) {
                            inItemTag = true;
                            currencyData = new HashMap<>();
                        } else if (inItemTag && parser.getName().equals("title")) {
                            String title = parser.nextText().trim();
                            currencyData.put("title", title);
                        } else if (inItemTag && parser.getName().equals("targetName")) {
                            String targetName = parser.nextText().trim();
                            currencyData.put("targetName", targetName);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            inItemTag = false;
                            currencyList.add(currencyData);
                        }
                        break;
                }
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return currencyList;
    }
}
