/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;
import SqlFilter.SqlInstruction;
import SqlInstructionFetcher.SqlCreateFetcher;
import SqlInstructionFetcher.SqlCreateTableFetcher;
import SqlInstructionFetcher.SqlInsertIntoFetcher;
import java.io.File;
import java.io.FileWriter;
import java.lang.Exception;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * This class is used to execute the instruction.
 * Which id design with the factory pattern.
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
    
    public void exeSql() throws Exception{
        Exception invalidException = new Exception("Invalid SQL Instruction");
        switch (sqlType){
            case CREATE:
                SqlCreateFetcher fetch = new SqlCreateFetcher(this.sqlIns);
                if(fetch.fetchClause().equals("TABLE")){
                    System.out.println("create table");
                    
                    /*
                     * tableFetcher is an object to 
                     * let you fetch the detail of the 
                     * CREATE TABLE instruction.
                     */
                    SqlCreateTableFetcher tableFetcher = new SqlCreateTableFetcher(fetch);
                    if (tableFetcher.judgeCorrect()) {
                        /*To subSeven:
                         * Add your implement of create table below
                         * by using tableFetcher.
                         */
                        
                        /*
                         * Below is an example of how to get the 
                         * columns in the CREATE TABLE instruction.
                         */
                        ArrayList list =  tableFetcher.getColums();
                        Iterator it = list.iterator();
                        while (it.hasNext()) {                            
                            Map map = (Map) it.next();
                            System.out.print("NAME:"+(String)map.get("Name"));
                            System.out.print("    TYPE:"+(String)map.get("Type"));
                            System.out.print("    Quantity:"+((Integer)map.get("Quantity")).toString());
                            if ((Boolean)map.get("PRIMARY")) {
                                System.out.println(" IS PRIMARY");
                            }else{
                                System.out.println();
                            }
                        }
                        /*
                         * END of example.
                         */
                        
                    }else{
                        throw invalidException;
                    }
                }
                
                break;
            case INSERT:
                System.out.println("insert");
                
                /*This is the object that let you access the information of the instruction.*/
                SqlInsertIntoFetcher insertFetcher = new SqlInsertIntoFetcher(this.sqlIns);
                if (insertFetcher.judgeCorrect()) {
                    /**
                     * To subSeven:
                     * Add your own code below by using insertFetcher.
                     * The correctness of the instruction has been judge, you
                     * don't need to worry about that.
                     * BTW, it is suggested to use iterator to access ArrayList.
                     * The usage way is much like the example above.
                     */
                    
                }else{
                    throw invalidException;
                }
                break;
            default:
                System.out.println("no");
                if(this.sqlIns.equals("")){
                    System.out.println("end");
                }
                break;
        }
    }
    
}
