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
        ImageView img;
        switch (type){
            case King:
                if (group.equals("black")){
                    img = new ImageView("img/KingBlack.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }else if (group.equals("red")){
                    img = new ImageView("img/KingRed.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }
                break;
            case Adviser:
                if (group.equals("black")){
                    img = new ImageView("img/AdviserBlack.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }else if (group.equals("red")){
                    img = new ImageView("img/AdviserRed.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }
                break;
            case Minister:
                if (group.equals("black")){
                    img = new ImageView("img/MinisterBlack.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }else if (group.equals("red")){
                    img = new ImageView("img/MinisterRed.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }
                break;
            case Chariots:
                if (group.equals("black")){
                    img = new ImageView("img/ChariotsBlack.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }else if (group.equals("red")){
                    img = new ImageView("img/ChariotsRed.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }
                break;
            case Knight:
                if (group.equals("black")){
                    img = new ImageView("img/KnightBlack.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }else if (group.equals("red")){
                    img = new ImageView("img/KnightRed.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }
                break;
            case Cannon:
                if (group.equals("black")){
                    img = new ImageView("img/CannonBlack.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }else if (group.equals("red")){
                    img = new ImageView("img/CannonRed.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }
                break;
            case Pawn:
                if (group.equals("black")){
                    img = new ImageView("img/PawnBlack.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }else if (group.equals("red")){
                    img = new ImageView("img/PawnRed.png");
                    img.setPreserveRatio(false);
                    img.setFitWidth(80);
                    img.setFitHeight(80);
                    setGraphic(img);
                }
                break;
            case None:
                setGraphic(null);
                break;
        }
    }
}
