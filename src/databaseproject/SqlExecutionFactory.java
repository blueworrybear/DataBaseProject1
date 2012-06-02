/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;
import DataStructure.SqlBTreeData;
import SqlFilter.SqlInstruction;
import SqlInstructionFetcher.SqlCreateFetcher;
import SqlInstructionFetcher.SqlCreateTableFetcher;
import SqlInstructionFetcher.SqlInsertIntoFetcher;
import SqlInstructionFetcher.SqlSelectFetcher;
import SqlManipulation.SqlCreateTableExec;
import SqlManipulation.SqlInsertTableExec;
import SqlManipulation.SqlSelectTableExec;
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
    
    public static queryDataRecord dataRecord = new queryDataRecord();
    
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
//                    System.out.println("create table");
                    
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
                        ArrayList list =  tableFetcher.getColumns();
                        Iterator it = list.iterator();
                      /*  while (it.hasNext()) {                            
                            Map map = (Map) it.next();
                            System.out.print("NAME:"+(String)map.get("Name"));
                            System.out.print("    TYPE:"+(String)map.get("Type"));
                            System.out.print("    Quantity:"+((Integer)map.get("Quantity")).toString());
                            if ((Boolean)map.get("PRIMARY")) {
                                System.out.println(" IS PRIMARY");
                            }else{
                                System.out.println();
                            }
                        }*/
                        
                        SqlCreateTableExec sqlExec = new SqlCreateTableExec(tableFetcher); //Initialize a SQL Executer
                        if(sqlExec.exec()){ //Start Crate Manipulation 
                            sqlExec.display();
                        }
                        
                        /*
                         * END of example.
                         */
                        
                    }else{
                        throw invalidException;
                    }
                }else{
                    throw invalidException;
                }
                
                break;
            case INSERT:
//                System.out.println("insert");
                
                /*This is the object that let you access the information of the instruction.*/
                SqlInsertIntoFetcher insertFetcher = new SqlInsertIntoFetcher(this.sqlIns);
                if (insertFetcher.judgeCorrect()) {
                    
                    SqlInsertTableExec sqlExec = new SqlInsertTableExec(insertFetcher);
                    sqlExec.exec();
//                    sqlExec.display();
                    
                }else{
                    throw invalidException;
                }
                break;
            case SELECT:
                
                SqlSelectFetcher selectFetcher = new SqlSelectFetcher(this.sqlIns);
                
                if(selectFetcher.judgeCorrect()){
                    
                    
                    SqlSelectTableExec sqlExec = new SqlSelectTableExec(selectFetcher);
                    
//                    if (sqlExec.if_where_is_not_null()) {
                        if(sqlExec.exec()){;
                            sqlExec.display();
                        }else{
//                            System.out.println("未知的嚴重錯誤。");
                        }
//                    }
                    sqlExec.exec();
                    sqlExec.display();
                    
                    
                }else{
                    throw invalidException;
                }
                
                break;
            default:
                throw invalidException;
//                if(this.sqlIns.equals("")){
//                    System.out.println("end");
//                }
//                break;
        }
    }
    
}
