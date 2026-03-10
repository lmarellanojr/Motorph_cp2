package com.group33.cp2.motorph.model;

/**
 * Represents a generated system report with textual content.
 *
 * <p>Returned by {@link com.group33.cp2.motorph.model.AdminOperations#generateSystemReport(String)}
 * to encapsulate report output in a typed object rather than a raw {@code String}.</p>
 *
 * @author Group 33
 * @version 2.0
 */
public class Report {

    private String content;

    /**
     * Constructs a Report with the given content string.
     *
     * @param content the report content; must not be null
     */
    public Report(String content) {
        this.content = content;
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "Report {content='" + content + "'}";
    }
}
