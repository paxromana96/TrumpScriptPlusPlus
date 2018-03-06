package com.brownian.trumpscript.tokenizer.token;

public class ErrorToken extends Token{
    public ErrorToken(String erroneousText){
        super(erroneousText, false);
    }

    @Override
    protected String getTokenTypeString() {
        return "Error";
    }
}