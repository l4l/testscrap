package com;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kitsu on 10.09.14.
 * This file is part of TestScrap in package com.
 */
public class ComLine {
    public List<URL> getURL_LIST() {
        return URL_LIST;
    }

    public List<String> getWORDS() {
        return WORDS;
    }

    // List of urls
    private final List<URL> URL_LIST;
    // Words that need to find
    private final List<String> WORDS;

    private ComLine(java.util.List<String> words, java.util.List<URL> urls) {
        WORDS = words;
        URL_LIST = urls;
    }

    public static ComLine parseArguments(String[] args, Main m)
            throws java.net.MalformedURLException, java.net.URISyntaxException {
        final List<URL> url = parseUrls(args[0]);
        List<String> words = new java.util.ArrayList<>();
        java.util.Collections.addAll(words, args[1].split("[,.;:]"));

        for (int i = 2; i < args.length; ++i) {
            if (args[i].length() == 2 &&
                    args[i].charAt(0) == '-') {

                int temp = 1;
                switch (args[i].charAt(1)) {
                    case 'e':       //flag = 0b1000;
                        temp <<= 1;
                    case 'c':       //flag = 0b0100;
                        temp <<= 1;
                    case 'w':       //flag = 0b0010;
                        temp <<= 1;
                    case 'v':       //flag = 0b0001;
                        m.flagBitsDivides(temp);
                }
            } else
                break; //If input is ok, this will never execute
        }

        return new ComLine(words, url);
    }


    // Check input and fill uri variable with links
    private static List<URL> parseUrls(String str) {
        java.net.URI uri = null;
        try {
            uri = new java.net.URI(str);
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
        }
        List<URL> url = new ArrayList<>();
        if (uri != null &&
                (uri.getScheme() == null ||
                !uri.getScheme().equals("http"))) {

            try (java.io.BufferedReader fis = new java.io.BufferedReader(
                    new java.io.InputStreamReader(
                        new java.io.FileInputStream(uri.toString())))) {
                String lines;
                while ((lines = fis.readLine()) != null) {
                    url.add(new URL(lines));
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
        return url;
    }

}
