import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class IndexManager {
    static int block = 512;         //set values
    static int degree = 10;
    static int maxKeys = 19; 
    static int maxChildren = 20; 
    static String MAGIC = "4348PRJ3";







    static class BTreeNode {
        long blockID;       //tree node values
        long parentID;
        int numKeys;
        long[] keys = new long[maxKeys];
        long[] values = new long[maxKeys];      //arrays of btree node content
        long[] children = new long[maxChildren];

        public BTreeNode(long blockID, long parentID) {//node constructor
            this.blockID = blockID;
            this.parentID = parentID;
            this.numKeys = 0;
            Arrays.fill(keys, 0);
            Arrays.fill(values, 0); //sets my arrays of 512 byte to all 0 as initial
            Arrays.fill(children, 0);
        }

        public byte[] toBytes() { // create 512 bytes
            ByteBuffer buffer = ByteBuffer.allocate(block);
            buffer.putLong(blockID);    //adds to buffer
            buffer.putLong(parentID);
            buffer.putLong(numKeys);
            
            for (int i = 0; i < maxKeys; i++) {
                buffer.putLong(keys[i]);    //adds keys and values to buffer too
            buffer.putLong(values[i]);
        }
             
            for (int i = 0; i < maxChildren; i++)
             buffer.putLong(children[i]); //children to buffer
            return buffer.array();//return complete buffer
        }

        public static BTreeNode fromBytes(byte[] data) {//read buffer 
            ByteBuffer buffer = ByteBuffer.wrap(data);
            long blockID = buffer.getLong();
            long parentID = buffer.getLong();
            int numKeys = (int) buffer.getLong();
                                                    //gets from buffer the same way i inserted bytes per bytes to saveto node
            BTreeNode node = new BTreeNode(blockID, parentID);
            node.numKeys = numKeys;
            for (int i = 0; i < maxKeys; i++)
            node.keys[i] = buffer.getLong();
            for (int i = 0; i < maxKeys; i++) 
            node.values[i] = buffer.getLong();
            for (int i = 0; i < maxChildren; i++) 
            node.children[i] = buffer.getLong();
            return node;
        }
    }



    static class BTreeFile {
        RandomAccessFile file;
        long rootBlockID = 0;
        long nextBlockID = 1;

       public BTreeFile(String filename, boolean createNew) throws IOException {
    File f = new File(filename);
    
    if (createNew) {
        if (f.exists()) 
            throw new IOException("File already exists.");
            
        
        file = new RandomAccessFile(f, "rw"); // Initialize the file 
        writeHeader();//make the header 
    } else {
       
        file = new RandomAccessFile(f, "rw");

        byte[] magic = new byte[8];
        file.readFully(magic);
        rootBlockID = file.readLong();
        nextBlockID = file.readLong();
    }
}



       
public void insert(int key, int value) throws IOException {
    BTreeNode root = readNode(rootBlockID);

    if (root.numKeys < maxKeys) {  //btree insert method 
        int i = root.numKeys - 1;

        while (i >= 0 && key < root.keys[i]) {
            root.keys[i + 1] = root.keys[i];
            root.values[i + 1] = root.values[i];
            i--;
        }
        root.keys[i + 1] = key;
        root.values[i + 1] = value;
        root.numKeys++;
        writeNode(root);
        updateHeader();
    } 
}


        void writeHeader() throws IOException {
            file.seek(0);
            byte[] magicBytes = MAGIC.getBytes(StandardCharsets.US_ASCII); //decode
            file.write(Arrays.copyOf(magicBytes, 8));     // magic
            file.writeLong(rootBlockID);                  // root ID
            file.writeLong(nextBlockID);                  // next block
            file.write(new byte[block - 24]);        // rest of block
        }

        void updateHeader() throws IOException {//when inserted needs to update root and next id
            file.seek(8);
            file.writeLong(rootBlockID);
            file.writeLong(nextBlockID);
        }

        void writeNode(BTreeNode node) throws IOException {
            file.seek(node.blockID * block);//starts at right position of byte block
            file.write(node.toBytes());//add to file as byte form 
        }

        BTreeNode readNode(long blockID) throws IOException {//read method
            file.seek(blockID * block);  
            byte[] buffer = new byte[block];
            file.readFully(buffer);
            return BTreeNode.fromBytes(buffer);
        }

        public void close() throws IOException {//closes my file 
            file.close();
        }










private void traverseAndPrint(BTreeFile btf, long blockID) throws IOException {
    BTreeNode node = btf.readNode(blockID);//readable method to read my node

    for (int i = 0; i < node.numKeys; i++) {
        
        if (node.children[i] != 0) {    //left child 
            traverseAndPrint(btf, node.children[i]);
        }

        if (node.keys[i] != 0) {//keys and values as long as not 0
            System.out.println(node.keys[i] + " -> " + node.values[i]);
        }
    }
    if (node.children[node.numKeys] != 0) {// if there is one child left occurs sometimes
        traverseAndPrint(btf, node.children[node.numKeys]);
    }
}



    }





    public static void main(String[] args) {
       

        String command = args[0];
        String filename = args[1];

        try {
            switch (command) {
                case "create": {
                    BTreeFile btf = new BTreeFile(filename, true);//makes file 
                   
                    BTreeNode root = new BTreeNode(1, 0);//create root 
                    btf.rootBlockID = 1;
                    btf.nextBlockID = 2;
                    btf.writeNode(root);// write to file as beggining header
                    btf.writeHeader();
                    btf.close();
                    break;
                }

                case "print": {
                    BTreeFile btf = new BTreeFile(filename, false);
                    btf.traverseAndPrint(btf, btf.rootBlockID); //easy print viewer 
                    btf.close();
                    break;
                }

                case "search": {
                    long searchKey = Long.parseLong(args[2]);// what we are looking for 
                    BTreeFile btf = new BTreeFile(filename, false);
                    BTreeNode root = btf.readNode(btf.rootBlockID);
    
                    boolean found = false;
                    for (int i = 0; i < root.numKeys; i++) {//loops thhorugh until found
                        if (root.keys[i] == searchKey) {
                            System.out.println("Key: " + searchKey + ", Value: " + root.values[i]);
                            found = true;
                            break;
                        }
                    }
    
                    if (!found) {//not found
                        System.err.println("Error: Key not found.");
                    }
    
                    btf.close();
                    break;
                }




                case "load": {
        
                    String csv = args[2];
                
                    try {
                        BTreeFile btf = new BTreeFile(filename, false);
                        BufferedReader reader = new BufferedReader(new FileReader(csv));
                        String line;
                
                        while ((line = reader.readLine()) != null) {//read from file loop
                            line = line.trim();
                
                            String[] parts = line.split(",");//splitter regex form
                
                
                            int key = Integer.parseInt(parts[0].trim());
                            int value = Integer.parseInt(parts[1].trim());
                
                            btf.insert(key, value); //insert key and value found
                        }
                
                       reader.close(); //close files
                        btf.close();
                    } catch (FileNotFoundException e) {
                        System.err.println("File not found: " + e.getMessage());
                    } 
                
                    break;
                }

                case "insert": {//inserts to my b tree 
                long key = Long.parseLong(args[2]);
                long value = Long.parseLong(args[3]);
                BTreeFile btf = new BTreeFile(filename, false);
                btf.insert((int)key, (int)value); // use your method
                System.out.println("Inserted key " + key + " with value " + value);
                btf.close();
                break;
                }

                default:
                    System.err.println("Unknown command: " + command);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
