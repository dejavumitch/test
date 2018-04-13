import java.util.ArrayList;
import java.util.List;

class Gate {
    private String name;
    private List<Gate> neig;
    private List<Integer> neigDistance;
    private Gate prev;
    private int dis;

    Gate(String name) {
        neig = new ArrayList<Gate>();
        neigDistance = new ArrayList<Integer>();
        prev = null;
        dis = Integer.MAX_VALUE;
        this.name = name;
    }

    void addNeig(Gate gate, Integer dis) {
        neig.add(gate);
        neigDistance.add(dis);
    }

    public String getName() {
        return name;
    }

    public List<Integer> getNeigDistance() {
        return neigDistance;
    }

    public List<Gate> getNeig() {
        return neig;
    }

    public Gate getPrev() {
        return prev;
    }

    public void setPrev(Gate prev) {
        this.prev = prev;
    }

    public int getDis() {
        return dis;
    }

    public void setDis(int dis) {
        this.dis = dis;
    }

    @Override
    public String toString() {
        return "Gate{" +
                "name='" + name + '\'' +
                ", dis=" + dis +
                '}';
    }
}