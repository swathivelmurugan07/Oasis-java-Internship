import java.util.Random;
import java.util.Scanner;
public class NumGuessGame{
  public static void main(String[] args)
  {
Scanner sc =new Scanner(System.in);
Random rand = new Random();
int notoGuess=rand.nextInt(100)+1;
int userGuess =0;
int attempts =0;
System.out.println("Welcome to Number Guess Game!");
System.out.println("Guess a no between 1 to 100 ");
while(userGuess!=notoGuess)
  {
    System.out.println("Enter your Guess :");
    userGuess = sc.nextInt();
    attempts++;
    if(userGuess<notoGuess)
    {
      System.out.println("Too low!Try Again.");
    }
    else if(userGuess>notoGuess)
    {
      System.out.println("Too high!Try Again.");
    }
    else
    {
      System.out.println("Congratulations!You Guessed" +attempts+ "attempts.");
    }
  }
sc.close();
}
}
 
 