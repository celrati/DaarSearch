package jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.charset.StandardCharsets;

import java.io.IOException;
import java.lang.SecurityException;

import java.util.List;
import java.util.ArrayList;

import kmp.Facteur;

@Path("/search")
public class Search {

  @GET
  @Path("/{word}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Book> getMessage(@PathParam("word") String word) {
    List<Book> result = new ArrayList<Book>();
    try {
      // this file is already sorted by betweenness
      java.nio.file.Path pathBookName = FileSystems.getDefault().getPath("src/main/resources/", "books_list.txt");
      List<String> books = Files.readAllLines(pathBookName, StandardCharsets.ISO_8859_1);
      for (String bookName : books) {
        String nameOnly = bookName.substring("src/main/resources/indexes/".length(), bookName.lastIndexOf('.'));
        char[] facteur = word.toCharArray();
        int[] retenue = Facteur.createRetenue(facteur);
        List<char[]> lines = new ArrayList<char[]>();
        java.nio.file.Path path = FileSystems.getDefault().getPath(".", bookName);
        List<String> text = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
        for (String str : text) {
          lines.add(str.toCharArray());
        }
        for (int i = 0; i < lines.size(); i++) {
          if (Facteur.matchingAlgo(facteur, retenue, lines.get(i)) != -1) {
            addBook(nameOnly, result);
            break;
          }
        }
      }
    }
    catch(Exception e) {
      System.out.println(e);
    }
    return result;
  }

  private void addBook(String name, List<Book> result) throws IOException, SecurityException {
    java.nio.file.Path pathBookContent = FileSystems.getDefault().getPath("src/main/resources/books", name + ".txt");
    List<String> listContent = Files.readAllLines(pathBookContent, StandardCharsets.ISO_8859_1);
    String content = String.join("\n",listContent);
    result.add(new Book(name, content));
  }
}
