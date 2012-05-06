/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlInstructionFetcher;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bear
 */
public class SqlInsertIntoFetcher extends SqlFetcher{
    
    String typeStringPattern1 = "((\\s?[\"\']([A-Za-z0-9(),!@#$%^&*_+-=]|(\\\\\")|(\\\\\'))+[\"\']\\s?,\\s?)|(\\s?\\d+\\s?,\\s?))";
    String typeStringPattern2 = "((\\s?[\"\']([A-Za-z0-9(),!@#$%^&*_+-=]|(\\\\\")|(\\\\\'))+[\"\']\\s?)|(\\s?\\d+\\s?))";
    
    public SqlInsertIntoFetcher(String instruc){
        super(instruc);
    }
    
    /**
     * This method can fetch the Table name.
     * @return 
     */
    public String fetchTableName(){
        
        String tableName = new String();
        String patternStr = "INTO\\s?\\w+";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        
        if (matcher.find()) {
            tableName = matcher.group(0).replaceAll("INTO", "");
            tableName = tableName.replaceAll("\\s", "");
        }
        return tableName;
    }
    
    /**
     * This method return an Arraylist if
     * the instruction has mention the insert order of the
     * columns, or it will return an empty Arraylist.
     * @return 
     */
    public ArrayList<String> fetchInsertSequence(){
        ArrayList<String> sequence = new ArrayList<String>();
        String patternStr = this.fetchTableName().toUpperCase()+"\\s?\\((\\s?\\w+\\s?,?\\s?)+\\)";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        if (matcher.find()) {
            String columns =new String();
            columns = matcher.group(0).replaceAll("^"+this.fetchTableName(), "");
            columns = columns.replaceAll("\\s", "");
            String _patternStr = "\\w+";
            Pattern _pattern = Pattern.compile(_patternStr);
            Matcher _matcher = _pattern.matcher(columns);
            while (_matcher.find()) {                
                sequence.add(_matcher.group(0));
            }
        }
        return sequence;
    }
    
    /**
     * This method return the insert values in the form of ArrayList.
     * Notice that the element type in the Array list is Object type.
     * The casting is the response of the caller.
     * Besides, before calling this method, you may call fetchInsertSequence()
     * before to make it sure that the order to insert.
     * @return 
     */
    public ArrayList<Object> fetchInsertValue(){
        ArrayList<Object> insertValues = new ArrayList<Object>();
        String patternStr = "VALUES\\s?\\((\\s?[\"\']?(\\w|\\s)+[\"\']?\\s?,?\\s?)+\\)";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        if (matcher.find()) {
            String values = matcher.group(0);
            values = values.replaceAll("^VALUES", "");
//            values = values.replaceAll("\\s", "");
//            System.out.println(values);
            String _patternStr = "([\'\"](\\w|\\s)+[\'\"]|\\d+)";
            Pattern _pattern = Pattern.compile(_patternStr);
            Matcher _matcher = _pattern.matcher(values);
            while (_matcher.find()) {                
                String value = _matcher.group(0);
                Matcher tmp_matcher = Pattern.compile("^\\d+$").matcher(value);
                if (tmp_matcher.find()) {
                    insertValues.add(Integer.valueOf(value));
                }else{
                    insertValues.add(value);
                }
//                System.out.println("value:"+value);
            }
        }
        return insertValues;
    }
    
    @Override
    public boolean judgeCorrect(){
        String patternStr = "INSERT\\sINTO\\s\\w+\\s(\\((\\s?\\w+\\s?,?\\s?)+\\))?\\s?VALUES\\s?(\\(((\\s?[\"\']{1}\\s?.+\\s?[\"\']{1}\\s?,?\\s?)|(\\s?\\d+\\s?,\\s?))*\\)){1}\\s?;\\s?";
//        String patternStr = "INSERT\\sINTO\\s\\w+\\s(\\((\\s?\\w+\\s?,?\\s?)+\\))?\\s?VALUES\\s?(\\("+typeStringPattern1+"*"+typeStringPattern2+"?\\)){1}\\s?;\\s?";
//        String patternStr = this.typeStringPattern1;
//        String patternStr = "[A-Za-z0-9(),&&[^\'\"]]";
        Pattern pattern = Pattern.compile(patternStr);
        //System.out.println(pattern.toString());
        Matcher matcher = pattern.matcher(this.statement.toUpperCase());
        boolean matcher_correct = matcher.find();
        
        if (matcher_correct) {
//            System.out.println(matcher.group(0));
        }
        
        return matcher_correct;
    }
}
