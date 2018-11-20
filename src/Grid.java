import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class Grid extends Label {

    private Chess type;

    Grid (){
        type = Chess.None;
    }

    public Chess getType() {
        return type;
    }

    public void setType(Chess type) {
        switch (type){
            case King:
                break;
            case Adviser:
                break;
            case Minister:
                break;
            case Chariots:
                break;
            case Knight:
                break;
            case Cannon:
                break;
            case Pawn:
                break;
            case None:
                setGraphic(null);
                break;
        }
        this.type = type;
    }
}
