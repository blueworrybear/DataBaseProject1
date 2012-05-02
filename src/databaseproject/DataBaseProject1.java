/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import SqlReader.SqlDataReader;
import SqlReader.SqlFileDataReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import SqlInstructionFetcher.SqlSelectFetcher;
import java.util.ArrayList;
import java.util.Iterator;
import model.SelectColumn;
import model.SelectWhere;

/**
 *
 * @author bear
 */
//Test
//Test2
public class DataBaseProject1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Start");
        
//        SqlSelectFetcher select = new SqlSelectFetcher("SELECT E.NAME, D.DNOL , COUNT(E.NAME),EMPLOYEE.NO FROM EMPLOYEE AS E, DEPARTMENT AS D WHERE E.NAME = D.NAME, E.NO = D.NO;");
        SqlSelectFetcher select = new SqlSelectFetcher("SELECT NAME FROM EMPLOYEE WHERE NAME = NO");
        select.fetchTableMapping();
        ArrayList<SelectColumn> list = select.fetchColumns();
        Iterator it = list.iterator();
        while (it.hasNext()) {            
            SelectColumn i = (SelectColumn) it.next();
            System.out.println("Table:= "+i.getTable()+" Value:= "+i.getColumn()+" agg:= "+i.getAggregation());
        }
        ArrayList<SelectWhere> _list = select.fetchWhereExpressions();
        if (_list != null) {
            
            it = _list.iterator();
            while (it.hasNext()) {            
                SelectWhere where = (SelectWhere) it.next();
                System.out.println(where.get_operand1_tableName()+"."+where.get_operand1_column()+where.get_operator()+where.get_operand2_tableName()+"."+where.get_operand2_column());
            }
        }
        
        
        /*
         * NOTICE:
         * The end of the reading SQL file is that 
         * if the reader read the null value.
         */
        SqlDataReader reader = new SqlFileDataReader("src/Resource/data.in");
        reader.openReader();
        SqlExecutionFactory factory = new SqlExecutionFactory();
        String instruc = new String();
        while(!(instruc = reader.readNextInstruc()).equals("")){
        factory.setInstruction(instruc);
        try {
            factory.exeSql();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        }
//        factory.setInstruction(reader.readNextInstruc());
//        try {
//            factory.exeSql();
//        } catch (Exception ex) {
//            System.out.print(ex);
//        }
    }
}
