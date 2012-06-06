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
    ArrayList<String> selTableNames = new ArrayList<String>();
    ArrayList<String> selTablesWithoutDup = new ArrayList<String>();
    ArrayList<SelectWhere> whereStatements;
    ArrayList<ArrayList<String>>answerArray;
    ArrayList<String>answerList1 = new ArrayList<String>();
    ArrayList<String>answerList2 = new ArrayList<String>();
    Set answerSet ;
    Set answerSet2;
    
    public SqlSelectTableExec_HashTable(SqlSelectFetcher _fetcher){
        
        sqlFetcher = _fetcher;
        contentIndex  = new SqlHashMapData();
        
        
        
    }
    
    public boolean exec(){
        
        
        booleanOperand = sqlFetcher.fetchBooleanFunction();
        whereStatements = sqlFetcher.fetchWhereExpressions();
        
        ArrayList<SelectColumn> _selCol = sqlFetcher.fetchColumns();
        
        Iterator _it = _selCol.iterator();
        
        while(_it.hasNext()){
            
            SelectColumn _tmp = (SelectColumn)_it.next();
            
            selTableNames.add(_tmp.getTable());
            
        }
        
        HashSet<Object> _tmpSet = new HashSet<Object>();
        
        _it = selTableNames.iterator();
        
        while(_it.hasNext()){
            
            String _str = (String)_it.next();
            
            if(_tmpSet.add(_str)){
                selTablesWithoutDup.add(_str);
            }
            
        }
        
        
        
        if(booleanOperand.compareTo("")==0){ //If only 1 where expression w/o AND|OR
            
            SelectWhere _whereClause = whereStatements.get(0);
            
            
            //System.out.println(_whereClause.get_operand1_column()+" "+_whereClause.get_operand2_column()+" "+_whereClause.get_operand1_tableName()+" "+_whereClause.get_operand2_tableName()+" ");    
                
                
            if(_whereClause.get_operand2_tableName()==null ||_whereClause.get_operand2_tableName().compareTo(_whereClause.get_operand1_tableName())==0) {
                
                answerSet = contentIndex.getEntrySet(_whereClause.get_operand1_tableName(),_whereClause.get_operand1_column(),_whereClause.get_operand2_column());
                answerList1.addAll(answerSet);
                
                
            }else if(_whereClause.get_operand2_tableName().compareTo(_whereClause.get_operand1_tableName())!=0){
                
                
                answerArray = contentIndex.getJoinEntrySet(_whereClause.get_operand1_tableName(),_whereClause.get_operand2_tableName(),_whereClause.get_operand1_column(),_whereClause.get_operand2_column());
                answerList1 =  answerArray.get(0);
                answerList2 = answerArray.get(1);
                
                
                
            }   
            
            
        }else{ //If 2 where expression with AND/OR
            
            SelectWhere _whereClause1 = whereStatements.get(0);
            SelectWhere _whereClause2= whereStatements.get(1);
            
            //System.out.println(_whereClause1.get_operand1_column()+" "+_whereClause1.get_operand2_column()+" "+_whereClause1.get_operand1_tableName()+" "+_whereClause1.get_operand2_tableName()+" ");
            //System.out.println(_whereClause2.get_operand1_column()+" "+_whereClause2.get_operand2_column()+" "+_whereClause2.get_operand1_tableName()+" "+_whereClause2.get_operand2_tableName()+" ");
            
            if(_whereClause1.get_operand2_tableName()==null ||_whereClause1.get_operand2_tableName().compareTo(_whereClause1.get_operand1_tableName())==0 &&
               _whereClause2.get_operand2_tableName()==null ||_whereClause2.get_operand2_tableName().compareTo(_whereClause2.get_operand1_tableName())==0     ) {
                
                if(booleanOperand.compareTo("AND")==0){
                    answerSet = SqlSetOperator.intersect(contentIndex.getEntrySet(_whereClause1.get_operand1_tableName(),_whereClause1.get_operand1_column(),_whereClause1.get_operand2_column()), 
                            contentIndex.getEntrySet(_whereClause2.get_operand1_tableName(),_whereClause2.get_operand1_column(),_whereClause2.get_operand2_column()));
                    answerList1.addAll(answerSet);
                }else if(booleanOperand.compareTo("OR")==0){
                    answerSet = SqlSetOperator.union(contentIndex.getEntrySet(_whereClause1.get_operand1_tableName(),_whereClause1.get_operand1_column(),_whereClause1.get_operand2_column()), 
                            contentIndex.getEntrySet(_whereClause2.get_operand1_tableName(),_whereClause2.get_operand1_column(),_whereClause2.get_operand2_column()));
                    answerList1.addAll(answerSet);
                }else{
                    System.err.println("Unsupported boolean operation!");
                    return false;
                }
                
            }else if(_whereClause1.get_operand2_tableName().compareTo(_whereClause1.get_operand1_tableName())!=0 &&
                     _whereClause2.get_operand2_tableName().compareTo(_whereClause2.get_operand1_tableName())!=0){
                
                
                ArrayList<ArrayList<String>> answerArray1 = contentIndex.getJoinEntrySet(_whereClause1.get_operand1_tableName(),_whereClause1.get_operand2_tableName(),_whereClause1.get_operand1_column(),_whereClause1.get_operand2_column());
                ArrayList<ArrayList<String>> answerArray2 = contentIndex.getJoinEntrySet(_whereClause2.get_operand1_tableName(),_whereClause2.get_operand2_tableName(),_whereClause2.get_operand1_column(),_whereClause2.get_operand2_column());
                
                if(booleanOperand.compareTo("AND")==0){
                    answerList1 = SqlSetOperator.intersect(answerArray1.get(0), answerArray2.get(0));
                    answerList2 = SqlSetOperator.intersect(answerArray1.get(1), answerArray2.get(1));
                }else if(booleanOperand.compareTo("OR")==0){
                    answerList1 = SqlSetOperator.union(answerArray1.get(0), answerArray2.get(0));
                    answerList2 = SqlSetOperator.union(answerArray1.get(1), answerArray2.get(1));
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
        
        int tableNumber =0;
        
        
        Iterator _it = _selectedColumns.iterator(); 
        
        while(_it.hasNext()){
            
            SelectColumn _currentSelectColumn = (SelectColumn)_it.next();
            
            _selectedTableNames.add(_currentSelectColumn.getTable());
            
            
            
            if(_currentSelectColumn.getColumn().compareTo("*")==0){
                
                Iterator _it2;
                
                if(_currentSelectColumn.getTable().compareTo(selTablesWithoutDup.get(0))==0){
                    _it2 = answerList1.iterator();
                    //System.out.println("GTA3");
                }else{
                    _it2 = answerList2.iterator();
                    //System.out.println("GTA4");
                }
                //System.out.println("GTA");
                String _randomPK = "";
                
                if(_it2.hasNext())
                    _randomPK = (String)_it2.next();
                else
                    continue;
                
                
                HashMap<String,Object> _map = (HashMap<String,Object>)SqlExecutionFactory.dataRecord.getHashTable(_currentSelectColumn.getTable());
                
                HashMap<String,Object> _randomTuple = (HashMap<String,Object>)_map.get(_randomPK);
                
                _selectedColNames.addAll(_randomTuple.keySet());
                
                for(int i=0;i<_randomTuple.keySet().size()-1;i++){
                    _selectedTableNames.add(_currentSelectColumn.getTable());
                }
                if(selTablesWithoutDup.size()==1||_currentSelectColumn.getTable().compareTo(selTablesWithoutDup.get(1))==0)
                    break;
                else
                    continue;
                
            }else{
                _selectedColNames.add(_currentSelectColumn.getColumn());
            }
            
        }
            
            //System.out.println("Size= "+selTablesWithoutDup.size());
            
            if(selTablesWithoutDup.size()<2){
                
                System.out.println(_selectedColNames);
                
                Iterator _it2 = answerList1.iterator();
                
                Map<String,Object> _currentTable = SqlExecutionFactory.dataRecord.getHashTable(selTablesWithoutDup.get(0));
                
                while(_it2.hasNext()){
                    
                    String _currentPK = (String)_it2.next();
                    
                    Map<String,Object> _currentTuple = (Map<String,Object>)_currentTable.get(_currentPK);
                    
                    Iterator _it3 = _selectedColNames.iterator();
                    
                    while(_it3.hasNext()){
                        
                        String _currentColumn = (String)_it3.next();
                        
                        
                        
                        System.out.print(_currentTuple.get(_currentColumn)+" ");
                        
                        
                    }
                    
                    System.out.println("");
                    
                    
                }
                
                
            }else{
                
                ArrayList<String> colName4Display = new ArrayList<String>();
                
                for(int i=0;i<_selectedColNames.size();i++){
                    
                    colName4Display.add(_selectedTableNames.get(i)+"."+_selectedColNames.get(i));
                    
                }
                
                
                System.out.println(colName4Display);
                
                Map<String,Object> _table1 = SqlExecutionFactory.dataRecord.getHashTable(this.selTablesWithoutDup.get(0));
                Map<String,Object> _table2 = SqlExecutionFactory.dataRecord.getHashTable(this.selTablesWithoutDup.get(1));
                
                for(int i=0;i<answerList1.size();i++){
                    
                    Map<String,Object> _currentTuple;
                    
                    for(int j=0;j<_selectedColNames.size();j++){
                        
                        if(_selectedTableNames.get(j).compareTo(this.selTablesWithoutDup.get(0))==0){
                            
                            _currentTuple = (Map<String,Object>)_table1.get(answerList1.get(i));
                            
              
                        }else{
                            _currentTuple = (Map<String,Object>)_table2.get(answerList2.get(i));
                        }
                        
                        System.out.print(_currentTuple.get(_selectedColNames.get(j))+" ");
                        
                    }
                    
                    System.out.println();
                    
                }
                
                
            }
            
            
            
        
        
        
                
        
        
    }
    
    
    
}
