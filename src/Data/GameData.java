package Data;

import java.io.Serializable;

public class GameData implements Serializable {

    public String roomName;     //房間名稱
    public String requester;    //發出資料的玩家

    public int restOftime;      //發出資料玩家的剩餘時間

    public int from[];          //原座標
    public int to[];            //新座標
}
