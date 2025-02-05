package vttp.batch5.paf.day28.services;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.batch5.paf.day28.models.Comment;
import vttp.batch5.paf.day28.models.Game;
import vttp.batch5.paf.day28.repositories.GameRepository;

@Service
public class GameService {
    
    @Autowired private GameRepository gameRepository;

    public List<Game> getGames(String gameName){
        List<Document> gamesData = gameRepository.getGame(gameName);
        List<Game> games = new LinkedList<>();
        for(Document document: gamesData){

            Game game = new Game();
            List<Document> commentsDocuments = (List<Document>) document.get("Top5_Comments");

            for(Document commentDoc:commentsDocuments){
                Comment comment = new Comment();
                comment.setUser(commentDoc.getString("user"));
                comment.setRating(commentDoc.getInteger("rating"));
                comment.setText(commentDoc.getString("text"));
                game.addComment(comment);
            }
            /*
        "gid" : NumberInt(40398),
        "name" : "Monopoly Deal Card Game",
        "ranking" : NumberInt(1903),
        "url" : "https://www.boardgamegeek.com/boardgame/40398/monopoly-deal-card-game",
        "image" : "https://cf.geekdo-images.com/micro/img/JD53NEAPg3wQ3IzbEmkxw2kKuzs=/fit-in/64x64/pic716758.jpg"
            "comments" : [
        {
            "user" : "quixotequest",
            "rating" : NumberInt(6),
            "text" : "Not bad for a Monopoly themed game. A nice break from the regular board game, and plays pretty fast. "
        },
        {
            "user" : "TheLogLady",
            "rating" : NumberInt(5),
            "text" : "Honestly, it's better than it's big brother.  I would play the card game over the board game any day."
        },
        {
            "user" : "j-train1",
            "rating" : NumberInt(4),
            "text" : "Oh look it is Monotony ,but in card form. Played it twice and still didn't care for it. "
        },
        {
            "user" : "kanoe",
            "rating" : NumberInt(3),
            "text" : "Keeping only because it is a souvenir of our last trip to Europe that we played several times on the plane home."
        }
    ]
             */

             game.setGameId(document.getInteger("gid"));
             game.setName(document.getString("name"));
             game.setRanking(document.getInteger("ranking"));
             game.setUrl(document.getString("url"));
             game.setImage(document.getString("image"));
             games.add(game);
        }

        return games;
    }
}
