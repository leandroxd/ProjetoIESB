package br.com.arturbc.projetoiesb.activities;

public class Mensagem {

    private String text;
    private long timestamp;
    private String fromId;
    private String toId;

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }
}
