// Generated from ca/waterloo/dsg/graphflow/grammar/Graphflow.g4 by ANTLR 4.7
package ca.waterloo.dsg.graphflow.grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GraphflowParser extends Parser {
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
	public static final int
		RULE_graphflow = 0, RULE_statement = 1, RULE_query = 2, RULE_matchQuery = 3, 
		RULE_continuousMatchQuery = 4, RULE_explainMatchQuery = 5, RULE_explainContinuousMatchQuery = 6, 
		RULE_createQuery = 7, RULE_deleteQuery = 8, RULE_shortestPathQuery = 9, 
		RULE_continuousShortestPathQuery = 10, RULE_durabilityQuery = 11, RULE_matchPattern = 12, 
		RULE_deletePattern = 13, RULE_createEdgePattern = 14, RULE_createVertexPattern = 15, 
		RULE_pathPattern = 16, RULE_returnClause = 17, RULE_aggregationPattern = 18, 
		RULE_aggregationFunction = 19, RULE_countStarPattern = 20, RULE_whereClause = 21, 
		RULE_weightsClause = 22, RULE_predicates = 23, RULE_predicate = 24, RULE_operand = 25, 
		RULE_variableEdge = 26, RULE_digitsEdgeWithOptionalType = 27, RULE_digitsEdgeWithTypeAndProperties = 28, 
		RULE_digitsVertex = 29, RULE_digitsVertexWithTypeAndProperties = 30, RULE_variableVertex = 31, 
		RULE_edgeType = 32, RULE_edgeTypeAndOptionalProperties = 33, RULE_edgeVariable = 34, 
		RULE_variableWithProperty = 35, RULE_weight = 36, RULE_type = 37, RULE_properties = 38, 
		RULE_property = 39, RULE_literal = 40, RULE_fileSink = 41, RULE_udfCall = 42, 
		RULE_operator = 43, RULE_key = 44, RULE_functionName = 45, RULE_variable = 46, 
		RULE_keyword = 47, RULE_whitespace = 48, RULE_numericLiteral = 49, RULE_integerLiteral = 50, 
		RULE_doubleLiteral = 51, RULE_booleanLiteral = 52, RULE_stringLiteral = 53;
	public static final String[] ruleNames = {
		"graphflow", "statement", "query", "matchQuery", "continuousMatchQuery", 
		"explainMatchQuery", "explainContinuousMatchQuery", "createQuery", "deleteQuery", 
		"shortestPathQuery", "continuousShortestPathQuery", "durabilityQuery", 
		"matchPattern", "deletePattern", "createEdgePattern", "createVertexPattern", 
		"pathPattern", "returnClause", "aggregationPattern", "aggregationFunction", 
		"countStarPattern", "whereClause", "weightsClause", "predicates", "predicate", 
		"operand", "variableEdge", "digitsEdgeWithOptionalType", "digitsEdgeWithTypeAndProperties", 
		"digitsVertex", "digitsVertexWithTypeAndProperties", "variableVertex", 
		"edgeType", "edgeTypeAndOptionalProperties", "edgeVariable", "variableWithProperty", 
		"weight", "type", "properties", "property", "literal", "fileSink", "udfCall", 
		"operator", "key", "functionName", "variable", "keyword", "whitespace", 
		"numericLiteral", "integerLiteral", "doubleLiteral", "booleanLiteral", 
		"stringLiteral"
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

	@Override
	public String getGrammarFileName() { return "Graphflow.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GraphflowParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class GraphflowContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public TerminalNode EOF() { return getToken(GraphflowParser.EOF, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public TerminalNode SEMICOLON() { return getToken(GraphflowParser.SEMICOLON, 0); }
		public GraphflowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphflow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterGraphflow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitGraphflow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitGraphflow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphflowContext graphflow() throws RecognitionException {
		GraphflowContext _localctx = new GraphflowContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_graphflow);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(108);
				whitespace();
				}
			}

			setState(111);
			statement();
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(112);
				whitespace();
				}
			}

			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SEMICOLON) {
				{
				setState(115);
				match(SEMICOLON);
				setState(117);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(116);
					whitespace();
					}
				}

				}
			}

			setState(121);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public QueryContext query() {
			return getRuleContext(QueryContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			query();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QueryContext extends ParserRuleContext {
		public MatchQueryContext matchQuery() {
			return getRuleContext(MatchQueryContext.class,0);
		}
		public ContinuousMatchQueryContext continuousMatchQuery() {
			return getRuleContext(ContinuousMatchQueryContext.class,0);
		}
		public ExplainMatchQueryContext explainMatchQuery() {
			return getRuleContext(ExplainMatchQueryContext.class,0);
		}
		public ExplainContinuousMatchQueryContext explainContinuousMatchQuery() {
			return getRuleContext(ExplainContinuousMatchQueryContext.class,0);
		}
		public CreateQueryContext createQuery() {
			return getRuleContext(CreateQueryContext.class,0);
		}
		public DeleteQueryContext deleteQuery() {
			return getRuleContext(DeleteQueryContext.class,0);
		}
		public ShortestPathQueryContext shortestPathQuery() {
			return getRuleContext(ShortestPathQueryContext.class,0);
		}
		public ContinuousShortestPathQueryContext continuousShortestPathQuery() {
			return getRuleContext(ContinuousShortestPathQueryContext.class,0);
		}
		public DurabilityQueryContext durabilityQuery() {
			return getRuleContext(DurabilityQueryContext.class,0);
		}
		public QueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_query; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QueryContext query() throws RecognitionException {
		QueryContext _localctx = new QueryContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_query);
		try {
			setState(134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(125);
				matchQuery();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(126);
				continuousMatchQuery();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(127);
				explainMatchQuery();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(128);
				explainContinuousMatchQuery();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(129);
				createQuery();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(130);
				deleteQuery();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(131);
				shortestPathQuery();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(132);
				continuousShortestPathQuery();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(133);
				durabilityQuery();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MatchQueryContext extends ParserRuleContext {
		public TerminalNode MATCH() { return getToken(GraphflowParser.MATCH, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public MatchPatternContext matchPattern() {
			return getRuleContext(MatchPatternContext.class,0);
		}
		public WhereClauseContext whereClause() {
			return getRuleContext(WhereClauseContext.class,0);
		}
		public ReturnClauseContext returnClause() {
			return getRuleContext(ReturnClauseContext.class,0);
		}
		public MatchQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterMatchQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitMatchQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitMatchQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchQueryContext matchQuery() throws RecognitionException {
		MatchQueryContext _localctx = new MatchQueryContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_matchQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(MATCH);
			setState(137);
			whitespace();
			setState(138);
			matchPattern();
			setState(142);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(139);
				whitespace();
				setState(140);
				whereClause();
				}
				break;
			}
			setState(147);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(144);
				whitespace();
				setState(145);
				returnClause();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContinuousMatchQueryContext extends ParserRuleContext {
		public TerminalNode CONTINUOUSLY() { return getToken(GraphflowParser.CONTINUOUSLY, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public TerminalNode MATCH() { return getToken(GraphflowParser.MATCH, 0); }
		public MatchPatternContext matchPattern() {
			return getRuleContext(MatchPatternContext.class,0);
		}
		public FileSinkContext fileSink() {
			return getRuleContext(FileSinkContext.class,0);
		}
		public UdfCallContext udfCall() {
			return getRuleContext(UdfCallContext.class,0);
		}
		public WhereClauseContext whereClause() {
			return getRuleContext(WhereClauseContext.class,0);
		}
		public ContinuousMatchQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continuousMatchQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterContinuousMatchQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitContinuousMatchQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitContinuousMatchQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinuousMatchQueryContext continuousMatchQuery() throws RecognitionException {
		ContinuousMatchQueryContext _localctx = new ContinuousMatchQueryContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_continuousMatchQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			match(CONTINUOUSLY);
			setState(150);
			whitespace();
			setState(151);
			match(MATCH);
			setState(152);
			whitespace();
			setState(153);
			matchPattern();
			setState(157);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(154);
				whitespace();
				setState(155);
				whereClause();
				}
				break;
			}
			setState(159);
			whitespace();
			setState(162);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FILE:
				{
				setState(160);
				fileSink();
				}
				break;
			case ACTION:
				{
				setState(161);
				udfCall();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExplainMatchQueryContext extends ParserRuleContext {
		public TerminalNode EXPLAIN() { return getToken(GraphflowParser.EXPLAIN, 0); }
		public WhitespaceContext whitespace() {
			return getRuleContext(WhitespaceContext.class,0);
		}
		public MatchQueryContext matchQuery() {
			return getRuleContext(MatchQueryContext.class,0);
		}
		public ExplainMatchQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_explainMatchQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterExplainMatchQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitExplainMatchQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitExplainMatchQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExplainMatchQueryContext explainMatchQuery() throws RecognitionException {
		ExplainMatchQueryContext _localctx = new ExplainMatchQueryContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_explainMatchQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(EXPLAIN);
			setState(165);
			whitespace();
			setState(166);
			matchQuery();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExplainContinuousMatchQueryContext extends ParserRuleContext {
		public TerminalNode EXPLAIN() { return getToken(GraphflowParser.EXPLAIN, 0); }
		public WhitespaceContext whitespace() {
			return getRuleContext(WhitespaceContext.class,0);
		}
		public ContinuousMatchQueryContext continuousMatchQuery() {
			return getRuleContext(ContinuousMatchQueryContext.class,0);
		}
		public ExplainContinuousMatchQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_explainContinuousMatchQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterExplainContinuousMatchQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitExplainContinuousMatchQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitExplainContinuousMatchQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExplainContinuousMatchQueryContext explainContinuousMatchQuery() throws RecognitionException {
		ExplainContinuousMatchQueryContext _localctx = new ExplainContinuousMatchQueryContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_explainContinuousMatchQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(EXPLAIN);
			setState(169);
			whitespace();
			setState(170);
			continuousMatchQuery();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateQueryContext extends ParserRuleContext {
		public TerminalNode CREATE() { return getToken(GraphflowParser.CREATE, 0); }
		public WhitespaceContext whitespace() {
			return getRuleContext(WhitespaceContext.class,0);
		}
		public CreateEdgePatternContext createEdgePattern() {
			return getRuleContext(CreateEdgePatternContext.class,0);
		}
		public CreateVertexPatternContext createVertexPattern() {
			return getRuleContext(CreateVertexPatternContext.class,0);
		}
		public CreateQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterCreateQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitCreateQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitCreateQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateQueryContext createQuery() throws RecognitionException {
		CreateQueryContext _localctx = new CreateQueryContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_createQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			match(CREATE);
			setState(173);
			whitespace();
			setState(176);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(174);
				createEdgePattern();
				}
				break;
			case 2:
				{
				setState(175);
				createVertexPattern();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeleteQueryContext extends ParserRuleContext {
		public TerminalNode DELETE() { return getToken(GraphflowParser.DELETE, 0); }
		public WhitespaceContext whitespace() {
			return getRuleContext(WhitespaceContext.class,0);
		}
		public DeletePatternContext deletePattern() {
			return getRuleContext(DeletePatternContext.class,0);
		}
		public DeleteQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deleteQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterDeleteQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitDeleteQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitDeleteQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeleteQueryContext deleteQuery() throws RecognitionException {
		DeleteQueryContext _localctx = new DeleteQueryContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_deleteQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(DELETE);
			setState(179);
			whitespace();
			setState(180);
			deletePattern();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ShortestPathQueryContext extends ParserRuleContext {
		public TerminalNode SHORTEST() { return getToken(GraphflowParser.SHORTEST, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public TerminalNode PATH() { return getToken(GraphflowParser.PATH, 0); }
		public PathPatternContext pathPattern() {
			return getRuleContext(PathPatternContext.class,0);
		}
		public ShortestPathQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shortestPathQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterShortestPathQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitShortestPathQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitShortestPathQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ShortestPathQueryContext shortestPathQuery() throws RecognitionException {
		ShortestPathQueryContext _localctx = new ShortestPathQueryContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_shortestPathQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(SHORTEST);
			setState(183);
			whitespace();
			setState(184);
			match(PATH);
			setState(185);
			whitespace();
			setState(186);
			pathPattern();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ContinuousShortestPathQueryContext extends ParserRuleContext {
		public TerminalNode CONTINUOUSLY() { return getToken(GraphflowParser.CONTINUOUSLY, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public ShortestPathQueryContext shortestPathQuery() {
			return getRuleContext(ShortestPathQueryContext.class,0);
		}
		public FileSinkContext fileSink() {
			return getRuleContext(FileSinkContext.class,0);
		}
		public UdfCallContext udfCall() {
			return getRuleContext(UdfCallContext.class,0);
		}
		public WeightsClauseContext weightsClause() {
			return getRuleContext(WeightsClauseContext.class,0);
		}
		public ContinuousShortestPathQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continuousShortestPathQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterContinuousShortestPathQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitContinuousShortestPathQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitContinuousShortestPathQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContinuousShortestPathQueryContext continuousShortestPathQuery() throws RecognitionException {
		ContinuousShortestPathQueryContext _localctx = new ContinuousShortestPathQueryContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_continuousShortestPathQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			match(CONTINUOUSLY);
			setState(189);
			whitespace();
			setState(190);
			shortestPathQuery();
			setState(194);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(191);
				whitespace();
				setState(192);
				weightsClause();
				}
				break;
			}
			setState(196);
			whitespace();
			setState(199);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FILE:
				{
				setState(197);
				fileSink();
				}
				break;
			case ACTION:
				{
				setState(198);
				udfCall();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DurabilityQueryContext extends ParserRuleContext {
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public TerminalNode DIR() { return getToken(GraphflowParser.DIR, 0); }
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public TerminalNode LOAD() { return getToken(GraphflowParser.LOAD, 0); }
		public TerminalNode FROM() { return getToken(GraphflowParser.FROM, 0); }
		public TerminalNode SAVE() { return getToken(GraphflowParser.SAVE, 0); }
		public TerminalNode TO() { return getToken(GraphflowParser.TO, 0); }
		public DurabilityQueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_durabilityQuery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterDurabilityQuery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitDurabilityQuery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitDurabilityQuery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DurabilityQueryContext durabilityQuery() throws RecognitionException {
		DurabilityQueryContext _localctx = new DurabilityQueryContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_durabilityQuery);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(209);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOAD:
				{
				setState(201);
				match(LOAD);
				setState(202);
				whitespace();
				setState(203);
				match(FROM);
				}
				break;
			case SAVE:
				{
				setState(205);
				match(SAVE);
				setState(206);
				whitespace();
				setState(207);
				match(TO);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(211);
			whitespace();
			setState(212);
			match(DIR);
			setState(213);
			whitespace();
			setState(214);
			stringLiteral();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MatchPatternContext extends ParserRuleContext {
		public List<VariableEdgeContext> variableEdge() {
			return getRuleContexts(VariableEdgeContext.class);
		}
		public VariableEdgeContext variableEdge(int i) {
			return getRuleContext(VariableEdgeContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GraphflowParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GraphflowParser.COMMA, i);
		}
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public MatchPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_matchPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterMatchPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitMatchPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitMatchPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MatchPatternContext matchPattern() throws RecognitionException {
		MatchPatternContext _localctx = new MatchPatternContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_matchPattern);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			variableEdge();
			setState(227);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(218);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(217);
						whitespace();
						}
					}

					setState(220);
					match(COMMA);
					setState(222);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(221);
						whitespace();
						}
					}

					setState(224);
					variableEdge();
					}
					} 
				}
				setState(229);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DeletePatternContext extends ParserRuleContext {
		public List<DigitsEdgeWithOptionalTypeContext> digitsEdgeWithOptionalType() {
			return getRuleContexts(DigitsEdgeWithOptionalTypeContext.class);
		}
		public DigitsEdgeWithOptionalTypeContext digitsEdgeWithOptionalType(int i) {
			return getRuleContext(DigitsEdgeWithOptionalTypeContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GraphflowParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GraphflowParser.COMMA, i);
		}
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public DeletePatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deletePattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterDeletePattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitDeletePattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitDeletePattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeletePatternContext deletePattern() throws RecognitionException {
		DeletePatternContext _localctx = new DeletePatternContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_deletePattern);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			digitsEdgeWithOptionalType();
			setState(241);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(232);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(231);
						whitespace();
						}
					}

					setState(234);
					match(COMMA);
					setState(236);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(235);
						whitespace();
						}
					}

					setState(238);
					digitsEdgeWithOptionalType();
					}
					} 
				}
				setState(243);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateEdgePatternContext extends ParserRuleContext {
		public List<DigitsEdgeWithTypeAndPropertiesContext> digitsEdgeWithTypeAndProperties() {
			return getRuleContexts(DigitsEdgeWithTypeAndPropertiesContext.class);
		}
		public DigitsEdgeWithTypeAndPropertiesContext digitsEdgeWithTypeAndProperties(int i) {
			return getRuleContext(DigitsEdgeWithTypeAndPropertiesContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GraphflowParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GraphflowParser.COMMA, i);
		}
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public CreateEdgePatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createEdgePattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterCreateEdgePattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitCreateEdgePattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitCreateEdgePattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateEdgePatternContext createEdgePattern() throws RecognitionException {
		CreateEdgePatternContext _localctx = new CreateEdgePatternContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_createEdgePattern);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			digitsEdgeWithTypeAndProperties();
			setState(255);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(246);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(245);
						whitespace();
						}
					}

					setState(248);
					match(COMMA);
					setState(250);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(249);
						whitespace();
						}
					}

					setState(252);
					digitsEdgeWithTypeAndProperties();
					}
					} 
				}
				setState(257);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CreateVertexPatternContext extends ParserRuleContext {
		public List<DigitsVertexWithTypeAndPropertiesContext> digitsVertexWithTypeAndProperties() {
			return getRuleContexts(DigitsVertexWithTypeAndPropertiesContext.class);
		}
		public DigitsVertexWithTypeAndPropertiesContext digitsVertexWithTypeAndProperties(int i) {
			return getRuleContext(DigitsVertexWithTypeAndPropertiesContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GraphflowParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GraphflowParser.COMMA, i);
		}
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public CreateVertexPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_createVertexPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterCreateVertexPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitCreateVertexPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitCreateVertexPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CreateVertexPatternContext createVertexPattern() throws RecognitionException {
		CreateVertexPatternContext _localctx = new CreateVertexPatternContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_createVertexPattern);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			digitsVertexWithTypeAndProperties();
			setState(269);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(260);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(259);
						whitespace();
						}
					}

					setState(262);
					match(COMMA);
					setState(264);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(263);
						whitespace();
						}
					}

					setState(266);
					digitsVertexWithTypeAndProperties();
					}
					} 
				}
				setState(271);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PathPatternContext extends ParserRuleContext {
		public TerminalNode OPEN_ROUND_BRACKET() { return getToken(GraphflowParser.OPEN_ROUND_BRACKET, 0); }
		public List<TerminalNode> Digits() { return getTokens(GraphflowParser.Digits); }
		public TerminalNode Digits(int i) {
			return getToken(GraphflowParser.Digits, i);
		}
		public TerminalNode COMMA() { return getToken(GraphflowParser.COMMA, 0); }
		public TerminalNode CLOSE_ROUND_BRACKET() { return getToken(GraphflowParser.CLOSE_ROUND_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public PathPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pathPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterPathPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitPathPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitPathPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PathPatternContext pathPattern() throws RecognitionException {
		PathPatternContext _localctx = new PathPatternContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_pathPattern);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(272);
			match(OPEN_ROUND_BRACKET);
			setState(274);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(273);
				whitespace();
				}
			}

			setState(276);
			match(Digits);
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(277);
				whitespace();
				}
			}

			setState(280);
			match(COMMA);
			setState(282);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(281);
				whitespace();
				}
			}

			setState(284);
			match(Digits);
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(285);
				whitespace();
				}
			}

			setState(288);
			match(CLOSE_ROUND_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ReturnClauseContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(GraphflowParser.RETURN, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public List<VariableContext> variable() {
			return getRuleContexts(VariableContext.class);
		}
		public VariableContext variable(int i) {
			return getRuleContext(VariableContext.class,i);
		}
		public List<VariableWithPropertyContext> variableWithProperty() {
			return getRuleContexts(VariableWithPropertyContext.class);
		}
		public VariableWithPropertyContext variableWithProperty(int i) {
			return getRuleContext(VariableWithPropertyContext.class,i);
		}
		public List<AggregationPatternContext> aggregationPattern() {
			return getRuleContexts(AggregationPatternContext.class);
		}
		public AggregationPatternContext aggregationPattern(int i) {
			return getRuleContext(AggregationPatternContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GraphflowParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GraphflowParser.COMMA, i);
		}
		public ReturnClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterReturnClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitReturnClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitReturnClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnClauseContext returnClause() throws RecognitionException {
		ReturnClauseContext _localctx = new ReturnClauseContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_returnClause);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(290);
			match(RETURN);
			setState(291);
			whitespace();
			setState(295);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,29,_ctx) ) {
			case 1:
				{
				setState(292);
				variable();
				}
				break;
			case 2:
				{
				setState(293);
				variableWithProperty();
				}
				break;
			case 3:
				{
				setState(294);
				aggregationPattern();
				}
				break;
			}
			setState(311);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(298);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(297);
						whitespace();
						}
					}

					setState(300);
					match(COMMA);
					setState(302);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(301);
						whitespace();
						}
					}

					setState(307);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
					case 1:
						{
						setState(304);
						variable();
						}
						break;
					case 2:
						{
						setState(305);
						variableWithProperty();
						}
						break;
					case 3:
						{
						setState(306);
						aggregationPattern();
						}
						break;
					}
					}
					} 
				}
				setState(313);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AggregationPatternContext extends ParserRuleContext {
		public AggregationFunctionContext aggregationFunction() {
			return getRuleContext(AggregationFunctionContext.class,0);
		}
		public TerminalNode OPEN_ROUND_BRACKET() { return getToken(GraphflowParser.OPEN_ROUND_BRACKET, 0); }
		public TerminalNode CLOSE_ROUND_BRACKET() { return getToken(GraphflowParser.CLOSE_ROUND_BRACKET, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public VariableWithPropertyContext variableWithProperty() {
			return getRuleContext(VariableWithPropertyContext.class,0);
		}
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public CountStarPatternContext countStarPattern() {
			return getRuleContext(CountStarPatternContext.class,0);
		}
		public AggregationPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregationPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterAggregationPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitAggregationPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitAggregationPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregationPatternContext aggregationPattern() throws RecognitionException {
		AggregationPatternContext _localctx = new AggregationPatternContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_aggregationPattern);
		int _la;
		try {
			setState(329);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AVG:
			case MAX:
			case MIN:
			case SUM:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(314);
				aggregationFunction();
				setState(315);
				match(OPEN_ROUND_BRACKET);
				setState(317);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(316);
					whitespace();
					}
				}

				setState(321);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
				case 1:
					{
					setState(319);
					variable();
					}
					break;
				case 2:
					{
					setState(320);
					variableWithProperty();
					}
					break;
				}
				setState(324);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(323);
					whitespace();
					}
				}

				setState(326);
				match(CLOSE_ROUND_BRACKET);
				}
				}
				break;
			case COUNT:
				enterOuterAlt(_localctx, 2);
				{
				setState(328);
				countStarPattern();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AggregationFunctionContext extends ParserRuleContext {
		public TerminalNode AVG() { return getToken(GraphflowParser.AVG, 0); }
		public TerminalNode MAX() { return getToken(GraphflowParser.MAX, 0); }
		public TerminalNode MIN() { return getToken(GraphflowParser.MIN, 0); }
		public TerminalNode SUM() { return getToken(GraphflowParser.SUM, 0); }
		public AggregationFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregationFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterAggregationFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitAggregationFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitAggregationFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregationFunctionContext aggregationFunction() throws RecognitionException {
		AggregationFunctionContext _localctx = new AggregationFunctionContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_aggregationFunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AVG) | (1L << MAX) | (1L << MIN) | (1L << SUM))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CountStarPatternContext extends ParserRuleContext {
		public TerminalNode COUNT() { return getToken(GraphflowParser.COUNT, 0); }
		public TerminalNode OPEN_ROUND_BRACKET() { return getToken(GraphflowParser.OPEN_ROUND_BRACKET, 0); }
		public TerminalNode STAR() { return getToken(GraphflowParser.STAR, 0); }
		public TerminalNode CLOSE_ROUND_BRACKET() { return getToken(GraphflowParser.CLOSE_ROUND_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public CountStarPatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_countStarPattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterCountStarPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitCountStarPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitCountStarPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CountStarPatternContext countStarPattern() throws RecognitionException {
		CountStarPatternContext _localctx = new CountStarPatternContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_countStarPattern);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(333);
			match(COUNT);
			setState(334);
			match(OPEN_ROUND_BRACKET);
			setState(336);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(335);
				whitespace();
				}
			}

			setState(338);
			match(STAR);
			setState(340);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(339);
				whitespace();
				}
			}

			setState(342);
			match(CLOSE_ROUND_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhereClauseContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(GraphflowParser.WHERE, 0); }
		public WhitespaceContext whitespace() {
			return getRuleContext(WhitespaceContext.class,0);
		}
		public PredicatesContext predicates() {
			return getRuleContext(PredicatesContext.class,0);
		}
		public WhereClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterWhereClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitWhereClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitWhereClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereClauseContext whereClause() throws RecognitionException {
		WhereClauseContext _localctx = new WhereClauseContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_whereClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			match(WHERE);
			setState(345);
			whitespace();
			setState(346);
			predicates();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WeightsClauseContext extends ParserRuleContext {
		public TerminalNode WEIGHTS() { return getToken(GraphflowParser.WEIGHTS, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public TerminalNode ON() { return getToken(GraphflowParser.ON, 0); }
		public WeightContext weight() {
			return getRuleContext(WeightContext.class,0);
		}
		public WeightsClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weightsClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterWeightsClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitWeightsClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitWeightsClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightsClauseContext weightsClause() throws RecognitionException {
		WeightsClauseContext _localctx = new WeightsClauseContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_weightsClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(348);
			match(WEIGHTS);
			setState(349);
			whitespace();
			setState(350);
			match(ON);
			setState(351);
			whitespace();
			setState(352);
			weight();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicatesContext extends ParserRuleContext {
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(GraphflowParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(GraphflowParser.AND, i);
		}
		public PredicatesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicates; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterPredicates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitPredicates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitPredicates(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicatesContext predicates() throws RecognitionException {
		PredicatesContext _localctx = new PredicatesContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_predicates);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(354);
			predicate();
			setState(362);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(355);
					whitespace();
					setState(356);
					match(AND);
					setState(357);
					whitespace();
					setState(358);
					predicate();
					}
					} 
				}
				setState(364);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,40,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PredicateContext extends ParserRuleContext {
		public List<OperandContext> operand() {
			return getRuleContexts(OperandContext.class);
		}
		public OperandContext operand(int i) {
			return getRuleContext(OperandContext.class,i);
		}
		public OperatorContext operator() {
			return getRuleContext(OperatorContext.class,0);
		}
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitPredicate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitPredicate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_predicate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			operand();
			setState(367);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(366);
				whitespace();
				}
			}

			setState(369);
			operator();
			setState(371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(370);
				whitespace();
				}
			}

			setState(373);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperandContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public VariableWithPropertyContext variableWithProperty() {
			return getRuleContext(VariableWithPropertyContext.class,0);
		}
		public OperandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterOperand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitOperand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitOperand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperandContext operand() throws RecognitionException {
		OperandContext _localctx = new OperandContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_operand);
		try {
			setState(377);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(375);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(376);
				variableWithProperty();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableEdgeContext extends ParserRuleContext {
		public List<VariableVertexContext> variableVertex() {
			return getRuleContexts(VariableVertexContext.class);
		}
		public VariableVertexContext variableVertex(int i) {
			return getRuleContext(VariableVertexContext.class,i);
		}
		public List<TerminalNode> DASH() { return getTokens(GraphflowParser.DASH); }
		public TerminalNode DASH(int i) {
			return getToken(GraphflowParser.DASH, i);
		}
		public TerminalNode GREATER_THAN() { return getToken(GraphflowParser.GREATER_THAN, 0); }
		public EdgeVariableContext edgeVariable() {
			return getRuleContext(EdgeVariableContext.class,0);
		}
		public VariableEdgeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableEdge; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterVariableEdge(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitVariableEdge(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitVariableEdge(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableEdgeContext variableEdge() throws RecognitionException {
		VariableEdgeContext _localctx = new VariableEdgeContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_variableEdge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			variableVertex();
			setState(382);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(380);
				match(DASH);
				setState(381);
				edgeVariable();
				}
				break;
			}
			setState(384);
			match(DASH);
			setState(385);
			match(GREATER_THAN);
			setState(386);
			variableVertex();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DigitsEdgeWithOptionalTypeContext extends ParserRuleContext {
		public List<DigitsVertexContext> digitsVertex() {
			return getRuleContexts(DigitsVertexContext.class);
		}
		public DigitsVertexContext digitsVertex(int i) {
			return getRuleContext(DigitsVertexContext.class,i);
		}
		public List<TerminalNode> DASH() { return getTokens(GraphflowParser.DASH); }
		public TerminalNode DASH(int i) {
			return getToken(GraphflowParser.DASH, i);
		}
		public TerminalNode GREATER_THAN() { return getToken(GraphflowParser.GREATER_THAN, 0); }
		public EdgeTypeContext edgeType() {
			return getRuleContext(EdgeTypeContext.class,0);
		}
		public DigitsEdgeWithOptionalTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digitsEdgeWithOptionalType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterDigitsEdgeWithOptionalType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitDigitsEdgeWithOptionalType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitDigitsEdgeWithOptionalType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DigitsEdgeWithOptionalTypeContext digitsEdgeWithOptionalType() throws RecognitionException {
		DigitsEdgeWithOptionalTypeContext _localctx = new DigitsEdgeWithOptionalTypeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_digitsEdgeWithOptionalType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(388);
			digitsVertex();
			setState(391);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
			case 1:
				{
				setState(389);
				match(DASH);
				setState(390);
				edgeType();
				}
				break;
			}
			setState(393);
			match(DASH);
			setState(394);
			match(GREATER_THAN);
			setState(395);
			digitsVertex();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DigitsEdgeWithTypeAndPropertiesContext extends ParserRuleContext {
		public List<DigitsVertexWithTypeAndPropertiesContext> digitsVertexWithTypeAndProperties() {
			return getRuleContexts(DigitsVertexWithTypeAndPropertiesContext.class);
		}
		public DigitsVertexWithTypeAndPropertiesContext digitsVertexWithTypeAndProperties(int i) {
			return getRuleContext(DigitsVertexWithTypeAndPropertiesContext.class,i);
		}
		public List<TerminalNode> DASH() { return getTokens(GraphflowParser.DASH); }
		public TerminalNode DASH(int i) {
			return getToken(GraphflowParser.DASH, i);
		}
		public TerminalNode GREATER_THAN() { return getToken(GraphflowParser.GREATER_THAN, 0); }
		public EdgeTypeAndOptionalPropertiesContext edgeTypeAndOptionalProperties() {
			return getRuleContext(EdgeTypeAndOptionalPropertiesContext.class,0);
		}
		public DigitsEdgeWithTypeAndPropertiesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digitsEdgeWithTypeAndProperties; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterDigitsEdgeWithTypeAndProperties(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitDigitsEdgeWithTypeAndProperties(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitDigitsEdgeWithTypeAndProperties(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DigitsEdgeWithTypeAndPropertiesContext digitsEdgeWithTypeAndProperties() throws RecognitionException {
		DigitsEdgeWithTypeAndPropertiesContext _localctx = new DigitsEdgeWithTypeAndPropertiesContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_digitsEdgeWithTypeAndProperties);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(397);
			digitsVertexWithTypeAndProperties();
			{
			setState(398);
			match(DASH);
			setState(399);
			edgeTypeAndOptionalProperties();
			}
			setState(401);
			match(DASH);
			setState(402);
			match(GREATER_THAN);
			setState(403);
			digitsVertexWithTypeAndProperties();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DigitsVertexContext extends ParserRuleContext {
		public TerminalNode OPEN_ROUND_BRACKET() { return getToken(GraphflowParser.OPEN_ROUND_BRACKET, 0); }
		public TerminalNode Digits() { return getToken(GraphflowParser.Digits, 0); }
		public TerminalNode CLOSE_ROUND_BRACKET() { return getToken(GraphflowParser.CLOSE_ROUND_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public DigitsVertexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digitsVertex; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterDigitsVertex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitDigitsVertex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitDigitsVertex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DigitsVertexContext digitsVertex() throws RecognitionException {
		DigitsVertexContext _localctx = new DigitsVertexContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_digitsVertex);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405);
			match(OPEN_ROUND_BRACKET);
			setState(407);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(406);
				whitespace();
				}
			}

			setState(409);
			match(Digits);
			setState(411);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(410);
				whitespace();
				}
			}

			setState(413);
			match(CLOSE_ROUND_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DigitsVertexWithTypeAndPropertiesContext extends ParserRuleContext {
		public TerminalNode OPEN_ROUND_BRACKET() { return getToken(GraphflowParser.OPEN_ROUND_BRACKET, 0); }
		public TerminalNode Digits() { return getToken(GraphflowParser.Digits, 0); }
		public TerminalNode COLON() { return getToken(GraphflowParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CLOSE_ROUND_BRACKET() { return getToken(GraphflowParser.CLOSE_ROUND_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public PropertiesContext properties() {
			return getRuleContext(PropertiesContext.class,0);
		}
		public DigitsVertexWithTypeAndPropertiesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_digitsVertexWithTypeAndProperties; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterDigitsVertexWithTypeAndProperties(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitDigitsVertexWithTypeAndProperties(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitDigitsVertexWithTypeAndProperties(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DigitsVertexWithTypeAndPropertiesContext digitsVertexWithTypeAndProperties() throws RecognitionException {
		DigitsVertexWithTypeAndPropertiesContext _localctx = new DigitsVertexWithTypeAndPropertiesContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_digitsVertexWithTypeAndProperties);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
			match(OPEN_ROUND_BRACKET);
			setState(417);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(416);
				whitespace();
				}
			}

			setState(419);
			match(Digits);
			setState(421);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(420);
				whitespace();
				}
			}

			setState(423);
			match(COLON);
			setState(424);
			type();
			setState(426);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				{
				setState(425);
				whitespace();
				}
				break;
			}
			setState(429);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_CURLY_BRACKET) {
				{
				setState(428);
				properties();
				}
			}

			setState(432);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(431);
				whitespace();
				}
			}

			setState(434);
			match(CLOSE_ROUND_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableVertexContext extends ParserRuleContext {
		public TerminalNode OPEN_ROUND_BRACKET() { return getToken(GraphflowParser.OPEN_ROUND_BRACKET, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TerminalNode CLOSE_ROUND_BRACKET() { return getToken(GraphflowParser.CLOSE_ROUND_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public TerminalNode COLON() { return getToken(GraphflowParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public PropertiesContext properties() {
			return getRuleContext(PropertiesContext.class,0);
		}
		public VariableVertexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableVertex; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterVariableVertex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitVariableVertex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitVariableVertex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableVertexContext variableVertex() throws RecognitionException {
		VariableVertexContext _localctx = new VariableVertexContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_variableVertex);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(436);
			match(OPEN_ROUND_BRACKET);
			setState(438);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(437);
				whitespace();
				}
			}

			setState(440);
			variable();
			setState(446);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				{
				setState(442);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(441);
					whitespace();
					}
				}

				setState(444);
				match(COLON);
				setState(445);
				type();
				}
				break;
			}
			setState(452);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(449);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(448);
					whitespace();
					}
				}

				setState(451);
				properties();
				}
				break;
			}
			setState(455);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(454);
				whitespace();
				}
			}

			setState(457);
			match(CLOSE_ROUND_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EdgeTypeContext extends ParserRuleContext {
		public TerminalNode OPEN_SQUARE_BRACKET() { return getToken(GraphflowParser.OPEN_SQUARE_BRACKET, 0); }
		public TerminalNode COLON() { return getToken(GraphflowParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CLOSE_SQUARE_BRACKET() { return getToken(GraphflowParser.CLOSE_SQUARE_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public EdgeTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_edgeType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterEdgeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitEdgeType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitEdgeType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EdgeTypeContext edgeType() throws RecognitionException {
		EdgeTypeContext _localctx = new EdgeTypeContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_edgeType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(459);
			match(OPEN_SQUARE_BRACKET);
			setState(461);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(460);
				whitespace();
				}
			}

			setState(463);
			match(COLON);
			setState(464);
			type();
			setState(466);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(465);
				whitespace();
				}
			}

			setState(468);
			match(CLOSE_SQUARE_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EdgeTypeAndOptionalPropertiesContext extends ParserRuleContext {
		public TerminalNode OPEN_SQUARE_BRACKET() { return getToken(GraphflowParser.OPEN_SQUARE_BRACKET, 0); }
		public List<TerminalNode> COLON() { return getTokens(GraphflowParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(GraphflowParser.COLON, i);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode CLOSE_SQUARE_BRACKET() { return getToken(GraphflowParser.CLOSE_SQUARE_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public PropertiesContext properties() {
			return getRuleContext(PropertiesContext.class,0);
		}
		public TerminalNode WGHT() { return getToken(GraphflowParser.WGHT, 0); }
		public DoubleLiteralContext doubleLiteral() {
			return getRuleContext(DoubleLiteralContext.class,0);
		}
		public EdgeTypeAndOptionalPropertiesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_edgeTypeAndOptionalProperties; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterEdgeTypeAndOptionalProperties(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitEdgeTypeAndOptionalProperties(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitEdgeTypeAndOptionalProperties(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EdgeTypeAndOptionalPropertiesContext edgeTypeAndOptionalProperties() throws RecognitionException {
		EdgeTypeAndOptionalPropertiesContext _localctx = new EdgeTypeAndOptionalPropertiesContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_edgeTypeAndOptionalProperties);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(470);
			match(OPEN_SQUARE_BRACKET);
			setState(472);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(471);
				whitespace();
				}
			}

			setState(474);
			match(COLON);
			setState(475);
			type();
			setState(477);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,62,_ctx) ) {
			case 1:
				{
				setState(476);
				whitespace();
				}
				break;
			}
			setState(480);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==OPEN_CURLY_BRACKET) {
				{
				setState(479);
				properties();
				}
			}

			setState(489);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(482);
				whitespace();
				setState(483);
				match(WGHT);
				setState(484);
				match(COLON);
				setState(485);
				doubleLiteral();
				setState(487);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(486);
					whitespace();
					}
				}

				}
			}

			setState(491);
			match(CLOSE_SQUARE_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EdgeVariableContext extends ParserRuleContext {
		public TerminalNode OPEN_SQUARE_BRACKET() { return getToken(GraphflowParser.OPEN_SQUARE_BRACKET, 0); }
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public PropertiesContext properties() {
			return getRuleContext(PropertiesContext.class,0);
		}
		public TerminalNode CLOSE_SQUARE_BRACKET() { return getToken(GraphflowParser.CLOSE_SQUARE_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public TerminalNode COLON() { return getToken(GraphflowParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public EdgeVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_edgeVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterEdgeVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitEdgeVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitEdgeVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EdgeVariableContext edgeVariable() throws RecognitionException {
		EdgeVariableContext _localctx = new EdgeVariableContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_edgeVariable);
		int _la;
		try {
			setState(532);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(493);
				match(OPEN_SQUARE_BRACKET);
				setState(495);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(494);
					whitespace();
					}
				}

				setState(497);
				variable();
				setState(503);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
				case 1:
					{
					setState(499);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(498);
						whitespace();
						}
					}

					setState(501);
					match(COLON);
					setState(502);
					type();
					}
					break;
				}
				setState(506);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(505);
					whitespace();
					}
				}

				setState(508);
				properties();
				setState(510);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(509);
					whitespace();
					}
				}

				setState(512);
				match(CLOSE_SQUARE_BRACKET);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(514);
				match(OPEN_SQUARE_BRACKET);
				setState(516);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
				case 1:
					{
					setState(515);
					whitespace();
					}
					break;
				}
				setState(519);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 4)) & ~0x3f) == 0 && ((1L << (_la - 4)) & ((1L << (MATCH - 4)) | (1L << (CONTINUOUSLY - 4)) | (1L << (EXPLAIN - 4)) | (1L << (CREATE - 4)) | (1L << (DELETE - 4)) | (1L << (SHORTEST - 4)) | (1L << (PATH - 4)) | (1L << (WEIGHTS - 4)) | (1L << (WGHT - 4)) | (1L << (WHERE - 4)) | (1L << (RETURN - 4)) | (1L << (COUNT - 4)) | (1L << (AVG - 4)) | (1L << (MAX - 4)) | (1L << (MIN - 4)) | (1L << (SUM - 4)) | (1L << (ACTION - 4)) | (1L << (IN - 4)) | (1L << (UDF - 4)) | (1L << (JAR - 4)) | (1L << (LOAD - 4)) | (1L << (SAVE - 4)) | (1L << (FROM - 4)) | (1L << (TO - 4)) | (1L << (DIR - 4)) | (1L << (TRUE - 4)) | (1L << (FALSE - 4)) | (1L << (AND - 4)) | (1L << (OR - 4)) | (1L << (ON - 4)) | (1L << (FILE - 4)) | (1L << (DASH - 4)) | (1L << (UNDERSCORE - 4)) | (1L << (Characters - 4)))) != 0)) {
					{
					setState(518);
					variable();
					}
				}

				setState(526);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
				case 1:
					{
					setState(522);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
						{
						setState(521);
						whitespace();
						}
					}

					setState(524);
					match(COLON);
					setState(525);
					type();
					}
					break;
				}
				setState(529);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(528);
					whitespace();
					}
				}

				setState(531);
				match(CLOSE_SQUARE_BRACKET);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableWithPropertyContext extends ParserRuleContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TerminalNode DOT() { return getToken(GraphflowParser.DOT, 0); }
		public KeyContext key() {
			return getRuleContext(KeyContext.class,0);
		}
		public VariableWithPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableWithProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterVariableWithProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitVariableWithProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitVariableWithProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableWithPropertyContext variableWithProperty() throws RecognitionException {
		VariableWithPropertyContext _localctx = new VariableWithPropertyContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_variableWithProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(534);
			variable();
			setState(535);
			match(DOT);
			setState(536);
			key();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WeightContext extends ParserRuleContext {
		public KeyContext key() {
			return getRuleContext(KeyContext.class,0);
		}
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_weight);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(538);
			key();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(540);
			variable();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropertiesContext extends ParserRuleContext {
		public TerminalNode OPEN_CURLY_BRACKET() { return getToken(GraphflowParser.OPEN_CURLY_BRACKET, 0); }
		public TerminalNode CLOSE_CURLY_BRACKET() { return getToken(GraphflowParser.CLOSE_CURLY_BRACKET, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public List<PropertyContext> property() {
			return getRuleContexts(PropertyContext.class);
		}
		public PropertyContext property(int i) {
			return getRuleContext(PropertyContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GraphflowParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GraphflowParser.COMMA, i);
		}
		public PropertiesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_properties; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterProperties(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitProperties(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitProperties(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PropertiesContext properties() throws RecognitionException {
		PropertiesContext _localctx = new PropertiesContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_properties);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			match(OPEN_CURLY_BRACKET);
			setState(544);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,77,_ctx) ) {
			case 1:
				{
				setState(543);
				whitespace();
				}
				break;
			}
			setState(560);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 4)) & ~0x3f) == 0 && ((1L << (_la - 4)) & ((1L << (MATCH - 4)) | (1L << (CONTINUOUSLY - 4)) | (1L << (EXPLAIN - 4)) | (1L << (CREATE - 4)) | (1L << (DELETE - 4)) | (1L << (SHORTEST - 4)) | (1L << (PATH - 4)) | (1L << (WEIGHTS - 4)) | (1L << (WGHT - 4)) | (1L << (WHERE - 4)) | (1L << (RETURN - 4)) | (1L << (COUNT - 4)) | (1L << (AVG - 4)) | (1L << (MAX - 4)) | (1L << (MIN - 4)) | (1L << (SUM - 4)) | (1L << (ACTION - 4)) | (1L << (IN - 4)) | (1L << (UDF - 4)) | (1L << (JAR - 4)) | (1L << (LOAD - 4)) | (1L << (SAVE - 4)) | (1L << (FROM - 4)) | (1L << (TO - 4)) | (1L << (DIR - 4)) | (1L << (TRUE - 4)) | (1L << (FALSE - 4)) | (1L << (AND - 4)) | (1L << (OR - 4)) | (1L << (ON - 4)) | (1L << (FILE - 4)) | (1L << (UNDERSCORE - 4)) | (1L << (Characters - 4)))) != 0)) {
				{
				setState(546);
				property();
				setState(557);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(548);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
							{
							setState(547);
							whitespace();
							}
						}

						setState(550);
						match(COMMA);
						setState(552);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
							{
							setState(551);
							whitespace();
							}
						}

						setState(554);
						property();
						}
						} 
					}
					setState(559);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,80,_ctx);
				}
				}
			}

			setState(563);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(562);
				whitespace();
				}
			}

			setState(565);
			match(CLOSE_CURLY_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropertyContext extends ParserRuleContext {
		public KeyContext key() {
			return getRuleContext(KeyContext.class,0);
		}
		public TerminalNode COLON() { return getToken(GraphflowParser.COLON, 0); }
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public PropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_property; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PropertyContext property() throws RecognitionException {
		PropertyContext _localctx = new PropertyContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_property);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(567);
			key();
			setState(569);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(568);
				whitespace();
				}
			}

			setState(571);
			match(COLON);
			setState(573);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
				{
				setState(572);
				whitespace();
				}
			}

			setState(575);
			literal();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public NumericLiteralContext numericLiteral() {
			return getRuleContext(NumericLiteralContext.class,0);
		}
		public BooleanLiteralContext booleanLiteral() {
			return getRuleContext(BooleanLiteralContext.class,0);
		}
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_literal);
		try {
			setState(580);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DASH:
			case Digits:
				enterOuterAlt(_localctx, 1);
				{
				setState(577);
				numericLiteral();
				}
				break;
			case TRUE:
			case FALSE:
				enterOuterAlt(_localctx, 2);
				{
				setState(578);
				booleanLiteral();
				}
				break;
			case QuotedString:
				enterOuterAlt(_localctx, 3);
				{
				setState(579);
				stringLiteral();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FileSinkContext extends ParserRuleContext {
		public TerminalNode FILE() { return getToken(GraphflowParser.FILE, 0); }
		public WhitespaceContext whitespace() {
			return getRuleContext(WhitespaceContext.class,0);
		}
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public FileSinkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fileSink; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterFileSink(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitFileSink(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitFileSink(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileSinkContext fileSink() throws RecognitionException {
		FileSinkContext _localctx = new FileSinkContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_fileSink);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(582);
			match(FILE);
			setState(583);
			whitespace();
			setState(584);
			stringLiteral();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UdfCallContext extends ParserRuleContext {
		public TerminalNode ACTION() { return getToken(GraphflowParser.ACTION, 0); }
		public List<WhitespaceContext> whitespace() {
			return getRuleContexts(WhitespaceContext.class);
		}
		public WhitespaceContext whitespace(int i) {
			return getRuleContext(WhitespaceContext.class,i);
		}
		public TerminalNode UDF() { return getToken(GraphflowParser.UDF, 0); }
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public TerminalNode IN() { return getToken(GraphflowParser.IN, 0); }
		public StringLiteralContext stringLiteral() {
			return getRuleContext(StringLiteralContext.class,0);
		}
		public UdfCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_udfCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterUdfCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitUdfCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitUdfCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UdfCallContext udfCall() throws RecognitionException {
		UdfCallContext _localctx = new UdfCallContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_udfCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(586);
			match(ACTION);
			setState(587);
			whitespace();
			setState(588);
			match(UDF);
			setState(589);
			whitespace();
			setState(590);
			functionName();
			setState(591);
			whitespace();
			setState(592);
			match(IN);
			setState(593);
			whitespace();
			setState(594);
			stringLiteral();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperatorContext extends ParserRuleContext {
		public TerminalNode EQUAL_TO() { return getToken(GraphflowParser.EQUAL_TO, 0); }
		public TerminalNode NOT_EQUAL_TO() { return getToken(GraphflowParser.NOT_EQUAL_TO, 0); }
		public TerminalNode LESS_THAN() { return getToken(GraphflowParser.LESS_THAN, 0); }
		public TerminalNode LESS_THAN_OR_EQUAL() { return getToken(GraphflowParser.LESS_THAN_OR_EQUAL, 0); }
		public TerminalNode GREATER_THAN() { return getToken(GraphflowParser.GREATER_THAN, 0); }
		public TerminalNode GREATER_THAN_OR_EQUAL() { return getToken(GraphflowParser.GREATER_THAN_OR_EQUAL, 0); }
		public OperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperatorContext operator() throws RecognitionException {
		OperatorContext _localctx = new OperatorContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(596);
			_la = _input.LA(1);
			if ( !(((((_la - 60)) & ~0x3f) == 0 && ((1L << (_la - 60)) & ((1L << (EQUAL_TO - 60)) | (1L << (NOT_EQUAL_TO - 60)) | (1L << (LESS_THAN - 60)) | (1L << (GREATER_THAN - 60)) | (1L << (LESS_THAN_OR_EQUAL - 60)) | (1L << (GREATER_THAN_OR_EQUAL - 60)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeyContext extends ParserRuleContext {
		public List<TerminalNode> Characters() { return getTokens(GraphflowParser.Characters); }
		public TerminalNode Characters(int i) {
			return getToken(GraphflowParser.Characters, i);
		}
		public List<TerminalNode> UNDERSCORE() { return getTokens(GraphflowParser.UNDERSCORE); }
		public TerminalNode UNDERSCORE(int i) {
			return getToken(GraphflowParser.UNDERSCORE, i);
		}
		public List<KeywordContext> keyword() {
			return getRuleContexts(KeywordContext.class);
		}
		public KeywordContext keyword(int i) {
			return getRuleContext(KeywordContext.class,i);
		}
		public List<TerminalNode> Digits() { return getTokens(GraphflowParser.Digits); }
		public TerminalNode Digits(int i) {
			return getToken(GraphflowParser.Digits, i);
		}
		public KeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_key; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitKey(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitKey(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_key);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(601);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Characters:
				{
				setState(598);
				match(Characters);
				}
				break;
			case UNDERSCORE:
				{
				setState(599);
				match(UNDERSCORE);
				}
				break;
			case MATCH:
			case CONTINUOUSLY:
			case EXPLAIN:
			case CREATE:
			case DELETE:
			case SHORTEST:
			case PATH:
			case WEIGHTS:
			case WGHT:
			case WHERE:
			case RETURN:
			case COUNT:
			case AVG:
			case MAX:
			case MIN:
			case SUM:
			case ACTION:
			case IN:
			case UDF:
			case JAR:
			case LOAD:
			case SAVE:
			case FROM:
			case TO:
			case DIR:
			case TRUE:
			case FALSE:
			case AND:
			case OR:
			case ON:
			case FILE:
				{
				setState(600);
				keyword();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(609);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 4)) & ~0x3f) == 0 && ((1L << (_la - 4)) & ((1L << (MATCH - 4)) | (1L << (CONTINUOUSLY - 4)) | (1L << (EXPLAIN - 4)) | (1L << (CREATE - 4)) | (1L << (DELETE - 4)) | (1L << (SHORTEST - 4)) | (1L << (PATH - 4)) | (1L << (WEIGHTS - 4)) | (1L << (WGHT - 4)) | (1L << (WHERE - 4)) | (1L << (RETURN - 4)) | (1L << (COUNT - 4)) | (1L << (AVG - 4)) | (1L << (MAX - 4)) | (1L << (MIN - 4)) | (1L << (SUM - 4)) | (1L << (ACTION - 4)) | (1L << (IN - 4)) | (1L << (UDF - 4)) | (1L << (JAR - 4)) | (1L << (LOAD - 4)) | (1L << (SAVE - 4)) | (1L << (FROM - 4)) | (1L << (TO - 4)) | (1L << (DIR - 4)) | (1L << (TRUE - 4)) | (1L << (FALSE - 4)) | (1L << (AND - 4)) | (1L << (OR - 4)) | (1L << (ON - 4)) | (1L << (FILE - 4)) | (1L << (UNDERSCORE - 4)) | (1L << (Characters - 4)) | (1L << (Digits - 4)))) != 0)) {
				{
				setState(607);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Digits:
					{
					setState(603);
					match(Digits);
					}
					break;
				case Characters:
					{
					setState(604);
					match(Characters);
					}
					break;
				case UNDERSCORE:
					{
					setState(605);
					match(UNDERSCORE);
					}
					break;
				case MATCH:
				case CONTINUOUSLY:
				case EXPLAIN:
				case CREATE:
				case DELETE:
				case SHORTEST:
				case PATH:
				case WEIGHTS:
				case WGHT:
				case WHERE:
				case RETURN:
				case COUNT:
				case AVG:
				case MAX:
				case MIN:
				case SUM:
				case ACTION:
				case IN:
				case UDF:
				case JAR:
				case LOAD:
				case SAVE:
				case FROM:
				case TO:
				case DIR:
				case TRUE:
				case FALSE:
				case AND:
				case OR:
				case ON:
				case FILE:
					{
					setState(606);
					keyword();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(611);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionNameContext extends ParserRuleContext {
		public List<TerminalNode> Characters() { return getTokens(GraphflowParser.Characters); }
		public TerminalNode Characters(int i) {
			return getToken(GraphflowParser.Characters, i);
		}
		public List<TerminalNode> Digits() { return getTokens(GraphflowParser.Digits); }
		public TerminalNode Digits(int i) {
			return getToken(GraphflowParser.Digits, i);
		}
		public List<TerminalNode> UNDERSCORE() { return getTokens(GraphflowParser.UNDERSCORE); }
		public TerminalNode UNDERSCORE(int i) {
			return getToken(GraphflowParser.UNDERSCORE, i);
		}
		public List<TerminalNode> DASH() { return getTokens(GraphflowParser.DASH); }
		public TerminalNode DASH(int i) {
			return getToken(GraphflowParser.DASH, i);
		}
		public List<TerminalNode> DOT() { return getTokens(GraphflowParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(GraphflowParser.DOT, i);
		}
		public List<KeywordContext> keyword() {
			return getRuleContexts(KeywordContext.class);
		}
		public KeywordContext keyword(int i) {
			return getRuleContext(KeywordContext.class,i);
		}
		public FunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterFunctionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitFunctionName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitFunctionName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionNameContext functionName() throws RecognitionException {
		FunctionNameContext _localctx = new FunctionNameContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_functionName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(618); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(618);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Characters:
					{
					setState(612);
					match(Characters);
					}
					break;
				case Digits:
					{
					setState(613);
					match(Digits);
					}
					break;
				case UNDERSCORE:
					{
					setState(614);
					match(UNDERSCORE);
					}
					break;
				case DASH:
					{
					setState(615);
					match(DASH);
					}
					break;
				case DOT:
					{
					setState(616);
					match(DOT);
					}
					break;
				case MATCH:
				case CONTINUOUSLY:
				case EXPLAIN:
				case CREATE:
				case DELETE:
				case SHORTEST:
				case PATH:
				case WEIGHTS:
				case WGHT:
				case WHERE:
				case RETURN:
				case COUNT:
				case AVG:
				case MAX:
				case MIN:
				case SUM:
				case ACTION:
				case IN:
				case UDF:
				case JAR:
				case LOAD:
				case SAVE:
				case FROM:
				case TO:
				case DIR:
				case TRUE:
				case FALSE:
				case AND:
				case OR:
				case ON:
				case FILE:
					{
					setState(617);
					keyword();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(620); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( ((((_la - 4)) & ~0x3f) == 0 && ((1L << (_la - 4)) & ((1L << (MATCH - 4)) | (1L << (CONTINUOUSLY - 4)) | (1L << (EXPLAIN - 4)) | (1L << (CREATE - 4)) | (1L << (DELETE - 4)) | (1L << (SHORTEST - 4)) | (1L << (PATH - 4)) | (1L << (WEIGHTS - 4)) | (1L << (WGHT - 4)) | (1L << (WHERE - 4)) | (1L << (RETURN - 4)) | (1L << (COUNT - 4)) | (1L << (AVG - 4)) | (1L << (MAX - 4)) | (1L << (MIN - 4)) | (1L << (SUM - 4)) | (1L << (ACTION - 4)) | (1L << (IN - 4)) | (1L << (UDF - 4)) | (1L << (JAR - 4)) | (1L << (LOAD - 4)) | (1L << (SAVE - 4)) | (1L << (FROM - 4)) | (1L << (TO - 4)) | (1L << (DIR - 4)) | (1L << (TRUE - 4)) | (1L << (FALSE - 4)) | (1L << (AND - 4)) | (1L << (OR - 4)) | (1L << (ON - 4)) | (1L << (FILE - 4)) | (1L << (DASH - 4)) | (1L << (UNDERSCORE - 4)) | (1L << (DOT - 4)) | (1L << (Characters - 4)) | (1L << (Digits - 4)))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public List<TerminalNode> Characters() { return getTokens(GraphflowParser.Characters); }
		public TerminalNode Characters(int i) {
			return getToken(GraphflowParser.Characters, i);
		}
		public List<TerminalNode> UNDERSCORE() { return getTokens(GraphflowParser.UNDERSCORE); }
		public TerminalNode UNDERSCORE(int i) {
			return getToken(GraphflowParser.UNDERSCORE, i);
		}
		public List<TerminalNode> DASH() { return getTokens(GraphflowParser.DASH); }
		public TerminalNode DASH(int i) {
			return getToken(GraphflowParser.DASH, i);
		}
		public List<KeywordContext> keyword() {
			return getRuleContexts(KeywordContext.class);
		}
		public KeywordContext keyword(int i) {
			return getRuleContext(KeywordContext.class,i);
		}
		public List<TerminalNode> Digits() { return getTokens(GraphflowParser.Digits); }
		public TerminalNode Digits(int i) {
			return getToken(GraphflowParser.Digits, i);
		}
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_variable);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(626);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Characters:
				{
				setState(622);
				match(Characters);
				}
				break;
			case UNDERSCORE:
				{
				setState(623);
				match(UNDERSCORE);
				}
				break;
			case DASH:
				{
				setState(624);
				match(DASH);
				}
				break;
			case MATCH:
			case CONTINUOUSLY:
			case EXPLAIN:
			case CREATE:
			case DELETE:
			case SHORTEST:
			case PATH:
			case WEIGHTS:
			case WGHT:
			case WHERE:
			case RETURN:
			case COUNT:
			case AVG:
			case MAX:
			case MIN:
			case SUM:
			case ACTION:
			case IN:
			case UDF:
			case JAR:
			case LOAD:
			case SAVE:
			case FROM:
			case TO:
			case DIR:
			case TRUE:
			case FALSE:
			case AND:
			case OR:
			case ON:
			case FILE:
				{
				setState(625);
				keyword();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(635);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 4)) & ~0x3f) == 0 && ((1L << (_la - 4)) & ((1L << (MATCH - 4)) | (1L << (CONTINUOUSLY - 4)) | (1L << (EXPLAIN - 4)) | (1L << (CREATE - 4)) | (1L << (DELETE - 4)) | (1L << (SHORTEST - 4)) | (1L << (PATH - 4)) | (1L << (WEIGHTS - 4)) | (1L << (WGHT - 4)) | (1L << (WHERE - 4)) | (1L << (RETURN - 4)) | (1L << (COUNT - 4)) | (1L << (AVG - 4)) | (1L << (MAX - 4)) | (1L << (MIN - 4)) | (1L << (SUM - 4)) | (1L << (ACTION - 4)) | (1L << (IN - 4)) | (1L << (UDF - 4)) | (1L << (JAR - 4)) | (1L << (LOAD - 4)) | (1L << (SAVE - 4)) | (1L << (FROM - 4)) | (1L << (TO - 4)) | (1L << (DIR - 4)) | (1L << (TRUE - 4)) | (1L << (FALSE - 4)) | (1L << (AND - 4)) | (1L << (OR - 4)) | (1L << (ON - 4)) | (1L << (FILE - 4)) | (1L << (DASH - 4)) | (1L << (UNDERSCORE - 4)) | (1L << (Characters - 4)) | (1L << (Digits - 4)))) != 0)) {
				{
				setState(633);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Digits:
					{
					setState(628);
					match(Digits);
					}
					break;
				case Characters:
					{
					setState(629);
					match(Characters);
					}
					break;
				case UNDERSCORE:
					{
					setState(630);
					match(UNDERSCORE);
					}
					break;
				case DASH:
					{
					setState(631);
					match(DASH);
					}
					break;
				case MATCH:
				case CONTINUOUSLY:
				case EXPLAIN:
				case CREATE:
				case DELETE:
				case SHORTEST:
				case PATH:
				case WEIGHTS:
				case WGHT:
				case WHERE:
				case RETURN:
				case COUNT:
				case AVG:
				case MAX:
				case MIN:
				case SUM:
				case ACTION:
				case IN:
				case UDF:
				case JAR:
				case LOAD:
				case SAVE:
				case FROM:
				case TO:
				case DIR:
				case TRUE:
				case FALSE:
				case AND:
				case OR:
				case ON:
				case FILE:
					{
					setState(632);
					keyword();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(637);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeywordContext extends ParserRuleContext {
		public TerminalNode MATCH() { return getToken(GraphflowParser.MATCH, 0); }
		public TerminalNode CONTINUOUSLY() { return getToken(GraphflowParser.CONTINUOUSLY, 0); }
		public TerminalNode EXPLAIN() { return getToken(GraphflowParser.EXPLAIN, 0); }
		public TerminalNode CREATE() { return getToken(GraphflowParser.CREATE, 0); }
		public TerminalNode DELETE() { return getToken(GraphflowParser.DELETE, 0); }
		public TerminalNode SHORTEST() { return getToken(GraphflowParser.SHORTEST, 0); }
		public TerminalNode PATH() { return getToken(GraphflowParser.PATH, 0); }
		public TerminalNode WHERE() { return getToken(GraphflowParser.WHERE, 0); }
		public TerminalNode WEIGHTS() { return getToken(GraphflowParser.WEIGHTS, 0); }
		public TerminalNode WGHT() { return getToken(GraphflowParser.WGHT, 0); }
		public TerminalNode RETURN() { return getToken(GraphflowParser.RETURN, 0); }
		public TerminalNode COUNT() { return getToken(GraphflowParser.COUNT, 0); }
		public TerminalNode AVG() { return getToken(GraphflowParser.AVG, 0); }
		public TerminalNode MAX() { return getToken(GraphflowParser.MAX, 0); }
		public TerminalNode MIN() { return getToken(GraphflowParser.MIN, 0); }
		public TerminalNode SUM() { return getToken(GraphflowParser.SUM, 0); }
		public TerminalNode ACTION() { return getToken(GraphflowParser.ACTION, 0); }
		public TerminalNode IN() { return getToken(GraphflowParser.IN, 0); }
		public TerminalNode UDF() { return getToken(GraphflowParser.UDF, 0); }
		public TerminalNode JAR() { return getToken(GraphflowParser.JAR, 0); }
		public TerminalNode LOAD() { return getToken(GraphflowParser.LOAD, 0); }
		public TerminalNode SAVE() { return getToken(GraphflowParser.SAVE, 0); }
		public TerminalNode FROM() { return getToken(GraphflowParser.FROM, 0); }
		public TerminalNode TO() { return getToken(GraphflowParser.TO, 0); }
		public TerminalNode DIR() { return getToken(GraphflowParser.DIR, 0); }
		public TerminalNode TRUE() { return getToken(GraphflowParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(GraphflowParser.FALSE, 0); }
		public TerminalNode AND() { return getToken(GraphflowParser.AND, 0); }
		public TerminalNode OR() { return getToken(GraphflowParser.OR, 0); }
		public TerminalNode ON() { return getToken(GraphflowParser.ON, 0); }
		public TerminalNode FILE() { return getToken(GraphflowParser.FILE, 0); }
		public KeywordContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyword; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterKeyword(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitKeyword(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitKeyword(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeywordContext keyword() throws RecognitionException {
		KeywordContext _localctx = new KeywordContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_keyword);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(638);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MATCH) | (1L << CONTINUOUSLY) | (1L << EXPLAIN) | (1L << CREATE) | (1L << DELETE) | (1L << SHORTEST) | (1L << PATH) | (1L << WEIGHTS) | (1L << WGHT) | (1L << WHERE) | (1L << RETURN) | (1L << COUNT) | (1L << AVG) | (1L << MAX) | (1L << MIN) | (1L << SUM) | (1L << ACTION) | (1L << IN) | (1L << UDF) | (1L << JAR) | (1L << LOAD) | (1L << SAVE) | (1L << FROM) | (1L << TO) | (1L << DIR) | (1L << TRUE) | (1L << FALSE) | (1L << AND) | (1L << OR) | (1L << ON) | (1L << FILE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WhitespaceContext extends ParserRuleContext {
		public List<TerminalNode> SPACE() { return getTokens(GraphflowParser.SPACE); }
		public TerminalNode SPACE(int i) {
			return getToken(GraphflowParser.SPACE, i);
		}
		public List<TerminalNode> TAB() { return getTokens(GraphflowParser.TAB); }
		public TerminalNode TAB(int i) {
			return getToken(GraphflowParser.TAB, i);
		}
		public List<TerminalNode> CARRIAGE_RETURN() { return getTokens(GraphflowParser.CARRIAGE_RETURN); }
		public TerminalNode CARRIAGE_RETURN(int i) {
			return getToken(GraphflowParser.CARRIAGE_RETURN, i);
		}
		public List<TerminalNode> LINE_FEED() { return getTokens(GraphflowParser.LINE_FEED); }
		public TerminalNode LINE_FEED(int i) {
			return getToken(GraphflowParser.LINE_FEED, i);
		}
		public List<TerminalNode> FORM_FEED() { return getTokens(GraphflowParser.FORM_FEED); }
		public TerminalNode FORM_FEED(int i) {
			return getToken(GraphflowParser.FORM_FEED, i);
		}
		public List<TerminalNode> Comment() { return getTokens(GraphflowParser.Comment); }
		public TerminalNode Comment(int i) {
			return getToken(GraphflowParser.Comment, i);
		}
		public WhitespaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whitespace; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterWhitespace(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitWhitespace(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitWhitespace(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhitespaceContext whitespace() throws RecognitionException {
		WhitespaceContext _localctx = new WhitespaceContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_whitespace);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(641); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(640);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(643); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,94,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumericLiteralContext extends ParserRuleContext {
		public IntegerLiteralContext integerLiteral() {
			return getRuleContext(IntegerLiteralContext.class,0);
		}
		public DoubleLiteralContext doubleLiteral() {
			return getRuleContext(DoubleLiteralContext.class,0);
		}
		public TerminalNode DASH() { return getToken(GraphflowParser.DASH, 0); }
		public WhitespaceContext whitespace() {
			return getRuleContext(WhitespaceContext.class,0);
		}
		public NumericLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numericLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterNumericLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitNumericLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitNumericLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumericLiteralContext numericLiteral() throws RecognitionException {
		NumericLiteralContext _localctx = new NumericLiteralContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_numericLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(649);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,96,_ctx) ) {
			case 1:
				{
				setState(645);
				match(DASH);
				setState(647);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Comment) | (1L << SPACE) | (1L << TAB) | (1L << CARRIAGE_RETURN) | (1L << LINE_FEED) | (1L << FORM_FEED))) != 0)) {
					{
					setState(646);
					whitespace();
					}
				}

				}
				break;
			}
			setState(653);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,97,_ctx) ) {
			case 1:
				{
				setState(651);
				integerLiteral();
				}
				break;
			case 2:
				{
				setState(652);
				doubleLiteral();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntegerLiteralContext extends ParserRuleContext {
		public TerminalNode Digits() { return getToken(GraphflowParser.Digits, 0); }
		public IntegerLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integerLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterIntegerLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitIntegerLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitIntegerLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegerLiteralContext integerLiteral() throws RecognitionException {
		IntegerLiteralContext _localctx = new IntegerLiteralContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_integerLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(655);
			match(Digits);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DoubleLiteralContext extends ParserRuleContext {
		public List<TerminalNode> Digits() { return getTokens(GraphflowParser.Digits); }
		public TerminalNode Digits(int i) {
			return getToken(GraphflowParser.Digits, i);
		}
		public TerminalNode DOT() { return getToken(GraphflowParser.DOT, 0); }
		public TerminalNode DASH() { return getToken(GraphflowParser.DASH, 0); }
		public DoubleLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_doubleLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterDoubleLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitDoubleLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitDoubleLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DoubleLiteralContext doubleLiteral() throws RecognitionException {
		DoubleLiteralContext _localctx = new DoubleLiteralContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_doubleLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(658);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DASH) {
				{
				setState(657);
				match(DASH);
				}
			}

			setState(660);
			match(Digits);
			setState(661);
			match(DOT);
			setState(662);
			match(Digits);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleanLiteralContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(GraphflowParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(GraphflowParser.FALSE, 0); }
		public BooleanLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleanLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterBooleanLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitBooleanLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitBooleanLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleanLiteralContext booleanLiteral() throws RecognitionException {
		BooleanLiteralContext _localctx = new BooleanLiteralContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_booleanLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(664);
			_la = _input.LA(1);
			if ( !(_la==TRUE || _la==FALSE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringLiteralContext extends ParserRuleContext {
		public TerminalNode QuotedString() { return getToken(GraphflowParser.QuotedString, 0); }
		public StringLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GraphflowListener ) ((GraphflowListener)listener).exitStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GraphflowVisitor ) return ((GraphflowVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringLiteralContext stringLiteral() throws RecognitionException {
		StringLiteralContext _localctx = new StringLiteralContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_stringLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(666);
			match(QuotedString);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3E\u029f\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\3\2\5\2p\n\2\3\2\3\2\5\2t\n\2\3\2\3"+
		"\2\5\2x\n\2\5\2z\n\2\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\5\4\u0089\n\4\3\5\3\5\3\5\3\5\3\5\3\5\5\5\u0091\n\5\3\5\3\5\3\5\5\5"+
		"\u0096\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00a0\n\6\3\6\3\6\3\6\5"+
		"\6\u00a5\n\6\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\5\t\u00b3"+
		"\n\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\5\f\u00c5\n\f\3\f\3\f\3\f\5\f\u00ca\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\5\r\u00d4\n\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\5\16\u00dd\n\16\3\16"+
		"\3\16\5\16\u00e1\n\16\3\16\7\16\u00e4\n\16\f\16\16\16\u00e7\13\16\3\17"+
		"\3\17\5\17\u00eb\n\17\3\17\3\17\5\17\u00ef\n\17\3\17\7\17\u00f2\n\17\f"+
		"\17\16\17\u00f5\13\17\3\20\3\20\5\20\u00f9\n\20\3\20\3\20\5\20\u00fd\n"+
		"\20\3\20\7\20\u0100\n\20\f\20\16\20\u0103\13\20\3\21\3\21\5\21\u0107\n"+
		"\21\3\21\3\21\5\21\u010b\n\21\3\21\7\21\u010e\n\21\f\21\16\21\u0111\13"+
		"\21\3\22\3\22\5\22\u0115\n\22\3\22\3\22\5\22\u0119\n\22\3\22\3\22\5\22"+
		"\u011d\n\22\3\22\3\22\5\22\u0121\n\22\3\22\3\22\3\23\3\23\3\23\3\23\3"+
		"\23\5\23\u012a\n\23\3\23\5\23\u012d\n\23\3\23\3\23\5\23\u0131\n\23\3\23"+
		"\3\23\3\23\5\23\u0136\n\23\7\23\u0138\n\23\f\23\16\23\u013b\13\23\3\24"+
		"\3\24\3\24\5\24\u0140\n\24\3\24\3\24\5\24\u0144\n\24\3\24\5\24\u0147\n"+
		"\24\3\24\3\24\3\24\5\24\u014c\n\24\3\25\3\25\3\26\3\26\3\26\5\26\u0153"+
		"\n\26\3\26\3\26\5\26\u0157\n\26\3\26\3\26\3\27\3\27\3\27\3\27\3\30\3\30"+
		"\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\7\31\u016b\n\31\f\31"+
		"\16\31\u016e\13\31\3\32\3\32\5\32\u0172\n\32\3\32\3\32\5\32\u0176\n\32"+
		"\3\32\3\32\3\33\3\33\5\33\u017c\n\33\3\34\3\34\3\34\5\34\u0181\n\34\3"+
		"\34\3\34\3\34\3\34\3\35\3\35\3\35\5\35\u018a\n\35\3\35\3\35\3\35\3\35"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\5\37\u019a\n\37\3\37"+
		"\3\37\5\37\u019e\n\37\3\37\3\37\3 \3 \5 \u01a4\n \3 \3 \5 \u01a8\n \3"+
		" \3 \3 \5 \u01ad\n \3 \5 \u01b0\n \3 \5 \u01b3\n \3 \3 \3!\3!\5!\u01b9"+
		"\n!\3!\3!\5!\u01bd\n!\3!\3!\5!\u01c1\n!\3!\5!\u01c4\n!\3!\5!\u01c7\n!"+
		"\3!\5!\u01ca\n!\3!\3!\3\"\3\"\5\"\u01d0\n\"\3\"\3\"\3\"\5\"\u01d5\n\""+
		"\3\"\3\"\3#\3#\5#\u01db\n#\3#\3#\3#\5#\u01e0\n#\3#\5#\u01e3\n#\3#\3#\3"+
		"#\3#\3#\5#\u01ea\n#\5#\u01ec\n#\3#\3#\3$\3$\5$\u01f2\n$\3$\3$\5$\u01f6"+
		"\n$\3$\3$\5$\u01fa\n$\3$\5$\u01fd\n$\3$\3$\5$\u0201\n$\3$\3$\3$\3$\5$"+
		"\u0207\n$\3$\5$\u020a\n$\3$\5$\u020d\n$\3$\3$\5$\u0211\n$\3$\5$\u0214"+
		"\n$\3$\5$\u0217\n$\3%\3%\3%\3%\3&\3&\3\'\3\'\3(\3(\5(\u0223\n(\3(\3(\5"+
		"(\u0227\n(\3(\3(\5(\u022b\n(\3(\7(\u022e\n(\f(\16(\u0231\13(\5(\u0233"+
		"\n(\3(\5(\u0236\n(\3(\3(\3)\3)\5)\u023c\n)\3)\3)\5)\u0240\n)\3)\3)\3*"+
		"\3*\3*\5*\u0247\n*\3+\3+\3+\3+\3,\3,\3,\3,\3,\3,\3,\3,\3,\3,\3-\3-\3."+
		"\3.\3.\5.\u025c\n.\3.\3.\3.\3.\7.\u0262\n.\f.\16.\u0265\13.\3/\3/\3/\3"+
		"/\3/\3/\6/\u026d\n/\r/\16/\u026e\3\60\3\60\3\60\3\60\5\60\u0275\n\60\3"+
		"\60\3\60\3\60\3\60\3\60\7\60\u027c\n\60\f\60\16\60\u027f\13\60\3\61\3"+
		"\61\3\62\6\62\u0284\n\62\r\62\16\62\u0285\3\63\3\63\5\63\u028a\n\63\5"+
		"\63\u028c\n\63\3\63\3\63\5\63\u0290\n\63\3\64\3\64\3\65\5\65\u0295\n\65"+
		"\3\65\3\65\3\65\3\65\3\66\3\66\3\67\3\67\3\67\2\28\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjl\2"+
		"\7\3\2\22\25\3\2>C\3\2\6$\4\2\5\5%)\3\2\37 \2\u02e1\2o\3\2\2\2\4}\3\2"+
		"\2\2\6\u0088\3\2\2\2\b\u008a\3\2\2\2\n\u0097\3\2\2\2\f\u00a6\3\2\2\2\16"+
		"\u00aa\3\2\2\2\20\u00ae\3\2\2\2\22\u00b4\3\2\2\2\24\u00b8\3\2\2\2\26\u00be"+
		"\3\2\2\2\30\u00d3\3\2\2\2\32\u00da\3\2\2\2\34\u00e8\3\2\2\2\36\u00f6\3"+
		"\2\2\2 \u0104\3\2\2\2\"\u0112\3\2\2\2$\u0124\3\2\2\2&\u014b\3\2\2\2(\u014d"+
		"\3\2\2\2*\u014f\3\2\2\2,\u015a\3\2\2\2.\u015e\3\2\2\2\60\u0164\3\2\2\2"+
		"\62\u016f\3\2\2\2\64\u017b\3\2\2\2\66\u017d\3\2\2\28\u0186\3\2\2\2:\u018f"+
		"\3\2\2\2<\u0197\3\2\2\2>\u01a1\3\2\2\2@\u01b6\3\2\2\2B\u01cd\3\2\2\2D"+
		"\u01d8\3\2\2\2F\u0216\3\2\2\2H\u0218\3\2\2\2J\u021c\3\2\2\2L\u021e\3\2"+
		"\2\2N\u0220\3\2\2\2P\u0239\3\2\2\2R\u0246\3\2\2\2T\u0248\3\2\2\2V\u024c"+
		"\3\2\2\2X\u0256\3\2\2\2Z\u025b\3\2\2\2\\\u026c\3\2\2\2^\u0274\3\2\2\2"+
		"`\u0280\3\2\2\2b\u0283\3\2\2\2d\u028b\3\2\2\2f\u0291\3\2\2\2h\u0294\3"+
		"\2\2\2j\u029a\3\2\2\2l\u029c\3\2\2\2np\5b\62\2on\3\2\2\2op\3\2\2\2pq\3"+
		"\2\2\2qs\5\4\3\2rt\5b\62\2sr\3\2\2\2st\3\2\2\2ty\3\2\2\2uw\7\62\2\2vx"+
		"\5b\62\2wv\3\2\2\2wx\3\2\2\2xz\3\2\2\2yu\3\2\2\2yz\3\2\2\2z{\3\2\2\2{"+
		"|\7\2\2\3|\3\3\2\2\2}~\5\6\4\2~\5\3\2\2\2\177\u0089\5\b\5\2\u0080\u0089"+
		"\5\n\6\2\u0081\u0089\5\f\7\2\u0082\u0089\5\16\b\2\u0083\u0089\5\20\t\2"+
		"\u0084\u0089\5\22\n\2\u0085\u0089\5\24\13\2\u0086\u0089\5\26\f\2\u0087"+
		"\u0089\5\30\r\2\u0088\177\3\2\2\2\u0088\u0080\3\2\2\2\u0088\u0081\3\2"+
		"\2\2\u0088\u0082\3\2\2\2\u0088\u0083\3\2\2\2\u0088\u0084\3\2\2\2\u0088"+
		"\u0085\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0087\3\2\2\2\u0089\7\3\2\2\2"+
		"\u008a\u008b\7\6\2\2\u008b\u008c\5b\62\2\u008c\u0090\5\32\16\2\u008d\u008e"+
		"\5b\62\2\u008e\u008f\5,\27\2\u008f\u0091\3\2\2\2\u0090\u008d\3\2\2\2\u0090"+
		"\u0091\3\2\2\2\u0091\u0095\3\2\2\2\u0092\u0093\5b\62\2\u0093\u0094\5$"+
		"\23\2\u0094\u0096\3\2\2\2\u0095\u0092\3\2\2\2\u0095\u0096\3\2\2\2\u0096"+
		"\t\3\2\2\2\u0097\u0098\7\7\2\2\u0098\u0099\5b\62\2\u0099\u009a\7\6\2\2"+
		"\u009a\u009b\5b\62\2\u009b\u009f\5\32\16\2\u009c\u009d\5b\62\2\u009d\u009e"+
		"\5,\27\2\u009e\u00a0\3\2\2\2\u009f\u009c\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0"+
		"\u00a1\3\2\2\2\u00a1\u00a4\5b\62\2\u00a2\u00a5\5T+\2\u00a3\u00a5\5V,\2"+
		"\u00a4\u00a2\3\2\2\2\u00a4\u00a3\3\2\2\2\u00a5\13\3\2\2\2\u00a6\u00a7"+
		"\7\b\2\2\u00a7\u00a8\5b\62\2\u00a8\u00a9\5\b\5\2\u00a9\r\3\2\2\2\u00aa"+
		"\u00ab\7\b\2\2\u00ab\u00ac\5b\62\2\u00ac\u00ad\5\n\6\2\u00ad\17\3\2\2"+
		"\2\u00ae\u00af\7\t\2\2\u00af\u00b2\5b\62\2\u00b0\u00b3\5\36\20\2\u00b1"+
		"\u00b3\5 \21\2\u00b2\u00b0\3\2\2\2\u00b2\u00b1\3\2\2\2\u00b3\21\3\2\2"+
		"\2\u00b4\u00b5\7\n\2\2\u00b5\u00b6\5b\62\2\u00b6\u00b7\5\34\17\2\u00b7"+
		"\23\3\2\2\2\u00b8\u00b9\7\13\2\2\u00b9\u00ba\5b\62\2\u00ba\u00bb\7\f\2"+
		"\2\u00bb\u00bc\5b\62\2\u00bc\u00bd\5\"\22\2\u00bd\25\3\2\2\2\u00be\u00bf"+
		"\7\7\2\2\u00bf\u00c0\5b\62\2\u00c0\u00c4\5\24\13\2\u00c1\u00c2\5b\62\2"+
		"\u00c2\u00c3\5.\30\2\u00c3\u00c5\3\2\2\2\u00c4\u00c1\3\2\2\2\u00c4\u00c5"+
		"\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c9\5b\62\2\u00c7\u00ca\5T+\2\u00c8"+
		"\u00ca\5V,\2\u00c9\u00c7\3\2\2\2\u00c9\u00c8\3\2\2\2\u00ca\27\3\2\2\2"+
		"\u00cb\u00cc\7\32\2\2\u00cc\u00cd\5b\62\2\u00cd\u00ce\7\34\2\2\u00ce\u00d4"+
		"\3\2\2\2\u00cf\u00d0\7\33\2\2\u00d0\u00d1\5b\62\2\u00d1\u00d2\7\35\2\2"+
		"\u00d2\u00d4\3\2\2\2\u00d3\u00cb\3\2\2\2\u00d3\u00cf\3\2\2\2\u00d4\u00d5"+
		"\3\2\2\2\u00d5\u00d6\5b\62\2\u00d6\u00d7\7\36\2\2\u00d7\u00d8\5b\62\2"+
		"\u00d8\u00d9\5l\67\2\u00d9\31\3\2\2\2\u00da\u00e5\5\66\34\2\u00db\u00dd"+
		"\5b\62\2\u00dc\u00db\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00de\3\2\2\2\u00de"+
		"\u00e0\7\64\2\2\u00df\u00e1\5b\62\2\u00e0\u00df\3\2\2\2\u00e0\u00e1\3"+
		"\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e4\5\66\34\2\u00e3\u00dc\3\2\2\2\u00e4"+
		"\u00e7\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\33\3\2\2"+
		"\2\u00e7\u00e5\3\2\2\2\u00e8\u00f3\58\35\2\u00e9\u00eb\5b\62\2\u00ea\u00e9"+
		"\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ee\7\64\2\2"+
		"\u00ed\u00ef\5b\62\2\u00ee\u00ed\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f0"+
		"\3\2\2\2\u00f0\u00f2\58\35\2\u00f1\u00ea\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3"+
		"\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\35\3\2\2\2\u00f5\u00f3\3\2\2"+
		"\2\u00f6\u0101\5:\36\2\u00f7\u00f9\5b\62\2\u00f8\u00f7\3\2\2\2\u00f8\u00f9"+
		"\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u00fc\7\64\2\2\u00fb\u00fd\5b\62\2"+
		"\u00fc\u00fb\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u0100"+
		"\5:\36\2\u00ff\u00f8\3\2\2\2\u0100\u0103\3\2\2\2\u0101\u00ff\3\2\2\2\u0101"+
		"\u0102\3\2\2\2\u0102\37\3\2\2\2\u0103\u0101\3\2\2\2\u0104\u010f\5> \2"+
		"\u0105\u0107\5b\62\2\u0106\u0105\3\2\2\2\u0106\u0107\3\2\2\2\u0107\u0108"+
		"\3\2\2\2\u0108\u010a\7\64\2\2\u0109\u010b\5b\62\2\u010a\u0109\3\2\2\2"+
		"\u010a\u010b\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010e\5> \2\u010d\u0106"+
		"\3\2\2\2\u010e\u0111\3\2\2\2\u010f\u010d\3\2\2\2\u010f\u0110\3\2\2\2\u0110"+
		"!\3\2\2\2\u0111\u010f\3\2\2\2\u0112\u0114\78\2\2\u0113\u0115\5b\62\2\u0114"+
		"\u0113\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0118\7E"+
		"\2\2\u0117\u0119\5b\62\2\u0118\u0117\3\2\2\2\u0118\u0119\3\2\2\2\u0119"+
		"\u011a\3\2\2\2\u011a\u011c\7\64\2\2\u011b\u011d\5b\62\2\u011c\u011b\3"+
		"\2\2\2\u011c\u011d\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u0120\7E\2\2\u011f"+
		"\u0121\5b\62\2\u0120\u011f\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0122\3\2"+
		"\2\2\u0122\u0123\79\2\2\u0123#\3\2\2\2\u0124\u0125\7\20\2\2\u0125\u0129"+
		"\5b\62\2\u0126\u012a\5^\60\2\u0127\u012a\5H%\2\u0128\u012a\5&\24\2\u0129"+
		"\u0126\3\2\2\2\u0129\u0127\3\2\2\2\u0129\u0128\3\2\2\2\u012a\u0139\3\2"+
		"\2\2\u012b\u012d\5b\62\2\u012c\u012b\3\2\2\2\u012c\u012d\3\2\2\2\u012d"+
		"\u012e\3\2\2\2\u012e\u0130\7\64\2\2\u012f\u0131\5b\62\2\u0130\u012f\3"+
		"\2\2\2\u0130\u0131\3\2\2\2\u0131\u0135\3\2\2\2\u0132\u0136\5^\60\2\u0133"+
		"\u0136\5H%\2\u0134\u0136\5&\24\2\u0135\u0132\3\2\2\2\u0135\u0133\3\2\2"+
		"\2\u0135\u0134\3\2\2\2\u0136\u0138\3\2\2\2\u0137\u012c\3\2\2\2\u0138\u013b"+
		"\3\2\2\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a%\3\2\2\2\u013b"+
		"\u0139\3\2\2\2\u013c\u013d\5(\25\2\u013d\u013f\78\2\2\u013e\u0140\5b\62"+
		"\2\u013f\u013e\3\2\2\2\u013f\u0140\3\2\2\2\u0140\u0143\3\2\2\2\u0141\u0144"+
		"\5^\60\2\u0142\u0144\5H%\2\u0143\u0141\3\2\2\2\u0143\u0142\3\2\2\2\u0144"+
		"\u0146\3\2\2\2\u0145\u0147\5b\62\2\u0146\u0145\3\2\2\2\u0146\u0147\3\2"+
		"\2\2\u0147\u0148\3\2\2\2\u0148\u0149\79\2\2\u0149\u014c\3\2\2\2\u014a"+
		"\u014c\5*\26\2\u014b\u013c\3\2\2\2\u014b\u014a\3\2\2\2\u014c\'\3\2\2\2"+
		"\u014d\u014e\t\2\2\2\u014e)\3\2\2\2\u014f\u0150\7\21\2\2\u0150\u0152\7"+
		"8\2\2\u0151\u0153\5b\62\2\u0152\u0151\3\2\2\2\u0152\u0153\3\2\2\2\u0153"+
		"\u0154\3\2\2\2\u0154\u0156\7,\2\2\u0155\u0157\5b\62\2\u0156\u0155\3\2"+
		"\2\2\u0156\u0157\3\2\2\2\u0157\u0158\3\2\2\2\u0158\u0159\79\2\2\u0159"+
		"+\3\2\2\2\u015a\u015b\7\17\2\2\u015b\u015c\5b\62\2\u015c\u015d\5\60\31"+
		"\2\u015d-\3\2\2\2\u015e\u015f\7\r\2\2\u015f\u0160\5b\62\2\u0160\u0161"+
		"\7#\2\2\u0161\u0162\5b\62\2\u0162\u0163\5J&\2\u0163/\3\2\2\2\u0164\u016c"+
		"\5\62\32\2\u0165\u0166\5b\62\2\u0166\u0167\7!\2\2\u0167\u0168\5b\62\2"+
		"\u0168\u0169\5\62\32\2\u0169\u016b\3\2\2\2\u016a\u0165\3\2\2\2\u016b\u016e"+
		"\3\2\2\2\u016c\u016a\3\2\2\2\u016c\u016d\3\2\2\2\u016d\61\3\2\2\2\u016e"+
		"\u016c\3\2\2\2\u016f\u0171\5\64\33\2\u0170\u0172\5b\62\2\u0171\u0170\3"+
		"\2\2\2\u0171\u0172\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u0175\5X-\2\u0174"+
		"\u0176\5b\62\2\u0175\u0174\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0177\3\2"+
		"\2\2\u0177\u0178\5\64\33\2\u0178\63\3\2\2\2\u0179\u017c\5R*\2\u017a\u017c"+
		"\5H%\2\u017b\u0179\3\2\2\2\u017b\u017a\3\2\2\2\u017c\65\3\2\2\2\u017d"+
		"\u0180\5@!\2\u017e\u017f\7-\2\2\u017f\u0181\5F$\2\u0180\u017e\3\2\2\2"+
		"\u0180\u0181\3\2\2\2\u0181\u0182\3\2\2\2\u0182\u0183\7-\2\2\u0183\u0184"+
		"\7A\2\2\u0184\u0185\5@!\2\u0185\67\3\2\2\2\u0186\u0189\5<\37\2\u0187\u0188"+
		"\7-\2\2\u0188\u018a\5B\"\2\u0189\u0187\3\2\2\2\u0189\u018a\3\2\2\2\u018a"+
		"\u018b\3\2\2\2\u018b\u018c\7-\2\2\u018c\u018d\7A\2\2\u018d\u018e\5<\37"+
		"\2\u018e9\3\2\2\2\u018f\u0190\5> \2\u0190\u0191\7-\2\2\u0191\u0192\5D"+
		"#\2\u0192\u0193\3\2\2\2\u0193\u0194\7-\2\2\u0194\u0195\7A\2\2\u0195\u0196"+
		"\5> \2\u0196;\3\2\2\2\u0197\u0199\78\2\2\u0198\u019a\5b\62\2\u0199\u0198"+
		"\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u019b\3\2\2\2\u019b\u019d\7E\2\2\u019c"+
		"\u019e\5b\62\2\u019d\u019c\3\2\2\2\u019d\u019e\3\2\2\2\u019e\u019f\3\2"+
		"\2\2\u019f\u01a0\79\2\2\u01a0=\3\2\2\2\u01a1\u01a3\78\2\2\u01a2\u01a4"+
		"\5b\62\2\u01a3\u01a2\3\2\2\2\u01a3\u01a4\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5"+
		"\u01a7\7E\2\2\u01a6\u01a8\5b\62\2\u01a7\u01a6\3\2\2\2\u01a7\u01a8\3\2"+
		"\2\2\u01a8\u01a9\3\2\2\2\u01a9\u01aa\7\63\2\2\u01aa\u01ac\5L\'\2\u01ab"+
		"\u01ad\5b\62\2\u01ac\u01ab\3\2\2\2\u01ac\u01ad\3\2\2\2\u01ad\u01af\3\2"+
		"\2\2\u01ae\u01b0\5N(\2\u01af\u01ae\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0\u01b2"+
		"\3\2\2\2\u01b1\u01b3\5b\62\2\u01b2\u01b1\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3"+
		"\u01b4\3\2\2\2\u01b4\u01b5\79\2\2\u01b5?\3\2\2\2\u01b6\u01b8\78\2\2\u01b7"+
		"\u01b9\5b\62\2\u01b8\u01b7\3\2\2\2\u01b8\u01b9\3\2\2\2\u01b9\u01ba\3\2"+
		"\2\2\u01ba\u01c0\5^\60\2\u01bb\u01bd\5b\62\2\u01bc\u01bb\3\2\2\2\u01bc"+
		"\u01bd\3\2\2\2\u01bd\u01be\3\2\2\2\u01be\u01bf\7\63\2\2\u01bf\u01c1\5"+
		"L\'\2\u01c0\u01bc\3\2\2\2\u01c0\u01c1\3\2\2\2\u01c1\u01c6\3\2\2\2\u01c2"+
		"\u01c4\5b\62\2\u01c3\u01c2\3\2\2\2\u01c3\u01c4\3\2\2\2\u01c4\u01c5\3\2"+
		"\2\2\u01c5\u01c7\5N(\2\u01c6\u01c3\3\2\2\2\u01c6\u01c7\3\2\2\2\u01c7\u01c9"+
		"\3\2\2\2\u01c8\u01ca\5b\62\2\u01c9\u01c8\3\2\2\2\u01c9\u01ca\3\2\2\2\u01ca"+
		"\u01cb\3\2\2\2\u01cb\u01cc\79\2\2\u01ccA\3\2\2\2\u01cd\u01cf\7<\2\2\u01ce"+
		"\u01d0\5b\62\2\u01cf\u01ce\3\2\2\2\u01cf\u01d0\3\2\2\2\u01d0\u01d1\3\2"+
		"\2\2\u01d1\u01d2\7\63\2\2\u01d2\u01d4\5L\'\2\u01d3\u01d5\5b\62\2\u01d4"+
		"\u01d3\3\2\2\2\u01d4\u01d5\3\2\2\2\u01d5\u01d6\3\2\2\2\u01d6\u01d7\7="+
		"\2\2\u01d7C\3\2\2\2\u01d8\u01da\7<\2\2\u01d9\u01db\5b\62\2\u01da\u01d9"+
		"\3\2\2\2\u01da\u01db\3\2\2\2\u01db\u01dc\3\2\2\2\u01dc\u01dd\7\63\2\2"+
		"\u01dd\u01df\5L\'\2\u01de\u01e0\5b\62\2\u01df\u01de\3\2\2\2\u01df\u01e0"+
		"\3\2\2\2\u01e0\u01e2\3\2\2\2\u01e1\u01e3\5N(\2\u01e2\u01e1\3\2\2\2\u01e2"+
		"\u01e3\3\2\2\2\u01e3\u01eb\3\2\2\2\u01e4\u01e5\5b\62\2\u01e5\u01e6\7\16"+
		"\2\2\u01e6\u01e7\7\63\2\2\u01e7\u01e9\5h\65\2\u01e8\u01ea\5b\62\2\u01e9"+
		"\u01e8\3\2\2\2\u01e9\u01ea\3\2\2\2\u01ea\u01ec\3\2\2\2\u01eb\u01e4\3\2"+
		"\2\2\u01eb\u01ec\3\2\2\2\u01ec\u01ed\3\2\2\2\u01ed\u01ee\7=\2\2\u01ee"+
		"E\3\2\2\2\u01ef\u01f1\7<\2\2\u01f0\u01f2\5b\62\2\u01f1\u01f0\3\2\2\2\u01f1"+
		"\u01f2\3\2\2\2\u01f2\u01f3\3\2\2\2\u01f3\u01f9\5^\60\2\u01f4\u01f6\5b"+
		"\62\2\u01f5\u01f4\3\2\2\2\u01f5\u01f6\3\2\2\2\u01f6\u01f7\3\2\2\2\u01f7"+
		"\u01f8\7\63\2\2\u01f8\u01fa\5L\'\2\u01f9\u01f5\3\2\2\2\u01f9\u01fa\3\2"+
		"\2\2\u01fa\u01fc\3\2\2\2\u01fb\u01fd\5b\62\2\u01fc\u01fb\3\2\2\2\u01fc"+
		"\u01fd\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe\u0200\5N(\2\u01ff\u0201\5b\62"+
		"\2\u0200\u01ff\3\2\2\2\u0200\u0201\3\2\2\2\u0201\u0202\3\2\2\2\u0202\u0203"+
		"\7=\2\2\u0203\u0217\3\2\2\2\u0204\u0206\7<\2\2\u0205\u0207\5b\62\2\u0206"+
		"\u0205\3\2\2\2\u0206\u0207\3\2\2\2\u0207\u0209\3\2\2\2\u0208\u020a\5^"+
		"\60\2\u0209\u0208\3\2\2\2\u0209\u020a\3\2\2\2\u020a\u0210\3\2\2\2\u020b"+
		"\u020d\5b\62\2\u020c\u020b\3\2\2\2\u020c\u020d\3\2\2\2\u020d\u020e\3\2"+
		"\2\2\u020e\u020f\7\63\2\2\u020f\u0211\5L\'\2\u0210\u020c\3\2\2\2\u0210"+
		"\u0211\3\2\2\2\u0211\u0213\3\2\2\2\u0212\u0214\5b\62\2\u0213\u0212\3\2"+
		"\2\2\u0213\u0214\3\2\2\2\u0214\u0215\3\2\2\2\u0215\u0217\7=\2\2\u0216"+
		"\u01ef\3\2\2\2\u0216\u0204\3\2\2\2\u0217G\3\2\2\2\u0218\u0219\5^\60\2"+
		"\u0219\u021a\7/\2\2\u021a\u021b\5Z.\2\u021bI\3\2\2\2\u021c\u021d\5Z.\2"+
		"\u021dK\3\2\2\2\u021e\u021f\5^\60\2\u021fM\3\2\2\2\u0220\u0222\7:\2\2"+
		"\u0221\u0223\5b\62\2\u0222\u0221\3\2\2\2\u0222\u0223\3\2\2\2\u0223\u0232"+
		"\3\2\2\2\u0224\u022f\5P)\2\u0225\u0227\5b\62\2\u0226\u0225\3\2\2\2\u0226"+
		"\u0227\3\2\2\2\u0227\u0228\3\2\2\2\u0228\u022a\7\64\2\2\u0229\u022b\5"+
		"b\62\2\u022a\u0229\3\2\2\2\u022a\u022b\3\2\2\2\u022b\u022c\3\2\2\2\u022c"+
		"\u022e\5P)\2\u022d\u0226\3\2\2\2\u022e\u0231\3\2\2\2\u022f\u022d\3\2\2"+
		"\2\u022f\u0230\3\2\2\2\u0230\u0233\3\2\2\2\u0231\u022f\3\2\2\2\u0232\u0224"+
		"\3\2\2\2\u0232\u0233\3\2\2\2\u0233\u0235\3\2\2\2\u0234\u0236\5b\62\2\u0235"+
		"\u0234\3\2\2\2\u0235\u0236\3\2\2\2\u0236\u0237\3\2\2\2\u0237\u0238\7;"+
		"\2\2\u0238O\3\2\2\2\u0239\u023b\5Z.\2\u023a\u023c\5b\62\2\u023b\u023a"+
		"\3\2\2\2\u023b\u023c\3\2\2\2\u023c\u023d\3\2\2\2\u023d\u023f\7\63\2\2"+
		"\u023e\u0240\5b\62\2\u023f\u023e\3\2\2\2\u023f\u0240\3\2\2\2\u0240\u0241"+
		"\3\2\2\2\u0241\u0242\5R*\2\u0242Q\3\2\2\2\u0243\u0247\5d\63\2\u0244\u0247"+
		"\5j\66\2\u0245\u0247\5l\67\2\u0246\u0243\3\2\2\2\u0246\u0244\3\2\2\2\u0246"+
		"\u0245\3\2\2\2\u0247S\3\2\2\2\u0248\u0249\7$\2\2\u0249\u024a\5b\62\2\u024a"+
		"\u024b\5l\67\2\u024bU\3\2\2\2\u024c\u024d\7\26\2\2\u024d\u024e\5b\62\2"+
		"\u024e\u024f\7\30\2\2\u024f\u0250\5b\62\2\u0250\u0251\5\\/\2\u0251\u0252"+
		"\5b\62\2\u0252\u0253\7\27\2\2\u0253\u0254\5b\62\2\u0254\u0255\5l\67\2"+
		"\u0255W\3\2\2\2\u0256\u0257\t\3\2\2\u0257Y\3\2\2\2\u0258\u025c\7D\2\2"+
		"\u0259\u025c\7.\2\2\u025a\u025c\5`\61\2\u025b\u0258\3\2\2\2\u025b\u0259"+
		"\3\2\2\2\u025b\u025a\3\2\2\2\u025c\u0263\3\2\2\2\u025d\u0262\7E\2\2\u025e"+
		"\u0262\7D\2\2\u025f\u0262\7.\2\2\u0260\u0262\5`\61\2\u0261\u025d\3\2\2"+
		"\2\u0261\u025e\3\2\2\2\u0261\u025f\3\2\2\2\u0261\u0260\3\2\2\2\u0262\u0265"+
		"\3\2\2\2\u0263\u0261\3\2\2\2\u0263\u0264\3\2\2\2\u0264[\3\2\2\2\u0265"+
		"\u0263\3\2\2\2\u0266\u026d\7D\2\2\u0267\u026d\7E\2\2\u0268\u026d\7.\2"+
		"\2\u0269\u026d\7-\2\2\u026a\u026d\7/\2\2\u026b\u026d\5`\61\2\u026c\u0266"+
		"\3\2\2\2\u026c\u0267\3\2\2\2\u026c\u0268\3\2\2\2\u026c\u0269\3\2\2\2\u026c"+
		"\u026a\3\2\2\2\u026c\u026b\3\2\2\2\u026d\u026e\3\2\2\2\u026e\u026c\3\2"+
		"\2\2\u026e\u026f\3\2\2\2\u026f]\3\2\2\2\u0270\u0275\7D\2\2\u0271\u0275"+
		"\7.\2\2\u0272\u0275\7-\2\2\u0273\u0275\5`\61\2\u0274\u0270\3\2\2\2\u0274"+
		"\u0271\3\2\2\2\u0274\u0272\3\2\2\2\u0274\u0273\3\2\2\2\u0275\u027d\3\2"+
		"\2\2\u0276\u027c\7E\2\2\u0277\u027c\7D\2\2\u0278\u027c\7.\2\2\u0279\u027c"+
		"\7-\2\2\u027a\u027c\5`\61\2\u027b\u0276\3\2\2\2\u027b\u0277\3\2\2\2\u027b"+
		"\u0278\3\2\2\2\u027b\u0279\3\2\2\2\u027b\u027a\3\2\2\2\u027c\u027f\3\2"+
		"\2\2\u027d\u027b\3\2\2\2\u027d\u027e\3\2\2\2\u027e_\3\2\2\2\u027f\u027d"+
		"\3\2\2\2\u0280\u0281\t\4\2\2\u0281a\3\2\2\2\u0282\u0284\t\5\2\2\u0283"+
		"\u0282\3\2\2\2\u0284\u0285\3\2\2\2\u0285\u0283\3\2\2\2\u0285\u0286\3\2"+
		"\2\2\u0286c\3\2\2\2\u0287\u0289\7-\2\2\u0288\u028a\5b\62\2\u0289\u0288"+
		"\3\2\2\2\u0289\u028a\3\2\2\2\u028a\u028c\3\2\2\2\u028b\u0287\3\2\2\2\u028b"+
		"\u028c\3\2\2\2\u028c\u028f\3\2\2\2\u028d\u0290\5f\64\2\u028e\u0290\5h"+
		"\65\2\u028f\u028d\3\2\2\2\u028f\u028e\3\2\2\2\u0290e\3\2\2\2\u0291\u0292"+
		"\7E\2\2\u0292g\3\2\2\2\u0293\u0295\7-\2\2\u0294\u0293\3\2\2\2\u0294\u0295"+
		"\3\2\2\2\u0295\u0296\3\2\2\2\u0296\u0297\7E\2\2\u0297\u0298\7/\2\2\u0298"+
		"\u0299\7E\2\2\u0299i\3\2\2\2\u029a\u029b\t\6\2\2\u029bk\3\2\2\2\u029c"+
		"\u029d\7\4\2\2\u029dm\3\2\2\2eoswy\u0088\u0090\u0095\u009f\u00a4\u00b2"+
		"\u00c4\u00c9\u00d3\u00dc\u00e0\u00e5\u00ea\u00ee\u00f3\u00f8\u00fc\u0101"+
		"\u0106\u010a\u010f\u0114\u0118\u011c\u0120\u0129\u012c\u0130\u0135\u0139"+
		"\u013f\u0143\u0146\u014b\u0152\u0156\u016c\u0171\u0175\u017b\u0180\u0189"+
		"\u0199\u019d\u01a3\u01a7\u01ac\u01af\u01b2\u01b8\u01bc\u01c0\u01c3\u01c6"+
		"\u01c9\u01cf\u01d4\u01da\u01df\u01e2\u01e9\u01eb\u01f1\u01f5\u01f9\u01fc"+
		"\u0200\u0206\u0209\u020c\u0210\u0213\u0216\u0222\u0226\u022a\u022f\u0232"+
		"\u0235\u023b\u023f\u0246\u025b\u0261\u0263\u026c\u026e\u0274\u027b\u027d"+
		"\u0285\u0289\u028b\u028f\u0294";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}