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
    int sum;
    
    
    public SelectColumn()
    {
        sum = 0;
    }
            
    
    public void setColumn(String str){
        this.column = str;
    }
    
    public void setTable(String str){
        this.table = str;
    }
    
    public void setAggregation(String str){
        this.aggregation = str;
    }
    
    public void addSum(int value)
    {
        sum = sum + value;
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
    
    public int getSum()
    {
        return this.sum;
    }
    
    public String toString(){
        return "Column = "+column+"\nTable = "+table+"\nAggregation = "+aggregation+"\n";
    }
    
}
