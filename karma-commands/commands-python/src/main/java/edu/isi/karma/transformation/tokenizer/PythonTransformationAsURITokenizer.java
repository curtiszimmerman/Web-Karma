package edu.isi.karma.transformation.tokenizer;

import edu.isi.karma.controller.command.transformation.PythonTransformationCommand;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonTransformationAsURITokenizer {

	private static final String returnStatement = "return";
	private static final String getValue = "getValue\\(\\s*\\\"(\\w+)\\\"\\s*\\)";
	private static final String literal = "\\\"([$-_.+!*'()\\w]+)\\\"";
	private static List<String> acceptedTokens;
	private static final Map<String, Pattern> patterns;
	static {
		patterns = new HashMap<String, Pattern>();
		acceptedTokens = new LinkedList<String>();
		acceptedTokens.add(getValue);
		acceptedTokens.add(literal);;
		for(String acceptedToken : acceptedTokens)
		{
			Pattern p = Pattern.compile(acceptedToken);
			patterns.put(acceptedToken, p);
		}
		
	}
	public static List<PythonTransformationToken> tokenize(PythonTransformationCommand command)
	{
		return tokenize(command.getTransformationCode());
		
	}
	public static List<PythonTransformationToken> tokenize(String transformationCode)
	{	
		 List<PythonTransformationToken> tokens = new  LinkedList<PythonTransformationToken>();
		if(transformationCode == null || transformationCode.isEmpty())
		{	
			return tokens;
		}
		if(!transformationCode.contains(returnStatement) || transformationCode.trim().indexOf(returnStatement) != 0 )
		{
			return tokens;
		}
		String codeToParse =transformationCode.substring(transformationCode.indexOf(returnStatement) + returnStatement.length());
	
		StringTokenizer tokenizer = new StringTokenizer(codeToParse, "+");
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken().trim();
			
			Matcher getValueMatcher = patterns.get(getValue).matcher(token);
			if(getValueMatcher.matches())
			{
				tokens.add(new PythonTransformationColumnToken(getValueMatcher.group(1)));
				continue;
			}
			Matcher literalMatcher = patterns.get(literal).matcher(token);
			if(literalMatcher.matches())
			{
				tokens.add(new PythonTransformationStringToken(literalMatcher.group(1)));
				continue;
			}
			
			tokens.add(new PythonTransformationInvalidToken(token));
		}
		
		return tokens;
	}
}
