package com.mcp;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class IPLServer {

    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService pool;

    // Sample data
    private final List<String> teams = Arrays.asList("MI", "CSK", "RCB", "KKR", "SRH", "DC", "RR", "PBKS");
    private final List<String> schedule = Arrays.asList(
        "MI vs CSK - 1st April 7:30 PM",
        "RCB vs KKR - 2nd April 7:30 PM",
        "SRH vs DC - 3rd April 7:30 PM"
    );
    private final Map<String, String> playerInfo = Map.of(
        "Rohit", "Rohit Sharma - MI Captain, Opener",
        "Dhoni", "MS Dhoni - CSK Captain, Wicketkeeper",
        "Virat", "Virat Kohli - RCB Captain, Batsman",
        "Andre", "Andre Russell - KKR All-rounder"
    );

    public IPLServer(int port, int poolSize) {
        this.port = port;
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("IPL Server running on port " + port);

        while (!serverSocket.isClosed()) {
            Socket client = serverSocket.accept();
            System.out.println("Client connected: " + client.getInetAddress());
            pool.execute(new ClientHandler(client));
        }
    }

    public void stop() throws IOException {
        pool.shutdown();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket client;

        ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            ) {
                out.println("{\"message\":\"Welcome to IPL Stats Server\"}");

                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    // Parse JSON command manually (no library used here)
                    Map<String, String> request = parseJson(line);

                    if (request == null || !request.containsKey("command")) {
                        out.println("{\"error\":\"Invalid JSON or missing 'command'\"}");
                        continue;
                    }

                    String cmd = request.get("command").toLowerCase();

                    switch (cmd) {
                        case "getteams":
                            out.println(toJson("teams", teams));
                            break;

                        case "getschedule":
                            out.println(toJson("schedule", schedule));
                            break;

                        case "getplayer":
                            String name = request.get("name");
                            if (name == null) {
                                out.println("{\"error\":\"Missing 'name' parameter for getPlayer\"}");
                            } else {
                                String info = playerInfo.get(name);
                                if (info == null) {
                                    out.println("{\"error\":\"Player not found\"}");
                                } else {
                                    out.println(toJson("player", info));
                                }
                            }
                            break;

                        case "exit":
                            out.println("{\"message\":\"Goodbye!\"}");
                            client.close();
                            return;

                        default:
                            out.println("{\"error\":\"Unknown command\"}");
                    }
                }
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                try {
                    client.close();
                    System.out.println("Client disconnected.");
                } catch (IOException ignored) {}
            }
        }

        private Map<String, String> parseJson(String json) {
            // VERY simple parsing for demo only (not production safe)
            try {
                Map<String, String> map = new HashMap<>();
                json = json.trim();
                if (!json.startsWith("{") || !json.endsWith("}")) return null;
                json = json.substring(1, json.length() - 1).trim();

                if (json.isEmpty()) return map;

                String[] pairs = json.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split(":", 2);
                    if (kv.length != 2) return null;
                    String key = kv[0].trim().replaceAll("\"", "");
                    String value = kv[1].trim().replaceAll("\"", "");
                    map.put(key, value);
                }
                return map;
            } catch (Exception e) {
                return null;
            }
        }

        private String toJson(String key, Object value) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"").append(key).append("\":");

            if (value instanceof List) {
                List<?> list = (List<?>) value;
                sb.append("[");
                for (int i = 0; i < list.size(); i++) {
                    sb.append("\"").append(list.get(i)).append("\"");
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
            } else {
                sb.append("\"").append(value).append("\"");
            }
            sb.append("}");
            return sb.toString();
        }
    }
}
