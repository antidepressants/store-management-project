import java.util.Scanner;

public interface UI {
    static final Scanner scanner = new Scanner(System.in);

    public default int chooseFromList(String options[]) {
        for (int i = 0; i < options.length; i++) {
            System.out.println(" [" + Integer.toString(i + 1) + "] " + options[i]);
        }
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine();
            if (choice < 1 || choice > options.length)
                throw new ArithmeticException();
            return choice - 1;
        } catch (Exception e) {
            System.out.println("Invalid input");
        }
        return -1;
    }

    public default double validateDouble(double lBound, double hBound, String var) {
        boolean valid = true;
        double result = lBound;
        do {
            System.out.println(var + "(" + Double.toString(lBound) + " - " + Double.toString(hBound) + "): ");
            try {
                result = scanner.nextDouble();
                scanner.nextLine();
                if (result < lBound || result > hBound)
                    throw new ArithmeticException();
            } catch (Exception e) {
                System.out.println("Invalid input: " + var + "should be between " + Double.toString(lBound) + " and "
                        + Double.toString(hBound));
            }
        } while (!valid);
        return result;
    }
}
