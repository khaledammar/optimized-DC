package ca.waterloo.dsg.graphflow.query.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.BitSet;

/**
 * This class is used to throw parse exceptions.
 */
public class ErrorListener extends BaseErrorListener {

    public static final ErrorListener INSTANCE = new ErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                            String msg, RecognitionException e) throws ParseCancellationException {
        throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
                                BitSet ambigAlts, ATNConfigSet configs) throws ParseCancellationException {
        throw new ParseCancellationException(
                "Ambiguity Exception startIndex:stopIndex=" + startIndex + ":" + stopIndex);
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                            BitSet conflictingAlts, ATNConfigSet configs)
            throws ParseCancellationException {
        throw new ParseCancellationException(
                "AttemptingFullContext Exception " + "startIndex:stopIndex=" + startIndex + ":" + stopIndex);
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
                                         ATNConfigSet configs) throws ParseCancellationException {
        throw new ParseCancellationException(
                "ContextSensitivity Exception startIndex:stopIndex=" + startIndex + ":" + stopIndex);
    }
}
