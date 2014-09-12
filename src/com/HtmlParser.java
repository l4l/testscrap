package com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by kitsu on 10.09.14.
 * This file is part of TestScrap in package com.
 */
public class HtmlParser {

    //Tags where should search for plain text
    private static final char[][] parsable;
    private static final Map<String, String> htmlReplacements = new java.util.HashMap<>();

    static {
        htmlReplacements.put("&nbsp;", " ");
        htmlReplacements.put("&mdash;", "—");
        parsable = new char[][]{
                "p".toCharArray(), "h1".toCharArray(), "h2".toCharArray(), "h3".toCharArray(),
                "h4".toCharArray(), "span".toCharArray(), "script".toCharArray()
        };
    }

    private long chars;

    private java.util.ArrayList<String> sentences = new java.util.ArrayList<>();


    public HtmlParser(java.net.URL url) {
        init(url);
    }

    private void init(java.net.URL u) {

        try (java.io.BufferedReader in = new java.io.BufferedReader(
                new java.io.InputStreamReader(u.openStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }

            String str = sb.toString();
            chars = str.length();
            parse(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parse(String html) {
        boolean isJS = false, isText = false, isOpenedTag = false;
        ArrayList<Character> temp = new ArrayList<>();
        for (char c : html.toCharArray()) {
            if (c == '<') {
                isOpenedTag = true;
                if (isText) {
                    isText = putToList(temp);
                    temp.clear();
                }
            } else if (isOpenedTag) {
                if (c == '>') {
                    isOpenedTag = false;
                    if (!temp.isEmpty())
                        for (char[] tmp: parsable) {
                            if (isEqual(tmp, temp)) {
                                if (isEqual(parsable[parsable.length-1], temp)) {
                                    isJS ^= true;
                                } else {
                                    isText = !isJS;
                                }
                                break;
                            }
                        }
                    temp.clear();
                } else
                    temp.add(c);
            } else if (isText && !isJS) {
                isText = true;
                temp.add(c);
            }
        }
    }

    private boolean putToList(ArrayList<Character> charList) {
        StringBuilder sb = new StringBuilder(charList.size());
        for (char c : charList) {
            sb.append(c);
        }
        String s[] = String.valueOf(
                fixHtmlSymbols(
                        sb.toString()
                                .replaceAll("[\\s](\\x20)*$|^(\\x20)*", ""))).split("[\\.\\?!\t]");
        for (String str : s) {
            if (!str.equals(""))
                sentences.add(str);
        }

        return false;
    }

    private String fixHtmlSymbols(String s) {

        for (Map.Entry<String, String> i : htmlReplacements.entrySet()) {
            s = s.replaceAll(i.getKey(), i.getValue());
        }
        return s;
    }

    private boolean isEqual(char[] c, ArrayList<Character> charList) {
        if (charList.get(0) == '/') {
            charList.remove(0);
        }
        for (int i = 0; i < c.length; i++) {
            if (c[i] != charList.get(i)) {
                return false;
            }
        }
        return true;
    }

    public long getChars() {
        return chars;
    }

    public ArrayList<String> getSentences() {
        return sentences;
    }
}
