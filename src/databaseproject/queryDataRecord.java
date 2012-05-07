/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import SqlContentFileManipulation.FileScanner;
import SqlInstructionFetcher.SqlCreateTableFetcher;
import SqlInstructionFetcher.SqlInsertIntoFetcher;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 *
 * @author ljybowser
 */
public class queryDataRecord {
    
    //private HashMap<String,SqlCreateTableFetcher> sqlCreateMap;
    //private HashMap<String,SqlInsertIntoFetcher> sqlInsertMap;
    private HashMap<String, Map<String,Object>> sqlContentHashMap;
    
    public queryDataRecord(){
        
        //sqlCreateMap = new HashMap<String,SqlCreateTableFetcher>();
        //sqlInsertMap = new HashMap<String,SqlInsertIntoFetcher>();
        sqlContentHashMap = new HashMap<String, Map<String,Object>>();
        FileScanner catchFile = new FileScanner();
        
        Iterator it = catchFile.getTableNames().iterator();
        while(it.hasNext())
        {
            String tableName = (String)it.next();
            sqlContentHashMap.put(tableName, catchFile.getTableContent(tableName));
        }
        
    }
    
    /*void addSqlCreateTableFetcher(String tableName, SqlCreateTableFetcher sqlCreateInfo){
        
        sqlCreateMap.put(tableName,sqlCreateInfo);
        
    }*/
    public void addSqlContentHashMap(String tableName, Map<String,Object> map){
        sqlContentHashMap.put(tableName, map);
    }
    
    public Map<String,Object> getHashTable(String tableName){
        
        return sqlContentHashMap.get(tableName);
        
    }
    
    
}
