// Generated from ca/waterloo/dsg/graphflow/grammar/Graphflow.g4 by ANTLR 4.7
package ca.waterloo.dsg.graphflow.grammar;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GraphflowParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GraphflowVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#graphflow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGraphflow(GraphflowParser.GraphflowContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(GraphflowParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery(GraphflowParser.QueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#matchQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchQuery(GraphflowParser.MatchQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#continuousMatchQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinuousMatchQuery(GraphflowParser.ContinuousMatchQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#explainMatchQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExplainMatchQuery(GraphflowParser.ExplainMatchQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#explainContinuousMatchQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExplainContinuousMatchQuery(GraphflowParser.ExplainContinuousMatchQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#createQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreateQuery(GraphflowParser.CreateQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#deleteQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeleteQuery(GraphflowParser.DeleteQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#shortestPathQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShortestPathQuery(GraphflowParser.ShortestPathQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#continuousShortestPathQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinuousShortestPathQuery(GraphflowParser.ContinuousShortestPathQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#durabilityQuery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDurabilityQuery(GraphflowParser.DurabilityQueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#matchPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMatchPattern(GraphflowParser.MatchPatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#deletePattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeletePattern(GraphflowParser.DeletePatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#createEdgePattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreateEdgePattern(GraphflowParser.CreateEdgePatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#createVertexPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreateVertexPattern(GraphflowParser.CreateVertexPatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#pathPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPathPattern(GraphflowParser.PathPatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#returnClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnClause(GraphflowParser.ReturnClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#aggregationPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregationPattern(GraphflowParser.AggregationPatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#aggregationFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregationFunction(GraphflowParser.AggregationFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#countStarPattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCountStarPattern(GraphflowParser.CountStarPatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#whereClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereClause(GraphflowParser.WhereClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#weightsClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeightsClause(GraphflowParser.WeightsClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#predicates}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicates(GraphflowParser.PredicatesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#predicate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPredicate(GraphflowParser.PredicateContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#operand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand(GraphflowParser.OperandContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#variableEdge}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableEdge(GraphflowParser.VariableEdgeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#digitsEdgeWithOptionalType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigitsEdgeWithOptionalType(GraphflowParser.DigitsEdgeWithOptionalTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#digitsEdgeWithTypeAndProperties}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigitsEdgeWithTypeAndProperties(GraphflowParser.DigitsEdgeWithTypeAndPropertiesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#digitsVertex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigitsVertex(GraphflowParser.DigitsVertexContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#digitsVertexWithTypeAndProperties}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigitsVertexWithTypeAndProperties(GraphflowParser.DigitsVertexWithTypeAndPropertiesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#variableVertex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableVertex(GraphflowParser.VariableVertexContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#edgeType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdgeType(GraphflowParser.EdgeTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#edgeTypeAndOptionalProperties}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdgeTypeAndOptionalProperties(GraphflowParser.EdgeTypeAndOptionalPropertiesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#edgeVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdgeVariable(GraphflowParser.EdgeVariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#variableWithProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableWithProperty(GraphflowParser.VariableWithPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#weight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeight(GraphflowParser.WeightContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(GraphflowParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#properties}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperties(GraphflowParser.PropertiesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty(GraphflowParser.PropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(GraphflowParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#fileSink}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFileSink(GraphflowParser.FileSinkContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#udfCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUdfCall(GraphflowParser.UdfCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperator(GraphflowParser.OperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#key}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKey(GraphflowParser.KeyContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#functionName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionName(GraphflowParser.FunctionNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(GraphflowParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyword(GraphflowParser.KeywordContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#whitespace}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhitespace(GraphflowParser.WhitespaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#numericLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumericLiteral(GraphflowParser.NumericLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#integerLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerLiteral(GraphflowParser.IntegerLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#doubleLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoubleLiteral(GraphflowParser.DoubleLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#booleanLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanLiteral(GraphflowParser.BooleanLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link GraphflowParser#stringLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(GraphflowParser.StringLiteralContext ctx);
}