package net.royalmind.stats.data.containers.leaderboards;

import org.junit.Test;

public class LeaderboardContainerImplTest {

    @Test
    public void split() {
        //%top_1_%
        final String line = "%top_1_name% - %top_1_kills%";
        if (line.contains("%top_1_name%")) {
            final String[] s = line.split("_");
            int a = 0;
            for (String s1 : s) {
                System.out.println(a + " | " + s1);
                if (s1.equalsIgnoreCase("name%")) {
                    System.out.println("1       - Detect");
                } else if (s1.equalsIgnoreCase("kilss%")){
                    System.out.println("2       - Detect");
                }
                a++;
            }
        }
    }

}