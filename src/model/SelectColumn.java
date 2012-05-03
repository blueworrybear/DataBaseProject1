/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author bear
 */
public class SelectColumn {
    
    String column;
    String table;
    String aggregation;
    
    public void setColumn(String str){
        this.column = str;
    }
    
    public void setTable(String str){
        this.table = str;
    }
    
    public void setAggregation(String str){
        this.aggregation = str;
    }
    
    public String getColumn(){
        return this.column;
    }
    
    public String getTable(){
        return this.table;
    }
    
    public String getAggregation(){
        return this.aggregation;
    }
}
