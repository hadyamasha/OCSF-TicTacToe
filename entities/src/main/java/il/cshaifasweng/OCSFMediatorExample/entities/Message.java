package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;     // e.g., "START", "MOVE", "WAIT", "GAME_OVER"
    private String content;  // e.g., "X", "O", "Draw", "Win"
    private int cellIndex;   // 0 to 8 (which button was clicked)

    public Message(String type, String content, int cellIndex) {
        this.type = type;
        this.content = content;
        this.cellIndex = cellIndex;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getCellIndex() { return cellIndex; }
    public void setCellIndex(int cellIndex) { this.cellIndex = cellIndex; }
}
