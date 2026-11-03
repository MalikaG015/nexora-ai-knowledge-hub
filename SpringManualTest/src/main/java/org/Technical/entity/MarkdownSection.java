package org.Technical.entity;

public class MarkdownSection {
    private String pageTitle;
    private String heading;
    private String subheading;
    private String content;

    public MarkdownSection(String pageTitle, String heading,String subheading, String content) {
        this.pageTitle = pageTitle;
        this.heading = heading;
        this.content = content;
        this.subheading=subheading;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getHeading() {
        return heading;
    }

    public String getContent() {
        return content;
    }
    public String getSubheading() {
        return subheading;
    }
}
