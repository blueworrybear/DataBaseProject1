/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlInstructionFetcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bear
 */
public class SqlCreateFetcher extends SqlFetcher{
    
    public SqlCreateFetcher(String instruc){
        super(instruc);
    }
    
    public String fetchClause(){
        String patternStr = this.fetchCommandString()+"\\s[A-Z]+";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        boolean matchFound = matcher.find();
        if(matchFound){
            String clause = matcher.group(0);
            clause = clause.replaceAll(this.fetchCommandString()+"\\s", "");
            return clause;
        }
        return null;
    }
}
