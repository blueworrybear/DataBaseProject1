/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlManipulation;

import SqlInstructionFetcher.SqlSelectFetcher;
import databaseproject.SqlExecutionFactory;
import java.util.*;
import model.SelectColumn;
import model.SelectWhere;

/**
 *
 * @author loe800210
 */
public class SqlSelectTableExec {
    
    SqlSelectFetcher selectFetcher;
    HashMap<String, ArrayList<Object>> outputTable;
    
    public SqlSelectTableExec(SqlSelectFetcher selectFetcher)
    {
        this.selectFetcher = selectFetcher;
        outputTable = new HashMap<String, ArrayList<Object>>();
    }
    
    public String operandType(String tableName, String colName)
    {
        ArrayList<Map<String, Object>> colInfo;
        String type = "";
        
        colInfo = SqlColNameFileParser.parseColNameFile(tableName);
        Iterator it = colInfo.iterator();
        while(it.hasNext())
        {
              Map<String, Object> col = (Map<String, Object>)it.next();
              if( col.get("Name").toString().equals(colName) )
              {
                    type = col.get("Type").toString();
                    return type;
              }
        }
        return type;
        
    }
    
    public boolean isColInTable(String tableName, String colName)
    {
        ArrayList<Map<String, Object>> colInfo;
        colInfo = SqlColNameFileParser.parseColNameFile(tableName);
        Iterator it = colInfo.iterator();
        while(it.hasNext())
        {
             Map<String, Object> col = (Map<String, Object>)it.next();
             if( col.get("Name").toString().equals(colName) )
             {
                   return true;
             }
        }
        return false;
    }
    
    public boolean checkSelectLogic()
    {
        ArrayList<SelectColumn> column = selectFetcher.fetchColumns();
        int aggCount = 0;
        
        Iterator it = column.iterator();
        while(it.hasNext())
        {
            SelectColumn col = (SelectColumn)it.next();
            
            if( col.getAggregation()== null )
            {
                if( !isColInTable(col.getTable(), col.getColumn()) )
                {
                    System.out.println("Syntex error : column \""+col.getColumn()+"\" is not exit in the table \""+col.getTable()+"\".");
                    return false;
                }
            }else
            {
                if( !col.getColumn().equals("*") )
                {
                    if( !isColInTable(col.getTable(), col.getColumn()) )
                    {
                        System.out.println("Syntex error : column \""+col.getColumn()+"\" is not exit in the table \""+col.getTable()+"\".");
                        return false;
                    }
                }
                aggCount++;
            }
        }
        
        if( aggCount!=0 && aggCount != column.size() )
        {
            System.out.println("Logic error : Set cannot be combined with aggregation function.");
            return false;
        }
        
        return true;
    }
    
    public boolean checkFromLogic()
    {
        ArrayList<String> tableName = selectFetcher.fetchFromExpressions();
        
        Iterator it = tableName.iterator();
        while(it.hasNext())
        {
            String table = (String)it.next();
            if( SqlExecutionFactory.dataRecord.getHashTable(table) == null )
            {
                System.out.println("Syntex error : table \""+table+"\" is not exist.");
                return false;
            }
        }
        
        return true;
    }
    
    
    public boolean checkWhereLogic(int num)
    {
        ArrayList<SelectWhere> clause = selectFetcher.fetchWhereExpressions();
                
        if( clause.get(num).get_operand1_is_integer() && isColInTable(clause.get(num).get_operand2_tableName(), clause.get(num).get_operand2_column()) )
        {
            if(this.operandType(clause.get(num).get_operand2_tableName(), clause.get(num).get_operand2_column()).equals("INT"))
            {
                return true;
            }else
            {
                System.out.println("Syntex error : incompatible type of Integer comparing with String.");
                return false;
            }
        }else if( clause.get(num).get_operand2_is_integer() && isColInTable(clause.get(num).get_operand1_tableName(), clause.get(num).get_operand1_column()) )
        {
            if( this.operandType(clause.get(num).get_operand1_tableName(), clause.get(num).get_operand1_column()).equals("INT") )
            {
                return true;
            }else
            {
                System.out.println("Syntex error : incompatible type of Integer comparing with String.");
                return false;
            }
        }else if( clause.get(num).get_operand1_tableName() == null && isColInTable(clause.get(num).get_operand2_tableName(), clause.get(num).get_operand2_column()) )
        {
            if( !this.operandType(clause.get(num).get_operand2_tableName(), clause.get(num).get_operand2_column()).equals("INT") )
            {
                return true;
            }else
            {
                System.out.println("Syntex error : incompatible type of Integer comparing with String.");
                return false;
            }
        }else if( clause.get(num).get_operand2_tableName() == null && isColInTable(clause.get(num).get_operand1_tableName(), clause.get(num).get_operand1_column()))
        {
            if( !this.operandType(clause.get(num).get_operand1_tableName(), clause.get(num).get_operand1_column()).equals("INT") )
            {
                return true;
            }else
            {
                System.out.println("Syntex error : incompatible type of Integer comparing with String.");
                return false;
            }
        }else if( isColInTable(clause.get(num).get_operand1_tableName(), clause.get(num).get_operand1_column()) && isColInTable(clause.get(num).get_operand2_tableName(), clause.get(num).get_operand2_column()) )
        {
            if(    (     this.operandType(clause.get(num).get_operand1_tableName(), clause.get(num).get_operand1_column()).equals("INT")
                      && this.operandType(clause.get(num).get_operand2_tableName(), clause.get(num).get_operand2_column()).equals("INT") )
                || (     !this.operandType(clause.get(num).get_operand1_tableName(), clause.get(num).get_operand1_column()).equals("INT")
                      && !this.operandType(clause.get(num).get_operand2_tableName(), clause.get(num).get_operand2_column()).equals("INT")  )   )
            {
                return true;
            }else
            {
                System.out.println("Syntex error : incompatible type of Integer comparing with String.");
                return false;
            }
        }
        System.out.println("Syntex error : column is not exit in the table(In the WHERE clause).");
        return false;
    }
    
