package PAF.day8_example.Repository;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.BucketOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;

@Repository
public class BreweriesRepository {
    @Autowired private MongoTemplate template;

    /*
     db.breweries.aggregate(
        {
            $match:{
                country:{$regex:"country",$options:{i}}

            }
        },
        {
            $project{
            _id:0,name:1,address1:1,city:1
            }
        },
        {
            $group{
            _id:"$city",
            breweries:
                {
                    $push:{
                        name:"$name"
                        address: "$address1"
                    }
                }
            }
        },
        {
            $sort{_id:1}
        }
     )
     */

     public List<Document> getBreweries(String country){
        //Create 1 or more pipeline stages
        Criteria criteria = Criteria.where("country").regex(country,"i");
        //Create a matchOperation
        MatchOperation filterByCountry = Aggregation.match(criteria);

        ProjectionOperation projectionOperation = Aggregation.project("name","address1","city")
                                                    .andExclude("_id");


        /*
                    breweries:
                {
                    $push:"$name"
                }
            }
        GroupOperation groupOperation = Aggregation.group("city")
                                        .push("name").as("breweries")
                                        .count().as("count");
        */

        GroupOperation groupOperation = Aggregation.group("city")
                                        .push(new BasicDBObject()
                                                .append("name","$name")
                                                .append("address", "$address1")
                                        ).as("breweries")
                                        .count().as("count");


        SortOperation sortOperation = Aggregation.sort(Sort.by(Direction.ASC,"_id"));

        //Create pipeline
        Aggregation pipeline = Aggregation.newAggregation(filterByCountry,projectionOperation,groupOperation,sortOperation);

        AggregationResults<Document> results = template.aggregate(pipeline,"breweries",Document.class);
        return results.getMappedResults();
     }


     /*
        db.styles.aggregate([
            {
                $lookup: {
                    from: "beers",
                    foreignField: "style_id",
                    localField: "id",
                    as: "beers",
                    pipeline: [
                        {
                            $project: {
                                _id: 0,
                                name: 1
                            }
                        }
                    ]
                }
            }
        ])

      */
      public List<Document> listBeersByStyle() {

        // Project the beer name only
        ProjectionOperation beerName = Aggregation.project("name")
              .andExclude("_id");
  
        // Lookup operation
        // LookupOperation lookupBeers = Aggregation.lookup("beers", "id", "style_id", "beers");
        LookupOperation lookupBeers = LookupOperation.newLookup()
              .from("beers")
              .localField("id").foreignField("style_id")
              .pipeline(beerName)
              .as("beers");
  
        // Create a pipeline
        Aggregation pipeline = Aggregation.newAggregation(lookupBeers);
  
        // Execute the pipeline
        return template.aggregate(pipeline, "styles", Document.class).getMappedResults();
     }

      /*
       db.beers.aggregate(  
        {
            $bucket:{
                groupBy: "$abv",
                bourdaries:[0,3,5,7,9,15],
                default:">=9",
                output:{
                    count:{$sum:1},
                    beers:{$push:"%name"}
                }
            }
        }
       )
       */

       public List<Document> categorizedByABV(){
        BucketOperation bucketOperation = Aggregation.bucket("abv").withBoundaries(0,3,5,7,9,15).withDefaultBucket(">=15").andOutputCount().as("count").andOutput("name").push().as("beers");

        Aggregation pipeline = Aggregation.newAggregation(bucketOperation);

        AggregationResults results = template.aggregate(pipeline,"beers", Document.class);

        return results.getMappedResults();
       }
}
