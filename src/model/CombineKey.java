/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;
import java.lang.NullPointerException;

/**
 *
 * @author bear
 */
public class CombineKey extends Object{
    String key1;
    String key2;
    String table1;
    String table2;
    
    public CombineKey(){
        
    }
    
    public CombineKey(String _key1, String _key2){
        if(_key1 == null || _key2 == null){
            throw new NullPointerException("Key can't be null.");
        }
        this.key1 = _key1;
        this.key2 = _key2;
    }
    
    public void setKey1(String _key){
        this.key1 = _key;
    }
    
    public void setKey2(String _key){
        this.key2 = _key;
    }
    
    public void setTable1(String _table){
        this.table1 = _table;
    }
    
    public void setTable2(String _table){
        this.table2 = _table;
    }
    
    public String getKey1(){
        return this.key1;
    }
    public String getKey2(){
        return this.key2;
    }
    
    public String getTable1(){
        return this.table1;
    }
    
    public String getTable2(){
        return this.table2;
    }
    
    @Override
    public boolean equals(Object obj){
        CombineKey key = (CombineKey) obj;
        if(this.key1.equals(key.key1) && this.key2.equals(key.key2)){
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode()
    {
            String new_key;
            new_key = this.key1 + this.key2;
            return new_key.hashCode();
    }
    
}
