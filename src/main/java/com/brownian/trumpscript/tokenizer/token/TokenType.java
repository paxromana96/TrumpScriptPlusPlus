package com.brownian.trumpscript.tokenizer.token;

/**
 * Indicate the type of a particular token.
 *
 * The numeric value of that type can be found via {@link TokenType#ordinal()}.
 *
 * Except for a {@link TokenType#MALFORMED_TOKEN}, these are matched
 * to the same ordinal of equivalent {@link com.brownian.trumpscript.parser.StackItemType} enums.
 */
public enum TokenType {
    MALFORMED_TOKEN, // 0 -- should not be used to select a StackItemType
    //// Terminal tokens
    ID, // 1
    CONST, // 2
    STRING, // 3
    // keywords
    MAKE,  // 4
    PROGRAMMING,
    GREAT,
    AGAIN,
    AMERICA,
    IS,
    ELSE,
    NUMBER,
    BOOLEAN,
    IF,
    AS,
    LONG,
    TELL,
    SAY,
    FACT,
    LIE,
    NOT,
    AND,
    OR,
    LESS,
    MORE,
    PLUS,
    TIMES, //26
    // special symbols
    COMMA, // 27
    SEMICOLON,
    COLON,
    EXCLAMATION_MARK,
    QUESTION_MARK,
    LEFT_PAREN,
    RIGHT_PAREN; // 33

    public boolean isKeyword(){
        return this.ordinal() >= 4 && this.ordinal() <= 26;
    }

    public boolean isSpecialSymbol(){
        return this.ordinal() >= 27 && this.ordinal() <= 33;
    }

    @Override
    public String toString(){
        if(this.isKeyword()){
            return this.name().toLowerCase();
        }
        if(this.isSpecialSymbol()){
            switch(this){
                case COMMA:
                    return ",";
                case SEMICOLON:
                    return ";";
                case COLON:
                    return ":";
                case EXCLAMATION_MARK:
                    return "!";
                case QUESTION_MARK:
                    return "?";
                case LEFT_PAREN:
                    return "(";
                case RIGHT_PAREN:
                    return ")";
                default:
                    throw new IllegalStateException("Calling toString() of unexpected special symbol "+this.name());
            }
        }
        if(this == ID || this == CONST || this == STRING){
            return String.format("[%s]", this.name().toLowerCase());
        }
        if(this == MALFORMED_TOKEN){
            return this.name();
        }
        throw new IllegalStateException("Calling toString() of unexpected, unknown token type "+this.name());
    }
}
