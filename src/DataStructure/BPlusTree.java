/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructure;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author loe800210
 */
public class BPlusTree<keyType extends Comparable<keyType>, valueType> 
{
    private int minKeySize = 2;
    private int maxKeySize = 4;
    private int minChildSize = 3;
    private int maxChildSize = 5;
    
    
    private Node root = null;
    private int height;
    
    
    public BPlusTree()
    {
        this.root = new Node(null, this.maxKeySize, this.maxChildSize);
        this.height = 1;
    }
    public int getHeight()
    {
        return this.height;
    }
    
    
    //  Entry declaration
    public class Entry<keyType extends Comparable<keyType>, valueType>
    {
        private keyType key;
        private ArrayList<Object> primaryKeySet;
        private Node left;
        private Node right;
        public Entry(keyType key)
        {
            this.key = key;
            this.left = null;
            this.right = null;
        }
        public Entry(keyType key, valueType primaryKey)
        {
            this.key = key;
            this.primaryKeySet = new ArrayList<Object>();
            this.primaryKeySet.add(primaryKey);
            this.left = null;
            this.right = null;
        }
        
        public keyType getKey()
        {
            return this.key;
        }
        
        public ArrayList<Object> getPrimaryKey()
        {
            return this.primaryKeySet;
        }
        
        public void setLeftChild(Node node)
        {
            this.left = node;
        }
        
        public void setRightChild(Node node)
        {
            this.right = node;
        }
        
        public Node getLeftChild()
        {
            return this.left;
        }
        
        public Node getRightChild()
        {
            return this.right;
        }
    }
    //  End Entry declaration
    
    //  Node structure declaration
    public class Node
    {
        private ArrayList<Entry> keys;
        private Node parent;
        private Node front;
        private Node next;
        
        public Node(Node parent, int maxKeySize, int maxChildSize)
        {
            this.keys = new ArrayList<Entry>();
            this.parent = parent;
            this.front = null;
            this.next = null;
        }
        
        public Entry getEntry(int index)
        {
            if( index >= this.keys.size() )
            {
                System.out.println("Error : Cannot get the KEY with null.");
                return null;
            }else
            {
                return this.keys.get(index);
            }
        }
        
        private int addEntry(Entry entry)
        {
            for(int i=0;i<this.keys.size();i++)
            {
                if( equal(this.keys.get(i).key, entry.key) )
                {
                    this.keys.get(i).primaryKeySet.addAll(entry.primaryKeySet);
                    return i;
                }
            }
            this.keys.add(entry);
            int pos = this.keys.size()-1;
            
            for(int i=this.keys.size()-1;i>0;i--)
            {
                if( less(this.keys.get(i).key, this.keys.get(i-1).key) )
                {
                    // swap
                    Entry temp = this.keys.get(i-1);
                    this.keys.set(i-1, this.keys.get(i));
                    this.keys.set(i, temp);
                    pos = i-1;
                }
            }
            return pos;
        }
        
        public int getEntryLocation(Entry entry)
        {
            for(int i=0;i<this.numberOfKeys();i++)
            {
                if( equal(this.getEntry(i).key, entry.key) )
                {
                    return i;
                }
            }
            return -1;
        }
        
        public int numberOfKeys()
        {
            return this.keys.size();
        }
        
        public Node getFront()
        {
            return this.front;
        }
        
        public Node getNext()
        {
            return this.next;
        }
        
    }
    //  end Node structure declaration
    
