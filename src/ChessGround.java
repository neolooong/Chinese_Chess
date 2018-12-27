import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class ChessGround extends Label {
    private int positionX, positionY;
    private Chess type = Chess.None;
    private String group = "";

    ChessGround(){
    }

    ChessGround(int positionX, int positionY){
        setAlignment(Pos.CENTER);
        setFont(new Font(15));
        setTextFill(Paint.valueOf("white"));
        setContentDisplay(ContentDisplay.CENTER);
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public int getX() {
        return positionX;
    }

    public int getY() {
        return positionY;
    }

    public void setUpChess(String group, Chess type){
        this.group = group;
        this.type = type;
        changeImg();
    }

    public Chess getType() {
        return type;
    }

    public String getGroup() {
        return group;
    }

    private void changeImg(){
        ImageView img = new ImageView();
        switch (type){
            case King:
                if (group.equals("black")){
                    img = new ImageView("img/KingBlack.png");
                }else if (group.equals("red")){
                    img = new ImageView("img/KingRed.png");
                }
                break;
            case Adviser:
                if (group.equals("black")){
                    img = new ImageView("img/AdviserBlack.png");
                }else if (group.equals("red")){
                    img = new ImageView("img/AdviserRed.png");
                }
                break;
            case Minister:
                if (group.equals("black")){
                    img = new ImageView("img/MinisterBlack.png");
                }else if (group.equals("red")){
                    img = new ImageView("img/MinisterRed.png");
                }
                break;
            case Chariots:
                if (group.equals("black")){
                    img = new ImageView("img/ChariotsBlack.png");
                }else if (group.equals("red")){
                    img = new ImageView("img/ChariotsRed.png");
                }
                break;
            case Knight:
                if (group.equals("black")){
                    img = new ImageView("img/KnightBlack.png");
                }else if (group.equals("red")){
                    img = new ImageView("img/KnightRed.png");
                }
                break;
            case Cannon:
                if (group.equals("black")){
                    img = new ImageView("img/CannonBlack.png");
                }else if (group.equals("red")){
                    img = new ImageView("img/CannonRed.png");
                }
                break;
            case Pawn:
                if (group.equals("black")){
                    img = new ImageView("img/PawnBlack.png");
                }else if (group.equals("red")){
                    img = new ImageView("img/PawnRed.png");
                }
                break;
            case None:
                setGraphic(null);
                break;
        }
        img.setPreserveRatio(false);
        img.setFitWidth(80);
        img.setFitHeight(80);
        setGraphic(img);
    }
}
