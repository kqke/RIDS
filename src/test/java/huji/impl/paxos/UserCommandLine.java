package huji.impl.paxos;

import java.util.Scanner;

public class UserCommandLine implements Runnable {
    @Override
    public void run() {
        try(Scanner scan = new Scanner(System.in)){
            String str = "";
            while ( ! str.equals("exit") ) {
                str = scan.next();
                handle(str);
            }
        } catch (Exception ignored){}
    }

    /*
     * Handle
     */
    private void handle(String str) {
        String[] splitStr = str.split("\\s+");
        switch(splitStr[0]) {
            case "listen":
                break;
            case "get":
                break;
            default:
                System.out.println(str);
        }
    }
}
