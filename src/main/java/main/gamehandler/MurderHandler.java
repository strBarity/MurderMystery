package main.gamehandler;

public class MurderHandler {
    public static boolean gameStarted = false;
    public static void startGame() {
        gameStarted = true;
    } public static void stopGame() {
        gameStarted = false;
    }
}
