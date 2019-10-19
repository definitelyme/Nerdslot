package org.nerdslot.Foundation.Helper;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.nerdslot.Fragments.RootInterface;

/**
 * Pluralizes a word
 * @return String - Pluralized word.
 */
public class Pluralizer implements RootInterface {

    public static String build(String word) {
        if (StringUtils.endsWithIgnoreCase(word, "y"))
            return RegExUtils.replacePattern(word, "[yY]$", "ies");
        else if (StringUtils.endsWithIgnoreCase(word, "s"))
            return String.format("%s%s", word, "es");
        else if (StringUtils.endsWithIgnoreCase(word, "d"))
            return word;
        else
            return String.format("%s%s", word, "s");
    }
}
