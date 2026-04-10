package LLDProblems.gamesAndPuzzles.ticTacToe;

import java.util.*;

enum Symbol {
    X('X'), O('O'), EMPTY('-');
    private final char displayChar;
    Symbol(char displayChar) { this.displayChar = displayChar; }
    public char getDisplayChar() { return displayChar; }
}

enum GameStatus { IN_PROGRESS, WINNER_X, WINNER_O, DRAW } // Changed WINNER_Y to WINNER_O for clarity

enum GameResult { WIN, LOSS, DRAW }

class Cell {
    private Symbol symbol = Symbol.EMPTY;
    public boolean isEmpty() { return Symbol.EMPTY.equals(symbol); }
    public Symbol getSymbol() { return symbol; }
    public void setSymbol(Symbol symbol) { this.symbol = symbol; }
}

class Board {
    private final Cell[][] grid;
    private final int size;

    public Board(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) grid[i][j] = new Cell();
        }
    }

    public boolean isValidPos(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public Cell getCell(int row, int col) {
        return isValidPos(row, col) ? grid[row][col] : null;
    }

    public void placeSymbol(int row, int col, Symbol symbol) {
        if (symbol == null || !isValidPos(row, col)) throw new IllegalArgumentException();
        grid[row][col].setSymbol(symbol);
    }

    public int getSize() { return size; }

    public boolean isFull() {
        for (Cell[] row : grid) {
            for (Cell cell : row) if (cell.isEmpty()) return false;
        }
        return true;
    }
}

class Player {
    private String name, id;
    private Symbol marker;
    public Player(String name, String id, Symbol marker) {
        this.id = id; this.name = name; this.marker = marker;
    }
    public Symbol getMarker() { return marker; }
    public String getId() { return id; }
}

class PlayerScore {
    private int totalMatch, win, losses;
    public int getTotalMatch() { return totalMatch; }
    public int getWin() { return win; }
    public int loss() { return losses; }
    public int getDraw() { return totalMatch - win - losses; }

    public void updateScore(GameResult result) {
        if (GameResult.WIN.equals(result)) win++;
        else if (GameResult.LOSS.equals(result)) losses++;
        totalMatch++;
    }
}

interface WinningStrategy {
    boolean CheckWin(Board board, int row, int col, Symbol symbol);
}

class RowWinningStrategy implements WinningStrategy {
    @Override
    public boolean CheckWin(Board board, int row, int col, Symbol symbol) {
        for (int i = 0; i < board.getSize(); i++) {
            if (board.getCell(row, i).getSymbol() != symbol) return false;
        }
        return true;
    }
}

class ColWinningStrategy implements WinningStrategy {
    @Override
    public boolean CheckWin(Board board, int row, int col, Symbol symbol) {
        for (int i = 0; i < board.getSize(); i++) {
            if (board.getCell(i, col).getSymbol() != symbol) return false;
        }
        return true;
    }
}

/** * BUG FIXED: Logic now checks main and anti-diagonals independently.
 */
class DiagWinningStrategy implements WinningStrategy {
    @Override
    public boolean CheckWin(Board board, int row, int col, Symbol symbol) {
        int size = board.getSize();
        boolean mainDiag = true, antiDiag = true;

        for (int i = 0; i < size; i++) {
            if (board.getCell(i, i).getSymbol() != symbol) mainDiag = false;
            if (board.getCell(i, size - 1 - i).getSymbol() != symbol) antiDiag = false;
        }
        return mainDiag || antiDiag;
    }
}

interface GameObserver { void update(Game game); }

class Game {
    private Board board;
    private List<Player> players;
    private GameStatus status = GameStatus.IN_PROGRESS;
    private int currPlayerIdx = 0;
    private List<WinningStrategy> winningStrategies;
    private List<GameObserver> observers;

    public Game(Board board, List<Player> players, List<WinningStrategy> strats, List<GameObserver> observers) {
        this.board = board;
        this.players = players;
        this.winningStrategies = strats;
        // BUG FIXED: Use modifiable list to avoid UnsupportedOperationException
        this.observers = new ArrayList<>(observers); 
    }

