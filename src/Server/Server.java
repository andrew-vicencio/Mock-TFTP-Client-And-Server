package Server;

public class Server {

    public static void main(String args[]) {
        Listener l = new Listener();
        l.listen(69);
    }

}

