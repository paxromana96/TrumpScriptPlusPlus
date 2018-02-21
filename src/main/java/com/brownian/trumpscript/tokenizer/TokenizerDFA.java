package com.brownian.trumpscript.tokenizer;

import com.brownian.trumpscript.SymbolTable;
import com.brownian.trumpscript.TrumpscriptErrorReporter;
import com.brownian.trumpscript.tokenizer.token.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;


public class TokenizerDFA {

    private final SymbolTable symbolTable;
    private Reader characterSource;
    private char peek;
    private boolean isAtEOF = false;
    private TokenizerPDAState currentState;
    private StringBuilder tokenBuilder;
    private TrumpscriptErrorReporter errorHandler;

    /**
     * @see Reader#read() The value returned when read()ing from the end of the file.
     */
    private final static int EOF = -1;

    //TODO: incorporate symbol table and error handler
    public TokenizerDFA(Reader characterSource, SymbolTable symbolTable, TrumpscriptErrorReporter errorHandler) {
        this.characterSource = characterSource;
        this.symbolTable = symbolTable;
        this.errorHandler = errorHandler;
        peek = ' ';
    }

    /**
     * Reads and returns the next token from input
     *
     * @return the next token to be read from input
     * @throws IOException if an error is encountered while reading in a character, or if all of the tokens have been read
     * @see #hasMoreTokens()
     */
    public Token getNextToken() throws IOException {
        tokenBuilder = new StringBuilder();
        currentState = TokenizerPDAState.INITIAL_STATE;
        while (true) {
            switch (currentState) {
                case INITIAL_STATE:
                    skipOverCommentsAndWhitespace();
                    if (isAtEOF()) {
                        throw new NoMoreTokensException();
                    }
                    switch (peek) {
                        case 'm':
                        case 'M':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_M_;
                            break;
                        case 'p':
                        case 'P':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_P_;
                            break;
                        case 'g':
                        case 'G':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_G_;
                            break;
                        case 'a':
                        case 'A':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_A_;
                            break;
                        case 'i':
                        case 'I':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_I_;
                            break;
                        case 'e':
                        case 'E':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_E_;
                            break;
                        case 'b':
                        case 'B':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_B_;
                            break;
                        case 'l':
                        case 'L':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_L_;
                            break;
                        case 't':
                        case 'T':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_T_;
                            break;
                        case 's':
                        case 'S':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_S_;
                            break;
                        case 'f':
                        case 'F':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_F_;
                            break;
                        case 'o':
                        case 'O':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.KEYWORD_O_;
                            break;
                        case '"':
                            appendPeekToCurrentToken();
                            currentState = TokenizerPDAState.STRING_LITERAL_INCOMPLETE;
                            break;
                        default:
                            if (Character.isWhitespace(peek)) {
                                advancePeek();
                                break;
                            } else if (Character.isAlphabetic(peek)) {
                                appendPeekToCurrentToken();
                                currentState = TokenizerPDAState.ID;
                                break;
                            } else if (Character.isDigit(peek)) {
                                appendPeekToCurrentToken();
                                currentState = TokenizerPDAState.CONST_DIGIT_1;
                                break;
                            } else if (isSpecialCharacter(peek)) {
                                appendPeekToCurrentToken();
                                currentState = TokenizerPDAState.EMIT_SPECIAL_CHARACTER;
                                break;
                            } else {
                                appendPeekToCurrentToken();
                                currentState = TokenizerPDAState.ERR_ID;
                                break;
                            }
                    }
                    break; // out of the state switch
                case ID:
                    if (isAtEOF() || Character.isWhitespace(peek) || isSpecialCharacter(peek) || peek == '#') {
                        currentState = TokenizerPDAState.EMIT_ID;
                        break;
                    } else if (Character.isLetterOrDigit(peek)) {
                        appendPeekToCurrentToken();
                        break;
                    } else {
                        appendPeekToCurrentToken();
                        currentState = TokenizerPDAState.ERR_ID;
                        break;
                    }
                case KEYWORD_M_:
                    if (!tryToGoToKeywordStateWith('a', TokenizerPDAState.KEYWORD_MA_)
                            && !tryToGoToKeywordStateWith('o', TokenizerPDAState.KEYWORD_MO_)) {
                        handleKeywordMismatch();
                    }
                    break;
                case KEYWORD_MA_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('k', TokenizerPDAState.KEYWORD_MAK_);
                    break;
                case KEYWORD_MAK_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_POSSIBLE_MATCH:
                    if (isAtEOF() || isSpecialCharacter(peek) || Character.isWhitespace(peek) || peek == '#') {
                        currentState = TokenizerPDAState.EMIT_KEYWORD;
                    } else {
                        handleKeywordMismatch();
                    }
                    break;
                case KEYWORD_P_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('r', TokenizerPDAState.KEYWORD_PR_);
                    break;
                case KEYWORD_PR_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('o', TokenizerPDAState.KEYWORD_PRO_);
                    break;
                case KEYWORD_PRO_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('g', TokenizerPDAState.KEYWORD_PROG_);
                    break;
                case KEYWORD_PROG_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('r', TokenizerPDAState.KEYWORD_PROGR_);
                    break;
                case KEYWORD_PROGR_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('a', TokenizerPDAState.KEYWORD_PROGRA_);
                    break;
                case KEYWORD_PROGRA_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('m', TokenizerPDAState.KEYWORD_PROGRAM_);
                    break;
                case KEYWORD_PROGRAM_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('m', TokenizerPDAState.KEYWORD_PROGRAMM_);
                    break;
                case KEYWORD_PROGRAMM_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('i', TokenizerPDAState.KEYWORD_PROGRAMMI_);
                    break;
                case KEYWORD_PROGRAMMI_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('n', TokenizerPDAState.KEYWORD_PROGRAMMIN_);
                    break;
                case KEYWORD_PROGRAMMIN_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('g', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
//                case KEYWORD_PROGRAMMING_:
//                    break;
                case KEYWORD_G_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('r', TokenizerPDAState.KEYWORD_GR_);
                    break;
                case KEYWORD_GR_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_GRE_);
                    break;
                case KEYWORD_GRE_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('a', TokenizerPDAState.KEYWORD_GREA_);
                    break;
                case KEYWORD_GREA_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('t', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
//                case KEYWORD_GREAT_:
//                    break;
                case KEYWORD_A_:
                    if (!tryToGoToKeywordStateWith('s', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH)
                            && !tryToGoToKeywordStateWith('n', TokenizerPDAState.KEYWORD_AN_)
                            && !tryToGoToKeywordStateWith('m', TokenizerPDAState.KEYWORD_AM_)) {
                        tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('g', TokenizerPDAState.KEYWORD_AG_);
                    }
                    break;
                case KEYWORD_AG_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('a', TokenizerPDAState.KEYWORD_AGA_);
                    break;
                case KEYWORD_AGA_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('i', TokenizerPDAState.KEYWORD_AGAI_);
                    break;
                case KEYWORD_AGAI_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('n', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
//                case KEYWORD_AGAIN_:
//                    break;
                case KEYWORD_AN_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('d', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                case KEYWORD_PL_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('u', TokenizerPDAState.KEYWORD_PLU_);
                    break;
                case KEYWORD_PLU_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('s', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
//                case KEYWORD_PLUS_:
//                    break;
                case KEYWORD_MO_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('r', TokenizerPDAState.KEYWORD_MOR_);
                    break;
                case KEYWORD_MOR_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
//                case KEYWORD_MORE_:
//                    break;
                case KEYWORD_AM_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_AME_);
                    break;
                case KEYWORD_AME_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('r', TokenizerPDAState.KEYWORD_AMER_);
                    break;
                case KEYWORD_AMER_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('i', TokenizerPDAState.KEYWORD_AMERI_);
                    break;
                case KEYWORD_AMERI_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('c', TokenizerPDAState.KEYWORD_AMERIC_);
                    break;
                case KEYWORD_AMERIC_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('a', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_I_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('s', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_E_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('l', TokenizerPDAState.KEYWORD_EL_);
                    break;
                case KEYWORD_EL_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('s', TokenizerPDAState.KEYWORD_ELS_);
                    break;
                case KEYWORD_ELS_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_N_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('u', TokenizerPDAState.KEYWORD_NU_);
                    break;
                case KEYWORD_NU_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('m', TokenizerPDAState.KEYWORD_NUM_);
                    break;
                case KEYWORD_NUM_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('b', TokenizerPDAState.KEYWORD_NUMB_);
                    break;
                case KEYWORD_NUMB_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_NUMBE_);
                    break;
                case KEYWORD_NUMBE_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('r', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case STRING_LITERAL_INCOMPLETE:
                    if (isAtEOF()) {
                        currentState = TokenizerPDAState.ERR_OTHER;
                    } else if (peek == '"') {
                        appendPeekToCurrentToken();
                        currentState = TokenizerPDAState.STRING_LITERAL_COMPLETE;
                    } else if (Character.isLetterOrDigit(peek) || isSpecialCharacter(peek) || Character.isWhitespace(peek)) {
                        appendPeekToCurrentToken(); //and stay on this state
                    } else if (peek == '#') {
                        currentState = TokenizerPDAState.ERR_OTHER;
                    } else {
                        appendPeekToCurrentToken();
                        currentState = TokenizerPDAState.ERR_OTHER;
                    }
                    break;
                case STRING_LITERAL_COMPLETE:
                    if (isAtEOF() || Character.isWhitespace(peek) || peek == '#') {
                        currentState = TokenizerPDAState.EMIT_STRING_LITERAL;
                    } else {
                        appendPeekToCurrentToken();
                        currentState = TokenizerPDAState.ERR_OTHER;
                    }
                    break;
                case CONST_DIGIT_1:
                    tryToAdvanceConstToState(TokenizerPDAState.CONST_DIGIT_2);
                    break;
                case CONST_DIGIT_2:
                    tryToAdvanceConstToState(TokenizerPDAState.CONST_DIGIT_3);
                    break;
                case CONST_DIGIT_3:
                    tryToAdvanceConstToState(TokenizerPDAState.CONST_DIGIT_4);
                    break;
                case CONST_DIGIT_4:
                    tryToAdvanceConstToState(TokenizerPDAState.CONST_DIGIT_5);
                    break;
                case CONST_DIGIT_5:
                    tryToAdvanceConstToState(TokenizerPDAState.CONST_DIGIT_6);
                    break;
                case CONST_DIGIT_6:
                    tryToAdvanceConstToState(TokenizerPDAState.CONST);
                    break;
                case CONST:
                    if (isAtEOF() || Character.isWhitespace(peek) || isSpecialCharacter(peek) || peek == '#') {
                        currentState = TokenizerPDAState.EMIT_CONST;
                    } else if (Character.isDigit(peek)) {
                        appendPeekToCurrentToken();
                        currentState = TokenizerPDAState.CONST;
                    } else {
                        appendPeekToCurrentToken();
                        currentState = TokenizerPDAState.ERR_CONST;
                    }
                    break;
                case EMIT_ID:
                    Token idToken = symbolTable.lookupToken(tokenBuilder.toString());
                    if (idToken == null) {
                        idToken = new IdToken(tokenBuilder.toString());
                        symbolTable.setToken(tokenBuilder.toString(), idToken);
                    }
                    return idToken;
                case EMIT_CONST:
                    Token constToken = symbolTable.lookupToken(tokenBuilder.toString());
                    if (constToken == null) {
                        constToken = new IntegerConstantToken(Long.parseLong(tokenBuilder.toString()));
                        symbolTable.setToken(tokenBuilder.toString(), constToken);
                    }
                    return constToken;
                case EMIT_STRING_LITERAL:
                    Token stringLiteralToken = symbolTable.lookupToken(tokenBuilder.toString());
                    if (stringLiteralToken == null) {
                        stringLiteralToken = new StringLiteralToken(tokenBuilder.toString());
                        symbolTable.setToken(tokenBuilder.toString(), stringLiteralToken);
                    }
                    return stringLiteralToken;
                case EMIT_SPECIAL_CHARACTER:
                    return new SpecialCharacterToken(tokenBuilder.charAt(0));
                case EMIT_KEYWORD:
                    return new KeywordToken(tokenBuilder.toString());
                case ERR_ID:
                    if (isAtEOF() || Character.isWhitespace(peek) || peek == '#') {
                        errorHandler.logError(new IdTokenizerError());
                        return new ErrorToken(tokenBuilder.toString());
                    } else {
                        appendPeekToCurrentToken();
                    }
                    break;
                case ERR_CONST:
                    if (isAtEOF() || Character.isWhitespace(peek) || peek == '#') {
                        errorHandler.logError(new ConstTokenizerError());
                        return new ErrorToken(tokenBuilder.toString());
                    } else {
                        appendPeekToCurrentToken();
                    }
                    break;
                case ERR_OTHER:
                    if (isAtEOF() || Character.isWhitespace(peek) || peek == '#') {
                        errorHandler.logError(new TrumpscriptTokenizerError());
                        return new ErrorToken(tokenBuilder.toString());
                    } else {
                        appendPeekToCurrentToken();
                    }
                    break;
                case KEYWORD_B_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('o', TokenizerPDAState.KEYWORD_BO_);
                    break;
                case KEYWORD_BO_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('o', TokenizerPDAState.KEYWORD_BOO_);
                    break;
                case KEYWORD_BOO_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('l', TokenizerPDAState.KEYWORD_BOOL_);
                    break;
                case KEYWORD_BOOL_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_BOOLE_);
                    break;
                case KEYWORD_BOOLE_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('a', TokenizerPDAState.KEYWORD_BOOLEA_);
                    break;
                case KEYWORD_BOOLEA_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('n', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_L_:
                    if (!tryToGoToKeywordStateWith('i', TokenizerPDAState.KEYWORD_LI_)
                            && !tryToGoToKeywordStateWith('e', TokenizerPDAState.KEYWORD_LE_)) {
                        tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('o', TokenizerPDAState.KEYWORD_LO_);
                    }
                    break;
                case KEYWORD_LO_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('n', TokenizerPDAState.KEYWORD_LON_);
                    break;
                case KEYWORD_LON_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('g', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_T_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_TE_);
                    break;
                case KEYWORD_TE_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('l', TokenizerPDAState.KEYWORD_TEL_);
                    break;
                case KEYWORD_TEL_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('l', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_S_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('a', TokenizerPDAState.KEYWORD_SA_);
                    break;
                case KEYWORD_SA_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('y', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_F_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('a', TokenizerPDAState.KEYWORD_FA_);
                    break;
                case KEYWORD_FA_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('c', TokenizerPDAState.KEYWORD_FAC_);
                    break;
                case KEYWORD_FAC_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('t', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_LI_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_NO_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('t', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_O_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('r', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_LE_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('s', TokenizerPDAState.KEYWORD_LES_);
                    break;
                case KEYWORD_LES_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('s', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
                case KEYWORD_TI_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('m', TokenizerPDAState.KEYWORD_TIM_);
                    break;
                case KEYWORD_TIM_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('e', TokenizerPDAState.KEYWORD_TIME_);
                    break;
                case KEYWORD_TIME_:
                    tryToGoToKeywordStateWithCharElseHandleKeywordMismatch('s', TokenizerPDAState.KEYWORD_POSSIBLE_MATCH);
                    break;
            }
        }
    }

    /**
     * Appends the current {@link #peek} character to the current token,
     * then advances {@link #peek}.
     *
     * @throws IOException if there is an IOException reading the next character
     * @see #advancePeek()
     */
    private void appendPeekToCurrentToken() throws IOException {
        tokenBuilder.append(peek);
        advancePeek();
    }


    /**
     * Tries to read the given character as the next character of a keyword,
     * and goes to the appropriate state if the next character is NOT that.
     *
     * @param expectedCharacter
     * @param nextStateIfMatch
     * @throws IOException
     * @see #tryToGoToKeywordStateWith(char, TokenizerPDAState)
     * @see #handleKeywordMismatch()
     */
    private void tryToGoToKeywordStateWithCharElseHandleKeywordMismatch(char expectedCharacter, TokenizerPDAState nextStateIfMatch) throws IOException {
        if (!tryToGoToKeywordStateWith(expectedCharacter, nextStateIfMatch))
            handleKeywordMismatch();
    }

    /**
     * Tries to read the given expected character of a keyword, and then go to the given state if successful.
     * On success, appends that character to the current token and returns true.
     * On failure, returns false.
     *
     * @param expectedCharacter the character of the keyword to try to read
     * @param nextStateOnMatch  the state to go to if that character is read
     * @return true if successful, and false otherwise
     * @throws IOException if an IOException is encountered while reading a character
     */
    private boolean tryToGoToKeywordStateWith(char expectedCharacter, TokenizerPDAState nextStateOnMatch) throws IOException {
        if (!isAtEOF() && Character.toLowerCase(peek) == expectedCharacter) {
            appendPeekToCurrentToken();
            currentState = nextStateOnMatch;
            return true;
        } else return false;
    }

    /**
     * Tries to read a digit to append to the current token as a constant, and proceed to the given state if successful.
     * Proceeds to ERR_CONST on encountering anything else.
     *
     * @param nextStateOnMatch the state to proceed to on successfully reading a digit.
     * @throws IOException if there is a problem reading a character
     */
    private void tryToAdvanceConstToState(TokenizerPDAState nextStateOnMatch) throws IOException {
        if (isAtEOF() || isSpecialCharacter(peek) || Character.isWhitespace(peek) || peek == '#') {
            currentState = TokenizerPDAState.ERR_CONST;
        } else if (Character.isDigit(peek)) {
            appendPeekToCurrentToken();
            currentState = nextStateOnMatch;
        } else {
            appendPeekToCurrentToken();
            currentState = TokenizerPDAState.ERR_CONST;
        }
    }

    /**
     * Reads another character from the {@link #characterSource}, storing the result in {@link #peek},
     * and updating the {@link #isAtEOF} flag if the end of input has been reached.
     *
     * @throws IOException if {@link Reader#read} throws such an error
     */
    private void advancePeek() throws IOException {
        if (isAtEOF) {
            peek = ' ';
            return;
        }
        int nextChar = characterSource.read();
        isAtEOF = (nextChar == EOF);
        peek = (char) nextChar;
    }

    /**
     * Called when partially through a keyword, but reading a character
     * that doesn't match the next in line for that keyword.
     * <p>
     * E.g., in state "M A K", reading an "E" would confirm "make", but reading
     * anything else would suggest an ID or something - that's when this is called.
     *
     * @throws IOException if an IOException is encountered while reading the next character
     */
    private void handleKeywordMismatch() throws IOException {
        if (isAtEOF() || Character.isWhitespace(peek) || isSpecialCharacter(peek) || peek == '#') {
            currentState = TokenizerPDAState.EMIT_ID;
        } else if (Character.isLetterOrDigit(peek)) {
            appendPeekToCurrentToken();
            currentState = TokenizerPDAState.ID;
        } else {
            appendPeekToCurrentToken();
            currentState = TokenizerPDAState.ERR_ID;
        }
    }

    /**
     * Determines whether or not a character is considered a special character in Trumpscript++.
     * Special characters are , ; : ! ? ( )
     *
     * @param character the character to consider
     * @return true if the character is a "special character" in Trumpscript++, and false otherwise.
     */
    private boolean isSpecialCharacter(char character) {
        return character == ',' || character == ';' || character == ':' || character == '!' || character == '?' || character == '(' || character == ')';
    }

    /**
     * Determines whether or not this TokenizerDFA has consumed all of the characters from the characterSource,
     * and is at the end of its input. This happens when {@link Reader#read()} returns -1.
     *
     * @return true if there are no more characters to consume, and false otherwise.
     */
    public boolean isAtEOF() {
        return isAtEOF;
    }

    public boolean hasMoreTokens() throws IOException {
        skipOverCommentsAndWhitespace();
        return !isAtEOF();
    }

    private void skipOverWhitespace() throws IOException {
        while (!isAtEOF() && Character.isWhitespace(peek))
            advancePeek();
    }

    private void skipUntilNewline() throws IOException {
        while (!isAtEOF() && peek != '\n')
            advancePeek();
    }

    private void skipOverComments() throws IOException {
        while (!isAtEOF() && peek == '#')
            skipUntilNewline();
    }

    private void skipOverCommentsAndWhitespace() throws IOException {
        while(!isAtEOF() && (Character.isWhitespace(peek) || peek == '#')){
            skipOverWhitespace();
            skipOverComments();
        }
    }

    private enum TokenizerPDAState {
        INITIAL_STATE,

        ID,

        KEYWORD_M_, KEYWORD_MA_, KEYWORD_MAK_,
        KEYWORD_P_, KEYWORD_PR_, KEYWORD_PRO_, KEYWORD_PROG_, KEYWORD_PROGR_, KEYWORD_PROGRA_,
        KEYWORD_PROGRAM_, KEYWORD_PROGRAMM_, KEYWORD_PROGRAMMI_, KEYWORD_PROGRAMMIN_,
        KEYWORD_G_, KEYWORD_GR_, KEYWORD_GRE_, KEYWORD_GREA_,
        KEYWORD_A_, KEYWORD_AG_, KEYWORD_AGA_, KEYWORD_AGAI_,
        KEYWORD_AM_, KEYWORD_AME_, KEYWORD_AMER_, KEYWORD_AMERI_, KEYWORD_AMERIC_,
        KEYWORD_I_,
        KEYWORD_E_, KEYWORD_EL_, KEYWORD_ELS_,
        KEYWORD_N_, KEYWORD_NU_, KEYWORD_NUM_, KEYWORD_NUMB_, KEYWORD_NUMBE_,
        KEYWORD_B_, KEYWORD_BO_, KEYWORD_BOO_, KEYWORD_BOOL_, KEYWORD_BOOLE_, KEYWORD_BOOLEA_,
        KEYWORD_L_, KEYWORD_LO_, KEYWORD_LON_,
        KEYWORD_T_, KEYWORD_TE_, KEYWORD_TEL_,
        KEYWORD_S_, KEYWORD_SA_,
        KEYWORD_F_, KEYWORD_FA_, KEYWORD_FAC_,
        KEYWORD_LI_,
        KEYWORD_NO_,
        KEYWORD_AN_,
        KEYWORD_O_,
        KEYWORD_LE_, KEYWORD_LES_,
        KEYWORD_MO_, KEYWORD_MOR_,
        KEYWORD_PL_, KEYWORD_PLU_,
        KEYWORD_TI_, KEYWORD_TIM_, KEYWORD_TIME_,
        KEYWORD_POSSIBLE_MATCH,

        STRING_LITERAL_INCOMPLETE, STRING_LITERAL_COMPLETE,

        CONST_DIGIT_1, CONST_DIGIT_2, CONST_DIGIT_3, CONST_DIGIT_4, CONST_DIGIT_5, CONST_DIGIT_6, CONST,

        EMIT_ID, EMIT_CONST, EMIT_STRING_LITERAL, EMIT_SPECIAL_CHARACTER, EMIT_KEYWORD,

        ERR_ID, ERR_CONST, ERR_OTHER
    }

    private class NoMoreTokensException extends EOFException {
        NoMoreTokensException() {
            super("EOF reached; no more tokens to be read");
        }
    }
}
