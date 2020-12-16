import java.util.Calendar;
import java.util.Scanner;

class Util {
  public static void main(String[] args) {
    // put your code here
    Scanner scanner = new Scanner(System.in);
    String name = scanner.next();
    System.out.println("Hello, " + name + "!");
  }

  static void print(String name) {
    int year = Calendar.getInstance().get(Calendar.YEAR);
    String s1 = String.format("Hello! My name is %s.", name);
    String s2 = String.format("I was created in %d.", year);
    String line = s1 + "\n" + s2;
    System.out.println(line);
  }
}
