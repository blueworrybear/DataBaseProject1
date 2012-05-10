/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlInstructionFetcher;
import SqlManipulation.SqlSelectTableExec;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.*;

/**
 *
 * @author bear
 */
public class SqlSelectFetcher extends SqlFetcher{
    
    int tableNumber;
    Map<String,String> table_map;
    
    public SqlSelectFetcher(String instruc){
        super(instruc);
    }
    
    public void fetchTableMapping(){
        if (this.table_map == null) {
            
            this.tableNumber = 0;
            
            this.table_map = new HashMap<String, String>();
            
            String patternStr = "FROM\\s(\\w+(\\sAS\\s\\w+)?\\s?)(,\\s?\\w+\\s(AS\\s\\w+)?)?";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(this.statement.toUpperCase());
            
            if (matcher.find()) {
//                System.out.println(matcher.group(0));
                String _patternStr = ",?\\w+(\\sAS\\s\\w+)?\\s?";
                Pattern _pattern = Pattern.compile(_patternStr);
                Matcher _matcher = _pattern.matcher(matcher.group(0).toUpperCase().replace("FROM", "").replace(", ", ","));
                int count = 0;
                while (_matcher.find()) {   
                    String key = new String();
                    String value = new String();
                    
                    String __patternStr = "^\\w+";
                    Pattern __pattern = Pattern.compile(__patternStr);
                    Matcher __matcher = __pattern.matcher(_matcher.group(0).replace(",", ""));
                    if (__matcher.find()) {
                        key = __matcher.group(0);
                    }
                    __patternStr = "AS\\s?\\w+";
                    __pattern = Pattern.compile(__patternStr);
                    __matcher = __pattern.matcher(_matcher.group(0).replace(",", ""));
                    value = "index"+count;
                    count ++;
                    if (__matcher.find()) {
                        value = __matcher.group(0).replace("AS", "").replace(" ", "");
                    }
//                    System.out.println(key+" = "+value);
                    this.table_map.put(value, key);
                    this.tableNumber ++;
                }
            }
            
        }
    }
    
    public ArrayList<SelectColumn> fetchColumns(){
        this.fetchTableMapping();
        String intruciton = new String();
        ArrayList<SelectColumn> list = new ArrayList<SelectColumn>();
        
        String patternStr = "FROM.+";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        
        if (matcher.find()) {
            intruciton = this.statement.toUpperCase().replace(matcher.group(0), "");
        }else{
            return null;
        }
        
        
        patternStr = "((COUNT|SUM)\\s?\\(.+?\\))|((\\w+\\.)?[A-Z*0-9]+)|(\\*)";
        pattern = Pattern.compile(patternStr);
        matcher = pattern.matcher(intruciton.replace("SELECT", ""));
        while (matcher.find()) {       
            SelectColumn select = new SelectColumn();
//            System.out.println(matcher.group(0));
            String table;
            String _instruction = matcher.group(0).replace(" ", "").replace(",", "");

            String _patternStr = "(COUNT|SUM)";
            Pattern _pattern = Pattern.compile(_patternStr);
            Matcher _matcher = _pattern.matcher(_instruction);
            select.setAggregation("");
            if (_matcher.find()) {
                select.setAggregation(_matcher.group(0));
                _instruction = _instruction.replace(_matcher.group(0), "").replace("(", "").replace(")", "");
//                System.out.println("AGG: "+select.getAggregation());
            }
            _patternStr = "\\w+\\.";
            _pattern = Pattern.compile(_patternStr);
            _matcher = _pattern.matcher(_instruction);
            if (_matcher.find()) {
                table = _matcher.group(0).replace(".", "");
                String _table_name = this.table_map.get(table);
                if (_table_name != null) {
                    select.setTable(_table_name);
                }else{
                    select.setTable(table);
                }
                _instruction = _instruction.replace(_matcher.group(0), "");
            }
            if (this.tableNumber == 1) {
                Iterator it = this.table_map.entrySet().iterator();
                Map.Entry<String,String> en = (Map.Entry<String,String>) it.next();
                select.setTable(en.getValue());
            }   
            
            select.setColumn(_instruction);
//            System.out.println("Table: "+select.getTable()+"  Column: "+select.getColumn()+"  AGG:"+select.getAggregation());
            list.add(select);
//            System.out.println();
        }

        
        return list;
    }
    
    public String fetchBooleanFunction(){
        this.fetchTableMapping();
        String patternStr = "\\s(AND|OR)\\s";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        if (matcher.find()) {
            return matcher.group(0).replace(" ", "");
        }
        return "";
    }
    
