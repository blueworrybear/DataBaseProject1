/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;

import DataStructure.SqlHashMapData;
import SqlInstructionFetcher.SqlSelectFetcher;
import databaseproject.SqlExecutionFactory;
import java.util.*;
import model.SelectColumn;
import model.SelectWhere;



/**
 *
 * @author ljybowser
 */
public class SqlSelectTableExec_HashTable {
    
    SqlSelectFetcher sqlFetcher;
    SqlHashMapData contentIndex;
    String booleanOperand;
    ArrayList<SelectWhere> whereStatements;
    Set answerSet;
    
    public SqlSelectTableExec_HashTable(SqlSelectFetcher _fetcher){
        
        sqlFetcher = _fetcher;
        contentIndex  = new SqlHashMapData();
        
        
        
    }
    
    public boolean exec(){
        
        
        booleanOperand = sqlFetcher.fetchBooleanFunction();
        whereStatements = sqlFetcher.fetchWhereExpressions();
        
        if(booleanOperand.compareTo("")==0){ //If only 1 where expression w/o AND|OR
            
            SelectWhere _whereClause = whereStatements.get(0);
            
            
            System.out.println(_whereClause.get_operand1_column()+" "+_whereClause.get_operand2_column()+" "+_whereClause.get_operand1_tableName()+" "+_whereClause.get_operand2_tableName()+" ");    
                
                
            if(_whereClause.get_operand2_tableName()==null ||_whereClause.get_operand2_tableName().compareTo(_whereClause.get_operand1_tableName())==0) {
                
                answerSet = contentIndex.getEntrySet(_whereClause.get_operand1_tableName(),_whereClause.get_operand1_column(),_whereClause.get_operand2_column());
                
            }   
            
            
        }else{
            
            SelectWhere _whereClause1 = whereStatements.get(0);
            SelectWhere _whereClause2= whereStatements.get(1);
            
            //System.out.println(_whereClause1.get_operand1_column()+" "+_whereClause1.get_operand2_column()+" "+_whereClause1.get_operand1_tableName()+" "+_whereClause1.get_operand2_tableName()+" ");
            //System.out.println(_whereClause2.get_operand1_column()+" "+_whereClause2.get_operand2_column()+" "+_whereClause2.get_operand1_tableName()+" "+_whereClause2.get_operand2_tableName()+" ");
            
            if(_whereClause1.get_operand2_tableName()==null ||_whereClause1.get_operand2_tableName().compareTo(_whereClause1.get_operand1_tableName())==0 &&
               _whereClause2.get_operand2_tableName()==null ||_whereClause2.get_operand2_tableName().compareTo(_whereClause2.get_operand1_tableName())==0     ) {
                
                if(booleanOperand.compareTo("AND")==0){
                    answerSet = SqlSetOperator.intersect(contentIndex.getEntrySet(_whereClause1.get_operand1_tableName(),_whereClause1.get_operand1_column(),_whereClause1.get_operand2_column()), 
                            contentIndex.getEntrySet(_whereClause2.get_operand1_tableName(),_whereClause2.get_operand1_column(),_whereClause2.get_operand2_column()));
                }else if(booleanOperand.compareTo("OR")==0){
                    answerSet = SqlSetOperator.union(contentIndex.getEntrySet(_whereClause1.get_operand1_tableName(),_whereClause1.get_operand1_column(),_whereClause1.get_operand2_column()), 
                            contentIndex.getEntrySet(_whereClause2.get_operand1_tableName(),_whereClause2.get_operand1_column(),_whereClause2.get_operand2_column()));
                }else{
                    System.err.println("Unsupported boolean operation!");
                    return false;
                }
                
            }
            
            
        }
        
        return true;
    }
    
    public void display(){
        
        ArrayList<SelectColumn> _selectedColumns = sqlFetcher.fetchColumns();
        ArrayList<String> _selectedColNames = new ArrayList<String>();
        ArrayList<String> _selectedTableNames = new ArrayList<String>();
        
        //System.out.println(_selectedColumns);
        
        //SqlExecutionFactory.dataRecord.getHashTable(booleanOperand)
        Iterator it = _selectedColumns.iterator();
        
        while(it.hasNext()){
            
            SelectColumn _currentSelectColumn =(SelectColumn)it.next(); 
            
            _selectedTableNames.add(_currentSelectColumn.getTable());
            
            if(_currentSelectColumn.getColumn().compareTo("*")==0){
                Iterator _it2 = answerSet.iterator();
                
                String _randomPK = (String)_it2.next();
                
                HashMap<String,Object> _map = (HashMap<String,Object>)SqlExecutionFactory.dataRecord.getHashTable(_currentSelectColumn.getTable());
                
                HashMap<String,Object> _randomTuple = (HashMap<String,Object>)_map.get(_randomPK);
                
                _selectedColNames.addAll(_randomTuple.keySet());
                
                break;
                
            }else{
                _selectedColNames.add(_currentSelectColumn.getColumn());
            }
        }
        
        System.out.println(_selectedColNames);
        
        it = answerSet.iterator();
        
        int i=0;
        
        while(it.hasNext()){
            
           HashMap<String,Object> _currentTable = (HashMap<String,Object>)SqlExecutionFactory.dataRecord.getHashTable(_selectedTableNames.get(i));
           HashMap<String,Object> _currentTuple = (HashMap<String,Object>)_currentTable.get((String)it.next());
           
           Iterator it2 = _selectedColNames.iterator();
           
           while(it2.hasNext()){
               System.out.print((String)_currentTuple.get((String)it2.next())+" ");
           }
            
           System.out.println("");
           //i++;
           
        }
        
        
    }
    
    
    
}
