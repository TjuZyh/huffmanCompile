package com.huff;

import java.io.*;
import java.util.*;

/**
 * A class for file compression
 */
public class CompressFile {

    // Linear Huffman Coding
    public static ArrayList<Byte> HufTable = new ArrayList<>();
    // Effective length of the compressed file
    public static long significantLength;
    // Temporarily store the Huffman map encoding table
    static Map<Byte, String> mapCode = new HashMap();

    /**
     * Makes a file compressor
     */
    public CompressFile() {}

    /**
     * Compress a file
     */
    public void compress(File f) {

        HufTable = new ArrayList<>();
        mapCode = new HashMap();

        /** 1. MagicNumCode = MagicNumSize + MagicNumCode
         * */
        long MagicNum = 123456789;
        byte[] MagicNumCode = LongNumCode(MagicNum);
        byte MagicNumSize = (byte) MagicNumCode.length;


        /** 2. HuffmanTreeCode
         * */
        //Get file binary content
        byte[] FileCode = null;
        try {
            FileCode = getFillCode(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Generate Huffman tree
        HuffmanNode TreeRoot = getHuTree(FileCode);
        //Obtain the Huffman linear encoding, which is stored in the global variable HufTable
        getHufTable(TreeRoot);
        byte[] HuffmanTreeCode = new byte[HufTable.size()];
        for (int i = 0; i < HufTable.size(); i++) {
            HuffmanTreeCode[i] = HufTable.get(i);
        }

        /** 4. ZippedFileCode
         * */
        //生成哈夫曼编码表
        Map<Byte, String> byteStringMap = createHuffmanCode(TreeRoot);
        byte[] ZippedFileCode = encodeByHuffmanCode(FileCode, byteStringMap);


        /** 3. NumOfDataCode = NumOfDataSize + NumOfDataCode。3一定在 4 的下面，不要改变顺序。
         * */
        byte[] NumOfDataCode = LongNumCode(significantLength);
        byte NumOfDataSize = (byte) NumOfDataCode.length;


        /** Combine the above 4 parts and put them in the array
         * */
        byte[] ans = new byte[MagicNumCode.length + 1 + HuffmanTreeCode.length + NumOfDataCode.length + 1 + ZippedFileCode.length];
        int n = 0;
        ans[n] = MagicNumSize;
        n++;
        for (int i = 0; i < MagicNumCode.length; i++) {
            ans[n] = MagicNumCode[i];
            n++;
        }
        for (int i = 0; i < HuffmanTreeCode.length; i++) {
            ans[n] = HuffmanTreeCode[i];
            n++;
        }
        ans[n] = NumOfDataSize;
        n++;
        for (int i = 0; i < NumOfDataCode.length; i++) {
            ans[n] = NumOfDataCode[i];
            n++;
        }
        for (int i = 0; i < ZippedFileCode.length; i++) {
            ans[n] = ZippedFileCode[i];
            n++;
        }


        /**
         * Delete the original file and regenerate the .hu file
         * */
        String path = f.getAbsolutePath();
        f.delete();
        path = path + ".hu";
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(ans);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * Read the file to be compressed into a byte array and return
     *
     * @param f
     * @return
     * @throws IOException
     */
    public static byte[] getFillCode(File f) throws IOException {

        InputStream in = new FileInputStream(f);

        byte[] srcBytes = new byte[in.available()];
        in.read(srcBytes);
        in.close();
        return srcBytes;
    }

    /**
     * Convert num of type long to base 256
     *
     * @param num
     * @return
     */
    public static byte[] LongNumCode(long num){
        ArrayList<Byte> bytes = new ArrayList<>();
        while (num!=0){
            bytes.add((byte) (num%256));
            num /= 256;
        }
        byte[] ans = new byte[bytes.size()];
        for (int i=0;i<bytes.size();i++){
            ans[i] = bytes.get(i);
        }
        return ans;
    }

    /**
     * create huffman tree using byte[]
     *
     * @param bytes
     * @return
     */
    public static HuffmanNode getHuTree(byte[] bytes){
        //First wrap each byte element and the number of occurrences into a HuffmanNode node
        List<HuffmanNode> nodeList = getNodeList(bytes);
        //Sort by number of occurrences (largest to smallest)
        Collections.sort(nodeList);
        //create huffman tree
        HuffmanNode rootNode = createHuffmanTree(nodeList);
        return rootNode;

    }

    /**
     * Pack the elements in bytes and the number of occurrences into a HuffmanNode list
     *
     * @param bytes
     * @return
     */
    private static List<HuffmanNode> getNodeList(byte[] bytes) {
        List<HuffmanNode> nodeList = new ArrayList<>();
        Map<Byte, Integer> byteIntegerMap = new HashMap<>();
        for (byte b : bytes) {
            Integer count = byteIntegerMap.get(b);
            if (count == null) {
                byteIntegerMap.put(b, 1);
            } else {
                byteIntegerMap.put(b, count + 1);
            }
        }
        for (Map.Entry<Byte, Integer> item : byteIntegerMap.entrySet()) {
            Byte b = item.getKey();
            Integer weigth = item.getValue();
            HuffmanNode node = new HuffmanNode(b, weigth);
            nodeList.add(node);
        }
        return nodeList;
    }

    /**
     * create huffman tree by nodeList
     * here used algorithm by create optimal huffman tree
     *
     * @param nodeList
     * @return
     */
    private static HuffmanNode createHuffmanTree(List<HuffmanNode> nodeList) {
        int length = nodeList.size();
        while (length > 1) {
            HuffmanNode huffmanNode01 = nodeList.get(length - 1);
            HuffmanNode huffmanNode02 = nodeList.get(length - 2);
            HuffmanNode huffmanNodeNew = new HuffmanNode(null, huffmanNode01.getWeight() + huffmanNode02.getWeight());

            huffmanNodeNew.setLeftNode(huffmanNode01);
            huffmanNodeNew.setRightNode(huffmanNode02);

            nodeList.remove(huffmanNode01);
            nodeList.remove(huffmanNode02);
            nodeList.add(huffmanNodeNew);

            Collections.sort(nodeList);
            length = nodeList.size();
        }
        return nodeList.get(0);
    }

    /**
     * use huffman tree to create huffman code
     *
     * @param rootNode
     * @return
     */
    private static Map<Byte, String> createHuffmanCode(HuffmanNode rootNode) {
        StringBuffer sb = new StringBuffer();
        if (rootNode != null) {
            getCodes(rootNode.getLeftNode(), "0", sb);
            getCodes(rootNode.getRightNode(), "1", sb);
            return mapCode;
        }
        return null;
    }

    private static void getCodes(HuffmanNode node, String s, StringBuffer sb) {
        StringBuffer tempSb = new StringBuffer(sb);
        tempSb.append(s);
        if (node.getData() == null) {
            getCodes(node.getLeftNode(), "0", tempSb);
            getCodes(node.getRightNode(), "1", tempSb);
        } else {
            mapCode.put(node.getData(), tempSb.toString());
        }
    }

    /**
     * Obtain a linearized sequence after generating the Huffman tree
     *
     * @param rootNode
     */
    private static void getHufTable(HuffmanNode rootNode) {
        byte zero = 0;
        byte one = 1;
        if (rootNode.getData()!=null){
            HufTable.add(zero);
            HufTable.add(rootNode.getData());
        }else {
            HufTable.add(one);
            getHufTable(rootNode.getLeftNode());
            getHufTable(rootNode.getRightNode());
        }
    }

    /**
     * Encode the original bytes according to the Huffman encoding table
     *
     * @param bytes
     * @param huffmanCodeMap
     * @return
     */
    private static byte[] encodeByHuffmanCode(byte[] bytes, Map<Byte, String> huffmanCodeMap) {
        //Convert bytes to binary string
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            String str = huffmanCodeMap.get(b);
            sb.append(str);
        }

        int len = sb.length();
        significantLength = len;
        while(len % 8 != 0){
            sb.append("0");
            len = sb.length();
        }
        byte[] targetBytes = new byte[len/8];
        for (int i = 0; i < targetBytes.length; i++) {
            if ((i + 1) * 8 > len) {
                targetBytes[i] = (byte) Integer.parseInt(sb.substring(i * 8), 2);
            } else {
                targetBytes[i] = (byte) Integer.parseInt(sb.substring(i * 8, (i + 1) * 8), 2);
            }
        }
        return targetBytes;
    }

}
