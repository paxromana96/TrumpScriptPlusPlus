package com.brownian.trumpscript.tokenizer;

import com.brownian.trumpscript.BOOKKEEPER;
import com.brownian.trumpscript.ERRORHANDLER;
import com.brownian.trumpscript.tokenizer.token.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerDFATest {

    private static final String
            AMERICA_IS_GREAT = "America is great";
    public static final String KEYWORDS_STRING = "make programming great again america is else number boolean if as long tell say fact lie not and or less more plus times";

    @Test
    void testIsAtEOFAfterReadingAllOfInput() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader(AMERICA_IS_GREAT)) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);

            assertFalse(tokenizerDFA.isAtEOF(), "Just opened SCANNER - with contents - but it's at EOF");
            tokenizerDFA.getNextToken();
            assertFalse(tokenizerDFA.isAtEOF(), "EOF after first token, should be 3");
            tokenizerDFA.getNextToken();
            assertFalse(tokenizerDFA.isAtEOF(), "EOF after second token, should be 3");
            tokenizerDFA.getNextToken();
            assertTrue(tokenizerDFA.isAtEOF(), "No EOF after third token, when it was the last");
        }
    }

    @Test
    void testCanReadThreeKeywordsWithExtraWhitespace() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader(" \n\t" + AMERICA_IS_GREAT + "   \r\n")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("America", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof KeywordToken, "Expected first token to be keyword");
            assertEquals(TokenType.AMERICA, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "No more tokens after first, should be 3");

            token = tokenizerDFA.getNextToken();
            assertEquals("is", token.getLexeme(), "Mismatch on second token");
            assertTrue(token instanceof KeywordToken, "Expected second token to be keyword");
            assertEquals(TokenType.IS, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "No more tokens after second, should be 3");

            token = tokenizerDFA.getNextToken();
            assertEquals("great", token.getLexeme(), "Mismatch on last (third) token");
            assertTrue(token instanceof KeywordToken, "Expected third token to be keyword");
            assertEquals(TokenType.GREAT, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "No EOF after last (third) token");
        }
    }

    @Test
    void testCanReadThreeKeywordsIgnoringTrailingComment() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader( AMERICA_IS_GREAT + "# Except Alabama")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("America", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof KeywordToken, "Expected first token to be keyword");
            assertEquals(TokenType.AMERICA, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "No more tokens after first, should be 3");

            token = tokenizerDFA.getNextToken();
            assertEquals("is", token.getLexeme(), "Mismatch on second token");
            assertTrue(token instanceof KeywordToken, "Expected second token to be keyword");
            assertEquals(TokenType.IS, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "No more tokens after second, should be 3");

            token = tokenizerDFA.getNextToken();
            assertEquals("great", token.getLexeme(), "Mismatch on last (third) token");
            assertTrue(token instanceof KeywordToken, "Expected third token to be keyword");
            assertEquals(TokenType.GREAT, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "No EOF after last (third) token");
        }
    }

    @Test
    void testCanReadSingleKeywordToken() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader(AMERICA_IS_GREAT)) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertFalse(tokenizerDFA.isAtEOF(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("America", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof KeywordToken, "Expected first token to be keyword");
            assertEquals(TokenType.AMERICA, token.getType());
            assertFalse(tokenizerDFA.isAtEOF(), "EOF after first token, should be 3");
        }
    }

    @Test
    void testCanReadTwoKeywordTokens() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader(AMERICA_IS_GREAT)) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertFalse(tokenizerDFA.isAtEOF(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("America", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof KeywordToken, "Expected first token to be keyword");
            assertEquals(TokenType.AMERICA, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "No more tokens after first, should be 3");

            token = tokenizerDFA.getNextToken();
            assertEquals("is", token.getLexeme(), "Mismatch on second token");
            assertTrue(token instanceof KeywordToken, "Expected first token to be keyword");
            assertEquals(TokenType.IS, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "No more tokens after second, should be 3");
        }
    }


    @Test
    void testCanReadAmericaIsGreatThenEOF() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader(AMERICA_IS_GREAT)) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("America", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof KeywordToken, "Expected first token to be keyword");
            assertEquals(TokenType.AMERICA, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "No more tokens after first, should be 3");

            token = tokenizerDFA.getNextToken();
            assertEquals("is", token.getLexeme(), "Mismatch on second token");
            assertTrue(token instanceof KeywordToken, "Expected second token to be keyword");
            assertEquals(TokenType.IS, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "No more tokens after second, should be 3");

            token = tokenizerDFA.getNextToken();
            assertEquals("great", token.getLexeme(), "Mismatch on last (third) token");
            assertTrue(token instanceof KeywordToken, "Expected third token to be keyword");
            assertEquals(TokenType.GREAT, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "No EOF after last (third) token");
        }
    }

    @Test
    void testCanReadSingleId() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader("x")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("x", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof IdToken, "Expected first token to be id");
            assertEquals(TokenType.ID, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "There should only be a single token");
        }
    }

    @Test
    void testCanFormIdFromKeywordPlusALetter() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader("AmericaX")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("AmericaX", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof IdToken, "Expected first token to be id");
            assertEquals(TokenType.ID, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "Should only be one token");
        }
    }

    @Test
    void testCanFormIdFromLettersAndNumbers() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader("Jenny8675309Number")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("Jenny8675309Number", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof IdToken, "Expected first token to be id");
            assertEquals(TokenType.ID, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "Should only be one token");
        }
    }

    @Test
    void testCanFollowIdWithSpecialCharacter() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader("Jenny8675309Number!")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("Jenny8675309Number", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof IdToken, "Expected first token to be id");
            assertEquals(TokenType.ID, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "Shouldn't end after first token");

            token = tokenizerDFA.getNextToken();
            assertEquals("!", token.getLexeme(), "Mismatch on second token");
            assertTrue(token instanceof SpecialCharacterToken, "Expected second token to be '!'");
            assertEquals(TokenType.EXCLAMATION_MARK, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "Should only be two tokens");
        }
    }

    @Test
    void testCanReadEachSpecialCharacter() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader("Jenny8675309Number!?,:;()")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("Jenny8675309Number", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof IdToken, "Expected first token to be id");
            assertEquals(TokenType.ID, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "Ended at first token, should be 8");

            token = tokenizerDFA.getNextToken();
            assertEquals("!", token.getLexeme(), "Mismatch on second token");
            assertTrue(token instanceof SpecialCharacterToken, "Expected second token to be '!'");
            assertEquals(TokenType.EXCLAMATION_MARK, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "Ended at second token, should be 8");

            token = tokenizerDFA.getNextToken();
            assertEquals("?", token.getLexeme(), "Mismatch on third token");
            assertTrue(token instanceof SpecialCharacterToken, "Expected third token to be '?'");
            assertEquals(TokenType.QUESTION_MARK, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "Ended at third token, should be 8");

            token = tokenizerDFA.getNextToken();
            assertEquals(",", token.getLexeme(), "Mismatch on fourth token");
            assertTrue(token instanceof SpecialCharacterToken, "Expected fourth token to be ','");
            assertEquals(TokenType.COMMA, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "Ended at fourth token, should be 8");

            token = tokenizerDFA.getNextToken();
            assertEquals(":", token.getLexeme(), "Mismatch on fifth token");
            assertTrue(token instanceof SpecialCharacterToken, "Expected fifth token to be ':'");
            assertEquals(TokenType.COLON, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "Ended at fifth token, should be 8");

            token = tokenizerDFA.getNextToken();
            assertEquals(";", token.getLexeme(), "Mismatch on sixth token");
            assertTrue(token instanceof SpecialCharacterToken, "Expected sixth token to be ';'");
            assertEquals(TokenType.SEMICOLON, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "Ended at sixth token, should be 8");

            token = tokenizerDFA.getNextToken();
            assertEquals("(", token.getLexeme(), "Mismatch on seventh token");
            assertTrue(token instanceof SpecialCharacterToken, "Expected seventh token to be '('");
            assertEquals(TokenType.LEFT_PAREN, token.getType());
            assertTrue(tokenizerDFA.hasMoreTokens(), "Ended at seventh token, should be 8");

            token = tokenizerDFA.getNextToken();
            assertEquals(")", token.getLexeme(), "Mismatch on eighth token");
            assertTrue(token instanceof SpecialCharacterToken, "Expected eighth token to be ')'");
            assertEquals(TokenType.RIGHT_PAREN, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "Didn't end at eighth token, should be only 8");
        }
    }

    @Test
    void testCanReadStringLiteral() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader("\"Jenny8675309Number!\"")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("\"Jenny8675309Number!\"", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof StringLiteralToken, "Expected first token to be string literal");
            assertEquals(TokenType.STRING, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "Should only be one token");
        }
    }

    @Test
    void testCanReadValidConstToken() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader("1234567")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("1234567", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof IntegerConstantToken, "Expected first token to be constant");
            assertEquals(TokenType.CONST, token.getType());
            assertEquals(1234567L, ((IntegerConstantToken) token).getValue());
            assertFalse(tokenizerDFA.hasMoreTokens(), "Should only be one token");
        }
    }

    @Test
    void testCanFormIdFromKeywordMinusALetter() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader("Americ")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("Americ", token.getLexeme(), "Mismatch on first token");
            assertTrue(token instanceof IdToken, "Expected first token to be id");
            assertEquals(TokenType.ID, token.getType());
            assertFalse(tokenizerDFA.hasMoreTokens(), "Should only be one token");
        }
    }

    @Test
    void testOnlyWhitespaceIndicatesNoMoreTokens() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader(" \n\r\t\n ")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertFalse(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with only whitespace - but it says there are some tokens");
        }
    }

    @Test
    void testEachKeywordIsReadAsAKeyword() throws IOException {
        try (StringReader americaIsGreatReader = new StringReader(KEYWORDS_STRING)) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorReporter = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(americaIsGreatReader, symbolTable, errorReporter);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token;

            String[] keywords = KEYWORDS_STRING.split("\\s+");
            for(String keyword : keywords) {
                token = tokenizerDFA.getNextToken();
                assertEquals(keyword.toLowerCase(), token.getLexeme().toLowerCase(), "Mismatch on token lexeme");
                assertTrue(token instanceof KeywordToken, "Expected token " + token.getLexeme() + " to be read as a keyword");
                assertEquals(keyword.toLowerCase(), token.getType().name().toLowerCase(), "Token type " + token.getType() + " didn't match expected lexeme " + keyword);
            }
            assertFalse(tokenizerDFA.hasMoreTokens(), "No EOF after last token");
        }
    }

    @Test
    void testCanRead2MillionAsAConst() throws IOException {
        try (Reader twoMillionReader = new StringReader("2000000")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorhandler = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(twoMillionReader, symbolTable, errorhandler);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("2000000", token.getLexeme(), "Lexeme read doesn't match input");
            assertTrue(token instanceof IntegerConstantToken, "Tried to read integer const, but failed. Read instead: "+token);
            assertEquals(TokenType.CONST, token.getType());
            assertEquals(2000000L, ((IntegerConstantToken) token).getValue() , "Value doesn't match");
        }
    }

    @Test
    void testMalformedConstIsMalformedToken() throws IOException {
        try (Reader badTokenReader = new StringReader("12345vv 3421\"")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorhandler = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(badTokenReader, symbolTable, errorhandler);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("12345vv", token.getLexeme(), "Lexeme read doesn't match input");
            assertTrue(token instanceof ErrorToken, "Tried to read malformed integer const as bad token, but failed. Read instead: " + token);
            assertEquals(TokenType.MALFORMED_TOKEN, token.getType());

            token = tokenizerDFA.getNextToken();
            assertEquals("3421\"", token.getLexeme(), "Lexeme read doesn't match input");
            assertTrue(token instanceof ErrorToken, "Tried to read malformed integer const as bad token, but failed. Read instead: " + token);
            assertEquals(TokenType.MALFORMED_TOKEN, token.getType());
        }
    }

    @Test
    void testSmallConstIsMalformedToken() throws IOException {
        try (Reader badTokenReader = new StringReader("123456")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorhandler = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(badTokenReader, symbolTable, errorhandler);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("123456", token.getLexeme(), "Lexeme read doesn't match input");
            assertTrue(token instanceof ErrorToken, "Tried to read malformed (small-valued) integer const as bad token, but failed. Read instead: " + token);
            assertEquals(TokenType.MALFORMED_TOKEN, token.getType());

        }
    }

    @Test
    void testOneMillionIsAMalformedToken() throws IOException {
        try (Reader badTokenReader = new StringReader("1000000")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorhandler = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(badTokenReader, symbolTable, errorhandler);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("1000000", token.getLexeme(), "Lexeme read doesn't match input");
            assertTrue(token instanceof ErrorToken, "Tried to read malformed integer const as bad token, but failed. Read instead: " + token);
            assertEquals(TokenType.MALFORMED_TOKEN, token.getType());
        }
    }

    @Test
    void testOneMillionAndOneIsAConstToken() throws IOException {
        try (Reader badTokenReader = new StringReader("1000001")) {
            BOOKKEEPER symbolTable = new BOOKKEEPER();
            ERRORHANDLER errorhandler = new ERRORHANDLER(System.err);
            SCANNER tokenizerDFA = new SCANNER(badTokenReader, symbolTable, errorhandler);
            assertTrue(tokenizerDFA.hasMoreTokens(), "Just opened SCANNER - with contents - but it's at EOF");

            Token token = tokenizerDFA.getNextToken();
            assertEquals("1000001", token.getLexeme(), "Lexeme read doesn't match input");
            assertTrue(token instanceof IntegerConstantToken, "Tried to read 1,000,001 as integer const token, but failed. Read instead: " + token);
            assertEquals(TokenType.CONST, token.getType());
            assertEquals(1000001L, ((IntegerConstantToken) token).getValue());
        }
    }
}