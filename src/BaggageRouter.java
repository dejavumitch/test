import java.io.*;
import java.util.*;

public class BaggageRouter {
    private static HashMap<String, Gate> gateGraph;
    private static HashMap<String, Gate> flightGateMap;

    BaggageRouter() {
        gateGraph = new HashMap<>();
        flightGateMap = new HashMap<>();
    }

    /**
     * interface
     */
    public void run() {
        readFileAndProcess();
    }

    private void readFileAndProcess() {
        try (BufferedReader bufferedReader =
                     new BufferedReader(new FileReader(Config.INPUT_FILE_NAME));
             OutputStreamWriter osw =
                     new OutputStreamWriter(new FileOutputStream(new File(Config.OUTPUT_FILE_NAME)))) {

            String line;
            int status = -1;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    status++;
                    continue;
                }
                switch (status) {
                    case 0:
                        buildGraph(line);
                        break;
                    case 1:
                        aggregateGates(line);
                        break;
                    case 2:
                        registerBag(line, osw);
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] processLine(String line) {
        return line.split(" ");
    }

    /**
     * build a graph of gates
     *
     * @param line incoming gates convey info
     */
    private void buildGraph(String line) {
        String[] input = processLine(line);
        String gate1 = input[0];
        String gate2 = input[1];
        int distance = Integer.parseInt(input[2]);
        if (!gateGraph.containsKey(gate1)) {
            gateGraph.put(gate1, new Gate(gate1));
        }
        if (!gateGraph.containsKey(gate2)) {
            gateGraph.put(gate2, new Gate(gate2));
        }
        Gate g1 = gateGraph.get(gate1);
        Gate g2 = gateGraph.get(gate2);
        g1.addNeig(g2, distance);
        g2.addNeig(g1, distance);

    }

    /**
     * map flight to gate
     *
     * @param line incoming flight-gate info
     */
    private void aggregateGates(String line) {
        String[] input = processLine(line);
        String flight = input[0];
        Gate gate = gateGraph.get(input[1]);
        flightGateMap.put(flight, gate);
    }

    /**
     * read bag info and calculate the best route given starting and ending gates
     *
     * @param line incoming bag-arriving gate-flight info
     * @param osw  output stream
     */
    private void registerBag(String line, OutputStreamWriter osw) throws IOException {
        String[] input = processLine(line);

        String bagId = input[0];
        Gate start = gateGraph.get(input[1]);
        String flight = input[2];
        Gate end = flight.equals("ARRIVAL") ? gateGraph.get("BaggageClaim") : flightGateMap.get(flight);

        // calculate route and write to output file
        calculateRoute(start, end);
        osw.write(formatAndWriteToFile(bagId, start, end));
    }

    /**
     * formatting before write to output file
     *
     * @param bagId ID of a bag
     * @param start starting gate
     * @param end   ending gate
     */
    private String formatAndWriteToFile(String bagId, Gate start, Gate end) {
        StringBuilder sb = new StringBuilder(bagId);
        sb.append(" ");
        // System.out.println("    route: " + route);
        Gate gate = end;
        Deque<String> routeStack = new ArrayDeque<>();
        while (gate != start) {
            routeStack.push(gate.getName());
            gate = gate.getPrev();
        }
        routeStack.push(start.getName());
        while (!routeStack.isEmpty()) {
            sb.append(routeStack.pop());
            sb.append(" ");
        }
        sb.append(": ");
        sb.append(end.getDis());
        sb.append("\n");
        return sb.toString();
    }

    /**
     * calculate shortest route, applying Dijkstra's Algorithm
     *
     * @param start starting gate
     * @param end   ending gate
     */
    private void calculateRoute(Gate start, Gate end) {
        PriorityQueue<Gate> gateQueue = new PriorityQueue<>(Comparator.comparing(Gate::getDis));
        // add neighbors of start Gate to minHeap
        for (int i = 0; i < start.getNeig().size(); i++) {
            Gate gate = start.getNeig().get(i);
            gate.setDis(start.getNeigDistance().get(i));
            gate.setPrev(start);
            gateQueue.offer(gate);
        }
        // add the rest Gates to minHeap
        for (Gate gate : gateGraph.values()) {
            if (gate == start || start.getNeig().contains(gate)) {
                continue;
            }
            gate.setDis(Integer.MAX_VALUE);
            gate.setPrev(null);
            gateQueue.offer(gate);
        }

        while (!gateQueue.isEmpty()) {
            Gate cur = gateQueue.poll();
            if (cur == end) {
                break;
            }
            for (int i = 0; i < cur.getNeig().size(); i++) {
                Gate neig = cur.getNeig().get(i);
                if (!gateQueue.contains(neig)) {
                    continue;
                }
                int alt = cur.getDis() + cur.getNeigDistance().get(i);
                if (alt < neig.getDis()) {
                    neig.setDis(alt);
                    neig.setPrev(cur);
                    gateQueue.remove(neig);
                    gateQueue.offer(neig);
                }
            }
        }
    }

}