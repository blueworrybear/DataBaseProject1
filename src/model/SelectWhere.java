/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author bear
 */
public class SelectWhere {
    
    String operand1_tableName;
    String operand1_column;
    boolean operand1_is_integer;
    String operand2_tableName;
    String operand2_column;
    boolean operand2_is_integer;
    String operator;
    
    public void set_operand1_tableName(String str){
        this.operand1_tableName = str;
    }
    public void set_operand2_tableName(String str){
        this.operand2_tableName = str;
    }
    public void set_operand1_column(String str){
        this.operand1_column = str;
    }
    public void set_operand2_column(String str){
        this.operand2_column = str;
    }
    public void set_operator(String str){
        this.operator = str;
    }
    public void set_operand1_is_integer(boolean b){
        this.operand1_is_integer = b;
    }
    public void set_operand2_is_integer(boolean b){
        this.operand2_is_integer = b;
    }
    
    public boolean get_operand1_is_integer(){
        return this.operand1_is_integer;
    }
    public boolean get_operand2_is_integer(){
        return this.operand2_is_integer;
    }
    public String get_operand1_tableName(){
        return this.operand1_tableName;
    }
    public String get_operand2_tableName(){
        return this.operand2_tableName;
    }
    public String get_operand1_column(){
        return this.operand1_column;
    }
    public String get_operand2_column(){
        return this.operand2_column;
    }
    public String get_operator(){
        return this.operator;
    }
}