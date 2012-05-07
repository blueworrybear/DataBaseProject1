/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

import SqlReader.SqlDataReader;
import SqlReader.SqlFileDataReader;
import SqlContentFileManipulation.FileScanner;
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
        String sql = "SELECT E.NAME FROM EMPLOYEE AS E,DEPARTMENT AS D WHERE D.NAME = \"HI\", E.NAME > 10,E.NO = \"WLL\",E.NO = D.NO";
        SqlSelectFetcher select = new SqlSelectFetcher(sql);
        System.out.println(sql);
        select.fetchTableMapping();
        ArrayList<SelectWhere> list = select.fetchWhereExpressions();
        Iterator it = list.iterator();
        
        while (it.hasNext()) {            
            SelectWhere where = (SelectWhere) it.next();
            System.out.println(where.get_operand1_tableName()+"."+where.get_operand1_column()+"("+where.get_operand1_is_integer()+")"+where.get_operator()+where.get_operand2_tableName()+"."+where.get_operand2_column()+"("+where.get_operand2_is_integer()+")");
        }
        if (select.judgeCorrect()) {
            System.out.println("true");
        }else{
            System.out.println("false");
        }*/
        
        /*
         * NOTICE:
         * The end of the reading SQL file is that 
         * if the reader read the null value.
         */
        System.out.println("Begin Main Function");
        SqlDataReader reader = new SqlFileDataReader("src/Resource/data.in");
        SqlExecutionFactory factory = new SqlExecutionFactory();
        
        reader.openReader();
        String instruc = new String();
        while(!(instruc = reader.readNextInstruc()).equals("")){
            factory.setInstruction(instruc);
            try {
                factory.exeSql();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }
}
