import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class ChessBoard {
    public Pane pane;
    public GridPane Grid;
    public ChessGround grounds[][] = new ChessGround[9][10];
    public ChessGround targetChess = new ChessGround();

    //    滑鼠點擊
    public EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            ChessGround eventChess = (ChessGround) event.getSource();
            if (targetChess.getType() == Chess.None){
                targetChess = eventChess;
                System.out.println("Target is " + targetChess.getType() + " now.");
            }else {
                if (targetChess.getGroup().equals(eventChess.getGroup())){
                    targetChess = eventChess;
                    System.out.println("Change target to " + targetChess.getType() + ".");
                }else {
                    switch (targetChess.getType()){
                        case King:
                            if (targetChess.getX() + 1 < 6 && targetChess.getX() + 1 == eventChess.getX() && targetChess.getY() == eventChess.getY()){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 1 > 2 && targetChess.getX() - 1 == eventChess.getX() && targetChess.getY() == eventChess.getY()){
                                move(true, eventChess);
                            }else if (targetChess.getY() + 1 == eventChess.getY() && targetChess.getX() == eventChess.getX()){
                                move(true, eventChess);
                            }else if (targetChess.getY() - 1 > 6 && targetChess.getY() - 1 == eventChess.getY() && targetChess.getX() == eventChess.getX()){
                                move(true, eventChess);
                            }else {
                                move(false, eventChess);
                            }
                            break;
                        case Adviser:
                            if (targetChess.getX() + 1 < 6 && targetChess.getX() + 1 == eventChess.getX() && targetChess.getY() + 1 == eventChess.getY()){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 1 > 2 && targetChess.getX() - 1 == eventChess.getX() && targetChess.getY() + 1 == eventChess.getY()){
                                move(true, eventChess);
                            }else if (targetChess.getX() + 1 < 6 && targetChess.getX() + 1 == eventChess.getX() && targetChess.getY() - 1 > 6 && targetChess.getY() - 1 == eventChess.getY()){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 1 > 2 && targetChess.getX() - 1 == eventChess.getX() && targetChess.getY() - 1 > 6 && targetChess.getY() - 1 == eventChess.getY()){
                                move(true, eventChess);
                            }else {
                                move(false, eventChess);
                            }
                            break;
                        case Minister:
                            if (targetChess.getX() + 2 == eventChess.getX() && targetChess.getY() + 2 == eventChess.getY() && grounds[targetChess.getX() + 1][targetChess.getY() + 1].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 2 == eventChess.getX() && targetChess.getY() + 2 == eventChess.getY() && grounds[targetChess.getX() - 1][targetChess.getY() + 1].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() + 2 == eventChess.getX() && targetChess.getY() - 2 > 4 && targetChess.getY() - 2 == eventChess.getY() && grounds[targetChess.getX() + 1][targetChess.getY() - 1].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 2 == eventChess.getX() && targetChess.getY() - 2 > 4 && targetChess.getY() - 2 == eventChess.getY() && grounds[targetChess.getX() - 1][targetChess.getY() - 1].getType() == Chess.None){
                                move(true, eventChess);
                            }else {
                                move(false, eventChess);
                            }
                            break;
                        case Chariots:
                            if (targetChess.getX() == eventChess.getX()){
                                if (targetChess.getY() > eventChess.getY()){
                                    for (int i = targetChess.getY() - 1; i >= eventChess.getY(); i--){
                                        if (i == eventChess.getY()){
                                            move(true, eventChess);
                                        }else if (grounds[targetChess.getX()][i].getType() != Chess.None){
                                            move(false, eventChess);
                                            break;
                                        }
                                    }
                                }else {
                                    for (int i = targetChess.getY() + 1; i <= eventChess.getY(); i++){
                                        if (i == eventChess.getY()){
                                            move(true, eventChess);
                                        }else if (grounds[targetChess.getX()][i].getType() != Chess.None){
                                            move(false, eventChess);
                                            break;
                                        }
                                    }
                                }
                            }else if (targetChess.getY() == eventChess.getY()){
                                if (targetChess.getX() > eventChess.getX()){
                                    for (int i = targetChess.getX() - 1; i >= eventChess.getX(); i--){
                                        if (i == eventChess.getX()){
                                            move(true, eventChess);
                                        }else if (grounds[i][targetChess.getY()].getType() != Chess.None){
                                            move(false, eventChess);
                                            break;
                                        }
                                    }
                                }else {
                                    for (int i = targetChess.getX() + 1; i <= eventChess.getX(); i++){
                                        if (i == eventChess.getX()){
                                            move(true, eventChess);
                                        }else if (grounds[i][targetChess.getY()].getType() != Chess.None){
                                            move(false, eventChess);
                                            break;
                                        }
                                    }
                                }
                            }else {
                                move(false, eventChess);
                            }
                            break;
                        case Knight:
                            if (targetChess.getX() + 2 == eventChess.getX() && targetChess.getY() + 1 == eventChess.getY() && grounds[targetChess.getX() + 1][targetChess.getY()].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() + 2 == eventChess.getX() && targetChess.getY() - 1 == eventChess.getY() && grounds[targetChess.getX() + 1][targetChess.getY()].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() + 1 == eventChess.getX() && targetChess.getY() + 2 == eventChess.getY() && grounds[targetChess.getX()][targetChess.getY() + 1].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() + 1 == eventChess.getX() && targetChess.getY() - 2 == eventChess.getY() && grounds[targetChess.getX()][targetChess.getY() - 1].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 1 == eventChess.getX() && targetChess.getY() + 2 == eventChess.getY() && grounds[targetChess.getX()][targetChess.getY() + 1].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 1 == eventChess.getX() && targetChess.getY() - 2 == eventChess.getY() && grounds[targetChess.getX()][targetChess.getY() - 1].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 2 == eventChess.getX() && targetChess.getY() + 1 == eventChess.getY() && grounds[targetChess.getX() - 1][targetChess.getY()].getType() == Chess.None){
                                move(true, eventChess);
                            }else if (targetChess.getX() - 2 == eventChess.getX() && targetChess.getY() - 1 == eventChess.getY() && grounds[targetChess.getX() - 1][targetChess.getY()].getType() == Chess.None){
                                move(true, eventChess);
                            }else {
                                move(false, eventChess);
                            }
                            break;
                        case Cannon:
                            if (eventChess.getType() == Chess.None){
                                if (targetChess.getX() == eventChess.getX()){
                                    if (targetChess.getY() > eventChess.getY()){
                                        for (int i = targetChess.getY() - 1; i >= eventChess.getY(); i--){
                                            if (grounds[targetChess.getX()][i].getType() != Chess.None){
                                                move(false, eventChess);
                                                break;
                                            }
                                            if (i == eventChess.getY()){
                                                move(true, eventChess);
                                            }
                                        }
                                    }else {
                                        for (int i = targetChess.getY() + 1; i <= eventChess.getY(); i++){
                                            if (grounds[targetChess.getX()][i].getType() != Chess.None){
                                                move(false, eventChess);
                                                break;
                                            }
                                            if (i == eventChess.getY()){
                                                move(true, eventChess);
                                            }
                                        }
                                    }
                                }else if (targetChess.getY() == eventChess.getY()){
                                    if (targetChess.getX() > eventChess.getX()){
                                        for (int i = targetChess.getX() - 1; i >= eventChess.getX(); i--){
                                            if (grounds[i][targetChess.getY()].getType() != Chess.None){
                                                move(false, eventChess);
                                                break;
                                            }
                                            if (i == eventChess.getX()){
                                                move(true, eventChess);
                                            }
                                        }
                                    }else {
                                        for (int i = targetChess.getX() + 1; i <= eventChess.getX(); i++){
                                            if (grounds[i][targetChess.getY()].getType() != Chess.None){
                                                move(false, eventChess);
                                                break;
                                            }
                                            if (i == eventChess.getX()){
                                                move(true, eventChess);
                                            }
                                        }
                                    }
                                }else {
                                    move(false, eventChess);
                                }
                            }else {
                                int chessNum = 0;
                                if (targetChess.getX() == eventChess.getX()){
                                    if (targetChess.getY() > eventChess.getY()){
                                        for (int i = targetChess.getY() - 1; i > eventChess.getY(); i--){
                                            if (grounds[targetChess.getX()][i].getType() != Chess.None){
                                                chessNum++;
                                            }
                                            if (i == eventChess.getY() + 1){
                                                if (chessNum == 1){
                                                    move(true, eventChess);
                                                }else {
                                                    move(false, eventChess);
                                                }
                                            }
                                        }
                                    }else {
                                        for (int i = targetChess.getY() + 1; i < eventChess.getY(); i++){
                                            if (grounds[targetChess.getX()][i].getType() != Chess.None){
                                                chessNum++;
                                            }
                                            if (i == eventChess.getY() - 1){
                                                if (chessNum == 1){
                                                    move(true, eventChess);
                                                }else {
                                                    move(false, eventChess);
                                                }
                                            }
                                        }
                                    }
                                }else if (targetChess.getY() == eventChess.getY()){
                                    if (targetChess.getX() > eventChess.getX()){
                                        for (int i = targetChess.getX() - 1; i > eventChess.getX(); i--){
                                            if (grounds[i][targetChess.getY()].getType() != Chess.None){
                                                chessNum++;
                                            }
                                            if (i == eventChess.getX() + 1){
                                                if (chessNum == 1){
                                                    move(true, eventChess);
                                                }else {
                                                    move(false, eventChess);
                                                }
                                            }
                                        }
                                    }else {
                                        for (int i = targetChess.getX() + 1; i < eventChess.getX(); i++){
                                            if (grounds[i][targetChess.getY()].getType() != Chess.None){
                                                chessNum++;
                                            }
                                            if (i == eventChess.getX() - 1){
                                                if (chessNum == 1){
                                                    move(true, eventChess);
                                                }else {
                                                    move(false, eventChess);
                                                }
                                            }
                                        }
                                    }
                                }else {
                                    move(false, eventChess);
                                }
                            }
                            break;
                        case Pawn:
                            if (targetChess.getY() > 4){ //未過河
                                if (targetChess.getX() == eventChess.getX() && targetChess.getY() - 1 == eventChess.getY()){
                                    move(true, eventChess);
                                }else {
                                    move(false, eventChess);
                                }
                            }else {
                                if (targetChess.getX() + 1 == eventChess.getX() && targetChess.getY() == eventChess.getY()){
                                    move(true, eventChess);
                                }else if (targetChess.getX() - 1 == eventChess.getX() && targetChess.getY() == eventChess.getY()){
                                    move(true, eventChess);
                                }else if (targetChess.getX() == eventChess.getX() && targetChess.getY() - 1 == eventChess.getY()){
                                    move(true, eventChess);
                                }else {
                                    move(false, eventChess);
                                }
                            }
                            break;
                    }
                }
            }
        }
    };

    public void initialize(){

        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 10; j++){
                grounds[i][j] = new ChessGround(i, j);
                grounds[i][j].setOnMouseClicked(click);
                grounds[i][j].setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
                Grid.add(grounds[i][j], i, j);
            }
        }

        grounds[0][6].setUpChess("black", Chess.Pawn);
        grounds[2][6].setUpChess("black", Chess.Pawn);
        grounds[4][6].setUpChess("black", Chess.Pawn);
        grounds[6][6].setUpChess("black", Chess.Pawn);
        grounds[8][6].setUpChess("black", Chess.Pawn);

        grounds[1][7].setUpChess("black", Chess.Cannon);
        grounds[7][7].setUpChess("black", Chess.Cannon);

        grounds[0][9].setUpChess("black", Chess.Chariots);
        grounds[1][9].setUpChess("black", Chess.Knight);
        grounds[2][9].setUpChess("black", Chess.Minister);
        grounds[3][9].setUpChess("black", Chess.Adviser);
        grounds[4][9].setUpChess("black", Chess.King);
        grounds[5][9].setUpChess("black", Chess.Adviser);
        grounds[6][9].setUpChess("black", Chess.Minister);
        grounds[7][9].setUpChess("black", Chess.Knight);
        grounds[8][9].setUpChess("black", Chess.Chariots);



        grounds[0][3].setUpChess("red", Chess.Pawn);
        grounds[2][3].setUpChess("red", Chess.Pawn);
        grounds[4][3].setUpChess("red", Chess.Pawn);
        grounds[6][3].setUpChess("red", Chess.Pawn);
        grounds[8][3].setUpChess("red", Chess.Pawn);

        grounds[1][2].setUpChess("red", Chess.Cannon);
        grounds[7][2].setUpChess("red", Chess.Cannon);

        grounds[0][0].setUpChess("red", Chess.Chariots);
        grounds[1][0].setUpChess("red", Chess.Knight);
        grounds[2][0].setUpChess("red", Chess.Minister);
        grounds[3][0].setUpChess("red", Chess.Adviser);
        grounds[4][0].setUpChess("red", Chess.King);
        grounds[5][0].setUpChess("red", Chess.Adviser);
        grounds[6][0].setUpChess("red", Chess.Minister);
        grounds[7][0].setUpChess("red", Chess.Knight);
        grounds[8][0].setUpChess("red", Chess.Chariots);



        System.out.println("Game Start!!");
        System.out.println("Original target is " + targetChess.getType() + ".");

    }

    public void move(boolean isMove, ChessGround destination){
        if (isMove){
            destination.setUpChess(targetChess.getGroup(), targetChess.getType());
            targetChess.setUpChess("", Chess.None);
            System.out.println(destination.getType() + " move to " + destination.getX() + "," + destination.getY() + ".");
        }else {
            targetChess = new ChessGround();
            System.out.println("Can't move to there.");
            System.out.println("And, Target is " + targetChess.getType() + " now.");
        }
    }
}
