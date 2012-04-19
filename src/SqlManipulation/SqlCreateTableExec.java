/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;

import SqlInstructionFetcher.SqlCreateFetcher;
import SqlInstructionFetcher.SqlCreateTableFetcher;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author ljybowser
 */
public class SqlCreateTableExec{
    
    private SqlCreateTableFetcher sqlInfo;
    private ArrayList<Map<String,Object>> columnInfo;
    private Iterator it;
    private File saveFile;
    private String tableName;
    
    
    public SqlCreateTableExec(SqlCreateTableFetcher tableInfo){
        this.sqlInfo = tableInfo;
        this.columnInfo = tableInfo.getColumns();      
        tableName = sqlInfo.fetchTableName();
        saveFile = new File(tableName+".txt");
        
    }
    
   
    
    public void display(){
        it = columnInfo.iterator();
        System.out.println(sqlInfo.fetchTableName()+" Created!");
        Map map;
        
      
        while(it.hasNext()){
            map = (Map)it.next();
            if("INT".compareTo((String)map.get("Type"))!=0){
                System.out.print("The Column Named by "+(String)map.get("Name")+" with Type "+(String)map.get("Type")+" and Quantity "+(Integer)map.get("Quantity")+" ");
            }else{
                System.out.print("The Column Named by "+(String)map.get("Name")+" with Type "+(String)map.get("Type")+" ");   
            }
            
            if((Boolean)map.get("PRIMARY")){
                System.out.print("which is a Primary Key\n");
            }else{
                System.out.print("which is an Ordinary Column\n");
            }
            
            
        }
        
        
    }
    
    public boolean exec(){
        
        
        
        
        
        
        try{
            
            if(saveFile.exists()){
                System.out.println("ERROR: Table Existed!!!!");
                return false;
            }else{
            
                FileWriter fWriter = new FileWriter(saveFile);
            
                //fWriter.write(tableName+";\n");
                it = columnInfo.iterator();
            
                while(it.hasNext()){
                    Map map =(Map) it.next();
                    fWriter.write((String)map.get("Name")+":"+(String)map.get("Type")+":"+(Integer)map.get("Quantity")+":"+(Boolean)map.get("PRIMARY")+",\n");
                
                }
            
                fWriter.close();
            }
            
            
        }catch(IOException ioe){
            System.out.println("IO Exception Occured!");
            return false;
        }
       
        
        return true;
        
    }

   
    
    
}