    public void makeMove(int row, int col) {
        // BUG FIXED: Prevent moves after game is over
        if (status != GameStatus.IN_PROGRESS) throw new IllegalStateException("Game Over");
        if (!board.isValidPos(row, col) || !board.getCell(row, col).isEmpty()) throw new IllegalArgumentException();

        Player currPlayer = players.get(currPlayerIdx);
        board.placeSymbol(row, col, currPlayer.getMarker());

        for (WinningStrategy strat : winningStrategies) {
            if (strat.CheckWin(board, row, col, currPlayer.getMarker())) {
                status = (currPlayer.getMarker() == Symbol.X) ? GameStatus.WINNER_X : GameStatus.WINNER_O;
                break;
            }
        }

        if (status == GameStatus.IN_PROGRESS && board.isFull()) status = GameStatus.DRAW;

        if (status != GameStatus.IN_PROGRESS) notifyObservers();
        currPlayerIdx = (currPlayerIdx + 1) % players.size();
    }

    private void notifyObservers() {
        for (GameObserver o : observers) o.update(this);
    }

    public GameStatus getStatus() { return status; }
    public List<Player> getPlayers() { return players; }

    public Player getWinner() {
        if (status == GameStatus.WINNER_X || status == GameStatus.WINNER_O) {
            Symbol winningMarker = (status == GameStatus.WINNER_X) ? Symbol.X : Symbol.O;
            for (Player p : players) if (p.getMarker() == winningMarker) return p;
        }
        return null;
    }
}

class ScoreBoard implements GameObserver {
    private Map<String, PlayerScore> playerScoreMap = new HashMap<>();

    @Override
    public void update(Game game) {
        GameStatus status = game.getStatus();
        if (status == GameStatus.DRAW) {
            for (Player p : game.getPlayers()) playerScoreMap.get(p.getId()).updateScore(GameResult.DRAW);
        } else {
            Player winner = game.getWinner();
            for (Player p : game.getPlayers()) {
                playerScoreMap.get(p.getId()).updateScore(p.equals(winner) ? GameResult.WIN : GameResult.LOSS);
            }
        }
    }

    public void addPlayer(Player p) { playerScoreMap.putIfAbsent(p.getId(), new PlayerScore()); }
    public boolean isPlayerExist(Player p) { return playerScoreMap.containsKey(p.getId()); }

    public void printScoreBoard() {
        playerScoreMap.forEach((id, score) -> System.out.println("Player: " + id + 
            " | Matches: " + score.getTotalMatch() + " W: " + score.getWin() + " L: " + score.loss() + " D: " + score.getDraw()));
    }
}

enum TicTacToeGameManager {
    INSTANCE;
    private final ScoreBoard scoreBoard = new ScoreBoard();
    private final List<WinningStrategy> winningStrategies = Arrays.asList(
        new RowWinningStrategy(), new ColWinningStrategy(), new DiagWinningStrategy()
    );

    public Game createNewGame(int boardSize, Player p1, Player p2) {
        if (!scoreBoard.isPlayerExist(p1)) scoreBoard.addPlayer(p1);
        if (!scoreBoard.isPlayerExist(p2)) scoreBoard.addPlayer(p2);
        return new Game(new Board(boardSize), Arrays.asList(p1, p2), winningStrategies, Collections.singletonList(scoreBoard));
    }

    public void makeMove(Game game, int r, int c) { game.makeMove(r, c); }
    public void printScoreBoard() { scoreBoard.printScoreBoard(); }
}

public class TicTacToeGame {
    public static void main(String[] args) {
        TicTacToeGameManager manager = TicTacToeGameManager.INSTANCE;
        Player p1 = new Player("Abhishek", "id1", Symbol.X);
        Player p2 = new Player("Rahul", "id2", Symbol.O);

        Game game = manager.createNewGame(3, p1, p2);
        manager.makeMove(game, 0, 0); // X
        manager.makeMove(game, 0, 1); // O
        manager.makeMove(game, 1, 1); // X
        manager.makeMove(game, 0, 2); // O
        manager.makeMove(game, 2, 2); // X wins!

        manager.printScoreBoard();
    }
}