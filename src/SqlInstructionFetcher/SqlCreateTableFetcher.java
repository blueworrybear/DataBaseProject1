/*
 * This Class is design for user
 * to get the detail of CREATE TABLE instruction.
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
    
    /**
     * This method return the name
     * of the table which will be create.
     * @param
     * @return tableName
     */
    public String fetchTableName(){
        /*@param the Table name mentioned in the instruciton.*/
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
    
    /**
     * This method return information of columns and attribute.
     * The return value is wraped in arraylist, which has several map object.
     * The map object in the array list have several key, including
     * Name, Type, Quantity and PRIMARY.
     * A map obejct in the array list represent a column.
     * You can retrieve the column information with the keys mention above.
     * @return 
     */
    public ArrayList<Map<String,Object>> getColumns(){
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
    
    /**
     *  This method is used to judge whether the input instruction is 
     * ivalid or not.
     * @return 
     */
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
