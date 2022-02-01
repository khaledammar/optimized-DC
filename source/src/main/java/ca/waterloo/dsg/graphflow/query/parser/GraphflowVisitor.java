package ca.waterloo.dsg.graphflow.query.parser;

import ca.waterloo.dsg.graphflow.exceptions.MalformedWhereClauseException;
import ca.waterloo.dsg.graphflow.grammar.GraphflowBaseVisitor;
import ca.waterloo.dsg.graphflow.grammar.GraphflowParser.*;
import ca.waterloo.dsg.graphflow.query.structuredquery.*;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryAggregation.AggregationFunction;
import ca.waterloo.dsg.graphflow.query.structuredquery.QueryPropertyPredicate.PredicateType;
import ca.waterloo.dsg.graphflow.query.structuredquery.StructuredQuery.QueryOperation;
import ca.waterloo.dsg.graphflow.util.DataType;
import ca.waterloo.dsg.graphflow.util.RuntimeTypeBasedComparator.ComparisonOperator;
import org.antlr.v4.runtime.misc.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the ANTLR4 methods used to traverse the parse tree.
 * Query structure is encapsulated within a {@code StructuredQuery} object.
 */
public class GraphflowVisitor extends GraphflowBaseVisitor<AbstractStructuredQuery> {

    @Override
    public AbstractStructuredQuery visitGraphflow(GraphflowContext ctx) {
        return visit(ctx.statement());
    }

