/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlInstructionFetcher;
import databaseproject.SqlInstrucInterpreter;
import databaseproject.SqlStatementType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bear
 */
public class SqlFetcher {
    
    String statement;
    
    public SqlFetcher(String instruc){
        this.statement = instruc;
    }
    
    public SqlStatementType fetchCommand(){
        return SqlInstrucInterpreter.interpreteStatementType(this.statement);
    }
    
    public String fetchCommandString(){
        String patternStr = "^[A-Z]+( INTO)?";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        boolean matchFound = matcher.find();
        String type = "Null";
        if(matchFound){
            type = matcher.group(0);
        }
        return type;
    }
    
    public boolean judgeCorrect(){
        return true;
    }
}
