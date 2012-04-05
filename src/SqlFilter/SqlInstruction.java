/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlFilter;

/**
 *
 * @author bear
 */
public class SqlInstruction {
    
    String instrucString;
    
    public SqlInstruction(){
        
    }
    
    public SqlInstruction(String ins){
        this.instrucString = ins;
    }
    
    
    public String filter(){
        return instrucString;
    }
}
