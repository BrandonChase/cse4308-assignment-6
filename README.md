# CSE 4308 Assignment 6

### Name: Brandon Chase
## INTRODUCTION
Performs entailment checking for a wumpus world.

## COMPILATION
From root directory, `javac -classpath ./wumpus ./wumpus/*.java`

## USAGE
`java wumpus/CheckTrueFalse [wumpus_rules] [knowledge_base] [alpha]`
## CODE STRUCTURE: 
* CheckTrueFalse.java: class that contains main(). Reads file rules and uses EntailmentChecker to perform entailment logic.
* LogicalExpression.java: class that handles representing expressions. Used by all other classes.
* EntailmentChecker.java: contains logic for performing TT-Entails? on two logical expressions and all needed functions.
