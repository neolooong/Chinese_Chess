import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class ChessBoard {
    public Pane pane;
    public GridPane chessGrid;
    public Grid grid[][] = new Grid[9][10];
    public Chess target = Chess.None;

    //    滑鼠點擊
    public EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            Grid grid = (Grid) event.getSource();

            switch (target){    //先判斷 target 有沒有指定 Chess
                case None:  //如果沒有 將 target 設定成 觸發事件的棋子
                    target = grid.getType();
                    System.out.println("123");
                    break;
                case King:
                    break;
            }

        }
    };

    public void initialize(){

        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 10; j++){
                grid[i][j] = new Grid();
                grid[i][j].setOnMouseClicked(click);
                grid[i][j].setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
                chessGrid.add(grid[i][j], i, j);
            }
        }

        grid[4][0].setType(Chess.King);
    }

    public void newGame(){

    }
}
