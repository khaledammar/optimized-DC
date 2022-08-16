// Generated from ca/waterloo/dsg/graphflow/grammar/Graphflow.g4 by ANTLR 4.7
package ca.waterloo.dsg.graphflow.grammar;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GraphflowLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		QuotedCharacter=1, QuotedString=2, Comment=3, MATCH=4, CONTINUOUSLY=5, 
		EXPLAIN=6, CREATE=7, DELETE=8, SHORTEST=9, PATH=10, WEIGHTS=11, WGHT=12, 
		WHERE=13, RETURN=14, COUNT=15, AVG=16, MAX=17, MIN=18, SUM=19, ACTION=20, 
		IN=21, UDF=22, JAR=23, LOAD=24, SAVE=25, FROM=26, TO=27, DIR=28, TRUE=29, 
		FALSE=30, AND=31, OR=32, ON=33, FILE=34, SPACE=35, TAB=36, CARRIAGE_RETURN=37, 
		LINE_FEED=38, FORM_FEED=39, BACKSPACE=40, VERTICAL_TAB=41, STAR=42, DASH=43, 
		UNDERSCORE=44, DOT=45, FORWARD_SLASH=46, BACKWARD_SLASH=47, SEMICOLON=48, 
		COLON=49, COMMA=50, SINGLE_QUOTE=51, DOUBLE_QUOTE=52, UNARY_OR=53, OPEN_ROUND_BRACKET=54, 
		CLOSE_ROUND_BRACKET=55, OPEN_CURLY_BRACKET=56, CLOSE_CURLY_BRACKET=57, 
		OPEN_SQUARE_BRACKET=58, CLOSE_SQUARE_BRACKET=59, EQUAL_TO=60, NOT_EQUAL_TO=61, 
		LESS_THAN=62, GREATER_THAN=63, LESS_THAN_OR_EQUAL=64, GREATER_THAN_OR_EQUAL=65, 
		Characters=66, Digits=67;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"EscapedChar", "QuotedCharacter", "QuotedString", "Comment", "MATCH", 
		"CONTINUOUSLY", "EXPLAIN", "CREATE", "DELETE", "SHORTEST", "PATH", "WEIGHTS", 
		"WGHT", "WHERE", "RETURN", "COUNT", "AVG", "MAX", "MIN", "SUM", "ACTION", 
		"IN", "UDF", "JAR", "LOAD", "SAVE", "FROM", "TO", "DIR", "TRUE", "FALSE", 
		"AND", "OR", "ON", "FILE", "SPACE", "TAB", "CARRIAGE_RETURN", "LINE_FEED", 
		"FORM_FEED", "BACKSPACE", "VERTICAL_TAB", "STAR", "DASH", "UNDERSCORE", 
		"DOT", "FORWARD_SLASH", "BACKWARD_SLASH", "SEMICOLON", "COLON", "COMMA", 
		"SINGLE_QUOTE", "DOUBLE_QUOTE", "UNARY_OR", "OPEN_ROUND_BRACKET", "CLOSE_ROUND_BRACKET", 
		"OPEN_CURLY_BRACKET", "CLOSE_CURLY_BRACKET", "OPEN_SQUARE_BRACKET", "CLOSE_SQUARE_BRACKET", 
		"EQUAL_TO", "NOT_EQUAL_TO", "LESS_THAN", "GREATER_THAN", "LESS_THAN_OR_EQUAL", 
		"GREATER_THAN_OR_EQUAL", "A", "B", "C", "D", "E", "F", "G", "H", "I", 
		"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", 
		"X", "Y", "Z", "Character", "Characters", "Digit", "Digits"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, "'*'", "'-'", "'_'", "'.'", "'/'", 
		"'\\'", "';'", "':'", "','", "'''", "'\"'", "'|'", "'('", "')'", "'{'", 
		"'}'", "'['", "']'", "'='", "'<>'", "'<'", "'>'", "'<='", "'>='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "QuotedCharacter", "QuotedString", "Comment", "MATCH", "CONTINUOUSLY", 
		"EXPLAIN", "CREATE", "DELETE", "SHORTEST", "PATH", "WEIGHTS", "WGHT", 
		"WHERE", "RETURN", "COUNT", "AVG", "MAX", "MIN", "SUM", "ACTION", "IN", 
		"UDF", "JAR", "LOAD", "SAVE", "FROM", "TO", "DIR", "TRUE", "FALSE", "AND", 
		"OR", "ON", "FILE", "SPACE", "TAB", "CARRIAGE_RETURN", "LINE_FEED", "FORM_FEED", 
		"BACKSPACE", "VERTICAL_TAB", "STAR", "DASH", "UNDERSCORE", "DOT", "FORWARD_SLASH", 
		"BACKWARD_SLASH", "SEMICOLON", "COLON", "COMMA", "SINGLE_QUOTE", "DOUBLE_QUOTE", 
		"UNARY_OR", "OPEN_ROUND_BRACKET", "CLOSE_ROUND_BRACKET", "OPEN_CURLY_BRACKET", 
		"CLOSE_CURLY_BRACKET", "OPEN_SQUARE_BRACKET", "CLOSE_SQUARE_BRACKET", 
		"EQUAL_TO", "NOT_EQUAL_TO", "LESS_THAN", "GREATER_THAN", "LESS_THAN_OR_EQUAL", 
		"GREATER_THAN_OR_EQUAL", "Characters", "Digits"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public GraphflowLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Graphflow.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2E\u024b\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\u00cb\n\2\3\3\3\3\3\3\5\3\u00d0"+
		"\n\3\3\3\3\3\3\4\3\4\3\4\7\4\u00d7\n\4\f\4\16\4\u00da\13\4\3\4\3\4\3\4"+
		"\3\4\3\4\7\4\u00e1\n\4\f\4\16\4\u00e4\13\4\3\4\3\4\5\4\u00e8\n\4\3\5\3"+
		"\5\3\5\3\5\7\5\u00ee\n\5\f\5\16\5\u00f1\13\5\3\5\3\5\3\5\3\5\3\5\3\5\7"+
		"\5\u00f9\n\5\f\5\16\5\u00fc\13\5\3\5\5\5\u00ff\n\5\3\5\5\5\u0102\n\5\5"+
		"\5\u0104\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3"+
		"\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3"+
		"\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3"+
		"\26\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3"+
		"\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3"+
		"\34\3\34\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3"+
		" \3 \3 \3 \3 \3 \3!\3!\3!\3!\3\"\3\"\3\"\3#\3#\3#\3$\3$\3$\3$\3$\3%\3"+
		"%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3"+
		"\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3"+
		"\67\38\38\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\3?\3?\3?\3@\3@\3A\3A\3B"+
		"\3B\3B\3C\3C\3C\3D\3D\3E\3E\3F\3F\3G\3G\3H\3H\3I\3I\3J\3J\3K\3K\3L\3L"+
		"\3M\3M\3N\3N\3O\3O\3P\3P\3Q\3Q\3R\3R\3S\3S\3T\3T\3U\3U\3V\3V\3W\3W\3X"+
		"\3X\3Y\3Y\3Z\3Z\3[\3[\3\\\3\\\3]\3]\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3"+
		"^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\5^\u023e\n^\3_\6_\u0241\n"+
		"_\r_\16_\u0242\3`\3`\3a\6a\u0248\na\ra\16a\u0249\3\u00ef\2b\3\2\5\3\7"+
		"\4\t\5\13\6\r\7\17\b\21\t\23\n\25\13\27\f\31\r\33\16\35\17\37\20!\21#"+
		"\22%\23\'\24)\25+\26-\27/\30\61\31\63\32\65\33\67\349\35;\36=\37? A!C"+
		"\"E#G$I%K&M\'O(Q)S*U+W,Y-[.]/_\60a\61c\62e\63g\64i\65k\66m\67o8q9s:u;"+
		"w<y={>}?\177@\u0081A\u0083B\u0085C\u0087\2\u0089\2\u008b\2\u008d\2\u008f"+
		"\2\u0091\2\u0093\2\u0095\2\u0097\2\u0099\2\u009b\2\u009d\2\u009f\2\u00a1"+
		"\2\u00a3\2\u00a5\2\u00a7\2\u00a9\2\u00ab\2\u00ad\2\u00af\2\u00b1\2\u00b3"+
		"\2\u00b5\2\u00b7\2\u00b9\2\u00bb\2\u00bdD\u00bf\2\u00c1E\3\2)\5\2$$))"+
		"^^\4\2))^^\3\2$$\3\2))\4\2\f\f\17\17\3\3\f\f\3\2\"\"\3\2\13\13\3\2\17"+
		"\17\3\2\f\f\3\2\16\16\3\2\n\n\3\2\r\r\4\2CCcc\4\2DDdd\4\2EEee\4\2FFff"+
		"\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2O"+
		"Ooo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4"+
		"\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u0257\2\5\3\2\2\2\2\7\3\2\2"+
		"\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23"+
		"\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2"+
		"\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3"+
		"\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2"+
		"\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2"+
		"\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2["+
		"\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2"+
		"\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2"+
		"\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2"+
		"\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u00bd\3\2\2\2\2\u00c1"+
		"\3\2\2\2\3\u00ca\3\2\2\2\5\u00cc\3\2\2\2\7\u00e7\3\2\2\2\t\u0103\3\2\2"+
		"\2\13\u0105\3\2\2\2\r\u010b\3\2\2\2\17\u0118\3\2\2\2\21\u0120\3\2\2\2"+
		"\23\u0127\3\2\2\2\25\u012e\3\2\2\2\27\u0137\3\2\2\2\31\u013c\3\2\2\2\33"+
		"\u0144\3\2\2\2\35\u0149\3\2\2\2\37\u014f\3\2\2\2!\u0156\3\2\2\2#\u015c"+
		"\3\2\2\2%\u0160\3\2\2\2\'\u0164\3\2\2\2)\u0168\3\2\2\2+\u016c\3\2\2\2"+
		"-\u0173\3\2\2\2/\u0176\3\2\2\2\61\u017a\3\2\2\2\63\u017e\3\2\2\2\65\u0183"+
		"\3\2\2\2\67\u0188\3\2\2\29\u018d\3\2\2\2;\u0190\3\2\2\2=\u0194\3\2\2\2"+
		"?\u0199\3\2\2\2A\u019f\3\2\2\2C\u01a3\3\2\2\2E\u01a6\3\2\2\2G\u01a9\3"+
		"\2\2\2I\u01ae\3\2\2\2K\u01b0\3\2\2\2M\u01b2\3\2\2\2O\u01b4\3\2\2\2Q\u01b6"+
		"\3\2\2\2S\u01b8\3\2\2\2U\u01ba\3\2\2\2W\u01bc\3\2\2\2Y\u01be\3\2\2\2["+
		"\u01c0\3\2\2\2]\u01c2\3\2\2\2_\u01c4\3\2\2\2a\u01c6\3\2\2\2c\u01c8\3\2"+
		"\2\2e\u01ca\3\2\2\2g\u01cc\3\2\2\2i\u01ce\3\2\2\2k\u01d0\3\2\2\2m\u01d2"+
		"\3\2\2\2o\u01d4\3\2\2\2q\u01d6\3\2\2\2s\u01d8\3\2\2\2u\u01da\3\2\2\2w"+
		"\u01dc\3\2\2\2y\u01de\3\2\2\2{\u01e0\3\2\2\2}\u01e2\3\2\2\2\177\u01e5"+
		"\3\2\2\2\u0081\u01e7\3\2\2\2\u0083\u01e9\3\2\2\2\u0085\u01ec\3\2\2\2\u0087"+
		"\u01ef\3\2\2\2\u0089\u01f1\3\2\2\2\u008b\u01f3\3\2\2\2\u008d\u01f5\3\2"+
		"\2\2\u008f\u01f7\3\2\2\2\u0091\u01f9\3\2\2\2\u0093\u01fb\3\2\2\2\u0095"+
		"\u01fd\3\2\2\2\u0097\u01ff\3\2\2\2\u0099\u0201\3\2\2\2\u009b\u0203\3\2"+
		"\2\2\u009d\u0205\3\2\2\2\u009f\u0207\3\2\2\2\u00a1\u0209\3\2\2\2\u00a3"+
		"\u020b\3\2\2\2\u00a5\u020d\3\2\2\2\u00a7\u020f\3\2\2\2\u00a9\u0211\3\2"+
		"\2\2\u00ab\u0213\3\2\2\2\u00ad\u0215\3\2\2\2\u00af\u0217\3\2\2\2\u00b1"+
		"\u0219\3\2\2\2\u00b3\u021b\3\2\2\2\u00b5\u021d\3\2\2\2\u00b7\u021f\3\2"+
		"\2\2\u00b9\u0221\3\2\2\2\u00bb\u023d\3\2\2\2\u00bd\u0240\3\2\2\2\u00bf"+
		"\u0244\3\2\2\2\u00c1\u0247\3\2\2\2\u00c3\u00cb\5K&\2\u00c4\u00cb\5M\'"+
		"\2\u00c5\u00cb\5O(\2\u00c6\u00cb\5S*\2\u00c7\u00cb\5Q)\2\u00c8\u00c9\7"+
		"^\2\2\u00c9\u00cb\t\2\2\2\u00ca\u00c3\3\2\2\2\u00ca\u00c4\3\2\2\2\u00ca"+
		"\u00c5\3\2\2\2\u00ca\u00c6\3\2\2\2\u00ca\u00c7\3\2\2\2\u00ca\u00c8\3\2"+
		"\2\2\u00cb\4\3\2\2\2\u00cc\u00cf\5i\65\2\u00cd\u00d0\5\3\2\2\u00ce\u00d0"+
		"\n\3\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1"+
		"\u00d2\5i\65\2\u00d2\6\3\2\2\2\u00d3\u00d8\5k\66\2\u00d4\u00d7\5\3\2\2"+
		"\u00d5\u00d7\n\4\2\2\u00d6\u00d4\3\2\2\2\u00d6\u00d5\3\2\2\2\u00d7\u00da"+
		"\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00db\3\2\2\2\u00da"+
		"\u00d8\3\2\2\2\u00db\u00dc\5k\66\2\u00dc\u00e8\3\2\2\2\u00dd\u00e2\5i"+
		"\65\2\u00de\u00e1\5\3\2\2\u00df\u00e1\n\5\2\2\u00e0\u00de\3\2\2\2\u00e0"+
		"\u00df\3\2\2\2\u00e1\u00e4\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e2\u00e3\3\2"+
		"\2\2\u00e3\u00e5\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e5\u00e6\5i\65\2\u00e6"+
		"\u00e8\3\2\2\2\u00e7\u00d3\3\2\2\2\u00e7\u00dd\3\2\2\2\u00e8\b\3\2\2\2"+
		"\u00e9\u00ea\7\61\2\2\u00ea\u00eb\7,\2\2\u00eb\u00ef\3\2\2\2\u00ec\u00ee"+
		"\13\2\2\2\u00ed\u00ec\3\2\2\2\u00ee\u00f1\3\2\2\2\u00ef\u00f0\3\2\2\2"+
		"\u00ef\u00ed\3\2\2\2\u00f0\u00f2\3\2\2\2\u00f1\u00ef\3\2\2\2\u00f2\u00f3"+
		"\7,\2\2\u00f3\u0104\7\61\2\2\u00f4\u00f5\7\61\2\2\u00f5\u00f6\7\61\2\2"+
		"\u00f6\u00fa\3\2\2\2\u00f7\u00f9\n\6\2\2\u00f8\u00f7\3\2\2\2\u00f9\u00fc"+
		"\3\2\2\2\u00fa\u00f8\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb\u00fe\3\2\2\2\u00fc"+
		"\u00fa\3\2\2\2\u00fd\u00ff\7\17\2\2\u00fe\u00fd\3\2\2\2\u00fe\u00ff\3"+
		"\2\2\2\u00ff\u0101\3\2\2\2\u0100\u0102\t\7\2\2\u0101\u0100\3\2\2\2\u0102"+
		"\u0104\3\2\2\2\u0103\u00e9\3\2\2\2\u0103\u00f4\3\2\2\2\u0104\n\3\2\2\2"+
		"\u0105\u0106\5\u009fP\2\u0106\u0107\5\u0087D\2\u0107\u0108\5\u00adW\2"+
		"\u0108\u0109\5\u008bF\2\u0109\u010a\5\u0095K\2\u010a\f\3\2\2\2\u010b\u010c"+
		"\5\u008bF\2\u010c\u010d\5\u00a3R\2\u010d\u010e\5\u00a1Q\2\u010e\u010f"+
		"\5\u00adW\2\u010f\u0110\5\u0097L\2\u0110\u0111\5\u00a1Q\2\u0111\u0112"+
		"\5\u00afX\2\u0112\u0113\5\u00a3R\2\u0113\u0114\5\u00afX\2\u0114\u0115"+
		"\5\u00abV\2\u0115\u0116\5\u009dO\2\u0116\u0117\5\u00b7\\\2\u0117\16\3"+
		"\2\2\2\u0118\u0119\5\u008fH\2\u0119\u011a\5\u00b5[\2\u011a\u011b\5\u00a5"+
		"S\2\u011b\u011c\5\u009dO\2\u011c\u011d\5\u0087D\2\u011d\u011e\5\u0097"+
		"L\2\u011e\u011f\5\u00a1Q\2\u011f\20\3\2\2\2\u0120\u0121\5\u008bF\2\u0121"+
		"\u0122\5\u00a9U\2\u0122\u0123\5\u008fH\2\u0123\u0124\5\u0087D\2\u0124"+
		"\u0125\5\u00adW\2\u0125\u0126\5\u008fH\2\u0126\22\3\2\2\2\u0127\u0128"+
		"\5\u008dG\2\u0128\u0129\5\u008fH\2\u0129\u012a\5\u009dO\2\u012a\u012b"+
		"\5\u008fH\2\u012b\u012c\5\u00adW\2\u012c\u012d\5\u008fH\2\u012d\24\3\2"+
		"\2\2\u012e\u012f\5\u00abV\2\u012f\u0130\5\u0095K\2\u0130\u0131\5\u00a3"+
		"R\2\u0131\u0132\5\u00a9U\2\u0132\u0133\5\u00adW\2\u0133\u0134\5\u008f"+
		"H\2\u0134\u0135\5\u00abV\2\u0135\u0136\5\u00adW\2\u0136\26\3\2\2\2\u0137"+
		"\u0138\5\u00a5S\2\u0138\u0139\5\u0087D\2\u0139\u013a\5\u00adW\2\u013a"+
		"\u013b\5\u0095K\2\u013b\30\3\2\2\2\u013c\u013d\5\u00b3Z\2\u013d\u013e"+
		"\5\u008fH\2\u013e\u013f\5\u0097L\2\u013f\u0140\5\u0093J\2\u0140\u0141"+
		"\5\u0095K\2\u0141\u0142\5\u00adW\2\u0142\u0143\5\u00abV\2\u0143\32\3\2"+
		"\2\2\u0144\u0145\5\u00b3Z\2\u0145\u0146\5\u0093J\2\u0146\u0147\5\u0095"+
		"K\2\u0147\u0148\5\u00adW\2\u0148\34\3\2\2\2\u0149\u014a\5\u00b3Z\2\u014a"+
		"\u014b\5\u0095K\2\u014b\u014c\5\u008fH\2\u014c\u014d\5\u00a9U\2\u014d"+
		"\u014e\5\u008fH\2\u014e\36\3\2\2\2\u014f\u0150\5\u00a9U\2\u0150\u0151"+
		"\5\u008fH\2\u0151\u0152\5\u00adW\2\u0152\u0153\5\u00afX\2\u0153\u0154"+
		"\5\u00a9U\2\u0154\u0155\5\u00a1Q\2\u0155 \3\2\2\2\u0156\u0157\5\u008b"+
		"F\2\u0157\u0158\5\u00a3R\2\u0158\u0159\5\u00afX\2\u0159\u015a\5\u00a1"+
		"Q\2\u015a\u015b\5\u00adW\2\u015b\"\3\2\2\2\u015c\u015d\5\u0087D\2\u015d"+
		"\u015e\5\u00b1Y\2\u015e\u015f\5\u0093J\2\u015f$\3\2\2\2\u0160\u0161\5"+
		"\u009fP\2\u0161\u0162\5\u0087D\2\u0162\u0163\5\u00b5[\2\u0163&\3\2\2\2"+
		"\u0164\u0165\5\u009fP\2\u0165\u0166\5\u0097L\2\u0166\u0167\5\u00a1Q\2"+
		"\u0167(\3\2\2\2\u0168\u0169\5\u00abV\2\u0169\u016a\5\u00afX\2\u016a\u016b"+
		"\5\u009fP\2\u016b*\3\2\2\2\u016c\u016d\5\u0087D\2\u016d\u016e\5\u008b"+
		"F\2\u016e\u016f\5\u00adW\2\u016f\u0170\5\u0097L\2\u0170\u0171\5\u00a3"+
		"R\2\u0171\u0172\5\u00a1Q\2\u0172,\3\2\2\2\u0173\u0174\5\u0097L\2\u0174"+
		"\u0175\5\u00a1Q\2\u0175.\3\2\2\2\u0176\u0177\5\u00afX\2\u0177\u0178\5"+
		"\u008dG\2\u0178\u0179\5\u0091I\2\u0179\60\3\2\2\2\u017a\u017b\5\u0099"+
		"M\2\u017b\u017c\5\u0087D\2\u017c\u017d\5\u00a9U\2\u017d\62\3\2\2\2\u017e"+
		"\u017f\5\u009dO\2\u017f\u0180\5\u00a3R\2\u0180\u0181\5\u0087D\2\u0181"+
		"\u0182\5\u008dG\2\u0182\64\3\2\2\2\u0183\u0184\5\u00abV\2\u0184\u0185"+
		"\5\u0087D\2\u0185\u0186\5\u00b1Y\2\u0186\u0187\5\u008fH\2\u0187\66\3\2"+
		"\2\2\u0188\u0189\5\u0091I\2\u0189\u018a\5\u00a9U\2\u018a\u018b\5\u00a3"+
		"R\2\u018b\u018c\5\u009fP\2\u018c8\3\2\2\2\u018d\u018e\5\u00adW\2\u018e"+
		"\u018f\5\u00a3R\2\u018f:\3\2\2\2\u0190\u0191\5\u008dG\2\u0191\u0192\5"+
		"\u0097L\2\u0192\u0193\5\u00a9U\2\u0193<\3\2\2\2\u0194\u0195\5\u00adW\2"+
		"\u0195\u0196\5\u00a9U\2\u0196\u0197\5\u00afX\2\u0197\u0198\5\u008fH\2"+
		"\u0198>\3\2\2\2\u0199\u019a\5\u0091I\2\u019a\u019b\5\u0087D\2\u019b\u019c"+
		"\5\u009dO\2\u019c\u019d\5\u00abV\2\u019d\u019e\5\u008fH\2\u019e@\3\2\2"+
		"\2\u019f\u01a0\5\u0087D\2\u01a0\u01a1\5\u00a1Q\2\u01a1\u01a2\5\u008dG"+
		"\2\u01a2B\3\2\2\2\u01a3\u01a4\5\u00a3R\2\u01a4\u01a5\5\u00a9U\2\u01a5"+
		"D\3\2\2\2\u01a6\u01a7\5\u00a3R\2\u01a7\u01a8\5\u00a1Q\2\u01a8F\3\2\2\2"+
		"\u01a9\u01aa\5\u0091I\2\u01aa\u01ab\5\u0097L\2\u01ab\u01ac\5\u009dO\2"+
		"\u01ac\u01ad\5\u008fH\2\u01adH\3\2\2\2\u01ae\u01af\t\b\2\2\u01afJ\3\2"+
		"\2\2\u01b0\u01b1\t\t\2\2\u01b1L\3\2\2\2\u01b2\u01b3\t\n\2\2\u01b3N\3\2"+
		"\2\2\u01b4\u01b5\t\13\2\2\u01b5P\3\2\2\2\u01b6\u01b7\t\f\2\2\u01b7R\3"+
		"\2\2\2\u01b8\u01b9\t\r\2\2\u01b9T\3\2\2\2\u01ba\u01bb\t\16\2\2\u01bbV"+
		"\3\2\2\2\u01bc\u01bd\7,\2\2\u01bdX\3\2\2\2\u01be\u01bf\7/\2\2\u01bfZ\3"+
		"\2\2\2\u01c0\u01c1\7a\2\2\u01c1\\\3\2\2\2\u01c2\u01c3\7\60\2\2\u01c3^"+
		"\3\2\2\2\u01c4\u01c5\7\61\2\2\u01c5`\3\2\2\2\u01c6\u01c7\7^\2\2\u01c7"+
		"b\3\2\2\2\u01c8\u01c9\7=\2\2\u01c9d\3\2\2\2\u01ca\u01cb\7<\2\2\u01cbf"+
		"\3\2\2\2\u01cc\u01cd\7.\2\2\u01cdh\3\2\2\2\u01ce\u01cf\7)\2\2\u01cfj\3"+
		"\2\2\2\u01d0\u01d1\7$\2\2\u01d1l\3\2\2\2\u01d2\u01d3\7~\2\2\u01d3n\3\2"+
		"\2\2\u01d4\u01d5\7*\2\2\u01d5p\3\2\2\2\u01d6\u01d7\7+\2\2\u01d7r\3\2\2"+
		"\2\u01d8\u01d9\7}\2\2\u01d9t\3\2\2\2\u01da\u01db\7\177\2\2\u01dbv\3\2"+
		"\2\2\u01dc\u01dd\7]\2\2\u01ddx\3\2\2\2\u01de\u01df\7_\2\2\u01dfz\3\2\2"+
		"\2\u01e0\u01e1\7?\2\2\u01e1|\3\2\2\2\u01e2\u01e3\7>\2\2\u01e3\u01e4\7"+
		"@\2\2\u01e4~\3\2\2\2\u01e5\u01e6\7>\2\2\u01e6\u0080\3\2\2\2\u01e7\u01e8"+
		"\7@\2\2\u01e8\u0082\3\2\2\2\u01e9\u01ea\7>\2\2\u01ea\u01eb\7?\2\2\u01eb"+
		"\u0084\3\2\2\2\u01ec\u01ed\7@\2\2\u01ed\u01ee\7?\2\2\u01ee\u0086\3\2\2"+
		"\2\u01ef\u01f0\t\17\2\2\u01f0\u0088\3\2\2\2\u01f1\u01f2\t\20\2\2\u01f2"+
		"\u008a\3\2\2\2\u01f3\u01f4\t\21\2\2\u01f4\u008c\3\2\2\2\u01f5\u01f6\t"+
		"\22\2\2\u01f6\u008e\3\2\2\2\u01f7\u01f8\t\23\2\2\u01f8\u0090\3\2\2\2\u01f9"+
		"\u01fa\t\24\2\2\u01fa\u0092\3\2\2\2\u01fb\u01fc\t\25\2\2\u01fc\u0094\3"+
		"\2\2\2\u01fd\u01fe\t\26\2\2\u01fe\u0096\3\2\2\2\u01ff\u0200\t\27\2\2\u0200"+
		"\u0098\3\2\2\2\u0201\u0202\t\30\2\2\u0202\u009a\3\2\2\2\u0203\u0204\t"+
		"\31\2\2\u0204\u009c\3\2\2\2\u0205\u0206\t\32\2\2\u0206\u009e\3\2\2\2\u0207"+
		"\u0208\t\33\2\2\u0208\u00a0\3\2\2\2\u0209\u020a\t\34\2\2\u020a\u00a2\3"+
		"\2\2\2\u020b\u020c\t\35\2\2\u020c\u00a4\3\2\2\2\u020d\u020e\t\36\2\2\u020e"+
		"\u00a6\3\2\2\2\u020f\u0210\t\37\2\2\u0210\u00a8\3\2\2\2\u0211\u0212\t"+
		" \2\2\u0212\u00aa\3\2\2\2\u0213\u0214\t!\2\2\u0214\u00ac\3\2\2\2\u0215"+
		"\u0216\t\"\2\2\u0216\u00ae\3\2\2\2\u0217\u0218\t#\2\2\u0218\u00b0\3\2"+
		"\2\2\u0219\u021a\t$\2\2\u021a\u00b2\3\2\2\2\u021b\u021c\t%\2\2\u021c\u00b4"+
		"\3\2\2\2\u021d\u021e\t&\2\2\u021e\u00b6\3\2\2\2\u021f\u0220\t\'\2\2\u0220"+
		"\u00b8\3\2\2\2\u0221\u0222\t(\2\2\u0222\u00ba\3\2\2\2\u0223\u023e\5\u0087"+
		"D\2\u0224\u023e\5\u0089E\2\u0225\u023e\5\u008bF\2\u0226\u023e\5\u008d"+
		"G\2\u0227\u023e\5\u008fH\2\u0228\u023e\5\u0091I\2\u0229\u023e\5\u0093"+
		"J\2\u022a\u023e\5\u0095K\2\u022b\u023e\5\u0097L\2\u022c\u023e\5\u0099"+
		"M\2\u022d\u023e\5\u009bN\2\u022e\u023e\5\u009dO\2\u022f\u023e\5\u009f"+
		"P\2\u0230\u023e\5\u00a1Q\2\u0231\u023e\5\u00a3R\2\u0232\u023e\5\u00a5"+
		"S\2\u0233\u023e\5\u00a7T\2\u0234\u023e\5\u00a9U\2\u0235\u023e\5\u00ab"+
		"V\2\u0236\u023e\5\u00adW\2\u0237\u023e\5\u00afX\2\u0238\u023e\5\u00b1"+
		"Y\2\u0239\u023e\5\u00b3Z\2\u023a\u023e\5\u00b5[\2\u023b\u023e\5\u00b7"+
		"\\\2\u023c\u023e\5\u00b9]\2\u023d\u0223\3\2\2\2\u023d\u0224\3\2\2\2\u023d"+
		"\u0225\3\2\2\2\u023d\u0226\3\2\2\2\u023d\u0227\3\2\2\2\u023d\u0228\3\2"+
		"\2\2\u023d\u0229\3\2\2\2\u023d\u022a\3\2\2\2\u023d\u022b\3\2\2\2\u023d"+
		"\u022c\3\2\2\2\u023d\u022d\3\2\2\2\u023d\u022e\3\2\2\2\u023d\u022f\3\2"+
		"\2\2\u023d\u0230\3\2\2\2\u023d\u0231\3\2\2\2\u023d\u0232\3\2\2\2\u023d"+
		"\u0233\3\2\2\2\u023d\u0234\3\2\2\2\u023d\u0235\3\2\2\2\u023d\u0236\3\2"+
		"\2\2\u023d\u0237\3\2\2\2\u023d\u0238\3\2\2\2\u023d\u0239\3\2\2\2\u023d"+
		"\u023a\3\2\2\2\u023d\u023b\3\2\2\2\u023d\u023c\3\2\2\2\u023e\u00bc\3\2"+
		"\2\2\u023f\u0241\5\u00bb^\2\u0240\u023f\3\2\2\2\u0241\u0242\3\2\2\2\u0242"+
		"\u0240\3\2\2\2\u0242\u0243\3\2\2\2\u0243\u00be\3\2\2\2\u0244\u0245\4\62"+
		";\2\u0245\u00c0\3\2\2\2\u0246\u0248\5\u00bf`\2\u0247\u0246\3\2\2\2\u0248"+
		"\u0249\3\2\2\2\u0249\u0247\3\2\2\2\u0249\u024a\3\2\2\2\u024a\u00c2\3\2"+
		"\2\2\22\2\u00ca\u00cf\u00d6\u00d8\u00e0\u00e2\u00e7\u00ef\u00fa\u00fe"+
		"\u0101\u0103\u023d\u0242\u0249\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}