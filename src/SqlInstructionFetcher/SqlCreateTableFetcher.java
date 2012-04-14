/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlInstructionFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bear
 */
public class SqlCreateTableFetcher extends SqlCreateFetcher{
    
    private String typePattern = "\\s?\\w+\\s((INT)|(VARCHAR\\s?\\(\\s?\\d+\\s?\\))|(CHAR\\s?\\(\\s?\\d+\\s?\\)))\\s?(PRIMARY\\sKEY)?\\s?,?";
    
    public SqlCreateTableFetcher(SqlFetcher fetcher){
        super(fetcher.statement);
    }
    
    public SqlCreateTableFetcher(String instruc){
        super(instruc);
    }
    
    public String fetchTableName(){
        String tableName = new String();
        String patternStr = "TABLE\\s\\w+\\s";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        if (matcher.find()) {
            tableName = matcher.group(0);
            tableName = tableName.replaceAll("TABLE", "");
            tableName = tableName.replaceAll(" ", "");
        }
        return tableName;
    }
    
    public ArrayList<Map<String,Object>> getColums(){
        String patternStr = this.typePattern;
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        ArrayList<Map<String,Object>> colums = new ArrayList<Map<String, Object>>();
        
        while (matcher.find()) {
            /*Get one column description*/
            String column_str = matcher.group(0);
            /*Clear the description*/
            column_str = column_str.replaceAll(",", "");
            column_str = column_str.replaceAll("^\\s", "");
            String column_arr[] = column_str.split(" ");
            
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("Name", column_arr[0]);
            
            String column_type = column_arr[1];
            column_type = column_type.replaceAll("\\(\\d+\\)", "");
            map.put("Type", column_type);
            
            /*Determine if the value type has the limit of number*/
            String _patternStr = "\\d+";
            Pattern _pattern = Pattern.compile(_patternStr);
            Matcher _matcher = _pattern.matcher(column_arr[1]);
            if(_matcher.find()){
                map.put("Quantity", Integer.parseInt(_matcher.group(0)));
            }else{
                map.put("Quantity", 0);
            }
            
            _patternStr = "PRIMARY\\s*KEY";
            _pattern = Pattern.compile(_patternStr);
            _matcher = _pattern.matcher(column_str);
            if (_matcher.find()) {
                map.put("PRIMARY",true);
            }else{
                map.put("PRIMARY",false);
            }
                           
            colums.add(map);
        }
        
        
        return colums;
    }
    
    @Override
    public boolean judgeCorrect(){
        System.out.println(this.statement.toUpperCase());
        String patternStr = "^CREATE\\sTABLE\\s\\w+\\s?\\(\\s?("+this.typePattern+")+\\);";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        boolean matchFound = matcher.find();
        return matchFound;
    }
}
