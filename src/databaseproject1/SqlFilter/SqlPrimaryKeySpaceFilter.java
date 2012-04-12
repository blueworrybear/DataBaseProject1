/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject1.SqlFilter;

/**
 *
 * @author ljybowser
 */
public class SqlPrimaryKeySpaceFilter {
    
    public SqlPrimaryKeySpaceFilter(){
        
    }
    
    public String startFilter(String query){
        
        query = query.replaceAll("PRIMARY KEY","PRIMARY_KEY");
        
        return query;
        
    }
    
}
