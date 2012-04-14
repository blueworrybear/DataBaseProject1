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
public class SqlInstrucWhiteSpaceFilter extends SqlInstrucFilter{
    
    public SqlInstrucWhiteSpaceFilter(){
        
    }
    
    public SqlInstrucWhiteSpaceFilter(String ins){
        super(ins);
    }
    
    public String filter(){
        this.instrucString = instruction.filter();
        String patternString = "\\s\\s+";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(this.instrucString);
        this.instrucString = matcher.replaceAll(" ");
        return this.instrucString;
    }
}