    public ArrayList<SelectWhere> fetchWhereExpressions(){
        this.fetchTableMapping();
        String instruciton = new String();
        ArrayList<SelectWhere>list = new ArrayList<SelectWhere>();
        
        String patternStr = "WHERE.+";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        
        if (matcher.find()) {
            instruciton = matcher.group(0).replace(";", "").replace("WHERE ", "");
        }else{
            return null;
        }
        
        patternStr = "\\s?((\\w+\\.\\w+)|((\'|\")(.+?(\\\\\')*(\\\\\")*)+?(\'|\")|\\w+)|\\d+)\\s?(=|>|<|<=|>=)\\s?((\\w+\\.\\w+)|((\'|\")(.+?(\\\\\')*(\\\\\")*)+?(\'|\"))|\\d+|\\w+)\\s?,?";
        pattern = Pattern.compile(patternStr);
        matcher = pattern.matcher(instruciton);
        
        while (matcher.find()) { 
            
            SelectWhere where = new SelectWhere();
            String _patternStr = "((\\w+\\.\\w+)|((\'|\")(.+?(\\\\\')*(\\\\\")*)+?(\'|\")|\\w+)|\\d+)";
            Pattern _pattern = Pattern.compile(_patternStr);
            Matcher _matcher = _pattern.matcher(matcher.group(0));
            int i = 0;
            /*parse A.B*/
            while (_matcher.find()) {     
                String _instruction = _matcher.group(0);
                /*Set operand1's table name*/
//                System.out.println(_matcher.group(0));
                String boolean_patternStr = "\\w+\\.";
                Pattern boolean_pattern = Pattern.compile(boolean_patternStr);
                Matcher boolean_matcher = boolean_pattern.matcher(_matcher.group(0));
                if (boolean_matcher.find()) {
//                    System.out.println(boolean_matcher.group(0));
                    String key = this.table_map.get(boolean_matcher.group(0).replace(".", ""));
                    if (key != null) {
                        if (i == 0) {
                            where.set_operand1_tableName(key);
                        }else{
                            where.set_operand2_tableName(key);
                        }
                    }else{
                        if(i == 0){
                            where.set_operand1_tableName(boolean_matcher.group(0).replace(".", ""));
                        }else{
                            where.set_operand2_tableName(boolean_matcher.group(0).replace(".", ""));
                        }
                    }
                    _instruction = _instruction.replace(boolean_matcher.group(0), "");
                }
                if (this.tableNumber == 1) {
                    Iterator it = this.table_map.entrySet().iterator();
                    Map.Entry<String,String> en = (Map.Entry<String,String>) it.next();
                    /*Check for the Pure String not to put value into operand_tableName*/
                    if (i == 0) {
                        String tmp_pattern = "(\"|\')";
                        Pattern tmp_paPattern = Pattern.compile(tmp_pattern);
                        Matcher tmp_matcher = tmp_paPattern.matcher(_instruction);
                        if (!tmp_matcher.find()) {
                            where.set_operand1_tableName(en.getValue());
                        }
                    }else{
                        String tmp_pattern = "(\"|\')";
                        Pattern tmp_paPattern = Pattern.compile(tmp_pattern);
                        Matcher tmp_matcher = tmp_paPattern.matcher(_instruction);
                        if (!tmp_matcher.find()) {
                            where.set_operand2_tableName(en.getValue());
                        }
                    }
                }
                if (i == 0) {
                    try{
                        int t = Integer.parseInt(_instruction);
                        where.set_operand1_is_integer(true);
                    }catch(Exception e){
                        where.set_operand1_is_integer(false);
                    }
                    where.set_operand1_column(_instruction.replaceAll("^\"|\"$|^'|'$", ""));
                }else{
                    try{
                        int t = Integer.parseInt(_instruction);
                        where.set_operand2_is_integer(true);
                    }catch(Exception e){
                        where.set_operand2_is_integer(false);
                    }
                    where.set_operand2_column(_instruction.replaceAll("^\"|\"$|^'|'$", ""));
                }
                i++;
            }
            _patternStr = "(>|<|=|<=|>=)";
            _pattern = Pattern.compile(_patternStr);
            _matcher = _pattern.matcher(matcher.group(0));
            if (_matcher.find()) {
                where.set_operator(_matcher.group(0));
            }
//            System.out.println(where.get_operand1_tableName()+"."+where.get_operand1_column()+where.get_operator()+where.get_operand2_tableName()+"."+where.get_operand2_column());
            
            list.add(where);
        }
        return list;
    }
    
     public ArrayList<String> fetchFromExpressions(){
        //  need to be implement
         ArrayList<String> result = new ArrayList<String>();
        Set entries =  this.table_map.entrySet();
        Object[] list = entries.toArray();
        for(int i = 0; i < this.tableNumber; i++){
            Entry en = (Entry) list[i];
            result.add((String)en.getValue());
        }
        
        return result;
    }
    
    @Override
    public boolean judgeCorrect(){
        this.fetchTableMapping();
        String patternStr = "SELECT";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement);
//        System.out.println(this.statement);
        
