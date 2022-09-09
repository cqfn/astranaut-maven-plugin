Identifier <- $String$, $#$, $#$;

Expression <- BinaryExpression | Variable;
BinaryExpression <- Addition | Subtraction;
Variable <- Identifier;
Addition <- left@Expression, right@Expression;
Subtraction <- left@Expression, right@Expression;

js:

singleExpression(identifier(literal<#1>)) -> Variable(Identifier<#1>);
singleExpression(#1, literal<"+">, #2) -> Addition(#1, #2);

python:

This <- 0;

self -> This;

java:

BinaryExpr(#1, #2)<"+"> -> Addition(#1, #2);