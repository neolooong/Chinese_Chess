public class ChessBoardRecord {
    private int from[];
    private int to[];
    private String group = "";
    private Chess deadChess = null;

    public ChessBoardRecord(int[] from, int[] to,String group, Chess deadChess) {
        this.from = from;
        this.to = to;
        this.group = group;
        this.deadChess = deadChess;
    }

    public int[] getFrom() {
        return from;
    }

    public void setFrom(int[] from) {
        this.from = from;
    }

    public int[] getTo() {
        return to;
    }

    public void setTo(int[] to) {
        this.to = to;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Chess getDeadChess() {
        return deadChess;
    }

    public void setDeadChess(Chess deadChess) {
        this.deadChess = deadChess;
    }
}
