import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here
        String result = "";
        char[] arr = new char[32];
        int read = reader.read(arr);
        while (read != -1) {
            reverse(arr, read);
            result = String.valueOf(arr, 0, read) + result;
            read = reader.read(arr);
        }
        reader.close();
        System.out.println(result);
    }

    private static void reverse(char[] arr, int size) {
        for (int i = 0; i < size / 2; i++) {
            char tmp = arr[i];
            arr[i] = arr[size - 1 - i];
            arr[size - 1 - i] = tmp;
        }
    }
}