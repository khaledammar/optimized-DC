// Generated from ca/waterloo/dsg/graphflow/grammar/Graphflow.g4 by ANTLR 4.7
package ca.waterloo.dsg.graphflow.grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GraphflowParser}.
 */
public interface GraphflowListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#graphflow}.
	 * @param ctx the parse tree
	 */
	void enterGraphflow(GraphflowParser.GraphflowContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#graphflow}.
	 * @param ctx the parse tree
	 */
	void exitGraphflow(GraphflowParser.GraphflowContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(GraphflowParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(GraphflowParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#query}.
	 * @param ctx the parse tree
	 */
	void enterQuery(GraphflowParser.QueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#query}.
	 * @param ctx the parse tree
	 */
	void exitQuery(GraphflowParser.QueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#matchQuery}.
	 * @param ctx the parse tree
	 */
	void enterMatchQuery(GraphflowParser.MatchQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#matchQuery}.
	 * @param ctx the parse tree
	 */
	void exitMatchQuery(GraphflowParser.MatchQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#continuousMatchQuery}.
	 * @param ctx the parse tree
	 */
	void enterContinuousMatchQuery(GraphflowParser.ContinuousMatchQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#continuousMatchQuery}.
	 * @param ctx the parse tree
	 */
	void exitContinuousMatchQuery(GraphflowParser.ContinuousMatchQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#explainMatchQuery}.
	 * @param ctx the parse tree
	 */
	void enterExplainMatchQuery(GraphflowParser.ExplainMatchQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#explainMatchQuery}.
	 * @param ctx the parse tree
	 */
	void exitExplainMatchQuery(GraphflowParser.ExplainMatchQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#explainContinuousMatchQuery}.
	 * @param ctx the parse tree
	 */
	void enterExplainContinuousMatchQuery(GraphflowParser.ExplainContinuousMatchQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#explainContinuousMatchQuery}.
	 * @param ctx the parse tree
	 */
	void exitExplainContinuousMatchQuery(GraphflowParser.ExplainContinuousMatchQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#createQuery}.
	 * @param ctx the parse tree
	 */
	void enterCreateQuery(GraphflowParser.CreateQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#createQuery}.
	 * @param ctx the parse tree
	 */
	void exitCreateQuery(GraphflowParser.CreateQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#deleteQuery}.
	 * @param ctx the parse tree
	 */
	void enterDeleteQuery(GraphflowParser.DeleteQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#deleteQuery}.
	 * @param ctx the parse tree
	 */
	void exitDeleteQuery(GraphflowParser.DeleteQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#shortestPathQuery}.
	 * @param ctx the parse tree
	 */
	void enterShortestPathQuery(GraphflowParser.ShortestPathQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#shortestPathQuery}.
	 * @param ctx the parse tree
	 */
	void exitShortestPathQuery(GraphflowParser.ShortestPathQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#continuousShortestPathQuery}.
	 * @param ctx the parse tree
	 */
	void enterContinuousShortestPathQuery(GraphflowParser.ContinuousShortestPathQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#continuousShortestPathQuery}.
	 * @param ctx the parse tree
	 */
	void exitContinuousShortestPathQuery(GraphflowParser.ContinuousShortestPathQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#durabilityQuery}.
	 * @param ctx the parse tree
	 */
	void enterDurabilityQuery(GraphflowParser.DurabilityQueryContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#durabilityQuery}.
	 * @param ctx the parse tree
	 */
	void exitDurabilityQuery(GraphflowParser.DurabilityQueryContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#matchPattern}.
	 * @param ctx the parse tree
	 */
	void enterMatchPattern(GraphflowParser.MatchPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#matchPattern}.
	 * @param ctx the parse tree
	 */
	void exitMatchPattern(GraphflowParser.MatchPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#deletePattern}.
	 * @param ctx the parse tree
	 */
	void enterDeletePattern(GraphflowParser.DeletePatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#deletePattern}.
	 * @param ctx the parse tree
	 */
	void exitDeletePattern(GraphflowParser.DeletePatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#createEdgePattern}.
	 * @param ctx the parse tree
	 */
	void enterCreateEdgePattern(GraphflowParser.CreateEdgePatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#createEdgePattern}.
	 * @param ctx the parse tree
	 */
	void exitCreateEdgePattern(GraphflowParser.CreateEdgePatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#createVertexPattern}.
	 * @param ctx the parse tree
	 */
	void enterCreateVertexPattern(GraphflowParser.CreateVertexPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#createVertexPattern}.
	 * @param ctx the parse tree
	 */
	void exitCreateVertexPattern(GraphflowParser.CreateVertexPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#pathPattern}.
	 * @param ctx the parse tree
	 */
	void enterPathPattern(GraphflowParser.PathPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#pathPattern}.
	 * @param ctx the parse tree
	 */
	void exitPathPattern(GraphflowParser.PathPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#returnClause}.
	 * @param ctx the parse tree
	 */
	void enterReturnClause(GraphflowParser.ReturnClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#returnClause}.
	 * @param ctx the parse tree
	 */
	void exitReturnClause(GraphflowParser.ReturnClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#aggregationPattern}.
	 * @param ctx the parse tree
	 */
	void enterAggregationPattern(GraphflowParser.AggregationPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#aggregationPattern}.
	 * @param ctx the parse tree
	 */
	void exitAggregationPattern(GraphflowParser.AggregationPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#aggregationFunction}.
	 * @param ctx the parse tree
	 */
	void enterAggregationFunction(GraphflowParser.AggregationFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#aggregationFunction}.
	 * @param ctx the parse tree
	 */
	void exitAggregationFunction(GraphflowParser.AggregationFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#countStarPattern}.
	 * @param ctx the parse tree
	 */
	void enterCountStarPattern(GraphflowParser.CountStarPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#countStarPattern}.
	 * @param ctx the parse tree
	 */
	void exitCountStarPattern(GraphflowParser.CountStarPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void enterWhereClause(GraphflowParser.WhereClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void exitWhereClause(GraphflowParser.WhereClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#weightsClause}.
	 * @param ctx the parse tree
	 */
	void enterWeightsClause(GraphflowParser.WeightsClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#weightsClause}.
	 * @param ctx the parse tree
	 */
	void exitWeightsClause(GraphflowParser.WeightsClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#predicates}.
	 * @param ctx the parse tree
	 */
	void enterPredicates(GraphflowParser.PredicatesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#predicates}.
	 * @param ctx the parse tree
	 */
	void exitPredicates(GraphflowParser.PredicatesContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(GraphflowParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(GraphflowParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#operand}.
	 * @param ctx the parse tree
	 */
	void enterOperand(GraphflowParser.OperandContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#operand}.
	 * @param ctx the parse tree
	 */
	void exitOperand(GraphflowParser.OperandContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#variableEdge}.
	 * @param ctx the parse tree
	 */
	void enterVariableEdge(GraphflowParser.VariableEdgeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#variableEdge}.
	 * @param ctx the parse tree
	 */
	void exitVariableEdge(GraphflowParser.VariableEdgeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#digitsEdgeWithOptionalType}.
	 * @param ctx the parse tree
	 */
	void enterDigitsEdgeWithOptionalType(GraphflowParser.DigitsEdgeWithOptionalTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#digitsEdgeWithOptionalType}.
	 * @param ctx the parse tree
	 */
	void exitDigitsEdgeWithOptionalType(GraphflowParser.DigitsEdgeWithOptionalTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#digitsEdgeWithTypeAndProperties}.
	 * @param ctx the parse tree
	 */
	void enterDigitsEdgeWithTypeAndProperties(GraphflowParser.DigitsEdgeWithTypeAndPropertiesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#digitsEdgeWithTypeAndProperties}.
	 * @param ctx the parse tree
	 */
	void exitDigitsEdgeWithTypeAndProperties(GraphflowParser.DigitsEdgeWithTypeAndPropertiesContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#digitsVertex}.
	 * @param ctx the parse tree
	 */
	void enterDigitsVertex(GraphflowParser.DigitsVertexContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#digitsVertex}.
	 * @param ctx the parse tree
	 */
	void exitDigitsVertex(GraphflowParser.DigitsVertexContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#digitsVertexWithTypeAndProperties}.
	 * @param ctx the parse tree
	 */
	void enterDigitsVertexWithTypeAndProperties(GraphflowParser.DigitsVertexWithTypeAndPropertiesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#digitsVertexWithTypeAndProperties}.
	 * @param ctx the parse tree
	 */
	void exitDigitsVertexWithTypeAndProperties(GraphflowParser.DigitsVertexWithTypeAndPropertiesContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#variableVertex}.
	 * @param ctx the parse tree
	 */
	void enterVariableVertex(GraphflowParser.VariableVertexContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#variableVertex}.
	 * @param ctx the parse tree
	 */
	void exitVariableVertex(GraphflowParser.VariableVertexContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#edgeType}.
	 * @param ctx the parse tree
	 */
	void enterEdgeType(GraphflowParser.EdgeTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#edgeType}.
	 * @param ctx the parse tree
	 */
	void exitEdgeType(GraphflowParser.EdgeTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#edgeTypeAndOptionalProperties}.
	 * @param ctx the parse tree
	 */
	void enterEdgeTypeAndOptionalProperties(GraphflowParser.EdgeTypeAndOptionalPropertiesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#edgeTypeAndOptionalProperties}.
	 * @param ctx the parse tree
	 */
	void exitEdgeTypeAndOptionalProperties(GraphflowParser.EdgeTypeAndOptionalPropertiesContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#edgeVariable}.
	 * @param ctx the parse tree
	 */
	void enterEdgeVariable(GraphflowParser.EdgeVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#edgeVariable}.
	 * @param ctx the parse tree
	 */
	void exitEdgeVariable(GraphflowParser.EdgeVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#variableWithProperty}.
	 * @param ctx the parse tree
	 */
	void enterVariableWithProperty(GraphflowParser.VariableWithPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#variableWithProperty}.
	 * @param ctx the parse tree
	 */
	void exitVariableWithProperty(GraphflowParser.VariableWithPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(GraphflowParser.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(GraphflowParser.WeightContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(GraphflowParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(GraphflowParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#properties}.
	 * @param ctx the parse tree
	 */
	void enterProperties(GraphflowParser.PropertiesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#properties}.
	 * @param ctx the parse tree
	 */
	void exitProperties(GraphflowParser.PropertiesContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#property}.
	 * @param ctx the parse tree
	 */
	void enterProperty(GraphflowParser.PropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#property}.
	 * @param ctx the parse tree
	 */
	void exitProperty(GraphflowParser.PropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(GraphflowParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(GraphflowParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#fileSink}.
	 * @param ctx the parse tree
	 */
	void enterFileSink(GraphflowParser.FileSinkContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#fileSink}.
	 * @param ctx the parse tree
	 */
	void exitFileSink(GraphflowParser.FileSinkContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#udfCall}.
	 * @param ctx the parse tree
	 */
	void enterUdfCall(GraphflowParser.UdfCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#udfCall}.
	 * @param ctx the parse tree
	 */
	void exitUdfCall(GraphflowParser.UdfCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#operator}.
	 * @param ctx the parse tree
	 */
	void enterOperator(GraphflowParser.OperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#operator}.
	 * @param ctx the parse tree
	 */
	void exitOperator(GraphflowParser.OperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#key}.
	 * @param ctx the parse tree
	 */
	void enterKey(GraphflowParser.KeyContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#key}.
	 * @param ctx the parse tree
	 */
	void exitKey(GraphflowParser.KeyContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#functionName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionName(GraphflowParser.FunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#functionName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionName(GraphflowParser.FunctionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(GraphflowParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(GraphflowParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#keyword}.
	 * @param ctx the parse tree
	 */
	void enterKeyword(GraphflowParser.KeywordContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#keyword}.
	 * @param ctx the parse tree
	 */
	void exitKeyword(GraphflowParser.KeywordContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#whitespace}.
	 * @param ctx the parse tree
	 */
	void enterWhitespace(GraphflowParser.WhitespaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#whitespace}.
	 * @param ctx the parse tree
	 */
	void exitWhitespace(GraphflowParser.WhitespaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void enterNumericLiteral(GraphflowParser.NumericLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#numericLiteral}.
	 * @param ctx the parse tree
	 */
	void exitNumericLiteral(GraphflowParser.NumericLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#integerLiteral}.
	 * @param ctx the parse tree
	 */
	void enterIntegerLiteral(GraphflowParser.IntegerLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#integerLiteral}.
	 * @param ctx the parse tree
	 */
	void exitIntegerLiteral(GraphflowParser.IntegerLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#doubleLiteral}.
	 * @param ctx the parse tree
	 */
	void enterDoubleLiteral(GraphflowParser.DoubleLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#doubleLiteral}.
	 * @param ctx the parse tree
	 */
	void exitDoubleLiteral(GraphflowParser.DoubleLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(GraphflowParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#booleanLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(GraphflowParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link GraphflowParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(GraphflowParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link GraphflowParser#stringLiteral}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(GraphflowParser.StringLiteralContext ctx);
}