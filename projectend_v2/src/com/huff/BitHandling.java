package com.huff;
import java.io.*;

/**
 * This class shows how to use bitwise operators like '<<' and '&' for reading
 * (or writing) bits in (or from) a byte value.
 */
public class BitHandling {

    // constants
    private static final int HALF_BYTE   = 128;
    private static final int MAX_BYTE    = 255;
    private static final int BYTE_LENGTH =   8;

    // just for interactive integer reading
    private static BufferedReader in = 
        new BufferedReader(new InputStreamReader(System.in),64);

    private static String linein = null;

    /**
     * This method reads bits from the keyboard and write them into
     * a byte.
     */
    public static void writeByte() {
        writeByteInfo();
        int a_byte = 0;
        String binary = "";

        for ( int i = 0; i < BYTE_LENGTH; ) {
            int a_bit = readInt("bit #" + (BYTE_LENGTH - i) + " = ");
            if ( a_bit == 1 || a_bit == 0 ) {
                a_byte = a_byte << 1; // shifts one bit to the left
                if ( a_bit == 1 ) {
                    a_byte++;         // lowest bit (on the right) is 1
                    binary = binary + '1';
                }
                else {                // lowest bit (on the right) is 0
                    binary = binary + '0';
                }
                i++;
            }
            else {
                System.out.println("wrong bit value, please enter 0 or 1");
            }
        }
        System.out.println(binary + " = " + a_byte);
    }

    /**
     * This method reads a byte value b such that 0 <= b < 256
     * and prints out its binary representation as an 8 bits sequence.
     */
    public static void readByte() {
        readByteInfo();
        int b;

        do {
            b = readInt("byte value = ");
            if ( b < 0 || b > MAX_BYTE ) {
                System.out.println("wrong byte value, please enter a value b");
                System.out.println("such that 0 <= b < 256");
            }
        } while ( b < 0 || b > MAX_BYTE );

        System.out.print(b + " = ");

        for ( int i = 0; i < BYTE_LENGTH; i++ ) {
            if ( ( HALF_BYTE & b ) == HALF_BYTE ) 
                System.out.print('1'); // highest bit (on the left) is 1
            else
                System.out.print('0'); // highest bit (on the left) is 0
            b = b << 1; // shifts one bit to the left
        }

        System.out.println();
    }

    /**
     * Prints out a message about the writeByte method.
     */
    private static void writeByteInfo() {
        System.out.println("The method writeByte reads the 8 bits (0 or 1)");
        System.out.println("#8, #7, #6, #5, #4, #3, #2 and #1, and prints out");
        System.out.println("the corresponding byte value:");
        System.out.println("_________________________");
        System.out.println("|#8|#7|#6|#5|#4|#3|#2|#1|");
        System.out.println("-------------------------");
        System.out.println();
    }

    /**
     * Prints out a message about the readByte method.
     */
    private static void readByteInfo() {
        System.out.println("The method readByte reads a byte value and prints");
        System.out.println("out its binary representation:");
        System.out.println();
    }

    /**
     * Reads an int value from the keyboard after prompting with 'prompt'.
     */
    private static int readInt(String prompt) {
        System.out.print(prompt);
        try {
            linein = in.readLine();
        }
        catch (IOException ioexp) {
            System.err.println("error while reading");
            System.exit(1);
        }
        try {
            return (Integer.valueOf(linein)).intValue();
        }
        catch (NumberFormatException nfexp) {
            System.err.println("bad int value");
            System.exit(1);
        }
        return 0;
    }

    /**
     * The main method calls the writeByte and readByte methods.
     */
    public static void main(String[] args) {
    	while (true) {
    		System.out.println();
    		writeByte();
    		System.out.println();
    		readByte();
    		System.out.println();
    		System.out.println();
    	}
    }
}