    public void put(keyType key, valueType value)
    {
        Entry newEntry = new Entry<keyType, valueType>(key, value);
        Node node = this.root;
        int count = 1;
        
        while(node != null)
        {
            if(count == this.height)
            {
                node.addEntry(newEntry);
                if(node.numberOfKeys() > this.maxKeySize)
                {
                    this.split(node);
                }
                break;
            }else
            {
                int i;
                for(i=0;i<node.numberOfKeys();i++)
                {
                    if( this.less(newEntry.key, node.getEntry(i).key) )
                    {
                        node = node.getEntry(i).getLeftChild();
                        break;
                    }else if( this.equal(node.getEntry(i).key, newEntry.key) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                    if( i == (node.numberOfKeys()-1) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                }
                count++;
            }
               
        }
        
    }
    private void split(Node node)
    {
        int medianIndex = node.numberOfKeys()/2;
        
        Node left = new Node(null, this.maxKeySize, this.maxChildSize);
        Node right = new Node(null, this.maxKeySize, this.maxChildSize);
        Entry indexEntry = new Entry<keyType, valueType>((keyType)node.getEntry(medianIndex).key);
        
        for(int i=0;i<medianIndex;i++)
        {
            left.addEntry(node.getEntry(i));
            if(left.getEntry(i).left != null)
            {
                left.getEntry(i).left.parent = left;
            }
            if(left.getEntry(i).right != null)
            {
                left.getEntry(i).right.parent = left;
            }
        } 
        for(int i=medianIndex;i<node.numberOfKeys();i++)
        {
            right.addEntry(node.getEntry(i));
            if(i > medianIndex)
            {
                if(right.getEntry(i-medianIndex).left != null)
                {
                    right.getEntry(i-medianIndex).left.parent = right;
                }
                if(right.getEntry(i-medianIndex).right != null)
                {
                    right.getEntry(i-medianIndex).right.parent = right;
                }
            }
            
        }
        
        if(node.parent == null)
        {
            Node newRoot = new Node(null, this.maxKeySize, this.maxChildSize);
            newRoot.addEntry(indexEntry);
            newRoot.getEntry(0).left = left;
            newRoot.getEntry(0).right = right;
            newRoot.getEntry(0).left.parent = newRoot;
            newRoot.getEntry(0).right.parent = newRoot;
            newRoot.getEntry(0).left.next = newRoot.getEntry(0).right;
            newRoot.getEntry(0).right.front = newRoot.getEntry(0).left;
            
            this.root = newRoot;
            this.height++;
        }else
        {
            int location = node.parent.addEntry(indexEntry);
            
            node.parent.getEntry(location).left = left;
            node.parent.getEntry(location).right = right;
            node.parent.getEntry(location).left.parent = node.parent;
            node.parent.getEntry(location).right.parent = node.parent;
            node.parent.getEntry(location).left.next = node.parent.getEntry(location).right;
            node.parent.getEntry(location).right.front = node.parent.getEntry(location).left;
            
            if(node.parent.numberOfKeys() > 1 && location == 0)
            {
                node.parent.getEntry(location+1).left = right;
                
                node.parent.getEntry(location+1).right.front = node.parent.getEntry(location+1).left;
                node.parent.getEntry(location+1).left.next = node.parent.getEntry(location+1).right;
                
            }else if(node.parent.numberOfKeys() > 1 && location == node.parent.numberOfKeys()-1)
            {
                node.parent.getEntry(location-1).right = left;
                node.parent.getEntry(location-1).right.front = node.parent.getEntry(location-1).left;
                node.parent.getEntry(location-1).left.next = node.parent.getEntry(location-1).right;
                
                if( node.parent.next != null )
                {
                    node.parent.getEntry(location).right.next = node.parent.next.getEntry(0).right;
                    node.parent.next.getEntry(0).right.front = node.parent.getEntry(location).right;
                }
                
            }else if(0 < location && location < node.parent.numberOfKeys()-1)
            {
                node.parent.getEntry(location-1).right = left;
                node.parent.getEntry(location+1).left = right;
                
                node.parent.getEntry(location-1).right.front = node.parent.getEntry(location-1).left;
                node.parent.getEntry(location-1).left.next = node.parent.getEntry(location-1).right;
                
                node.parent.getEntry(location+1).right.front = node.parent.getEntry(location+1).left;
                node.parent.getEntry(location+1).left.next = node.parent.getEntry(location+1).right;
            }
            
            if(node.parent.numberOfKeys() > this.maxKeySize)
            {
                this.split(node.parent);
            }
        }
        
            
    }
    
    
    public Node get(keyType key)
    {
        Node node = this.root;
        int count = 1;
        while(node != null)
        {
            if(count == this.height)
            {
                for(int i=0;i<node.numberOfKeys();i++)
                {
                    if( this.equal(node.getEntry(i).key, key) )
                    {
                        //System.out.println(node.numberOfKeys());
                        return node;
                    }
                }
                return null;
            }else
            {
                int i;
                for(i=0;i<node.numberOfKeys();i++)
                {
                    if( this.less(key, node.getEntry(i).key) )
                    {
                        node = node.getEntry(i).getLeftChild();
                        break;
                    }else if( this.equal(node.getEntry(i).key, key) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                    if( i == (node.numberOfKeys()-1) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                }
                count++;
            }
            
            
        }
        return null;
    }
    
    public Node getRange(keyType key)
    {
        Node node = this.root;
        int count = 1;
        while(node != null)
        {
            if(count == this.height)
            {
                return node;
            }else
            {
                int i;
                for(i=0;i<node.numberOfKeys();i++)
                {
                    if( this.less(key, node.getEntry(i).key) )
                    {
                        node = node.getEntry(i).getLeftChild();
                        break;
                    }else if( this.equal(node.getEntry(i).key, key) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                    if( i == (node.numberOfKeys()-1) )
                    {
                        node = node.getEntry(i).getRightChild();
                        break;
                    }
                }
                count++;
                
            }
            
        }
        return null;
    }
    
    
    public boolean less(Comparable key1, Comparable key2)
    {
        return key1.compareTo(key2) < 0;
    }
    public boolean equal(Comparable key1, Comparable key2)
    {
        return key1.compareTo(key2) == 0;
    }
    public boolean lessEqual(Comparable key1, Comparable key2)
    {
        return (key1.compareTo(key2) < 0 || key1.compareTo(key2) == 0);
    }
    
}
