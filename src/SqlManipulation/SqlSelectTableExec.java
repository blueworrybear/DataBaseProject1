///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package SqlManipulation;
//
//import SqlInstructionFetcher.SqlSelectFetcher;
//import databaseproject.SqlExecutionFactory;
//import java.util.*;
//import model.SelectColumn;
//import model.SelectWhere;
//
///**
// *
// * @author loe800210
// */
//public class SqlSelectTableExec {
//    
//    SqlSelectFetcher selectFetcher;
//    HashMap<String, ArrayList<Object>> outputTable;
//    
//    public SqlSelectTableExec(SqlSelectFetcher selectFetcher)
//    {
//        this.selectFetcher = selectFetcher;
//        outputTable = new HashMap<String, ArrayList<Object>>();
//    }
//    
//    public String checkSync(String tableName, String colName)
//    {
//        ArrayList<Map<String, Object>> colInfo;
//        String type = "";
//        
//        colInfo = SqlColNameFileParser.parseColNameFile(tableName);
//        Iterator it = colInfo.iterator();
//        while(it.hasNext())
//        {
//              Map<String, Object> col = (Map<String, Object>)it.next();
//              if( col.get("Name").toString().equals(colName) )
//              {
//                    type = col.get("Type").toString();
//                    return type;
//              }
//        }
//        return type;
//        
//    }
//    
//    public boolean checkSyntex(int num)
//    {
//        ArrayList<SelectWhere> whereClause = selectFetcher.fetchWhereExpressions();
//        
//        if( whereClause.get(num).get_operand1_is_integer() )
//        {
//                if( this.checkSync(whereClause.get(num).get_operand2_tableName(), whereClause.get(num).get_operand2_column()).equals("INT") )
//                {
//                    return true;
//                }else
//                {
//                    System.out.println("Syntex error : incompatible type of Integer comparing with String");
//                }
//        }else if( whereClause.get(num).get_operand2_is_integer() )
//        {
//                if(this.checkSync(whereClause.get(num).get_operand1_tableName(), whereClause.get(num).get_operand1_column()).equals("INT"))
//                {
//                    return true;
//                }else
//                {
//                    System.out.println("Syntex error : incompatible type of Integer comparing with String");
//                }
//        }else
//        {
//                if( whereClause.get(num).get_operator().equals("=") )
//                {
//                    return true;
//                }else
//                {
//                    System.out.println("Syntex error : cannot compare between Strings");
//                }
//        }
//        return false;
//    }
//    
//    public ArrayList<Object> table(String tableName)
//    {
//        ArrayList<Object> tableList = new ArrayList<Object>();
//        Iterator itr = SqlExecutionFactory.dataRecord.getHashTable(tableName).entrySet().iterator();
//        while(itr.hasNext())
//        {
//              Map.Entry<String, Object> tuple = (Map.Entry<String, Object>)itr.next();
//              tableList.add(tuple.getValue());
//        }
//        return tableList;
//    }
//    
//    
//    public boolean exec()
//    {
//        ArrayList<SelectColumn> column = selectFetcher.fetchColumns();
//        ArrayList<SelectWhere> whereClause = selectFetcher.fetchWhereExpressions();
//        ArrayList<Object> outcome1 = new ArrayList<Object>();
//        ArrayList<Object> outcome2 = new ArrayList<Object>();
//        ArrayList<Object> outcome3 = new ArrayList<Object>();
//        ArrayList<Object> outcome4 = new ArrayList<Object>();
//        HashMap<String, ArrayList<Object>> tempTable = new HashMap<String, ArrayList<Object>>();
//        
//        if( whereClause.size() == 1 )
//        {
//            if( checkSyntex(0) )
//            {
//                        
//                if( whereClause.get(0).get_operand1_is_integer() )
//                {
//                    ArrayList<Object> tableList = this.table(whereClause.get(0).get_operand2_tableName());
//                    
//                    int op1 = Integer.parseInt(whereClause.get(0).get_operand1_column());
//                    for(int i=0;i<tableList.size();i++)
//                    {
//                        int op2 = (Integer)((Map<String, Object>)tableList.get(i)).get(whereClause.get(0).get_operand2_column());
//                        if( ( whereClause.get(0).get_operator().equals("=") && op1 == op2 ) ||
//                            ( whereClause.get(0).get_operator().equals("<") && op1 < op2  ) ||
//                            ( whereClause.get(0).get_operator().equals(">") && op1 > op2  ) )
//                        {
//                            outcome1.add((Map<String, Object>)tableList.get(i));
//                        }
//                    }
//                    
//                    outputTable.put(whereClause.get(0).get_operand2_tableName(), outcome1);
//                    
//                }else if( whereClause.get(0).get_operand2_is_integer() )
//                {
//                    ArrayList<Object> tableList = this.table(whereClause.get(0).get_operand1_tableName());
//                    
//                    int op2 = Integer.parseInt(whereClause.get(0).get_operand2_column());
//                    for(int i=0;i<tableList.size();i++)
//                    {
//                        int op1 = (Integer)((Map<String, Object>)tableList.get(i)).get(whereClause.get(0).get_operand1_column());
//                        if( ( whereClause.get(0).get_operator().equals("=") && op1 == op2 ) ||
//                            ( whereClause.get(0).get_operator().equals("<") && op1 < op2  ) ||
//                            ( whereClause.get(0).get_operator().equals(">") && op1 > op2  ) )
//                        {
//                            outcome1.add((Map<String, Object>)tableList.get(i));
//                        }
//                    }
//                    outputTable.put(whereClause.get(0).get_operand1_tableName(), outcome1);
//                    
//                }else if( whereClause.get(0).get_operand1_tableName().equals("") )
//                {
//                    ArrayList<Object> tableList = this.table(whereClause.get(0).get_operand2_tableName());
//                    
//                    String op1 = whereClause.get(0).get_operand1_column();
//                    for(int i=0;i<tableList.size();i++)
//                    {
//                         String op2 = ((Map<String, Object>)tableList.get(i)).get(whereClause.get(0).get_operand2_column()).toString();
//                         if( op1.equals(op2) )
//                         {
//                             outcome1.add((Map<String, Object>)tableList.get(i));
//                         }
//                    }  
//                    outputTable.put(whereClause.get(0).get_operand2_tableName(), outcome1);
//                    
//                }else if( whereClause.get(0).get_operand2_tableName().equals("") )
//                {
//                    ArrayList<Object> tableList = this.table(whereClause.get(0).get_operand1_tableName());
//                    
//                    String op2 = whereClause.get(0).get_operand2_column();
//                    for(int i=0;i<tableList.size();i++)
//                    {
//                         String op1 = ((Map<String, Object>)tableList.get(i)).get(whereClause.get(0).get_operand1_column()).toString();
//                         if( op1.equals(op2) )
//                         {
//                             outcome1.add((Map<String, Object>)tableList.get(i));
//                         }
//                    }  
//                    outputTable.put(whereClause.get(0).get_operand1_tableName(), outcome1);
//                    
//                }else
//                {
//                    ArrayList<Object> tableList1 = this.table(whereClause.get(0).get_operand1_tableName());
//                    ArrayList<Object> tableList2 = this.table(whereClause.get(0).get_operand2_tableName());
//                    
//                    for(int i=0;i<tableList1.size();i++)
//                    {
//                        String op1 = ((Map<String, Object>)tableList1.get(i)).get(whereClause.get(0).get_operand1_column()).toString();
//                        for(int j=0;j<tableList2.size();j++)
//                        {
//                            String op2 = ((Map<String, Object>)tableList2.get(j)).get(whereClause.get(0).get_operand2_column()).toString();
//                            if( op1.equals(op2) )
//                            {
//                                outcome1.add((Map<String, Object>)tableList1.get(i));
//                                outcome2.add((Map<String, Object>)tableList2.get(j));
//                            }
//                        }
//                    }
//                    
//                    outputTable.put(whereClause.get(0).get_operand1_tableName(), outcome1);
//                    outputTable.put(whereClause.get(0).get_operand2_tableName(), outcome2);
//                }
//            }
//        }else if( whereClause.size() == 2 )
//        {
//            if( checkSyntex(0) && checkSyntex(1))
//            {
//                if( whereClause.get(0).get_operand1_is_integer() )
//                {
//                    ArrayList<Object> tableList = this.table(whereClause.get(0).get_operand2_tableName());
//                    
//                    int op1 = Integer.parseInt(whereClause.get(0).get_operand1_column());
//                    for(int i=0;i<tableList.size();i++)
//                    {
//                        int op2 = (Integer)((Map<String, Object>)tableList.get(i)).get(whereClause.get(0).get_operand2_column());
//                        if( ( whereClause.get(0).get_operator().equals("=") && op1 == op2 ) ||
//                            ( whereClause.get(0).get_operator().equals("<") && op1 < op2  ) ||
//                            ( whereClause.get(0).get_operator().equals(">") && op1 > op2  ) )
//                        {
//                            outcome1.add((Map<String, Object>)tableList.get(i));
//                        }
//                        
//                    }
//                    
//                    tempTable.put(whereClause.get(0).get_operand2_tableName(), outcome1);
//                    
//                }else if( whereClause.get(0).get_operand2_is_integer() )
//                {
//                    ArrayList<Object> tableList = this.table(whereClause.get(0).get_operand1_tableName());
//                    
//                    int op2 = Integer.parseInt(whereClause.get(0).get_operand2_column());
//                    for(int i=0;i<tableList.size();i++)
//                    {
//                        int op1 = (Integer)((Map<String, Object>)tableList.get(i)).get(whereClause.get(0).get_operand1_column());
//                        if( ( whereClause.get(0).get_operator().equals("=") && op1 == op2 ) ||
//                            ( whereClause.get(0).get_operator().equals("<") && op1 < op2  ) ||
//                            ( whereClause.get(0).get_operator().equals(">") && op1 > op2  ) )
//                        {
//                            outcome1.add((Map<String, Object>)tableList.get(i));
//                        }
//                    }
//                    
//                    tempTable.put(whereClause.get(0).get_operand1_tableName(), outcome1);
//                    
//                }else if( whereClause.get(0).get_operand1_tableName().equals("") )
//                {
//                    ArrayList<Object> tableList = this.table(whereClause.get(0).get_operand2_tableName());
//                    
//                    String op1 = whereClause.get(0).get_operand1_column();
//                    for(int i=0;i<tableList.size();i++)
//                    {
//                         String op2 = ((Map<String, Object>)tableList.get(i)).get(whereClause.get(0).get_operand2_column()).toString();
//                         if( op1.equals(op2) )
//                         {
//                             outcome1.add((Map<String, Object>)tableList.get(i));
//                         }
//                    }
//                    
//                    tempTable.put(whereClause.get(0).get_operand2_tableName(), outcome1);
//                    
//                }else if( whereClause.get(0).get_operand2_tableName().equals("") )
//                {
//                    ArrayList<Object> tableList = this.table(whereClause.get(0).get_operand1_tableName());
//                    
//                    String op2 = whereClause.get(0).get_operand2_column();
//                    for(int i=0;i<tableList.size();i++)
//                    {
//                         String op1 = ((Map<String, Object>)tableList.get(i)).get(whereClause.get(0).get_operand1_column()).toString();
//                         if( op1.equals(op2) )
//                         {
//                             outcome1.add((Map<String, Object>)tableList.get(i));
//                         }
//                    }  
//                    
//                    tempTable.put(whereClause.get(0).get_operand1_tableName(), outcome1);
//                    
//                }else
//                {
//                    ArrayList<Object> tableList1 = this.table(whereClause.get(0).get_operand1_tableName());
//                    ArrayList<Object> tableList2 = this.table(whereClause.get(0).get_operand2_tableName());
//                    
//                    for(int i=0;i<tableList1.size();i++)
//                    {
//                        String op1 = ((Map<String, Object>)tableList1.get(i)).get(whereClause.get(0).get_operand1_column()).toString();
//                        for(int j=0;j<tableList2.size();j++)
//                        {
//                            String op2 = ((Map<String, Object>)tableList2.get(j)).get(whereClause.get(0).get_operand2_column()).toString();
//                            if( op1.equals(op2) )
//                            {
//                                outcome1.add((Map<String, Object>)tableList1.get(i));
//                                outcome2.add((Map<String, Object>)tableList2.get(j));
//                            }
//                        }
//                    }
//                    
//                    tempTable.put(whereClause.get(0).get_operand1_tableName(), outcome1);
//                    tempTable.put(whereClause.get(0).get_operand2_tableName(), outcome2);
//                    
//                }
//                if(true) // AND
//                {
//                    if( whereClause.get(1).get_operand1_is_integer() )
//                    {
//                        ArrayList<Object> tempList = tempTable.get(whereClause.get(1).get_operand2_tableName());
//                        
//                        int op1 = Integer.parseInt(whereClause.get(1).get_operand1_column());
//                        for(int i=0;i<tempList.size();i++)
//                        {
//                            int op2 = (Integer)((Map<String, Object>)tempList.get(i)).get(whereClause.get(1).get_operand2_column());
//                            if( ( whereClause.get(1).get_operator().equals("=") && op1 == op2 ) ||
//                                ( whereClause.get(1).get_operator().equals("<") && op1 < op2  ) ||
//                                ( whereClause.get(1).get_operator().equals(">") && op1 > op2  ) )
//                            {
//                                outcome3.add(tempList.get(i));
//                            }
//                        }
//                    
//                        outputTable.put(whereClause.get(1).get_operand2_tableName(), outcome3);
//                    
//                    
//                    }else if( whereClause.get(1).get_operand2_is_integer() )
//                    {
//                        ArrayList<Object> tempList = tempTable.get(whereClause.get(1).get_operand1_tableName());
//                        
//                        int op2 = Integer.parseInt(whereClause.get(1).get_operand2_column());
//                        for(int i=0;i<tempList.size();i++)
//                        {
//                            int op1 = (Integer)((Map<String, Object>)tempList.get(i)).get(whereClause.get(1).get_operand1_column());
//                            if( ( whereClause.get(1).get_operator().equals("=") && op1 == op2 ) ||
//                                ( whereClause.get(1).get_operator().equals("<") && op1 < op2  ) ||
//                                ( whereClause.get(1).get_operator().equals(">") && op1 > op2  ) )
//                            {
//                                outcome3.add(tempList.get(i));
//                            }
//                        }
//                    
//                        outputTable.put(whereClause.get(1).get_operand1_tableName(), outcome3);
//                    
//                    }else if( whereClause.get(1).get_operand1_tableName().equals("") )
//                    {
//                        ArrayList<Object> tempList = tempTable.get(whereClause.get(1).get_operand2_tableName());
//                        
//                        String op1 = whereClause.get(1).get_operand1_column();
//                        for(int i=0;i<tempList.size();i++)
//                        {
//                            String op2 = ((Map<String, Object>)tempList.get(i)).get(whereClause.get(1).get_operand2_column()).toString();
//                            if( op1.equals(op2) )
//                            {
//                                outcome3.add(tempList.get(i));
//                            }
//                        }  
//                    
//                        outputTable.put(whereClause.get(1).get_operand2_tableName(), outcome3);
//                    
//                    }else if( whereClause.get(1).get_operand2_tableName().equals("") )
//                    {
//                        ArrayList<Object> tempList = tempTable.get(whereClause.get(1).get_operand1_tableName());
//                    
//                        String op2 = whereClause.get(1).get_operand2_column();
//                        for(int i=0;i<tempList.size();i++)
//                        {
//                            String op1 = ((Map<String, Object>)tempList.get(i)).get(whereClause.get(1).get_operand1_column()).toString();
//                            if( op1.equals(op2) )
//                            {
//                                outcome3.add(tempList.get(i));
//                            }
//                        }  
//                    
//                        outputTable.put(whereClause.get(1).get_operand1_tableName(), outcome3);
//                    
//                    }else
//                    {
//                        ArrayList<Object> tempList1;
//                        if( tempTable.get(whereClause.get(1).get_operand1_tableName()) == null )
//                        {
//                            tempList1 = this.table(whereClause.get(1).get_operand1_tableName());
//                        }else
//                        {
//                            tempList1 = tempTable.get(whereClause.get(1).get_operand1_tableName());
//                        }
//                        
//                        ArrayList<Object> tempList2;
//                        if( tempTable.get(whereClause.get(1).get_operand2_tableName()) == null )
//                        {
//                            tempList2 = this.table(whereClause.get(1).get_operand2_tableName());
//                        }else
//                        {
//                            tempList2 = tempTable.get(whereClause.get(1).get_operand2_tableName());
//                        }
//                    
//                        for(int i=0;i<tempList1.size();i++)
//                        {
//                            String op1 = ((Map<String, Object>)tempList1.get(i)).get(whereClause.get(1).get_operand1_column()).toString();
//                            for(int j=0;j<tempList2.size();j++)
//                            {
//                                String op2 = ((Map<String, Object>)tempList1.get(i)).get(whereClause.get(1).get_operand2_column()).toString();
//                                if( op1.equals(op2) )
//                                {
//                                    outcome3.add(tempList1.get(i));
//                                    outcome4.add(tempList2.get(j));
//                                }
//                            }
//                        }
//                    
//                        outputTable.put(whereClause.get(1).get_operand1_tableName(), outcome3);
//                        outputTable.put(whereClause.get(1).get_operand2_tableName(), outcome4);
//                    
//                    
//                    }
//                    
//                    
//                }else   // OR
//                {
//                    
//                }
//                
//                
//                
//                
//            }
//        }
//        
//        
//        return true;
//    }
//    
//    public void display()
//    {
//        
//    }
//}
