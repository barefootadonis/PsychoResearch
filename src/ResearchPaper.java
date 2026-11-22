public class ResearchPaper {
    private String title;
    private String authors;
    private int publicationYear;
    private String link;

    public ResearchPaper(String title, String authors, int publicationYear, String link)
    {
        this.title = title;
        this.authors = authors;
        this.publicationYear = publicationYear;
        this.link = link;
    }

    public String getTitle()
    {
        return title;
    }
    public String  getAuthors()
    {
        return authors;
    }
    public int getPublicationYear()
    {
        return publicationYear;
    }
    public String getLink()
    {
        return link;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setAuthors(String authors)
    {
        this.authors = authors;
    }
    public void setPublicationYear(int publicationYear)
    {
        this.publicationYear = publicationYear;
    }
    public void setLink(String link)
    {
        this.link = link;
    }
}


