package net.NindyBun.jamt.Tools;

public class Helpers {

    public static String fancyNumber(int number) {
        if (number < 1000)
            return String.valueOf(number);
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f%c",
                number / Math.pow(1000, exp),
                "kMGTPE_____".charAt(exp - 1));
    }
}