    @Override
    public AbstractStructuredQuery visitMatchQuery(MatchQueryContext ctx) {
        StructuredQuery structuredQuery = (StructuredQuery) visit(ctx.matchPattern());
        structuredQuery.setQueryOperation(QueryOperation.MATCH);
        if (null != ctx.returnClause()) {
            visitReturnClauseAndAggregations(structuredQuery, ctx.returnClause());
        }
        if (null != ctx.whereClause()) {
            visitWhereClause(structuredQuery, ctx.whereClause());
        }
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitContinuousMatchQuery(ContinuousMatchQueryContext ctx) {
        StructuredQuery structuredQuery = (StructuredQuery) visit(ctx.matchPattern());
        structuredQuery.setQueryOperation(QueryOperation.CONTINUOUS_MATCH);
        if (null != ctx.whereClause()) {
            visitWhereClause(structuredQuery, ctx.whereClause());
        }
        if (null != ctx.udfCall()) {
            UdfCallContext udfCtx = ctx.udfCall();
            structuredQuery.setContinuousMatchAction(udfCtx.functionName().getText());
            structuredQuery.setContinuousMatchOutputLocation(getUnquotedString(udfCtx.stringLiteral().getText()));
        } else {
            FileSinkContext fileSinkCtx = ctx.fileSink();
            structuredQuery.setFilePath(getUnquotedString(fileSinkCtx.stringLiteral().getText()));
        }
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitExplainMatchQuery(ExplainMatchQueryContext ctx) {
        StructuredQuery structuredQuery = (StructuredQuery) visit(ctx.matchQuery());
        structuredQuery.setQueryOperation(QueryOperation.EXPLAIN);
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitExplainContinuousMatchQuery(ExplainContinuousMatchQueryContext ctx) {
        StructuredQuery structuredQuery = (StructuredQuery) visit(ctx.continuousMatchQuery());
        structuredQuery.setQueryOperation(QueryOperation.CONTINUOUS_EXPLAIN);
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitShortestPathQuery(ShortestPathQueryContext ctx) {
        StructuredQuery structuredQuery = new StructuredQuery();
        structuredQuery.setQueryOperation(QueryOperation.SHORTEST_PATH);
        structuredQuery.addRelation((QueryRelation) visit(ctx.pathPattern()));
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitContinuousShortestPathQuery(ContinuousShortestPathQueryContext ctx) {
        StructuredQuery structuredQuery = new StructuredQuery();
        structuredQuery.setQueryOperation(QueryOperation.CONTINUOUS_SHORTEST_PATH);
        structuredQuery.addRelation((QueryRelation) visit(ctx.shortestPathQuery().pathPattern()));
        if (null != ctx.weightsClause()) {
            visitWeightsClause(structuredQuery, ctx.weightsClause());
        }
        if (null != ctx.udfCall()) {
            /*TODO: handle ctx.udfCall() */
        } else if (null != ctx.fileSink()) {
            FileSinkContext fileSinkCtx = ctx.fileSink();
            structuredQuery.setFilePath(getUnquotedString(fileSinkCtx.stringLiteral().getText()));
        }
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitDurabilityQuery(DurabilityQueryContext ctx) {
        StructuredQuery structuredQuery = new StructuredQuery();
        if (null != ctx.LOAD()) {
            structuredQuery.setQueryOperation(QueryOperation.LOAD_GRAPH);
        } else {
            structuredQuery.setQueryOperation(QueryOperation.SAVE_GRAPH);
        }
        structuredQuery.setFilePath(getUnquotedString(ctx.stringLiteral().getText()));
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitMatchPattern(MatchPatternContext ctx) {
        StructuredQuery structuredQuery = new StructuredQuery();
        for (int i = 0; i < ctx.variableEdge().size(); i++) {
            visitVariableEdge(ctx.variableEdge(i), structuredQuery);
        }
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitDeletePattern(DeletePatternContext ctx) {
        StructuredQuery structuredQuery = new StructuredQuery();
        structuredQuery.setQueryOperation(QueryOperation.DELETE);
        for (int i = 0; i < ctx.digitsEdgeWithOptionalType().size(); i++) {
            structuredQuery.addRelation((QueryRelation) visit(ctx.digitsEdgeWithOptionalType(i)));
        }
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitCreateEdgePattern(CreateEdgePatternContext ctx) {
        StructuredQuery structuredQuery = new StructuredQuery();
        structuredQuery.setQueryOperation(QueryOperation.CREATE);
        for (int i = 0; i < ctx.digitsEdgeWithTypeAndProperties().size(); i++) {
            structuredQuery.addRelation((QueryRelation) visit(ctx.digitsEdgeWithTypeAndProperties(i)));
        }
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitCreateVertexPattern(CreateVertexPatternContext ctx) {
        StructuredQuery structuredQuery = new StructuredQuery();
        structuredQuery.setQueryOperation(QueryOperation.CREATE);
        for (int i = 0; i < ctx.digitsVertexWithTypeAndProperties().size(); i++) {
            structuredQuery.addVariable((QueryVariable) visit(ctx.digitsVertexWithTypeAndProperties(i)));
        }
        return structuredQuery;
    }

    @Override
    public AbstractStructuredQuery visitPathPattern(PathPatternContext ctx) {
        return new QueryRelation(new QueryVariable(ctx.Digits(0).getText()),
                new QueryVariable(ctx.Digits(1).getText()));
    }

    private void visitVariableEdge(VariableEdgeContext ctx, StructuredQuery structuredQuery) {
        QueryRelation queryRelation =
                new QueryRelation((QueryVariable) visitVariableVertex(structuredQuery, ctx.variableVertex(0)),
                        (QueryVariable) visitVariableVertex(structuredQuery, ctx.variableVertex(1)));
        if (null != ctx.edgeVariable()) {
            if (null != ctx.edgeVariable().variable()) {
                queryRelation.setRelationName(ctx.edgeVariable().variable().getText());
            }
            if (null != ctx.edgeVariable().type()) {
                queryRelation.setRelationType(ctx.edgeVariable().type().getText());
            }
            if (null != ctx.edgeVariable().properties()) {
                Map<String, Pair<String, String>> relationPropertyFilters =
                        parseProperties(ctx.edgeVariable().properties());
                QueryPropertyPredicate queryPropertyPredicate;
                for (String key : relationPropertyFilters.keySet()) {
                    queryPropertyPredicate = new QueryPropertyPredicate();
                    queryPropertyPredicate.setLeftOperand(new Pair<>(ctx.edgeVariable().variable().getText(), key));
                    queryPropertyPredicate.setLiteral(relationPropertyFilters.get(key).b);
                    queryPropertyPredicate.setComparisonOperator(ComparisonOperator.EQUALS);
                    queryPropertyPredicate.setPredicateType(PredicateType.PROPERTY_KEY_AND_LITERAL_OPERANDS);
                    structuredQuery.addQueryPropertyPredicate(queryPropertyPredicate);
                }
            }
        }
        structuredQuery.addRelation(queryRelation);
    }

    @Override
    public AbstractStructuredQuery visitDigitsEdgeWithOptionalType(DigitsEdgeWithOptionalTypeContext ctx) {
        QueryRelation queryRelation = new QueryRelation((QueryVariable) visit(ctx.digitsVertex(0)),
                (QueryVariable) visit(ctx.digitsVertex(1)));
        if (null != ctx.edgeType()) {
            queryRelation.setRelationType(ctx.edgeType().type().getText());
        }
        return queryRelation;
    }

    @Override
    public AbstractStructuredQuery visitDigitsEdgeWithTypeAndProperties(DigitsEdgeWithTypeAndPropertiesContext ctx) {
        QueryRelation queryRelation = new QueryRelation((QueryVariable) visit(ctx.digitsVertexWithTypeAndProperties(0)),
                (QueryVariable) visit(ctx.digitsVertexWithTypeAndProperties(1)));
        if (null != ctx.edgeTypeAndOptionalProperties().type()) {
            queryRelation.setRelationType(ctx.edgeTypeAndOptionalProperties().type().getText());
        }
        if (null != ctx.edgeTypeAndOptionalProperties().properties()) {
            queryRelation.setRelationProperties(parseProperties(ctx.edgeTypeAndOptionalProperties().properties()));
        }
        if (null != ctx.edgeTypeAndOptionalProperties().doubleLiteral()) {
            queryRelation.weight = Double.parseDouble(ctx.edgeTypeAndOptionalProperties().doubleLiteral().getText());
        }
        return queryRelation;
    }

    @Override
    public AbstractStructuredQuery visitDigitsVertex(DigitsVertexContext ctx) {
        return new QueryVariable(ctx.Digits().getText());
    }

    @Override
    public AbstractStructuredQuery visitDigitsVertexWithTypeAndProperties(
            DigitsVertexWithTypeAndPropertiesContext ctx) {
        QueryVariable queryVariable = new QueryVariable(ctx.Digits().getText());
        if (null != ctx.type()) {
            queryVariable.setVariableType(ctx.type().getText());
        }
        if (null != ctx.properties()) {
            queryVariable.setVariableProperties(parseProperties(ctx.properties()));
        }
        return queryVariable;
    }

    private void visitReturnClauseAndAggregations(StructuredQuery structuredQuery,
                                                  ReturnClauseContext returnClauseCtx) {
        for (VariableContext variableContext : returnClauseCtx.variable()) {
            structuredQuery.addReturnVariable(variableContext.getText());
        }
        for (VariableWithPropertyContext variableWithPropertyCtx : returnClauseCtx.variableWithProperty()) {
            structuredQuery.addReturnVariablePropertyPair(
                    new Pair<>(variableWithPropertyCtx.variable().getText(), variableWithPropertyCtx.key().getText()));
        }

        for (AggregationPatternContext aggregationCtx : returnClauseCtx.aggregationPattern()) {
            if (null != aggregationCtx.countStarPattern()) {
                structuredQuery.addQueryAggregation(QueryAggregation.COUNT_STAR);
                continue;
            }
            AggregationFunction aggregationFunction =
                    AggregationFunction.valueOf(aggregationCtx.aggregationFunction().getText().toUpperCase());
            if (null != aggregationCtx.variable()) {
                structuredQuery.addQueryAggregation(
                        new QueryAggregation(aggregationFunction, aggregationCtx.variable().getText()));
            } else {
                structuredQuery.addQueryAggregation(new QueryAggregation(aggregationFunction,
                        new Pair<>(aggregationCtx.variableWithProperty().variable().getText(),
                                aggregationCtx.variableWithProperty().key().getText())));
            }
        }
    }

    private void visitWeightsClause(StructuredQuery structuredQuery, WeightsClauseContext ctx) {
        WeightContext weightContext = ctx.weight();
        structuredQuery.setShortestPathWeight(weightContext.getText());
    }

    private void visitWhereClause(StructuredQuery structuredQuery, WhereClauseContext ctx) {
        for (int i = 0; i < ctx.predicates().predicate().size(); i++) {
            QueryPropertyPredicate queryPropertyPredicate = new QueryPropertyPredicate();
            OperandContext leftOperandCtx = ctx.predicates().predicate(i).operand(0);
            OperandContext rightOperandCtx = ctx.predicates().predicate(i).operand(1);

            if (null != leftOperandCtx.literal() && null != rightOperandCtx.literal()) {
                throw new MalformedWhereClauseException("Both operands of a where clause can't be" + " literals.");
            } else if (null != leftOperandCtx.variableWithProperty() &&
                    null != rightOperandCtx.variableWithProperty()) {
                queryPropertyPredicate.setLeftOperand(
                        new Pair<>(leftOperandCtx.variableWithProperty().variable().getText(),
                                leftOperandCtx.variableWithProperty().key().getText()));
                queryPropertyPredicate.setRightOperand(
                        new Pair<>(rightOperandCtx.variableWithProperty().variable().getText(),
                                rightOperandCtx.variableWithProperty().key().getText()));
            } else if (null != leftOperandCtx.variableWithProperty() && null != rightOperandCtx.literal()) {
                queryPropertyPredicate.setLeftOperand(
                        new Pair<>(leftOperandCtx.variableWithProperty().variable().getText(),
                                leftOperandCtx.variableWithProperty().key().getText()));
                queryPropertyPredicate.setLiteral(getLiteral(rightOperandCtx.literal()));
            } else {
                queryPropertyPredicate.setLeftOperand(
                        new Pair<>(rightOperandCtx.variableWithProperty().variable().getText(),
                                rightOperandCtx.variableWithProperty().key().getText()));
                queryPropertyPredicate.setLiteral(getLiteral(leftOperandCtx.literal()));
            }

            if (null == rightOperandCtx.literal() && null == leftOperandCtx.literal()) {
                queryPropertyPredicate.setPredicateType(PredicateType.TWO_PROPERTY_KEY_OPERANDS);
            } else {
                queryPropertyPredicate.setPredicateType(PredicateType.PROPERTY_KEY_AND_LITERAL_OPERANDS);
            }

            queryPropertyPredicate.setComparisonOperator(ComparisonOperator
                    .mapStringToComparisonOperator(ctx.predicates().predicate(i).operator().getText()));
            if (null != leftOperandCtx.literal()) {
                queryPropertyPredicate.invertComparisonOperator();
            }

            structuredQuery.addQueryPropertyPredicate(queryPropertyPredicate);
        }
    }

    private AbstractStructuredQuery visitVariableVertex(StructuredQuery structuredQuery, VariableVertexContext ctx) {
        QueryVariable queryVariable = new QueryVariable(ctx.variable().getText());
        if (null != ctx.type()) {
            queryVariable.setVariableType(ctx.type().variable().getText());
        }
        if (null != ctx.properties()) {
            Map<String, Pair<String, String>> variablePropertyFilters = parseProperties(ctx.properties());
            QueryPropertyPredicate queryPropertyPredicate;
            for (String key : variablePropertyFilters.keySet()) {
                queryPropertyPredicate = new QueryPropertyPredicate();
                queryPropertyPredicate.setLeftOperand(new Pair<>(ctx.variable().getText(), key));
                queryPropertyPredicate.setLiteral(variablePropertyFilters.get(key).b);
                queryPropertyPredicate.setComparisonOperator(ComparisonOperator.EQUALS);
                queryPropertyPredicate.setPredicateType(PredicateType.PROPERTY_KEY_AND_LITERAL_OPERANDS);
                structuredQuery.addQueryPropertyPredicate(queryPropertyPredicate);
            }
        }
        return queryVariable;
    }

    private Map<String, Pair<String, String>> parseProperties(PropertiesContext ctx) {
        Map<String, Pair<String, String>> properties = new HashMap<>();
        for (int i = 0; i < ctx.property().size(); ++i) {
            String dataType = parsePropertyDataType(ctx.property(i).literal());
            String value = getLiteral(ctx.property(i).literal());
            DataType.assertValueCanBeCastToDataType(dataType, value);
            properties.put(ctx.property(i).key().getText(), new Pair<>(dataType, value));
        }
        return properties;
    }

    private String parsePropertyDataType(LiteralContext literalCtx) {
        if (null != literalCtx.stringLiteral()) {
            return DataType.STRING.toString();
        } else if (null != literalCtx.booleanLiteral()) {
            return DataType.BOOLEAN.toString();
        } else if (null != literalCtx.numericLiteral().integerLiteral()) {
            return DataType.INTEGER.toString();
        } else {
            return DataType.DOUBLE.toString();
        }
    }

    private String getLiteral(LiteralContext ctx) {
        if (null != ctx.stringLiteral()) {
            return getUnquotedString(ctx.getText());
        } else if (null != ctx.booleanLiteral()) {
            return ctx.getText();
        } else {
            String numericalLiteral;
            if (null != ctx.numericLiteral().integerLiteral()) {
                numericalLiteral = ctx.numericLiteral().integerLiteral().getText();
            } else {
                numericalLiteral = ctx.numericLiteral().doubleLiteral().getText();
            }
            if (null != ctx.numericLiteral().DASH()) {
                numericalLiteral = "-" + numericalLiteral;
            }
            return numericalLiteral;
        }
    }

    private String getUnquotedString(String quotedString) {
        return quotedString.substring(1, quotedString.length() - 1);
    }
}
