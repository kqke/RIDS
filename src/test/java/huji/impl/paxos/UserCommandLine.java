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
                cmdListen();
                break;
            case "block":
                cmdBlock(Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2]));
                break;
            case "block_all":
                cmd_block_all(Integer.parseInt(splitStr[1]));
                break;
            case "unblock":
                cmdUnblock(Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2]));
                break;
            case "unblock_all":
                cmdUnblockAll(Integer.parseInt(splitStr[1]));
                break;
            case "crash":
                cmdCrash(Integer.parseInt(splitStr[1]));
                break;
            case "history":
                cmdHistory(Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2]));
                break;
            case "user":
                cmdUser(Integer.parseInt(splitStr[1]), splitStr[2]);
                break;
            default:
                System.out.println(str);
        }
    }

    /*
     * User message
     */

    private void cmdUser(int replica, String str) {
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

    private void cmdHistory(int replica, int start_storage) {
        replicas.get(replica).getCommitted(start_storage).forEach(
                (storage,value) -> System.out.println("storage: " + storage + ", value: " + value)
        );
    }

    /*
     * Crash
     */

    private void cmdCrash(int replica) {
        replicas.get(replica).shutdown();
    }

    /*
     * Omission
     */

    private void cmdBlock(int replica, int to_unblock) {
        replicas.get(replica).unblock(to_unblock);
    }

    private void cmd_block_all(int replica) {
        for ( int id : replicas.keySet() )
            cmdBlock(replica, id);
    }

    private void cmdUnblock(int replica, int to_block) {
        replicas.get(replica).block(to_block);
    }

    private void cmdUnblockAll(int replica) {
        for ( int id : replicas.keySet() )
            cmdUnblock(replica, id);
    }

    /*
     * Listen
     */

    private void cmdListen() {
        PaxosTest.logger.to_print = true;
        waitUntilPress();
        synchronized (System.out) {
            PaxosTest.logger.to_print = false;
        }
    }

    private void waitUntilPress() {
        int press = -1;
        while ( press < 0 ) {
            try {
                press = System.in.read();
            } catch (Exception ignored) {}
        }
    }
}
