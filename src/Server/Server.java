package Server;

public class Server {

    /**
     * Initialise the Connection listener of the server
     *
     * @param args arguments passed in when starting the java program.
     */
    public static void main(String args[]) {
        Listener l = new Listener();
        ServerCommandLine listen= new ServerCommandLine();
        l.listen(10069);
        listen.start();
    }
}



