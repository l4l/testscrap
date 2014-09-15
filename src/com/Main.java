package com;

/**
 * Created by kitsu on 10.09.14.
 * This file is part of TestScrap in package com.
 */

//0011111100100000001111110011111100111111001111110010000000111111001111110011111100111111001111110011111100111111001111110011111100111111001111110011111100100001
public class Main {

    /**
     * Var which contains flags info in bits:
     * 1: -v //verbose mode             //
     * 2: -w //num of every word at page//
     * 3: -c //chars of each page       //
     * 4: -e //extract sentences        //
     * 5: -m //don't match case         //
     * */
    private Integer flags = 0;

    //Time spent on parsing site(s)
    protected long[] time;

    private static Main m;

    public static void main(String[] args)
            throws java.net.MalformedURLException, java.net.URISyntaxException {
        if (args.length < 3) {
            System.out.println("Not enough arguments!");
            return;
        }

        m = new Main();

        ComLine comLine = ComLine.parseArguments(args, m);

        printer(m, comLine);

    }

    public static int counter(java.util.ArrayList<String> sent, String str) {
        int i = 0;
        if (!FLAGS_MODE.CASE_MODE.flagCheck(m.flags))
            str = str.toLowerCase();
        for (String s : sent) {
            if (!FLAGS_MODE.CASE_MODE.flagCheck(m.flags))
                s = s.toLowerCase();
            if (s.contains(str))
                i++;
        }

        return i;
    }

    public void flagBitsDivides(int t) {
        flags |= t;
    }

    private enum FLAGS_MODE {
        VERBOSE_MODE(1),
        WORD_COUNTER_MODE(2),
        CHARS_COUNTER_MODE(4),
        SENTENCE_MODE(8),
        CASE_MODE(16),
        ;

        private FLAGS_MODE(int i) {
            this.i = i;
        }

        private int i;

        public boolean flagCheck(int flags) {
            return (flags & this.i) == this.i;
        }
    }

    private static void printer(Main m, ComLine comLine) throws java.net.URISyntaxException {

        if (FLAGS_MODE.VERBOSE_MODE.flagCheck(m.flags))
            m.time = new long[comLine.getURL_LIST().size()];

        int i = 0;

        for (java.net.URL u : comLine.getURL_LIST()) {
            System.out.println("************************************");
            if (FLAGS_MODE.VERBOSE_MODE.flagCheck(m.flags))
                m.time[i] = System.currentTimeMillis();
            HtmlParser hp = new HtmlParser(u);
            System.out.println("Info about site " + u.toURI().toString());
            if (FLAGS_MODE.VERBOSE_MODE.flagCheck(m.flags)) {
                m.time[i] = System.currentTimeMillis() - m.time[i];
                System.out.printf("Time elapsed till page was parsed: %f seconds\n", (double) m.time[i++] / 1000);
            }
            if (FLAGS_MODE.WORD_COUNTER_MODE.flagCheck(m.flags)) {
                for (String s : comLine.getWORDS()) {
                    int num = counter(hp.getSentences(), s);
                    System.out.printf("\"%s\" occurrences is %d\n", s, num);
                }
            }
            if (FLAGS_MODE.CHARS_COUNTER_MODE.flagCheck(m.flags)) {
                System.out.println("Number of characters at page is " + hp.getChars());
            }
            if (FLAGS_MODE.SENTENCE_MODE.flagCheck(m.flags)) {
                StringBuilder s = new StringBuilder("");
                for (String str2 : comLine.getWORDS()) {
                    if (!FLAGS_MODE.CASE_MODE.flagCheck(m.flags))
                        str2 = str2.toLowerCase();
                    for (String str1 : hp.getSentences()) {
                        if (!FLAGS_MODE.CASE_MODE.flagCheck(m.flags))
                            str1 = str1.toLowerCase();
                        if (str1.contains(str2))
                            s.append("\t").append(str1).append("\n");
                    }
                    System.out.printf("Word \"%s\" was finded at this sentences:\n%s", str2, s);
                }
            }
            System.out.println("************************************");
        }
    }
}
