package pl.poznan.put.omw;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.Arrays;

public class ChessLibUtils {

    public static void main(String args[]) throws MoveConversionException {
        // Creates a new chessboard in the standard initial position
        Board board = new Board();
        String fen = "8/p5Q1/2ppq2p/3n1ppk/3B4/2P2P1P/P5P1/6K1 w - - 3 46";

        board.loadFromFen(fen);

        String san = "g4"; // "g5"
        System.out.println(isMoveACapture(board, san));
        System.out.println(isMoveACapture(board, san));
        System.out.println(ChessLibUtils.getOpponentMaterialDifferenceAfterMove(board, san));

        MoveList moves = new MoveList();
        moves.loadFromSan(san);
        board.doMove(moves.get(0));


        System.out.println(getWhiteMaterialSum(board));
        System.out.println(getBlackMaterialSum(board));
        System.out.println(board.getFen());
    }

    public static boolean isMaterialAdvantageAfterMove(Board board, String move) throws MoveConversionException {
        return getMyMaterial(board) > getOpponentMaterialAfterMove(board, move);
    }

    public static int getMyMaterial(Board board) {
        Side side = board.getSideToMove();
        if (side == Side.WHITE) {
            return getWhiteMaterialSum(board);
        } else {
            return getBlackMaterialSum(board);
        }
    }

    /**
     * Gets material sum of the opponent.
     *
     * @param board board before move
     * @return material sum of the opponent
     */
    public static int getOpponentMaterialBeforeMove(Board board) {
        Side side = board.getSideToMove();
        if (side == Side.BLACK) {
            return getWhiteMaterialSum(board);
        } else {
            return getBlackMaterialSum(board);
        }
    }

    /**
     * Gets material sum of the opponent after move.
     *
     * @param board board before move
     * @return material sum aterial sum of the opponent after move
     */
    public static int getOpponentMaterialAfterMove(Board board, String move) throws MoveConversionException {
        Side side = board.getSideToMove();
        MoveList moves = new MoveList();
        moves.loadFromSan(move);
        board.doMove(moves.get(0));
        int afterMoveOppponentMaterial;
        if (side == Side.BLACK) {
            afterMoveOppponentMaterial = getWhiteMaterialSum(board);
        } else {
            afterMoveOppponentMaterial = getBlackMaterialSum(board);
        }
        board.undoMove(); // undo move so state of the board is not changed
        return afterMoveOppponentMaterial;
    }

    public static int getOpponentMaterialDifferenceAfterMove(Board board, String move) throws MoveConversionException {
        return getOpponentMaterialBeforeMove(board) - getOpponentMaterialAfterMove(board, move);
    }

    public static int getWhiteMaterialSum(Board board) {
        Piece[] whitePieces = {Piece.WHITE_PAWN, Piece.WHITE_KNIGHT, Piece.WHITE_BISHOP, Piece.WHITE_ROOK, Piece.WHITE_QUEEN, Piece.WHITE_KING};
        return getMaterialSum(whitePieces, board);
    }

    public static int getBlackMaterialSum(Board board) {
        Piece[] blackPieces = {Piece.BLACK_PAWN, Piece.BLACK_KNIGHT, Piece.BLACK_BISHOP, Piece.BLACK_ROOK, Piece.BLACK_QUEEN, Piece.BLACK_KING};
        return getMaterialSum(blackPieces, board);
    }

    public static int getMaterialSum(Piece[] pieces, Board board) {
        return Arrays.stream(pieces).map(piece ->
                board.getPieceLocation(piece).size() * getValueFromPiece(piece.getPieceType())
        ).mapToInt(Integer::intValue).sum();
    }

    public static boolean isMoveACapture(Board board, String move) throws MoveConversionException {
        return getOpponentMaterialDifferenceAfterMove(board, move) > 0;
    }

    public static int getValueFromPiece(PieceType piece) {
        switch (piece) {
            case PAWN:
                return 100;
            case KNIGHT:
            case BISHOP:
                return 300;
            case KING:
                return 400;
            case ROOK:
                return 500;
            case QUEEN:
                return 900;
            default:
                return 0;
        }
    }
}
