/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject1;
import java.util.regex.*;
/**
 *
 * @author bear
 */

public class SqlExecutionFactory {
    
    Object dataBaseHandler;
    private SqlStatementType sqlType;
    private String sqlIns;
    
    public SqlExecutionFactory(){
        
    }
    
    public SqlExecutionFactory(Object dataBaseHandler){
        this.dataBaseHandler = dataBaseHandler;
    }
    
    public void setInstruction(String instruc)
    {
        sqlIns = instruc;
        sqlType = SqlInstrucInterpreter.interpreteStatementType(instruc);
    }
    
    public void exeSql(){
        switch (sqlType){
            case CREATE:
                System.out.println("create");
                break;
            case INSERT:
                System.out.println("insert");
                break;
            default:
                System.out.println("no");
                break;
        }
    }
    
}
