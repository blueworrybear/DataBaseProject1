/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;
import DataStructure.SqlBTreeData;
import SqlInstructionFetcher.*;
import java.util.HashMap;
import java.util.Hashtable;
import model.*;
import java.lang.*;
import java.util.*;

/**
 *
 * @author bear
 */
public class SqlSelectTableExec_BTree {
    
    SqlSelectFetcher selectFetch;
    
    public SqlSelectTableExec_BTree(){
        super();
    }
    
    public SqlSelectTableExec_BTree(SqlSelectFetcher _selectFetcher){
        this.selectFetch = _selectFetcher;
    }
    
    /**
     * This method will execution the given Select statement.
     * @return 
     */
    public boolean exec(){   
        String condition = this.selectFetch.fetchBooleanFunction();
        System.out.println(condition);
        if(condition.equals("AND")){
            this.exeAND();
        }else if(condition.equals("OR")){
            
        }else if(condition.equals("")){
            
        }else{
            throw new NoSuchMethodError("This select sql has illegal condition function excepts AND/OR.");
        }
        return true;
        
    }
    
    /**
     * This method is for the internel call to handle the result fetching job.
     * @param _con
     * @return 
     */
    private HashMap<CombineKey,Object> exeCondition(SelectWhere _con){
        if(_con.get_operand1_tableName() == null || _con.get_operand2_tableName() == null ||_con.get_operand1_is_integer() || _con.get_operand2_is_integer()){
            /*The condition of compare with value not in the table*/
            String pure_value;
            String column;
            String table;
            boolean is_integer;
            /*Let the boolean function always is like Table.Colum (<|>|>=|<=|<>) Value*/
            if (_con.get_operand1_tableName() == null || _con.get_operand1_is_integer()) {
                pure_value = _con.get_operand1_column();
                column = _con.get_operand2_column();
                table = _con.get_operand2_tableName();
                if(_con.get_operator().equals("<")){
                    _con.set_operator(">");
                }else if(_con.get_operator().equals(">")){
                    _con.set_operator("<");
                }else if(_con.get_operator().equals(">=")){
                    _con.set_operator("<=");
                }else if(_con.get_operator().equals("<=")){
                    _con.set_operator(">=");
                }
                is_integer = _con.get_operand1_is_integer();
            }else{
                pure_value = _con.get_operand2_column();
                table = _con.get_operand1_tableName();
                column = _con.get_operand1_column();
                is_integer = _con.get_operand2_is_integer();
            }
            
            /*Fetch the column's data type*/
            String col_type = new String();
            ArrayList<Map<String,Object>> list = SqlColNameFileParser.parseColNameFile(table);
            Iterator<Map<String,Object>> it = list.iterator();
            while(it.hasNext() && col_type.equals("")){
                Map<String,Object> map = it.next();
                if(((String)map.get("Name")).toUpperCase().equals(column.toUpperCase())){
                    col_type = (String) map.get("Type");
                }
            }
            
            /*Seperate the case of pure value is string or interger*/
            if (!is_integer) {
                /*Case of pure value is String*/
                if(!_con.get_operator().equals("=") && !_con.get_operator().equals("<>")){
                    throw new InternalError("The given select instruction is tring to compare string in lexical order.");
                }else{
                    /*Check that the column type is as same as the value*/
                    if(col_type.equals("VARCHAR") || col_type.equals("CHAR")){
                        SqlBTreeData Btree = new SqlBTreeData(table, column);
                        ArrayList result = Btree.get(_con.get_operator(), pure_value);
                        it = result.iterator();
                        /*Bug in the BTree, havn't finish*/
                    }else{
                        throw new NoSuchMethodError("Can't compare Integer to String ("+col_type+":"+table+"."+column+" compare to String)");
                    }
                }
            }else{
                /*Case of pure value is Integer*/
                if(col_type.equals("INT")){
                    //Implement your code
                }else{
                    throw new NoSuchMethodError("Can't compare Integer to String ("+col_type+":"+table+"."+column+" compare to Integer)");
                }
            }
            
        }else if(_con.get_operand1_tableName() != null && _con.get_operand2_tableName() != null){
            /* The both value are from the table*/
            //Implement your code
        }else{
            throw new UnknownError("Unexpected error in exeCondition.");
        }
        
        
        return null;
    }
    
    /**
     * This method is an internel call to handle the conditino of 
     * there is a boolean function of AND in select statement.
     * @return ArrayList of CombineKey.
     */
    private ArrayList<CombineKey> exeAND(){
        CombineKey firstKey = new CombineKey(); //This obj is in the purpose of recording where the key come from.
        
        ArrayList<SelectWhere> conditions = this.selectFetch.fetchWhereExpressions();
        System.out.println("size:"+conditions.size());
        if(conditions.size() != 2){
            throw new InternalError("The number of boolean functions of select sql isn't in our expectation.");
        }
        
        SelectWhere first_con = conditions.get(0);
        /* 
         * Set for the combine key of the first condition.
         */
        if(first_con.get_operand1_tableName() == null || first_con.get_operand2_tableName() == null ||first_con.get_operand1_is_integer() || first_con.get_operand2_is_integer()){
            /*The condition of compare with value not in the table*/
            String table;
            /*Let the boolean function always is like Table.Colum (<|>|>=|<=|<>) Value*/
            if (first_con.get_operand1_tableName() == null || first_con.get_operand1_is_integer()) {
                table = first_con.get_operand2_tableName();
            }else{
                table = first_con.get_operand1_tableName();
            }
            firstKey.setKey1(table);
            firstKey.setKey2("");
        }else{
            firstKey.setKey1(first_con.get_operand1_tableName());
            firstKey.setKey2(first_con.get_operand2_tableName());
        }
        /*Collect the first group of result*/
        HashMap<CombineKey,Object> firstMap = this.exeCondition(first_con);
        
        /* 
         * @Start to fetch the result of condition 2 according to the result of condition 1.
         * @Using the firstKey to determine how to fetch condition2.
         * @Consider two cases:
         *  1.key of condition2 doesn't in the condition1.
         *  2.key2 of condition2 are all in the condition1.
         */
        SelectWhere second_con = conditions.get(1);
        CombineKey secondKey = new CombineKey();
        /*
         * Set the key of second conditon.
         */
        if(second_con.get_operand1_tableName() == null || second_con.get_operand2_tableName() == null ||second_con.get_operand1_is_integer() || second_con.get_operand2_is_integer()){
            /*The condition of compare with value not in the table*/
            String pure_value;
            String column;
            String table;
            boolean is_integer;
            /*Let the boolean function always is like Table.Colum (<|>|>=|<=|<>) Value*/
            if (second_con.get_operand1_tableName() == null || second_con.get_operand1_is_integer()) {
                table = second_con.get_operand2_tableName();
            }else{
                table = second_con.get_operand1_tableName();
            }
            if(table.equals(firstKey.getKey2())){
                secondKey.setKey1("");
                secondKey.setKey2(table);
            }else{
                secondKey.setKey1(table);
                secondKey.setKey2("");
            }
        }else{
            if(second_con.get_operand1_tableName().equals(firstKey.getKey1())){
                secondKey.setKey1(second_con.get_operand1_tableName());
                secondKey.setKey2(second_con.get_operand2_tableName());
            }else{
                secondKey.setKey1(second_con.get_operand2_tableName());
                secondKey.setKey2(second_con.get_operand1_tableName());
            }
        }
        
        return null;
    }
}
