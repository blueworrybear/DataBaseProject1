/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject1;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                String sqlArg = sqlIns.substring(sqlIns.indexOf("(")+1,sqlIns.length()-3); //Fetch Arguments of command CREATE
                System.out.println(sqlArg);
                String[] sqlArgSplitted = sqlArg.split(",");  //Split the arguments
                String[] tableName = sqlIns.split(" ");
                File saveFile = new File("src/Resource/"+tableName[2]+".txt");
                try {
                     FileWriter columnNameWriter = new FileWriter(saveFile);
                     columnNameWriter.write(sqlArg);
                     columnNameWriter.close();
                    } catch (IOException ex) {
                     Logger.getLogger(SqlExecutionFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                
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
