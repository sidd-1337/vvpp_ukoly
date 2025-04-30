import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputFile;
        while (true) {
            System.out.print("Zadejte cestu k obrázku: ");
            inputFile = scanner.nextLine();
            if (Image.isValidImageFile(inputFile)) break;
            System.out.println("Chyba, špatná cesta k souboru anebo nepodporovaný formát.");
        }
        System.out.print("Zadejte počet vláken pro rozmazání: ");
        int threadCount = scanner.nextInt();
        scanner.close();

        Image img = Image.load(inputFile);
        Image blurred = img.applyBlur(5, threadCount);
        blurred.save("output.png");
    }
}
