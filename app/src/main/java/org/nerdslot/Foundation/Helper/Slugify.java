package org.nerdslot.Foundation.Helper;

import androidx.annotation.NonNull;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class Slugify {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGE_CASES = Pattern.compile("(^-|-$)");

    public Slugify(){}

    public static class Builder{
        private String seperator;
        private String input;

        public Builder input(@NonNull String input){
            this.input = input;
            return this;
        }

        public Builder seperator(@NonNull String seperator){
            this.seperator = seperator;
            return this;
        }

        public String make(){
            String noWhitespace = WHITESPACE.matcher(input).replaceAll(seperator);
            String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
            String slug = NONLATIN.matcher(normalized).replaceAll("");
            slug = EDGE_CASES.matcher(slug).replaceAll("");
            return slug.toLowerCase(Locale.ENGLISH);
        }
    }
}
