/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bear
 */
public class SqlInstrucInterpreter {

    public static SqlStatementType interpreteStatementType(String ins){
        SqlStatementType sqlType = SqlStatementType.NULL;
        String patternStr = "^[A-Z]+( INTO)?";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(ins.toUpperCase());
        boolean matchFound = matcher.find();
        if(matchFound){
            String type = matcher.group(0);
            if(type.equals("CREATE")){
                sqlType = SqlStatementType.CREATE;
            }else if(type.equals("INSERT INTO")){
                sqlType = SqlStatementType.INSERT;
            }else if(type.equals("SELECT")){
                sqlType = SqlStatementType.SELECT;
            }
        }
        return sqlType;
    }
}
