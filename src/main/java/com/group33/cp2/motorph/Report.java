package com.group33.cp2.motorph;

// Represents a generated system report with textual content.
public class Report {

    private String content;

    public Report(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Report {content='" + content + "'}";
    }
}
