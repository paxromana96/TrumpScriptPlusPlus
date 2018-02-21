package com.brownian.trumpscript.tokenizer.token;

public class IntegerConstantToken extends Token {
    public long getValue() {
        return value;
    }

    private final long value;

    public IntegerConstantToken(long value){
        super(String.valueOf(value), true);

        this.value = value;
    }


    @Override
    protected String getTokenTypeString() {
        return "Const";
    }
}
