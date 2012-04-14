/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bear
 */
public class SqlInstrucFilter extends SqlInstruction{
    
    
    SqlInstruction instruction;
    
    public SqlInstrucFilter(){
        
    }
    
    public SqlInstrucFilter(String ins){
        super(ins);
    }
    
    public void setFilter(SqlInstruction ins){
        this.instruction = ins;
    }
    
    @Override
    public String filter(){
        this.instrucString = instruction.filter();
        return this.instrucString;
    }
    
//    public static String filtWhiteSpace(String instruc){
//        String patternString = "\\s\\s+";
//        Pattern pattern = Pattern.compile(patternString);
//        Matcher matcher = pattern.matcher(instruc);
//        instruc = matcher.replaceAll(" ");
//
//        return instruc;
//    }
}
