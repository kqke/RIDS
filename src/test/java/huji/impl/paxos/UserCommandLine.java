package huji.impl.paxos;

import java.util.Map;
import java.util.Scanner;

public class UserCommandLine implements Runnable {
    Map<Integer, PaxosTest> replicas;

    public UserCommandLine( Map<Integer, PaxosTest> replicas ) {
        this.replicas = replicas;
    }

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
                cmd_listen();
                break;
            case "block":
                cmd_block(Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2]));
                break;
            case "block_all":
                cmd_block_all(Integer.parseInt(splitStr[1]));
                break;
            case "unblock":
                cmd_unblock(Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2]));
                break;
            case "unblock_all":
                cmd_unblock_all(Integer.parseInt(splitStr[1]));
                break;
            case "crash":
                cmd_crash(Integer.parseInt(splitStr[1]));
                break;
            case "history":
                cmd_history(Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2]));
                break;
            default:
                System.out.println(str);
        }
    }

    /*
     * History
     */

    private void cmd_history(int replica, int start_view) {
        replicas.get(replica);
        // TODO: print
    }

    /*
     * Crash
     */
    
    private void cmd_crash(int replica) {
        replicas.get(replica).shutdown();
    }

    /*
     * Omission
     */

    private void cmd_block(int replica, int to_unblock) {
        replicas.get(replica).unblock(to_unblock);
    }

    private void cmd_block_all(int replica) {
        for ( int id : replicas.keySet() )
            cmd_block(replica, id);
    }

    private void cmd_unblock(int replica, int to_block) {
        replicas.get(replica).block(to_block);
    }

    private void cmd_unblock_all(int replica) {
        for ( int id : replicas.keySet() )
            cmd_unblock(replica, id);
    }

    /*
     * Listen
     */

    private void cmd_listen() {
        PaxosTest.logger.to_print = true;
        wait_until_press();
        synchronized (System.out) {
            PaxosTest.logger.to_print = false;
        }
    }

    private void wait_until_press() {
        int press = -1;
        while ( press < 0 ) {
            try {
                press = System.in.read();
            } catch (Exception ignored) {}
        }
    }
}
