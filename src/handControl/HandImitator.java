package handControl;

import board.details.Move;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class HandImitator {

    private static final int CELL_STEP_NUMBER = 7;

    private final Point startPoint;
    private final Point endPoint;
    private final Point gameTab;
    private final Point HTMLTab;
    private final Point gameURLField;
    private final Point HTMLURLField;

//    private final Point returnButton;

    private Point a1;

    private int stepX;
    private int stepY;

    private final Robot robot;

    public HandImitator() throws InterruptedException, AWTException {
        this.robot = new Robot();

        startPoint = requestPoint("Left lower corner");
        endPoint = requestPoint("Right upper corner");
        stepX = (endPoint.x - startPoint.x) / CELL_STEP_NUMBER;
        stepY = (endPoint.y - startPoint.y) / CELL_STEP_NUMBER;

        gameTab = requestPoint("GAME Tab");
        gameURLField = requestPoint("GAME URL Field");
        HTMLTab = requestPoint("HTML Tab");
        HTMLURLField = requestPoint("HTML URL Field");
//        returnButton = requestPoint("Return button");
    }

    public void setGame(String gameID) {
        loadPage("https://lichess.org/" + gameID, gameTab, gameURLField);
        loadPage("view-source:https://lichess.org/" + gameID, HTMLTab, HTMLURLField);
    }

//    public void returnToTournament() {
//        openTab(gameTab);
//        mouseMove(returnButton);
//        clickMouse();
//    }

    private void loadPage(String URL, Point tab, Point field) {
        setClipboardContent(URL);

        openTab(tab);
        mouseMove(field);

        clickMouse();

        ctrlPlus(KeyEvent.VK_A);
        ctrlPlus(KeyEvent.VK_V);
        clickKey(KeyEvent.VK_ENTER);
    }

    public void setColor(boolean isWhite) {
        a1 = isWhite ? startPoint : endPoint;
        Point h8 = isWhite ? endPoint : startPoint;
        stepX = (h8.x - a1.x) / 7;
        stepY = (h8.y - a1.y) / 7;

        System.out.println(stepX + " " + stepY);
    }

    private void setClipboardContent(String content) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(content), null);
    }

    public void makeChessMove(Move move) {
        Point from = getShiftedPoint(move.letFrom - 1, move.digFrom - 1);
        Point to = getShiftedPoint(move.letTo - 1, move.digTo - 1);

        openTab(gameTab);
        mouseMove(from);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        mouseMove(to);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        clickMouse();

        robot.delay(2000);
    }

    public String getHTMLPage() throws IOException, UnsupportedFlavorException {
        copyHTMLPage();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor).toString();
    }

    private void copyHTMLPage() {
        openTab(HTMLTab);

        // Refresh Page (F5)
        robot.keyPress(KeyEvent.VK_F5);
        robot.keyRelease(KeyEvent.VK_F5);

        // Wait for loading
        robot.delay(1000);

        // Press Ctrl + A
        ctrlPlus(KeyEvent.VK_A);

        robot.delay(100);

        // Press Ctrl + C
        ctrlPlus(KeyEvent.VK_C);

        robot.delay(1000);
    }

    private Point getShiftedPoint(int stepsX, int stepsY) {
        return new Point(
                a1.x + stepX * stepsX,
                a1.y + stepY * stepsY
        );
    }

    private void mouseMove(Point point) {
        robot.delay(500);
        robot.mouseMove(point.x, point.y);
    }

    private Point requestPoint(String name) throws InterruptedException {
        int seconds = 5;
        System.out.println(name + " position will be received in " + seconds + " seconds.");
        Thread.sleep(seconds * 1000);
        Point point = MouseInfo.getPointerInfo().getLocation();
        System.out.printf("Position: x = %d, y = %d\n", point.x, point.y);
        return point;
    }

    private void openTab(Point tab) {
        mouseMove(tab);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void clickMouse() {
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(100);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(100);
    }

    private void clickKey(int key) {
        robot.delay(100);
        robot.keyPress(key);
        robot.delay(100);
        robot.keyRelease(key);
        robot.delay(100);
    }

    private void ctrlPlus(int keyEvent) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(keyEvent);
        robot.delay(100);
        robot.keyRelease(keyEvent);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
}