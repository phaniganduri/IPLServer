package com.mcp;
public class Test {
    public static void main(String[] args) {
        int port = 12345;
        int poolSize = 10; // max simultaneous clients

        IPLServer server = new IPLServer(port, poolSize);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
