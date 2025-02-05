package PAF.day8_example.Bootstrap;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import PAF.day8_example.Repository.BreweriesRepository;

@Component
public class RunQueries implements CommandLineRunner {
    @Autowired private BreweriesRepository breweriesRepository;

    @Override
    public void run(String... args){
        List<Document> results = breweriesRepository.listBeersByStyle();
        for(Document document:results){
            System.out.println(document);
            System.out.println("<<<<<<<>>>>>>>>");
        }
        System.out.println(results.size());
    }
    
}
