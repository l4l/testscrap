package com;

/**
 * Created by kitsu on 10.09.14.
 * This file is part of TestScrap in package com.
 */
public class Main {

    /**
     * Var which contains flags info in bits:
     * 1: -v //verbose mode//
     * 2: -w //num of every word at page//
     * 3: -c //chars of each page//
     * 4: -e //extract sentences//
     * */
    private Integer flags = 0;

    //Time spent on parsing site(s)
    protected long[] time;

    public static void main(String[] args)
            throws java.net.MalformedURLException, java.net.URISyntaxException {
        if (args.length < 3) {
            System.out.println("Not enough arguments!");
            return;
        }
        Main m = new Main();
        ComLine comLine = ComLine.parseArguments(args, m);

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
                System.out.printf("Time elapsed till parsing: %f seconds\n", (double) m.time[i++] / 1000);
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
                    for (String str1 : hp.getSentences()) {
                        if (str1.contains(str2))
                            s.append("\t").append(str1).append("\n");
                    }
                    System.out.printf("Word \"%s\" was finded at this sentences:\n%s", str2, s);
                }
            }
            System.out.println("************************************");
        }

    }

    public static int counter(java.util.ArrayList<String> sent, String str) {
        int i = 0;
        for (String s : sent) {
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
        SENTENCE_MODE(8);

        private FLAGS_MODE(int i) {
            this.i = i;
        }

        private int i;

        public boolean flagCheck(int flags) {
            return (flags & this.i) == this.i;
        }
    }
}
