package wumpus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class EntailmentChecker {
	public boolean TTEntails(LogicalExpression KB, LogicalExpression alpha) throws Exception {
		ArrayList<String> symbols = concatenate(extractSymbols(KB), extractSymbols(alpha));
		HashMap<String, Boolean> model = new HashMap<>();
		return TTCheckAll(KB, alpha, symbols, model);
	}
	
	private ArrayList<String> extractSymbols(LogicalExpression expression) {
		ArrayList<String> result = new ArrayList<>();
		if(expression.getUniqueSymbol() != null) {
			result.add(expression.getUniqueSymbol());
		} else {
			for(LogicalExpression subexpression : expression.getSubexpressions()) {
				result = concatenate(result, extractSymbols(subexpression));
			}
		}
		
		return result;
	}
	
	private boolean trueInKB(LogicalExpression KB, String symbol) {
		Vector<LogicalExpression> expressions = KB.getSubexpressions();
		
		for(LogicalExpression subexpression : expressions) {
			String currentSymbol = subexpression.getUniqueSymbol();
			
			if(currentSymbol != null && currentSymbol.equals(symbol)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean falseInKB(LogicalExpression KB, String symbol) {
		Vector<LogicalExpression> expressions = KB.getSubexpressions();
		
		for(LogicalExpression subexpression : expressions) {
			String currentConnective = subexpression.getConnective();
			
			if(currentConnective != null && currentConnective.equals("not")) {
				String currentSymbol = subexpression.getSubexpressions().get(0).getUniqueSymbol();
				if(currentSymbol != null && currentSymbol.equals(symbol)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private ArrayList<String> concatenate(ArrayList<String> symbols1, ArrayList<String> symbols2) {
		for(String symbol : symbols2) {
			if(!symbols1.contains(symbol)) {
				symbols1.add(symbol);
			}
		}
		
		return symbols1;
	}
	
	private boolean TTCheckAll(LogicalExpression KB, LogicalExpression alpha, ArrayList<String> symbols, HashMap<String, Boolean> model) throws Exception {
		if(symbols.isEmpty()) {
			if(PLTrue(KB, model)) {
				return PLTrue(alpha, model);
			} else {
				return true;
			}
		} else {
			String P = first(symbols);
			ArrayList<String> rest = rest(symbols);
			
			if(trueInKB(KB, P)) {
				return TTCheckAll(KB, alpha, rest, extend(P, true, model));
			} else if(falseInKB(KB, P)) {
				return TTCheckAll(KB, alpha, rest, extend(P, false, model));
			} else {
				return TTCheckAll(KB, alpha, rest, extend(P, true, model)) && TTCheckAll(KB, alpha, rest, extend(P, false, model));
			}
		}
	}
	
	private boolean PLTrue(LogicalExpression expression, HashMap<String, Boolean> model) throws Exception{
		String symbol = expression.getUniqueSymbol();
		String connective = expression.getConnective();
		
		if(symbol != null) {
			return model.get(symbol);
		} else if(connective.equals("and")) {
			for(LogicalExpression subexpression : expression.getSubexpressions()) {
				if(!PLTrue(subexpression, model)) {
					return false;
				}
			}
			
			return true;
		} else if(connective.equals("or")) {
			for(LogicalExpression subexpression : expression.getSubexpressions()) {
				if(PLTrue(subexpression, model)) {
					return true;
				}
			}
			
			return false;
		} else if(connective.equals("xor")) {
			int numTrue = 0;
			for(LogicalExpression subexpression : expression.getSubexpressions()) {
				if(PLTrue(subexpression, model)) {
					numTrue++;
					if(numTrue > 1) {
						return false;
					}
				}
			}
			
			return (numTrue == 1);
		} else if(connective.equals("if")) {
			LogicalExpression left = expression.getSubexpressions().get(0);
			LogicalExpression right = expression.getSubexpressions().get(1);
			
			if(PLTrue(left, model) && !PLTrue(right, model)) {
				return false;
			} else {
				return true;
			}
		} else if(connective.equals("iff")) {
			LogicalExpression left = expression.getSubexpressions().get(0);
			LogicalExpression right = expression.getSubexpressions().get(1);
			
			if(PLTrue(left, model) == PLTrue(right, model)) {
				return true;
			} else {
				return false;
			}
		} else if(connective.equals("not")) {
			LogicalExpression subexpression = expression.getSubexpressions().get(0);
			return !(PLTrue(subexpression, model));
		} else { //unsupported connective
			throw new Exception("Unsupported Connective!");
		}
	}
	
	private String first(ArrayList<String> symbols) {
		return symbols.get(0);
	}
	
	private ArrayList<String> rest(ArrayList<String> symbols) {
		symbols.remove(0);
		return symbols;
	}
	
	private HashMap<String, Boolean> extend(String symbol, boolean value, HashMap<String, Boolean> model) {
		model.put(symbol, value);
		return model;
	}
}