    public boolean checkSyntex()
    {
        ArrayList<SelectWhere> whereClause = selectFetcher.fetchWhereExpressions();
        if( checkSelectLogic() && checkFromLogic())
        {
            if( whereClause.size() == 1 )
            {
                if( checkWhereLogic(0) )
                {
                    return true;
                }
            }else
            {
                if( checkWhereLogic(0) && checkWhereLogic(1) )
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    public ArrayList<Object> table(String tableName)
    {
        ArrayList<Object> tableList = new ArrayList<Object>();
        Iterator itr = SqlExecutionFactory.dataRecord.getHashTable(tableName).entrySet().iterator();
        while(itr.hasNext())
        {
              Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
              tableList.add(tuple.getValue());
        }
        return tableList;
    }
    
    public boolean booleanExp(SelectWhere clause, Map<String, Object> tuple)
    {
        String operator = clause.get_operator();
//        System.out.println(clause.get_operand1_tableName()+"."+clause.get_operand1_column()+clause.get_operator()+clause.get_operand2_tableName()+"."+clause.get_operand2_column());
        if( clause.get_operand1_is_integer() )
        {
            int op1 = Integer.parseInt(clause.get_operand1_column());
            int op2 = Integer.parseInt((String)( (Map<String, Object>)( tuple.get(clause.get_operand2_tableName()) ) ).get(clause.get_operand2_column()));
            
            if( (operator.equals("=") && op1==op2) || (operator.equals(">") && op1>op2) || (operator.equals("<") && op1<op2) )
            {
                return true;
            }
            
        }else if( clause.get_operand2_is_integer() )
        {
            int op1 = Integer.parseInt((String)( (Map<String, Object>)( tuple.get(clause.get_operand1_tableName()) ) ).get(clause.get_operand1_column()));
            int op2 = Integer.parseInt(clause.get_operand2_column());
            if( (operator.equals("=") && op1==op2) || (operator.equals(">") && op1>op2) || (operator.equals("<") && op1<op2) )
            {
                return true;
            }
        }else if( clause.get_operand1_tableName() == null )
        {
            String op1 = clause.get_operand1_column();
            String op2 = ( (Map<String, Object>)( tuple.get(clause.get_operand2_tableName()) ) ).get(clause.get_operand2_column()).toString();
            if( op1.equals(op2) )
            {
                return true;
            }
        }else if( clause.get_operand2_tableName() == null )
        {
            String op1 = ( (Map<String, Object>)( tuple.get(clause.get_operand1_tableName()) ) ).get(clause.get_operand1_column()).toString();
            String op2 = clause.get_operand2_column();
            if( op1.equals(op2) )
            {
                return true;
            }
        }else
        {
            if( operandType(clause.get_operand1_tableName(), clause.get_operand1_column()).equals("INT") )
            {
                int op1 = (Integer)( (Map<String, Object>)( tuple.get(clause.get_operand1_tableName()) ) ).get(clause.get_operand1_column());
                int op2 = (Integer)( (Map<String, Object>)( tuple.get(clause.get_operand2_tableName()) ) ).get(clause.get_operand2_column());
                
                if( (operator.equals("=") && op1==op2) || (operator.equals(">") && op1>op2) || (operator.equals("<") && op1<op2) )
                {
                    return true;
                }
            }else
            {
                String op1 = ( (Map<String, Object>)( tuple.get(clause.get_operand1_tableName()) ) ).get(clause.get_operand1_column()).toString();
                String op2 = ( (Map<String, Object>)( tuple.get(clause.get_operand2_tableName()) ) ).get(clause.get_operand2_column()).toString();
                
                if( op1.equals(op2) )
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean exec()
    {   
        if( !checkSyntex() )
        {
            return false;
        }
        
        ArrayList<String> fromTable = selectFetcher.fetchFromExpressions();
        ArrayList<SelectWhere> clause = selectFetcher.fetchWhereExpressions();
        ArrayList<Object> tableList1;
        ArrayList<Object> tableList2;
        ArrayList<Object> outcome1 = new ArrayList<Object>();
        ArrayList<Object> outcome2 = new ArrayList<Object>();
        int count = 0;
        int sum = 0;
        
        if( fromTable.size() == 1 )   // Table number = 1
        {
            tableList1 = this.table(fromTable.get(0));
            Map tuple = new HashMap<String, Object>();
            for(int i=0;i<tableList1.size();i++)
            {
                tuple.put(fromTable.get(0), tableList1.get(i));
                if( selectFetcher.fetchBooleanFunction().equals("AND") &&
                    ( booleanExp(clause.get(0), tuple) && booleanExp(clause.get(1), tuple) ) )   //  AND
                {
                    outcome1.add(tableList1.get(i));
                    count++;
                }else if( selectFetcher.fetchBooleanFunction().equals("OR") &&
                    ( booleanExp(clause.get(0), tuple) || booleanExp(clause.get(1), tuple) ) )   // OR
                {
                    outcome1.add(tableList1.get(i));
                    count++;
                }else if( selectFetcher.fetchBooleanFunction().equals("") && booleanExp(clause.get(0), tuple) )
                {
                    outcome1.add(tableList1.get(i));
                    count++;
                }
                tuple.remove(fromTable.get(0));
            }
            outputTable.put(fromTable.get(0), outcome1);
            
        }else if( fromTable.size() == 2 )   // Table number = 2
        {
            tableList1 = this.table(fromTable.get(0));
            tableList2 = this.table(fromTable.get(1));
            Map tuple = new HashMap<String, Object>();
            
            for(int i=0;i<tableList1.size();i++)
            {
                tuple.put(fromTable.get(0), tableList1.get(i));
                
                for(int j=0;j<tableList2.size();j++)
                {
                    tuple.put(fromTable.get(1), tableList2.get(i));
                
                    if( selectFetcher.fetchBooleanFunction().equals("AND") &&
                        ( booleanExp(clause.get(0), tuple) && booleanExp(clause.get(1), tuple) ) )  // AND
                    {
                        outcome1.add(tableList1.get(i));
                        outcome2.add(tableList2.get(j));
                        count++;
                    }else if( selectFetcher.fetchBooleanFunction().equals("OR") && 
                        ( booleanExp(clause.get(0), tuple) || booleanExp(clause.get(1), tuple) ) )   // OR
                    {
                        outcome1.add(tableList1.get(i));
                        outcome2.add(tableList2.get(j));
                        count++;
                    }else if( selectFetcher.fetchBooleanFunction().equals("") && booleanExp(clause.get(0), tuple) )
                    {
                        outcome1.add(tableList1.get(i));
                        outcome2.add(tableList2.get(j));
                        count++;
                    }
                    
                    tuple.remove(fromTable.get(1));
                }
                tuple.remove(fromTable.get(0));
            }
            
            outputTable.put(fromTable.get(0), outcome1);
            outputTable.put(fromTable.get(1), outcome2);
            
        }
        
        return true;
    }
    
    public void display()
    {
        ArrayList<SelectColumn> column = selectFetcher.fetchColumns();
        
        int rowNum = outputTable.get(column.get(0).getTable()).size();
        for(int row=0;row<rowNum;row++)
        {
            for(int col=0;col<column.size();col++)
            {
                String tableName = column.get(col).getTable();
                String colName = column.get(col).getColumn();
        
                String value = ((Map<String, Object>)outputTable.get(tableName).get(row)).get(colName).toString();
                
                System.out.printf(" %s ",value);
            }
            System.out.printf("\n");
        }
        
        
    }
}
