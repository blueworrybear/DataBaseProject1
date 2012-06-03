/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;
import DataStructure.SqlBTreeData;
import SqlInstructionFetcher.*;
import databaseproject.SqlExecutionFactory;
import java.util.HashMap;
import java.util.Hashtable;
import model.*;
import java.lang.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 *
 * @author bear
 */
public class SqlSelectTableExec_BTree {
    
    SqlSelectFetcher selectFetch;
    ArrayList<CombineKey> result;
    
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
            ArrayList<CombineKey> list = this.exeAND();
            Iterator it = list.iterator();
//            while (it.hasNext()) {                
//                CombineKey key = (CombineKey) it.next();
//                System.out.println("Result And:"+key.getTable1()+"."+key.getKey1()+":"+key.getTable2()+"."+key.getKey2());
//            }
            this.result = list;
        }else if(condition.equals("OR")){
            ArrayList<CombineKey> list = this.exeOR();
            Iterator it = list.iterator();
//            while (it.hasNext()) {                
//                CombineKey key = (CombineKey) it.next();
//                System.out.println("Result OR:"+key.getTable1()+"."+key.getKey1()+":"+key.getTable2()+"."+key.getKey2());
//            }
            this.result = list;
        }else if(condition.equals("")){
            System.out.println("NOT AND OR");
            SelectWhere select = this.selectFetch.fetchWhereExpressions().get(0);
            HashMap<CombineKey,Object> map = this.exeCondition(select);
            ArrayList<CombineKey> list = new ArrayList<CombineKey>();
            if(select.get_operand1_tableName() != null && !select.get_operand1_is_integer()){
                Iterator it = map.keySet().iterator();
                while (it.hasNext()) {                    
                    CombineKey key = (CombineKey) it.next();
                    CombineKey rKey = new CombineKey(key.getKey1(), key.getKey2());
                    rKey.setTable1(select.get_operand1_tableName());
                    if(select.get_operand2_is_integer()){
                        rKey.setTable2("");
                    }else{
                        rKey.setTable2(select.get_operand2_tableName());
                    }
                    list.add(rKey);
                }
            }else{
                Iterator it = map.keySet().iterator();
                while (it.hasNext()) {                    
                    CombineKey key = (CombineKey) it.next();
                    CombineKey rKey = new CombineKey(key.getKey1(), key.getKey2());
                    rKey.setTable1(select.get_operand2_tableName());
                    if(select.get_operand1_is_integer()){
                        rKey.setTable2("");
                    }else{
                        rKey.setTable2(select.get_operand1_tableName());
                    }
                    list.add(rKey);
                }
            }
            Iterator it = list.iterator();
//            while (it.hasNext()) {                
//                CombineKey key = (CombineKey) it.next();
//                System.out.println("Result :"+key.getTable1()+"."+key.getKey1()+":"+key.getTable2()+"."+key.getKey2());
//            }
            this.result = list;
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
                        ArrayList<Object> result = Btree.get(_con.get_operator(), pure_value);
                        Iterator<Object> _it = result.iterator();
                        HashMap<CombineKey,Object> _result = new HashMap<CombineKey, Object>();
                        while (_it.hasNext()) {                            
                            String s = (String)_it.next();
                            _result.put(new CombineKey(s, ""), "");
                        }
                        return _result; 
                    }else{
                        throw new NoSuchMethodError("Can't compare Integer to String ("+col_type+":"+table+"."+column+" compare to String)");
                    }
                }
            }else{
                /*Case of pure value is Integer*/
                if(col_type.equals("INT")){
                    SqlBTreeData Btree = new SqlBTreeData(table, column);
                    ArrayList<Object> result = Btree.get(_con.get_operator(), Integer.parseInt(pure_value));
                    Iterator<Object> _it = result.iterator();
                    HashMap<CombineKey,Object> _result = new HashMap<CombineKey, Object>();
                    while (_it.hasNext()) {                        
                        String s =  _it.next().toString();
                        _result.put(new CombineKey(s, ""), "");
                    }
                    return _result;
                }else{
                    throw new NoSuchMethodError("Can't compare Integer to String ("+col_type+":"+table+"."+column+" compare to Integer)");
                }
            }
            
        }else if(_con.get_operand1_tableName() != null && _con.get_operand2_tableName() != null && !_con.get_operand1_is_integer() && !_con.get_operand2_is_integer()){
            /* The both value are from the table*/
            /*
             * Try to figure out the data type of the two column.
             */
            ArrayList<Map<String,Object>> list = SqlColNameFileParser.parseColNameFile(_con.get_operand1_tableName());
            Iterator it = list.iterator();
            String type1 = new String();
            while (it.hasNext() && type1.equals("")) {                
                Map<String,Object> col = (Map<String,Object>) it.next();
                if (col.get("Name").equals(_con.get_operand1_column().toUpperCase())) {
                    if (col.get("Type").equals("INT")) {
                        type1 = "INT";
                    }else if(col.get("Type").equals("CHAR") || col.get("Type").equals("VARCHAR")){
                        type1 = "STRING";
                    }else{
                        throw new InternalError("There is an unknow data type occur.("+col.get("Type")+")");
                    }
                }
            }
            list = SqlColNameFileParser.parseColNameFile(_con.get_operand2_tableName());
            it = list.iterator();
            String type2 = new String();
            while (it.hasNext() && type2.equals("")) {                
                Map<String,Object> col = (Map<String,Object>) it.next();
                if (col.get("Name").equals(_con.get_operand2_column().toUpperCase())) {
                    if (col.get("Type").equals("INT")) {
                        type2 = "INT";
                    }else if(col.get("Type").equals("CHAR") || col.get("Type").equals("VARCHAR")){
                        type2 = "STRING";
                    }else{
                        throw new InternalError("There is an unknow data type occur.("+col.get("Type")+")");
                    }
                }
            }//Fin.
            if (type1.equals(type2)) {
                if(type1.equals("INT")){
                    /*Handle the data type of Integer*/
                    /*Handling if two conditon are not from the same table*/
                    if(!_con.get_operand1_tableName().equals(_con.get_operand2_tableName())){
                        HashMap<CombineKey,Object> _result = new HashMap<CombineKey, Object>();
                        Map<String,Object> table2 = SqlExecutionFactory.dataRecord.getHashTable(_con.get_operand2_tableName());
                        Set set = table2.entrySet();
                        Iterator _it = set.iterator();
                        while (_it.hasNext()) {                
                            Map.Entry<String,Object> entry = (Map.Entry<String,Object>) _it.next();
                            HashMap<String,Object> tuple = (HashMap<String,Object>) entry.getValue();
                            SqlBTreeData BTree = new SqlBTreeData(_con.get_operand1_tableName(), _con.get_operand1_column());
                            ArrayList<Object> _list = BTree.get(_con.get_operator(), Integer.parseInt((String)tuple.get(_con.get_operand2_column())));
                            Iterator tit = _list.iterator();
                            while (tit.hasNext()) {                            
                                String s = tit.next().toString();
                                _result.put(new CombineKey(s, entry.getKey()),"");
                            }
                        }
                        return _result;
                    }else{
                        HashMap<CombineKey,Object> _result = new HashMap<CombineKey, Object>();
                        Map<String,Object> table2 = SqlExecutionFactory.dataRecord.getHashTable(_con.get_operand2_tableName());
                        Set set = table2.entrySet();
                        Iterator _it = set.iterator();
                        while (_it.hasNext()) {                
                            Map.Entry<String,Object> entry = (Map.Entry<String,Object>) _it.next();
                            HashMap<String,Object> tuple = (HashMap<String,Object>) entry.getValue();
                            SqlBTreeData BTree = new SqlBTreeData(_con.get_operand1_tableName(), _con.get_operand1_column());
                            ArrayList<Object> _list = BTree.get(_con.get_operator(), Integer.parseInt((String)tuple.get(_con.get_operand2_column())));
                            Iterator tit = _list.iterator();
                            while (tit.hasNext()) {                            
                                String s = tit.next().toString();
                                if(s.equals(entry.getKey())){
                                    _result.put(new CombineKey(s, ""),"");
                                }
                            }
                        }
                        return _result;
                    }
                }else{
                    /*Handle the data type of String*/
                    if(_con.get_operator().equals("=") || _con.get_operator().equals("<>")){
                        /*
                         * Handle the conditon about two values come from different table
                         */
                        if(!_con.get_operand1_tableName().equals(_con.get_operand2_tableName())){
                            HashMap<CombineKey,Object> _result = new HashMap<CombineKey, Object>();
                            Map<String,Object> table2 = SqlExecutionFactory.dataRecord.getHashTable(_con.get_operand2_tableName());
                            Set set = table2.entrySet();
                            Iterator _it = set.iterator();
                            while (_it.hasNext()) {                
                                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) _it.next();
                                HashMap<String,Object> tuple = (HashMap<String,Object>) entry.getValue();
                                SqlBTreeData BTree = new SqlBTreeData(_con.get_operand1_tableName(), _con.get_operand1_column());
                                ArrayList<Object> _list = BTree.get(_con.get_operator(), tuple.get(_con.get_operand2_column()));
                                Iterator tit = _list.iterator();
                                while (tit.hasNext()) {                            
                                    String s = (String) tit.next();
                                    _result.put(new CombineKey(s, entry.getKey()),"");
                                }
                            }
                            return _result;
                        }else{
                            HashMap<CombineKey,Object> _result = new HashMap<CombineKey, Object>();
                            Map<String,Object> table2 = SqlExecutionFactory.dataRecord.getHashTable(_con.get_operand2_tableName());
                            Set set = table2.entrySet();
                            Iterator _it = set.iterator();
                            while (_it.hasNext()) {                
                                Map.Entry<String,Object> entry = (Map.Entry<String,Object>) _it.next();
                                HashMap<String,Object> tuple = (HashMap<String,Object>) entry.getValue();
                                SqlBTreeData BTree = new SqlBTreeData(_con.get_operand1_tableName(), _con.get_operand1_column());
                                ArrayList<Object> _list = BTree.get(_con.get_operator(), tuple.get(_con.get_operand2_column()));
                                Iterator tit = _list.iterator();
                                while (tit.hasNext()) {                            
                                    String s = (String) tit.next();
                                    if(s.equals(entry.getKey())){
                                        _result.put(new CombineKey(s, ""),"");
                                    }
                                }
                            }
                            return _result;
                        }
                    }else{
                        throw new InternalError("The given select instruction is tring to compare string in lexical order.");
                    }
                }
            }else{
                throw new InternalError("The data types want to compare are not the same.("+type1+" != "+type2+")");
            }
        }else{
            throw new UnknownError("Unexpected error in exeCondition.");
        }
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
//        Set set = firstMap.keySet();
//        Iterator it = set.iterator();
//        while (it.hasNext()) {            
//            CombineKey combine = (CombineKey) it.next();
//            System.out.println("key:"+combine.getKey1()+":"+combine.getKey2());
//        }
//        System.out.println(firstKey.getKey1()+":"+firstKey.getKey2());
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
        /*
         * Their are two conditon in fetching group2
         * 1. group 1 and group 2 has intersectino.
         * 2. there is no relationship between group 1,2
         */
        if((secondKey.getKey1().equals(firstKey.getKey1()) && !secondKey.getKey1().equals("")) || (secondKey.getKey2().equals(firstKey.getKey2())&& !secondKey.getKey2().equals(""))){
            if(secondKey.getKey1().equals("") || secondKey.getKey2().equals("")){  
                CombineKey key = new CombineKey();
                if (second_con.get_operand1_tableName() == null || second_con.get_operand1_is_integer()) {
                    key.setKey1(second_con.get_operand2_tableName());
                    key.setKey2("");
                }else{
                    key.setKey1(second_con.get_operand1_tableName());
                    key.setKey2("");
                }
                HashMap<CombineKey,Object> map = this.exeCondition(second_con);
                if(!firstKey.getKey1().equals("") && !firstKey.getKey2().equals("")){
                    /*
                     * AB:A""
                     * AB:""B
                     */
                    if(!secondKey.getKey1().equals("")){
                        /*AB:A""*/
                        ArrayList<CombineKey> result_list = new ArrayList();
                        Iterator first = firstMap.keySet().iterator();
                        while (first.hasNext()) {                            
                            CombineKey _key = (CombineKey) first.next();
                            Object _t = map.get(new CombineKey(_key.getKey1(), ""));
                            if(_t != null){
                                _key.setTable1(firstKey.getKey1());
                                _key.setTable2(firstKey.getKey2());
                                result_list.add(_key);
                            }
                        }
                        return result_list;
                    }else{
                        ArrayList<CombineKey> result_list = new ArrayList();
                        Iterator first = firstMap.keySet().iterator();
                        while (first.hasNext()) {                            
                            CombineKey _key = (CombineKey) first.next();
                            Object _t = map.get(new CombineKey(_key.getKey2(), ""));
                            if(_t != null){
                                _key.setTable1(firstKey.getKey1());
                                _key.setTable2(firstKey.getKey2());
                                result_list.add(_key);
                            }
                        }
                        return result_list;
                    }
                }else{
                    /*
                    * A "": A ""
                    */
                    ArrayList<CombineKey> result_list = new ArrayList();
                    Iterator first = firstMap.keySet().iterator();
                    while (first.hasNext()) {                        
                        CombineKey _key = (CombineKey) first.next();
                        Object _t =  map.get(_key);
                        if(_t != null){
                            _key.setTable1(firstKey.getKey1());
                            _key.setTable2(firstKey.getKey2());
                            result_list.add(_key);
                        }
                    }
                    return result_list;
                }
            }else{
                /*
                 * AB:AB
                 * AB:BA
                 */
                HashMap<CombineKey,Object> map = this.exeCondition(second_con);
                if(firstKey.getKey1().equals(second_con.get_operand1_tableName())){
                    /*AB:AB*/
                    ArrayList<CombineKey> result_list = new ArrayList();
                    Iterator first = firstMap.keySet().iterator();
                    while (first.hasNext()) {                        
                        CombineKey _key = (CombineKey) first.next();
                        Object _t =  map.get(_key);
                        if(_t != null){
                            _key.setTable1(firstKey.getKey1());
                            _key.setTable2(firstKey.getKey2());
                            result_list.add(_key);
                        }
                    }
                    return result_list;
                }else{
                    /*AB:BA*/
                    ArrayList<CombineKey> result_list = new ArrayList();
                    Iterator first = firstMap.keySet().iterator();
                    while (first.hasNext()) {                        
                        CombineKey _key = (CombineKey) first.next();
                        Object _t =  map.get(new CombineKey(_key.getKey2(), _key.getKey1()));
                        if(_t != null){
                            _key.setTable1(firstKey.getKey1());
                            _key.setTable2(firstKey.getKey2());
                            result_list.add(_key);
                        }
                    }
                    return result_list;
                }
                
            }
        }else{
            if(secondKey.getKey1().equals("")){
                String tmp = secondKey.getKey2();
                secondKey.setKey2(secondKey.getKey1());
                secondKey.setKey1(tmp);
            }
            System.out.println(firstKey.getKey1()+":"+secondKey.getKey1());
            HashMap<CombineKey,Object> secondMap = this.exeCondition(second_con);
            Set set1 = firstMap.keySet();
            Set set2 = secondMap.keySet();
            Iterator t1 = set1.iterator();
            ArrayList<CombineKey> result_list = new ArrayList<CombineKey>();
            while (t1.hasNext()) {                
                CombineKey key = (CombineKey) t1.next();
                Iterator t2 = set2.iterator();
                while (t2.hasNext()) {                    
                    CombineKey key2 = (CombineKey) t2.next();
                    CombineKey item = new CombineKey(key.getKey1(), key2.getKey1());
                    item.setTable1(firstKey.getKey1());
                    item.setTable2(secondKey.getKey1());
                    result_list.add(item);
                }
            }
            return result_list;
        }
