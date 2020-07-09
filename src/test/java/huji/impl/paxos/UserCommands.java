package huji.impl.paxos;

import java.util.Scanner;

public class UserCommands implements Runnable {
    @Override
    public void run() {
        try(Scanner scan = new Scanner(System.in)){
            String s = "";
            while ( ! s.equals("exit") ) {
                s = scan.next();
                System.out.println(s);
            }
        } catch (Exception ignored){}
    }
}
