import java.io.InputStream;

class Main {
    public static void main(String[] args) throws Exception {
        InputStream inputStream = System.in;
        byte[] arr = new byte[5];
        int cur = inputStream.read(arr);
        while (cur != -1) {
            for (int i = 0; i < cur; i++) {
                System.out.print((int) arr[i]);
            }
            cur = inputStream.read(arr);
        }
        inputStream.close();
    }
}