//        return null;
    }
    
    private ArrayList<CombineKey> exeOR(){
        ArrayList<SelectWhere> conditions = this.selectFetch.fetchWhereExpressions();
        System.out.println("size:"+conditions.size());
        if(conditions.size() != 2){
            throw new InternalError("The number of boolean functions of select sql isn't in our expectation.");
        }
        
        CombineKey firstKey = new CombineKey(); //This obj is in the purpose of recording where the key come from.
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
        HashMap<CombineKey,Object> firstMap = this.exeCondition(first_con);
        
        SelectWhere second_con = conditions.get(1);
        CombineKey secondKey = new CombineKey();
        /*
         * Set the key of second conditon.
         */
        if(second_con.get_operand1_tableName() == null || second_con.get_operand2_tableName() == null ||second_con.get_operand1_is_integer() || second_con.get_operand2_is_integer()){
            /*The condition of compare with value not in the table*/
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
        /*
         * Their are two conditon in fetching group2
         * 1. group 1 and group 2 has intersectino.
         * 2. there is no relationship between group 1,2
         */
        if((secondKey.getKey1().equals(firstKey.getKey1()) && !secondKey.getKey1().equals("")) || (secondKey.getKey2().equals(firstKey.getKey2())&& !secondKey.getKey2().equals(""))){
            if(secondKey.getKey1().equals("") || secondKey.getKey2().equals("")){  
                CombineKey key = new CombineKey();
                if (second_con.get_operand1_tableName() == null || second_con.get_operand1_is_integer()) {
                    key.setKey1(second_con.get_operand2_tableName());
                    key.setKey2("");
                }else{
                    key.setKey1(second_con.get_operand1_tableName());
                    key.setKey2("");
                }
                HashMap<CombineKey,Object> map = this.exeCondition(second_con);
                if(!firstKey.getKey1().equals("") && !firstKey.getKey2().equals("")){
                    /*
                     * AB:A""
                     * AB:""B
                     */
                    if(!secondKey.getKey1().equals("")){
                        /*AB:A""*/
                        Hashtable<CombineKey,Object> resultMap = new Hashtable<CombineKey, Object>();
                        ArrayList<CombineKey> result_list = new ArrayList();
                        Iterator second = map.keySet().iterator();
                        while (second.hasNext()) {                            
                            CombineKey _key = (CombineKey) second.next();
                            HashMap<String,Object> table = (HashMap<String,Object>) SqlExecutionFactory.dataRecord.getHashTable(firstKey.getKey2());
                            Iterator table_it = table.keySet().iterator();
                            while (table_it.hasNext()) {                                
                                String p_key = (String) table_it.next();
                                CombineKey r_key = new CombineKey(_key.getKey1(), p_key);
                                r_key.setTable1(firstKey.getKey1());
                                r_key.setTable2(firstKey.getKey2());
                                resultMap.put(r_key,"");
                            }
                        }
                        Iterator first = firstMap.keySet().iterator();
                        while (first.hasNext()) {                            
                            CombineKey _key = (CombineKey) first.next();
                            _key.setTable1(firstKey.getKey1());
                            _key.setTable2(firstKey.getKey2());
                            resultMap.put(_key, "");
                        }
                        Iterator result_it = resultMap.keySet().iterator();
                        while (result_it.hasNext()) {                            
                            CombineKey _key = (CombineKey) result_it.next();
                            result_list.add(_key);
                        }
                        return result_list;
                    }else{
                        /*AB:""B*/
                        Hashtable<CombineKey,Object> resultMap = new Hashtable<CombineKey, Object>();
                        ArrayList<CombineKey> result_list = new ArrayList();
                        Iterator second = map.keySet().iterator();
                        while (second.hasNext()) {                            
                            CombineKey _key = (CombineKey) second.next();
                            HashMap<String,Object> table = (HashMap<String,Object>) SqlExecutionFactory.dataRecord.getHashTable(firstKey.getKey1());
                            Iterator table_it = table.keySet().iterator();
                            while (table_it.hasNext()) {                                
                                String p_key = (String) table_it.next();
                                CombineKey r_key = new CombineKey(p_key, _key.getKey1());
                                r_key.setTable1(firstKey.getKey1());
                                r_key.setTable2(firstKey.getKey2());
                                resultMap.put(r_key,"");
                            }
                        }
                        Iterator first = firstMap.keySet().iterator();
                        while (first.hasNext()) {                            
                            CombineKey _key = (CombineKey) first.next();
                            _key.setTable1(firstKey.getKey1());
                            _key.setTable2(firstKey.getKey2());
                            resultMap.put(_key, "");
                        }
                        Iterator result_it = resultMap.keySet().iterator();
                        while (result_it.hasNext()) {                            
                            CombineKey _key = (CombineKey) result_it.next();
                            result_list.add(_key);
                        }
                        return result_list;
                    }
                }else{
                    /*
                    * A "": A ""
                    */
                    Hashtable<CombineKey,Object> result_table = new Hashtable<CombineKey,Object>();
                    ArrayList<CombineKey> result_list = new ArrayList();
                    Iterator first = firstMap.keySet().iterator();
                    while (first.hasNext()) {                        
                        CombineKey _key = (CombineKey) first.next();
                        _key.setTable1(firstKey.getKey1());
                        _key.setTable2("");
                        result_table.put(_key, "");
                    }
                    Iterator second = map.keySet().iterator();
                    while (second.hasNext()) {                        
                        CombineKey _key = (CombineKey) second.next();
                        _key.setTable1(firstKey.getKey1());
                        _key.setTable2("");
                        result_table.put(_key, "");
                    }
                    Iterator result_it = result_table.keySet().iterator();
                    while (result_it.hasNext()) {                        
                        CombineKey _key = (CombineKey) result_it.next();
                        result_list.add(_key);
                    }
                    return result_list;
                }
            }else{
                /*
                 * AB:AB
                 * AB:BA
                 */
                HashMap<CombineKey,Object> map = this.exeCondition(second_con);
                if(firstKey.getKey1().equals(second_con.get_operand1_tableName())){
                    /*AB:AB*/
                    Hashtable<CombineKey,Object> result_table = new Hashtable<CombineKey,Object>();
                    ArrayList<CombineKey> result_list = new ArrayList();
                    Iterator first = firstMap.keySet().iterator();
                    while (first.hasNext()) {                        
                        CombineKey _key = (CombineKey) first.next();
                        _key.setTable1(firstKey.getKey1());
                        _key.setTable2(firstKey.getKey2());
                        result_table.put(_key, "");
                    }
                    Iterator second = map.keySet().iterator();
                    while (second.hasNext()) {                        
                        CombineKey _key = (CombineKey) second.next();
                        _key.setTable1(firstKey.getKey1());
                        _key.setTable2(firstKey.getKey2());
                        result_table.put(_key, "");
                    }
                    Iterator result_it = result_table.keySet().iterator();
                    while (result_it.hasNext()) {                        
                        CombineKey _key = (CombineKey) result_it.next();
                        result_list.add(_key);
                    }
                    return result_list;
                }else{
                    /*AB:BA*/
                    Hashtable<CombineKey,Object> result_table = new Hashtable<CombineKey,Object>();
                    ArrayList<CombineKey> result_list = new ArrayList();
                    Iterator first = firstMap.keySet().iterator();
                    while (first.hasNext()) {                        
                        CombineKey _key = (CombineKey) first.next();
                        _key.setTable1(firstKey.getKey1());
                        _key.setTable2(firstKey.getKey2());
                        result_table.put(_key, "");
                    }
                    Iterator second = map.keySet().iterator();
                    while (second.hasNext()) {                        
                        CombineKey _key = (CombineKey) second.next();
                        _key.setTable1(firstKey.getKey1());
                        _key.setTable2(firstKey.getKey2());
                        String s2 = _key.getKey2();
                        _key.setKey2(_key.getKey1());
                        _key.setKey1(s2);
                        result_table.put(_key, "");
                    }
                    Iterator result_it = result_table.keySet().iterator();
                    while (result_it.hasNext()) {                        
                        CombineKey _key = (CombineKey) result_it.next();
                        result_list.add(_key);
                    }
                    return result_list;
                }
                
            }
        }else{
            if(secondKey.getKey1().equals("")){
                String tmp = secondKey.getKey2();
                secondKey.setKey2(secondKey.getKey1());
                secondKey.setKey1(tmp);
            }
//            System.out.println(secondKey.getKey1()+":"+secondKey.getKey2());
            HashMap<CombineKey,Object> secondMap = this.exeCondition(second_con);
            Set set1 = firstMap.keySet();
            Set set2 = secondMap.keySet();
            Iterator t1 = set1.iterator();
            ArrayList<CombineKey> result_list = new ArrayList<CombineKey>();
            while (t1.hasNext()) {                
                CombineKey key = (CombineKey) t1.next();
                Iterator t2 = set2.iterator();
                while (t2.hasNext()) {                    
                    CombineKey key2 = (CombineKey) t2.next();
                    CombineKey item = new CombineKey(key.getKey1(), key2.getKey1());
                    item.setTable1(firstKey.getKey1());
                    item.setTable2(secondKey.getKey1());
                    result_list.add(item);
                }
            }
            return result_list;
        }
        
//        return null;
    }
    
    public void display(){
        ArrayList<SelectColumn> column = this.selectFetch.fetchColumns();
        boolean agg = false;
        Iterator column_it = column.iterator();
        while (column_it.hasNext()) {            
            SelectColumn col = (SelectColumn) column_it.next();
            if (!col.getAggregation().equals("")) {
                agg = true;
            }
        }
        if(!agg){
            column_it = column.iterator();
            while (column_it.hasNext()) {            
                SelectColumn col = (SelectColumn) column_it.next();
                System.out.print(col.getColumn()+"\t");
            }
            System.out.println();
            Iterator ans = this.result.iterator();
            HashMap<String,Object> table1 = (HashMap<String,Object>) SqlExecutionFactory.dataRecord.getHashTable(result.get(0).getTable1());
            HashMap<String,Object> table2 = null;
            if(! result.get(0).getTable2().equals("")){
                table2 = (HashMap<String, Object>) SqlExecutionFactory.dataRecord.getHashTable(result.get(0).getTable2());
            }
            while (ans.hasNext()) {                
                CombineKey key = (CombineKey) ans.next();
                column_it = column.iterator();
                while (column_it.hasNext()) {            
                    SelectColumn col = (SelectColumn) column_it.next();
                    if(col.getTable().toUpperCase().equals(key.getTable1().toUpperCase())){
                        Map<String,Object> tuple = (Map<String,Object>)table1.get(key.getKey1());
                        System.out.print(tuple.get(col.getColumn())+"\t");
                    }else{
                        Map<String,Object> tuple = (Map<String,Object>)table2.get(key.getKey2());
                        System.out.print(tuple.get(col.getColumn())+"\t");
                    }
                }
                System.out.println();
            }
        }else{
            /*Fetch the table*/
            HashMap<String,Object> table1 = (HashMap<String,Object>) SqlExecutionFactory.dataRecord.getHashTable(result.get(0).getTable1());
            HashMap<String,Object> table2 = null;
            if(! result.get(0).getTable2().equals("")){
                table2 = (HashMap<String, Object>) SqlExecutionFactory.dataRecord.getHashTable(result.get(0).getTable2());
            }
            
            column_it = column.iterator();
            while (column_it.hasNext()) {            
                SelectColumn col = (SelectColumn) column_it.next();
                if(!col.getAggregation().equals("")){
                    if(col.getAggregation().toUpperCase().equals("COUNT")){
                        System.out.println(this.result.size());
                    }else{
                        String _table = col.getTable();
                        String _column = col.getColumn();
                        HashMap<String,String> table_info = SqlColNameFileParser.parseColType(_table);
                        if(table_info.get(_column).equals("Integer")){
                            Integer sum = 0;
                            Iterator _it = this.result.iterator();
                            while (_it.hasNext()) {                                
                                CombineKey _key = (CombineKey) _it.next();
                                if(_table.equals(_key.getTable1())){
                                    Map<String,Object> tuple = (Map<String,Object>)table1.get(_key.getKey1());
                                    sum += Integer.parseInt(tuple.get(_column).toString());
                                }else{
                                    Map<String,Object> tuple = (Map<String,Object>)table1.get(_key.getKey2());
                                    sum += Integer.parseInt(tuple.get(_column).toString());
                                }
                            }
                            System.out.println(sum);
                        }else{
                            throw new InternalError("Can not sum up the String.(In "+_table+"."+_column+" )");
                        }
                    }
                }else{
                    throw new InternalError("Combine the agrregation functoin with normal values.");
                }
            }
            System.out.println();
        }
    }
}
