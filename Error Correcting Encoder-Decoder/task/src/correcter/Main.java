package correcter;

import java.io.*;
import java.util.*;

interface Message {
    public void encode();
    public void decode();
    public void addErrors();
}

class ByteMessage implements Message {
    byte[] data;

    public ByteMessage(byte[] buffer) {
        this.data = Arrays.copyOf(buffer, buffer.length);
    }

    public void encode() {
        String expand = "";
        String parity = "";
        byte[] encoded = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            byte[] bits = {
                    (byte) ((data[i] & 0b00000001)),
                    (byte) ((data[i] & 0b00000010) >> 1),
                    (byte) ((data[i] & 0b00000100) >> 2),
                    (byte) ((data[i] & 0b00001000) >> 3),
                    (byte) ((data[i] & 0b00010000) >> 4),
                    (byte) ((data[i] & 0b00100000) >> 5),
                    (byte) ((data[i] & 0b01000000) >> 6),
                    (byte) ((data[i] & 0b10000000) >> 7)};

            encoded[2 * i] |= bits[7] << 5;
            encoded[2 * i] |= bits[6] << 3;
            encoded[2 * i] |= bits[5] << 2;
            encoded[2 * i] |= bits[4] << 1;

            encoded[2 * i + 1] |= bits[3] << 5;
            encoded[2 * i + 1] |= bits[2] << 3;
            encoded[2 * i + 1] |= bits[1] << 2;
            encoded[2 * i + 1] |= bits[0] << 1;

            expand += String.format("..%d.%d%d%d. ..%d.%d%d%d. ",
                    bits[7], bits[6], bits[5], bits[4], bits[3], bits[2], bits[1], bits[0]);

            byte p1 = (byte) ((bits[7] + bits[6] + bits[4]) % 2);
            byte p2 = (byte) ((bits[7] + bits[5] + bits[4]) % 2);
            byte p4 = (byte) ((bits[6] + bits[5] + bits[4]) % 2);

            encoded[2 * i] |= p1 << 7;
            encoded[2 * i] |= p2 << 6;
            encoded[2 * i] |= p4 << 4;
            parity += String.format("%d%d%d%d%d%d%d0 ",
                    p1, p2, bits[7], p4, bits[6], bits[5], bits[4]);

            p1 = (byte) ((bits[3] + bits[2] + bits[0]) % 2);
            p2 = (byte) ((bits[3] + bits[1] + bits[0]) % 2);
            p4 = (byte) ((bits[2] + bits[1] + bits[0]) % 2);

            encoded[2 * i + 1] |= p1 << 7;
            encoded[2 * i + 1] |= p2 << 6;
            encoded[2 * i + 1] |= p4 << 4;
            parity += String.format("%d%d%d%d%d%d%d0 ",
                    p1, p2, bits[3], p4, bits[2], bits[1], bits[0]);
        }
        System.out.println("expand: " + expand);
        System.out.println("parity: " + parity);
        data = encoded;
    }

    public void decode() {
        byte[] decoded = new byte[data.length / 2];
        for (int i = 0; i < decoded.length; i++) {
            byte[] bits = {
                    (byte) ((data[2 * i] & 0b00000001)),
                    (byte) ((data[2 * i] & 0b00000010) >> 1),
                    (byte) ((data[2 * i] & 0b00000100) >> 2),
                    (byte) ((data[2 * i] & 0b00001000) >> 3),
                    (byte) ((data[2 * i] & 0b00010000) >> 4),
                    (byte) ((data[2 * i] & 0b00100000) >> 5),
                    (byte) ((data[2 * i] & 0b01000000) >> 6),
                    (byte) ((data[2 * i] & 0b10000000) >> 7)};
            byte p1 = (byte) ((bits[5] + bits[3] + bits[1]) % 2);
            byte p2 = (byte) ((bits[5] + bits[2] + bits[1]) % 2);
            byte p4 = (byte) ((bits[3] + bits[2] + bits[1]) % 2);
            int errorBit = 0;
            if (bits[7] != p1) {
                errorBit += 1;
            }
            if (bits[6] != p2) {
                errorBit += 2;
            }
            if (bits[4] != p4) {
                errorBit += 4;
            }
            if (errorBit != 0) {
                bits[8 - errorBit] = (byte) (~bits[8 - errorBit] & 0b1);
            }
            decoded[i] |= bits[5] << 7;
            decoded[i] |= bits[3] << 6;
            decoded[i] |= bits[2] << 5;
            decoded[i] |= bits[1] << 4;
            bits[0] = (byte) ((data[2 * i + 1] & 0b00000001));
            bits[1] = (byte) ((data[2 * i + 1] & 0b00000010) >> 1);
            bits[2] = (byte) ((data[2 * i + 1] & 0b00000100) >> 2);
            bits[3] = (byte) ((data[2 * i + 1] & 0b00001000) >> 3);
            bits[4] = (byte) ((data[2 * i + 1] & 0b00010000) >> 4);
            bits[5] = (byte) ((data[2 * i + 1] & 0b00100000) >> 5);
            bits[6] = (byte) ((data[2 * i + 1] & 0b01000000) >> 6);
            bits[7] = (byte) ((data[2 * i + 1] & 0b10000000) >> 7);
            p1 = (byte) ((bits[5] + bits[3] + bits[1]) % 2);
            p2 = (byte) ((bits[5] + bits[2] + bits[1]) % 2);
            p4 = (byte) ((bits[3] + bits[2] + bits[1]) % 2);
            errorBit = 0;
            if (bits[7] != p1) {
                errorBit += 1;
            }
            if (bits[6] != p2) {
                errorBit += 2;
            }
            if (bits[4] != p4) {
                errorBit += 4;
            }
            if (errorBit != 0) {
                bits[8 - errorBit] = (byte) (~bits[8 - errorBit] & 0b1);
            }
            decoded[i] |= bits[5] << 3;
            decoded[i] |= bits[3] << 2;
            decoded[i] |= bits[2] << 1;
            decoded[i] |= bits[1];
        }

        data = decoded;
    }

    public void addErrors() {
        for (int i = 0; i < data.length; i++) {
            Random random = new Random();
            int bit = random.nextInt(8);
            data[i] ^= (1 << bit);
        }
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < data.length; i++) {
            res += Integer.toString(data[i], 16) + " ";
        }
        return res;
    }
}

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write a mode: ");
        String mode = scanner.nextLine();
        switch (mode) {
            case "encode":
                encode();
                break;
            case "decode":
                decode();
                break;
            case "send":
                send();
                break;
            default:
                System.out.println("Unknown command '" + mode + "'");
        }
    }

    private static void send() throws IOException {
        File file = new File("encoded.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];

        int read = fileInputStream.read(buffer);
        fileInputStream.close();
        ByteMessage message = new ByteMessage(buffer);
        message.addErrors();

        FileOutputStream writer = new FileOutputStream("received.txt");
        writer.write(message.getData());
        writer.close();
    }

    private static void decode() throws IOException {
        File file = new File("received.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];

        int read = fileInputStream.read(buffer);
        fileInputStream.close();
        ByteMessage message = new ByteMessage(buffer);
        message.decode();

        FileOutputStream writer = new FileOutputStream("decoded.txt");
        writer.write(message.getData());
        writer.close();
    }

    private static void encode() throws IOException {
        File file = new File("send.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];

        int read = fileInputStream.read(buffer);
        fileInputStream.close();

        System.out.println("\nsend.txt:");
        System.out.println("text view: " + new String(buffer));
        System.out.println("hex view:" + hexView(buffer));
        System.out.println("bin view:" + binView(buffer));

        System.out.println("\nencoded.txt:");

        ByteMessage message = new ByteMessage(buffer);
        message.encode();
        FileOutputStream writer = new FileOutputStream("encoded.txt");
        writer.write(message.getData());
        writer.close();
        System.out.println("hex view:" + hexView(message.getData()));
    }

    private static String binView(byte[] buffer) {
        String res = "";
        for (int i = 0; i < buffer.length; i++) {
            res += " " + byteToBinaryString(buffer[i]);
        }
        return res;
    }

    private static String hexView(byte[] buffer) {
        String res = "";
        for (int i = 0; i < buffer.length; i++) {
            res += " " + String.format("%2X", buffer[i] & 0xFF).replace(' ', '0');
        }
        return res;
    }

    private static String byteToBinaryString(byte num) {
        char[] res = new char[8];
        for (int i = 7; i >= 0; i--) {
            res[7 - i] = ((num >> i) & 0b1) > 0 ? '1' : '0';
        }
        return String.valueOf(res);
    }
}