        if (matcher.find()) {
            String intruciton;

            patternStr = "FROM.+";
            pattern = Pattern.compile(patternStr);
            matcher = pattern.matcher(this.statement.toUpperCase());

            if (matcher.find()) {
                intruciton = this.statement.toUpperCase().replace(matcher.group(0), "");
            }else{
                return false;
            }

            /*Check for the SELECT part is correct*/
            patternStr = "((COUNT|SUM)\\s?\\(.+?\\))|((\\w+\\.)?[A-Z*0-9]+)";
            pattern = Pattern.compile(patternStr);
            matcher = pattern.matcher(intruciton.replace("SELECT", ""));
            while (matcher.find()) {       
                SelectColumn select = new SelectColumn();
                
//                System.out.println(matcher.group(0));
                
                String table;
                String _instruction = matcher.group(0).replace(" ", "").replace(",", "");

                String _patternStr = "(COUNT|SUM)";
                Pattern _pattern = Pattern.compile(_patternStr);
                Matcher _matcher = _pattern.matcher(_instruction);
                if (_matcher.find()) {
                    select.setAggregation(_matcher.group(0));
                    _instruction = _instruction.replace(_matcher.group(0), "").replace("(", "").replace(")", "");
            //                System.out.println("AGG: "+select.getAggregation());
                }
                _patternStr = "\\w+\\.";
                _pattern = Pattern.compile(_patternStr);
                _matcher = _pattern.matcher(_instruction);
//                System.out.println("judge "+_instruction);
                if (!_matcher.find() && this.tableNumber > 1) {
                    boolean pass = false;
                    int count = 0;
                    for(Entry<String, String> entry : table_map.entrySet()) {
                        String string = entry.getKey();
                        String string1 = entry.getValue();
                        SqlSelectTableExec exe = new SqlSelectTableExec(this);
                        if (exe.isColInTable(string1, _instruction)) {
                            count +=1;
                        
                        }
                        
                    }
                    if(count < 2){
                        for (Entry<String, String> entry : table_map.entrySet()) {
                            String string = entry.getKey();
                            String string1 = entry.getValue();
                            SqlSelectTableExec exe = new SqlSelectTableExec(this);
                            if (exe.isColInTable(string1, _instruction)) {
                                this.statement = this.statement.toUpperCase().replaceAll("(\\w+\\.)?"+_instruction, string1+"."+_instruction);
                                pass = true;
                            }

                        }
                    }
                    if(!pass){
                        System.out.println("有重覆的COLUMN但卻沒有指明TABLE");
                        return false;
                    }
                }
            }
//            System.out.println("stat:"+this.statement);
            /*Check for the WHERE part is correct*/
            patternStr = "\\w+\\.\\w+";
            pattern = Pattern.compile(patternStr);
            matcher = pattern.matcher(this.statement.toUpperCase().replaceAll("(\"|\')(.+?(\\\\\')*(\\\\\")*).?(\"|\')", " "));
            while (matcher.find()) {                
                String table = matcher.group().replaceAll("\\.\\w+", "");
//                System.out.println(matcher.group());
                boolean bool = false;
                ArrayList<String> list = this.fetchFromExpressions();
                for (String str : list) {
//                    System.out.println(str+":"+table);
                    if (str.equals(table)) {
                        bool = true;
                    }
                }
                if (this.table_map.get(table) != null) {
                    bool = true;
                }
                if (!bool) {
                    return false;
                }
                
            }
            /*Check for where part 2*/
            patternStr = "WHERE.+?;$?";
            pattern = Pattern.compile(patternStr);
            matcher = pattern.matcher(this.statement.toUpperCase());
            if (matcher.find() && this.tableNumber > 1) {
//                System.out.println(matcher.group());
                String set = matcher.group().replaceAll("(\"|\')(.+?(\\\\\')*(\\\\\")*).?(\"|\')", " ").replaceAll("\\d+", " ").replaceAll("^WHERE|;$", "").replaceAll("<|=|>|,", " ").replaceAll("\\s+", " ");
                String[] list = set.split(" ");
                for (String S : list) {
                    if (!(S.equals(" ") || S.equals("AND") || S.equals("OR")||S.equals(""))) {
                        patternStr = "\\w+\\.\\w+";
                        pattern = Pattern.compile(patternStr);
                        matcher = pattern.matcher(S);
//                        System.out.println(S);
                        if (!matcher.find()) {
                            boolean pass = false;
                            int count = 0;
                            for(Entry<String, String> entry : table_map.entrySet()) {
                                String string = entry.getKey();
                                String string1 = entry.getValue();
                                SqlSelectTableExec exe = new SqlSelectTableExec(this);
                                if (exe.isColInTable(string1, S)) {
                                    count +=1;

                                }

                            }
                            if(count < 2){
                                for (Entry<String, String> entry : table_map.entrySet()) {
                                    String string = entry.getKey();
                                    String string1 = entry.getValue();
                                    SqlSelectTableExec exe = new SqlSelectTableExec(this);
                                    if (exe.isColInTable(string1, S)) {
                                        this.statement = this.statement.toUpperCase().replaceAll("(\\w+\\.)?"+S, string1+"."+S);
                                        pass = true;
                                    }

                                }
                            }

                            if(!pass){
                                System.out.println("有重覆的COLUMN但卻沒有指明TABLE");
                                return false;
                            }
                        }
                    }
                }
//                System.out.println(this.statement);
            }
        }else{
            return false;
        }
        
        /*bonus check*/
        patternStr = "((<>)|(><))";
        pattern = Pattern.compile(patternStr);
        matcher = pattern.matcher(this.statement);
        if(matcher.find()){
            return false;
        }
        return true;
    }
    
}

