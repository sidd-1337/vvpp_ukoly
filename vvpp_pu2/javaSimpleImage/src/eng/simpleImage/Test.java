package eng.simpleImage;

public class Test {
    private static class Result {
        int value;
        Result(int value) {
            this.value = value;
        }
    }
    
    public static void main(String[] args) {
        Result r = new Result(42);
        System.out.println(r.value);
    }
} 