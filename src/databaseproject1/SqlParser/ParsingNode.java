/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject1.SqlParser;

/**
 *
 * @author ljybowser
 */
public class ParsingNode {
    
    String nodeName;
    String[] queryTok=null;
    ParsingNode leftChild,rightChild;
    int direction=0; //Left = 1  Right = 2
    
    
    public ParsingNode(String nodeName){
        
       String tmp="";
      
       if((nodeName.toUpperCase()).indexOf("TABLE")!=-1 && (nodeName.toUpperCase()).indexOf("‘TABLE’")==-1){
           queryTok = nodeName.split("TABLE",2);
          // tmp = "TABLE";
           
       }else if((nodeName.toUpperCase()).indexOf("INTO")!=-1 && (nodeName.toUpperCase()).indexOf("‘INTO’")==-1){
           queryTok = nodeName.split("INTO",2);
          // tmp = "INTO";
       }else if((nodeName.toUpperCase()).indexOf("(")!=-1){
           queryTok = nodeName.split("\\(",2);
           direction = 2;
          // tmp = "(";
       }else if((nodeName.toUpperCase()).indexOf(")")!=-1){
           queryTok = nodeName.split("\\)",2);
           direction = 1;
          // tmp = ")";
       }else if((nodeName.toUpperCase()).indexOf(",")!=-1){
           queryTok = nodeName.split("\\,",2);
           //tmp = ",";
       }else if((nodeName.toUpperCase()).indexOf(" ")!=-1){
           queryTok = nodeName.split(" ",2);
           //tmp = "^ ^";
       }else{
           tmp = nodeName;
       }
       
      // if(queryTok!=null && (queryTok.length==2 || (queryTok.length==1 && direction!=0))){
      //     for(int i=0;i<queryTok.length;i++){
      //         System.out.println("Tok="+i+queryTok[i]);
      //     }
      // }
          
       if(queryTok==null){
           this.rightChild =this.leftChild=null;
     
       }else if(queryTok.length==1){
           if(direction==1){
               this.leftChild = new ParsingNode(queryTok[0]);
               this.rightChild = null;
           }else if(direction==2){
               this.leftChild = null;
               this.rightChild = new ParsingNode(queryTok[0]);
           }else{
               this.leftChild = null;
               this.rightChild = null;
           }
       }
       else{ 
           this.leftChild = new ParsingNode(queryTok[0]);
           this.rightChild = new ParsingNode(queryTok[1]);
       }
       
       this.nodeName = tmp;
        
    }

    
    
    public String toString(){
        return nodeName;
    }
    
   
    
    public ParsingNode getLeftChild(){
        return leftChild;
    }
    
    public ParsingNode getRightChild(){
        return rightChild;
    }
    
    private void printTree(){
        System.out.println(this);
    }
    
    public String getTree(){
        
        String str="";
        
        if(this.leftChild!=null)
            str+=" "+this.leftChild.getTree(); 
       // this.printTree();
            //if(nodeName.compareTo("CREATE")!=0 && nodeName.compareTo("VALUES")!=0 && nodeName.compareTo("INSERT")!=0){
                str+=" "+this.toString();
            //}
        if(this.rightChild!=null)
            str+=" "+this.rightChild.getTree();
        
        return str;
        
    }
   
    
}
    

