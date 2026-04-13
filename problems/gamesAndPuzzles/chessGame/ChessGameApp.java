package LLDProblems.gamesAndPuzzles.chessGame;

// enums
//GameStatus, PlayerColor, PIECE_TYPE

enum GameStatus {
    ONGOING,
    CHECK,
    CHECKMATE,
    STALEMATE,
    RESIGN
}
enum PlayerColor {
    WHITE,
    BLACK
}

enum PIECE_TYPE {
    KING,
    QUEEN,
    ROOK,
    BISHOP,
    KNIGHT,
    PAWN
}

class ChessException extends RuntimeException {
    public ChessException(String message) {
        super(message);
    }
}

class Player {
    private String name;
    private PlayerColor color;

    public Player(String name, PlayerColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public PlayerColor getColor() { return color; }
}

class Position {
    private int x;
    private int y;

    public Position(int row, int col) {
        if(row < 0 || row > 7 || col < 0 || col > 7) {
            throw new ChessException("Invalid position: (" + x + ", " + y + ")");
        }
        x = row;
        y = col;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}

class Move {
    private Position from;
    private Position to;
    private ChessPiece piece;
    private ChessPiece capturedPiece;
    private boolean isPromotion;
    private PIECE_TYPE promotedTo;
    public Move(Position from, Position to, ChessPiece piece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
    }

    public Position getFrom() { return from; }
    public Position getTo() { return to; }
    public ChessPiece getPiece() { return piece; }
    public ChessPiece getCapturedPiece() { return capturedPiece; }
    public boolean isPromotion() { return isPromotion; }
    public PIECE_TYPE getPromotedTo() { return promotedTo; }
    public void setCapturedPiece(ChessPiece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }
    public void setPromotion(PIECE_TYPE promotedTo) {
        this.isPromotion = true;
        this.promotedTo = promotedTo;   
}

abstract class ChessPiece {
    protected PlayerColor color;
    protected PIECE_TYPE type;
    protected boolean hasMoved;


    public ChessPiece(PlayerColor color, PIECE_TYPE type) {
        this.color = color;
        this.type = type;
        this.hasMoved = false;
    }

    public abstract boolean canMove(Position from, Position to, ChessBoard board);
    public boolean hasMoved(){
        return hasMoved;
    }
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    public PlayerColor getColor() { return color; }
    public PIECE_TYPE getType() { return type; }
}





public class ChessGameApp {
    
}
