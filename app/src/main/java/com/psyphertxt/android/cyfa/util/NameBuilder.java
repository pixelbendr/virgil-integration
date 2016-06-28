package com.psyphertxt.android.cyfa.util;

/**
 * Created by Codebendr on 5/17/15.
 */
public class NameBuilder {
    private String delimiter = "-", tokenChars = "0123456789";
    private int tokenLength = 4;
    private boolean tokenHex = false;

    public NameBuilder setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public NameBuilder setTokenChars(String tokenChars) {
        this.tokenChars = tokenChars;
        return this;
    }

    public NameBuilder setTokenLength(int tokenLength) {
        this.tokenLength = tokenLength;
        return this;
    }

    public NameBuilder setTokenHex(boolean tokenHex) {
        this.tokenHex = tokenHex;
        return this;
    }

    public NameUtils build() {
        return new NameUtils(delimiter, tokenChars, tokenLength, tokenHex);
    }
}
