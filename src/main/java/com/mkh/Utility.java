package com.mkh;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Utility {
    public static ArrayList<String> extractHashtags(String text) {
        ArrayList<String> hashtags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#[a-zA-Z0-9_]+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            hashtags.add(matcher.group());
        }
        return hashtags;
    }
}
