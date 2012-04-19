/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import SqlInstructionFetcher.SqlCreateTableFetcher;
import SqlInstructionFetcher.SqlInsertIntoFetcher;
import java.util.HashMap;


/**
 *
 * @author ljybowser
 */
public class queryDataRecord {
    
    //private HashMap<String,SqlCreateTableFetcher> sqlCreateMap;
    //private HashMap<String,SqlInsertIntoFetcher> sqlInsertMap;
    private HashMap<String,HashMap<String,Integer>> sqlContentHashMap;
    
    public queryDataRecord(){
        
        //sqlCreateMap = new HashMap<String,SqlCreateTableFetcher>();
        //sqlInsertMap = new HashMap<String,SqlInsertIntoFetcher>();
        sqlContentHashMap = new HashMap<String,HashMap<String,Integer>>();
        
    }
    
    /*void addSqlCreateTableFetcher(String tableName, SqlCreateTableFetcher sqlCreateInfo){
        
        sqlCreateMap.put(tableName,sqlCreateInfo);
        
    }*/
    public void addSqlContentHashMap(String tableName, HashMap<String,Integer> map){
        sqlContentHashMap.put(tableName, map);
    }
    
    public HashMap<String,Integer>getHashTable(String tableName){
        
        return sqlContentHashMap.get(tableName);
        
    }
    
}
