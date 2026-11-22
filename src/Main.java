import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Welcome to PsychoResearch");

        ResearchPaper initial = new ResearchPaper("Test Title", "Test Authors", 2018, "www.pinterest.com");

        ResearchPaper two = new ResearchPaper("Jack and the Beanstalk", "Mrs. Somebody", 1998, "www.jandbs.com");

        ResearchPaper three = new ResearchPaper("My kink is karma", "Chapell Roan", 2024, "www.chapellroan.com");

        ArrayList<ResearchPaper> papers = new ArrayList<>();
        papers.add(initial);
        papers.add(two);
        papers.add(three);

        for (ResearchPaper paper : papers) {
            displayPaper(paper);
        }

        Scanner scanner =  new Scanner(System.in);
        System.out.println("Enter a title to search for: ");
        String searchTerm = scanner.nextLine();

        int minimumYear = 0;
        while (true) {
            System.out.println("Published from which year? Press Enter to skip:");
            String searchYear = scanner.nextLine().trim();

            if (searchYear.isEmpty()) {
            break;
            }
            try {
                minimumYear = Integer.parseInt(searchYear);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid year or leave it blank");
            }
        }

        boolean found = false;

        for (ResearchPaper paper : papers) {
            if (paper.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) && paper.getPublicationYear() >= minimumYear) {
                displayPaper(paper);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No papers found");
        }
    }

    public static void displayPaper(ResearchPaper paper) {
        System.out.println("Title: " + paper.getTitle());
        System.out.println("Authors: " + paper.getAuthors());
        System.out.println("Publication year: " + paper.getPublicationYear());
        System.out.println("Link: " + paper.getLink());
        System.out.println();
    }
}

