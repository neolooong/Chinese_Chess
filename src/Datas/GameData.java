package Datas;

import java.io.Serializable;

public class GameData implements Serializable {

    public enum Behavior{
        Register, CheckIn, CheckOut, Message, Ready, GameStart, Move, RequestUnMove, PermitUnMove, RejectUnMove, Surrender, GameEnd
    }
//    必填
    public String roomName;     //房間名稱
    public String source;   //發出資料的角色
    public Behavior behavior;     //行為
//    必填

    public String message;//訊息

//    checkIn
    public String players[];
//    checkIn

//    move
    public int from[];          //原座標
    public int to[];            //新座標
//    move

    public GameData(String roomName, String source, Behavior behavior){
        this.roomName = roomName;
        this.source = source;
        this.behavior = behavior;
    }
}
