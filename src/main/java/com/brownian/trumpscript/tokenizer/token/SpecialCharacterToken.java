package com.brownian.trumpscript.tokenizer.token;

import com.brownian.trumpscript.tokenizer.SCANNER;

/**
 * A {@link Token} representing a single special character in TrumpScript++.
 * Special characters are determined by {@link SCANNER#isSpecialCharacter(char)}
 */
public class SpecialCharacterToken extends Token{
    private char specialCharacter;

    /**
     * Constructs a {@link Token} representing the given special character
     * @param specialCharacter the special character for which to construct a {@link Token}
     * @param type which kind of special character this is
     */
    public SpecialCharacterToken(char specialCharacter, TokenType type) {
        super(Character.toString(specialCharacter), type);
        if (!type.isSpecialSymbol()) {
            throw new IllegalArgumentException("Tried to make a SpecialCharacterToken with a type that is not a special character: " + type);
        }

        this.specialCharacter = specialCharacter;
    }

    /**
     * Gets the special character this {@link SpecialCharacterToken} represents
     * @return the special character this {@link SpecialCharacterToken} represents
     */
    public char getSpecialCharacter(){
        return specialCharacter;
    }

    /**
     * Returns a short, human-readable string indicating that this {@link Token} is a {@link SpecialCharacterToken}
     * @return a short, human-readable string indicating that this is a special character
     */
    @Override
    protected String getTokenTypeString() {
        return "Special character";
    }
}
