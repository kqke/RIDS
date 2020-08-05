package huji.impl.paxos;

import huji.impl.paxos.messages.PaxosMessage;
import huji.interfaces.Factory;

import java.util.Map;
import java.util.Scanner;

public class UserCommandLine<T extends Comparable<T>> implements Runnable {
    Map<Integer, PaxosTest<T>> replicas;
    final Factory<T,String> factory;

    public UserCommandLine(Map<Integer, PaxosTest<T>> replicas, Factory<T,String> factory) {
        this.replicas = replicas;
        this.factory = factory;
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
            case "user":
                cmd_user(Integer.parseInt(splitStr[1]), splitStr[2]);
                break;
            default:
                System.out.println(str);
        }
    }

    /*
     * User message
     */

    private void cmd_user(int replica, String str) {
        replicas.get(replica).receive(
                new PaxosMessage<>(
                        0,
                        replica,
                        factory.get(str),
                        true,
                        -1,
                        -1,
                        null
                )
        );
    }

    /*
     * History
     */

    private void cmd_history(int replica, int start_storage) {
        replicas.get(replica).get_committed(start_storage).forEach(
                (storage,value) -> System.out.println("storage: " + storage + ", value: " + value)
        );
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
