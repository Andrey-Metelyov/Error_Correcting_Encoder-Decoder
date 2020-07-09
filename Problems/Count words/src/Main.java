import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
    enum State {
        WAIT_NONSPACE, WAIT_SPACE
    };

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here
        char[] buffer = new char[32];

        State state = State.WAIT_NONSPACE;
        int wordcount = 0;
        int read = reader.read(buffer);
        while (read != -1) {
            for (int i = 0; i < read; i++) {
                char c = buffer[i];
                switch (state) {
                    case WAIT_NONSPACE:
                        if (Character.isAlphabetic(c)) {
                            state = State.WAIT_SPACE;
                            wordcount++;
                        }
                        break;
                    case WAIT_SPACE:
                        if (Character.isSpaceChar(c)) {
                            state = State.WAIT_NONSPACE;
                        }
                        break;
                    default:
                        break;
                }
            }
            read = reader.read(buffer);
        }
        reader.close();
        System.out.println(wordcount);
    }
}