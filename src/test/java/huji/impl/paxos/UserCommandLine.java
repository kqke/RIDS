package huji.impl.paxos;

import huji.impl.paxos.messages.PaxosMessage;
import huji.interfaces.Factory;
import huji.logger.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class UserCommandLine<T extends Comparable<T>> implements Runnable {
    Map<Integer, PaxosTest<T>> replicas;
    final Factory<T,String> factory;

    boolean is_run = true;

    public UserCommandLine(Map<Integer, PaxosTest<T>> replicas, Factory<T,String> factory) {
        this.replicas = replicas;
        this.factory = factory;
    }

    @Override
    public void run() {
        while (is_run)
            try(Scanner scan = new Scanner(System.in)) {
                String str = "";
                while ( ! str.equals("exit") ) {
                    str = scan.nextLine();
                    handle(str);
                }
                is_run = false;
            } catch (Exception e) {
                e.printStackTrace();
                is_run = false;
            }
    }

    /*
     * Handle
     */
    private void handle(String str) {
        String[] splitStr = str.split("\\s+");
        switch(splitStr[0]) {
            case "listen":
                cmdListen(Integer.parseInt(splitStr[1]));
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
                cmdUser(Integer.parseInt(splitStr[1]), concat(splitStr,2));
                break;
            case "help":
                cmdHelp();
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

    private String concat ( String[] splits, int idx ) {
        String output = splits[idx];
        for (int i = idx + 1; i < splits.length; i++) {
            output = output.concat(splits[i]);
        }
        return output;
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
        replicas.get(replica).block(to_unblock);
    }

    private void cmd_block_all(int replica) {
        for ( int id : replicas.keySet() )
            cmdBlock(replica, id);
    }

    private void cmdUnblock(int replica, int to_block) {
        replicas.get(replica).unblock(to_block);
    }

    private void cmdUnblockAll(int replica) {
        for ( int id : replicas.keySet() )
            cmdUnblock(replica, id);
    }

    /*
     * Listen
     */

    private void cmdListen(int type) {
        Logger.Conditions condition = Logger.Conditions.values()[type];
        PaxosTest.logger.conditionTrue( condition );
        waitUntilPress();
        synchronized (System.out) {
            PaxosTest.logger.conditionFalse( condition );
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

    /*
     * Help
     */

    private void cmdHelp() {
        System.out.println("listen [type] \t\t 0 = all, 1 = protocol, 2 = commits");
        System.out.println("block [replica] [replica to block]");
        System.out.println("block_all [replica]");
        System.out.println("unblock [replica] [replica to unblock]");
        System.out.println("unblock_all [replica]");
        System.out.println("crash [replica] \t\t Cannot undo!!");
        System.out.println("history [replica] [start storage]");
        System.out.println("user [replica] [...string]");
    }
}
