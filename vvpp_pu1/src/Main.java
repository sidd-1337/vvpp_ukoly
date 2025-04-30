import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputFile;
        while (true) {
            System.out.print("Path to img: ");
            inputFile = scanner.nextLine();
            if (Image.isValidImageFile(inputFile)) break;
            System.out.println("Unsupported file ext or bad path.");
        }
        System.out.print("Enter the number of threads to blur: ");
        int threadCount = scanner.nextInt();
        scanner.close();

        Image img = Image.load(inputFile);
        Image blurred = img.applyBlur(5, threadCount);
        blurred.save("output.png");
    }
}
