package com.huff;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class for file unCompression
 */
public class UncompressFile {

    // point to the current location of the file
    private static int p=0;
    // Temporary storage code table
    static Map<Byte, String> mapCode = new HashMap();

    public UncompressFile() {
        
    }

    /**
     * Uncompress a file
     */
    public void uncompress(File file)  {

        p=0;
        mapCode = new HashMap();
        
        String suffix = file.getName();
        suffix = suffix.substring(suffix.length()-3);
        if (!suffix.equals(".hu")){
            System.out.println("The suffix is wrong! ! Unable to decompress!");
            return;
        }
        FileInputStream in = null;
        byte[] FileCode = new byte[0];
        try {
            in = new FileInputStream(file);
            FileCode = new byte[in.available()];
            in.read(FileCode);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = file.getAbsolutePath();



        /**
         * 1. Get the magic number and verify that it is equal to 123456789! !
         */
        byte num1 = FileCode[0];
        byte[] MagicNumCode = new byte[num1];
        for (int i=0;i<num1;i++){
            MagicNumCode[i] = FileCode[i+1];
        }
        long magicNum = getLongNum(MagicNumCode);
        if (magicNum != 123456789){
            System.out.println("Wrong MagicNumber!!");
            return;
        }

        //Get and convert a linear Huffman sequence into a Huffman tree
        p = num1+1;
        HuffmanNode HuTree = getMapCode(FileCode);

        //Get the effective length of the compressed file
        long fileTrueLength;
        byte num2 = FileCode[p];
        p++;
        int num2int = num2 & 0xff;
        byte[] fileLengthCode = new byte[num2int];
        for (int i=0;i<num2int;i++){
            fileLengthCode[i] = FileCode[p];
            p++;
        }
        long fileLength = getLongNum(fileLengthCode);

        //Get map encoding according to Huffman tree
        Map<Byte, String> huffmanCode = createHuffmanCode(HuTree);

        byte[] source = decodeByHuffman(fileLength,FileCode,p, mapCode);

        //output file
        file.delete();
        path = path.substring(0,path.length()-3);
        OutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(source);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Convert base 256 to num of type long
     *
     * @param bytes
     * @return
     */
    private static long getLongNum(byte[] bytes) {

        int lens = bytes.length;
        int tempByte = bytes[lens-1] & 0xff;
        long ans=tempByte;
        for (int i=lens-2;i>=0;i--){
            tempByte = bytes[i] & 0xff;
            ans = ans*256+tempByte;
        }
        return ans;
    }

    /**
     * get map code by byte[]
     *
     * @param bytes
     * @return
     */
    public static HuffmanNode getMapCode(byte[] bytes){
        HuffmanNode T = new HuffmanNode(null,0);
        if(bytes[p]==0){
            T.setData(bytes[p+1]);
            p = p+2;
            return T;
        }else {
            p++;
            T.setLeftNode(getMapCode(bytes));
            T.setRightNode(getMapCode(bytes));
        }
        return T;
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
     * Decompress using Huffman encoding
     *
     * @param filelenth
     * @param tar
     * @param p
     * @param huffmenCode
     * @return
     */
    private static byte[] decodeByHuffman(long filelenth, byte[] tar, int p, Map<Byte, String> huffmenCode) {
        StringBuffer sb = new StringBuffer();
        //Convert byte[] to binary string
        for (int i = p; i < tar.length; i++) {
            sb.append(byteToString(tar[i]));
        }
        while (sb.length()!=filelenth){
            sb.delete(sb.length()-1,sb.length());
        }
        //Swap the key-value pairs in the Huffman coding table to facilitate the next query
        Map<String, Byte> temp = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmenCode.entrySet()) {
            temp.put(entry.getValue(), entry.getKey());
        }
        //Convert binary string to original data according to Huffman encoding table
        List<Byte> source = getSource(sb.toString(), temp);
        //convert collection to array
        byte[] byteSource = new byte[source.size()];
        for (int i = 0; i < byteSource.length; i++) {
            byteSource[i] = source.get(i);
        }
        return byteSource;
    }

    /**
     * convert byte type to string type
     *
     * @param b
     * @return
     */
    private static String byteToString(byte b) {
        //Expand 8 bits to 32 bits, which is convenient for 'OR' operation, and extract 8 bits from the original value
        int temp = b;
        temp |= 256;
        String str = Integer.toBinaryString(temp);
        return str.substring(str.length() - 8);
    }


    /**
     * Convert binary string to original data according to Huffman encoding table
     *
     * @param codeStr
     * @param byteMap
     * @return
     */
    private static List<Byte> getSource(String codeStr, Map<String, Byte> byteMap) {
        List<Byte> tempList = new ArrayList<>();
        int PcodeStr = 0;
        while (PcodeStr<codeStr.length()){
            int i=PcodeStr;
            for (;i<=codeStr.length();i++){
                String subs = codeStr.substring(PcodeStr,i);
                if (byteMap.keySet().contains(subs)){
                    tempList.add(byteMap.get(subs));
                    break;
                }
            }
            PcodeStr = i;
        }
        return tempList;
    }

}
