package vttp.batch5.paf.day28.repositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import vttp.batch5.paf.day28.Utils.MongoQueries;


@Repository
public class GameRepository {
    @Autowired MongoTemplate template;

    /*


        db.games.aggregate(
        [
            {
                $match:{name:{$regex:"Monopoly",$options:"i"}}
            },
            {
                $lookup:{
                    from: "comments",
                    foreignField: "gid",
                    localField:"gid",
                    as: "top5_Comments",
                    pipeline:[
                        {
                            $project:{
                                _id:0,
                                user:"$user",
                                rating:"$rating",
                                text:"$c_text"
                            }
                        },
                        {$sort:{rating:-1}},
                        {$limit:5}
                    ]
                }
            },
            {
                $project:{
                    _id:0,gid:1,name:1,ranking:1,url:1,image:1,comments:1

                }
            },
            {
                $sort: {ranking:-1}
            }
        ])


     */
    public List<Document> getGame(String game){
        Criteria criteria = Criteria.where(MongoQueries.F_NAME).regex(game,"i");
        MatchOperation matchGame = Aggregation.match(criteria);
        ProjectionOperation projectSearch = Aggregation.project(MongoQueries.F_USER,MongoQueries.F_RATING).and(MongoQueries.F_C_TEXT).as("text").andExclude(MongoQueries.F_ID);
        SortOperation sortSearch = Aggregation.sort(Sort.by(Direction.DESC,MongoQueries.F_RATING));
        LimitOperation limitSearch = Aggregation.limit(5);
        LookupOperation lookupComments = LookupOperation.newLookup()
                                        .from(MongoQueries.C_COMMENTS)
                                        .localField(MongoQueries.F_GAME_ID)
                                        .foreignField(MongoQueries.F_GAME_ID)
                                        .pipeline(projectSearch,sortSearch,limitSearch)
                                        .as("Top5_Comments");

        ProjectionOperation projectGame = Aggregation.project(MongoQueries.F_GAME_ID,MongoQueries.F_NAME,MongoQueries.F_RANKING,MongoQueries.F_URL,MongoQueries.F_IMAGE,MongoQueries.F_TOP_5_COMMENTS).andExclude(MongoQueries.F_ID);
        SortOperation sortGame = Aggregation.sort(Sort.by(Direction.ASC,MongoQueries.F_RANKING));
        Aggregation pipeline = Aggregation.newAggregation(matchGame,lookupComments,projectGame,sortGame);
        
        return template.aggregate(pipeline,MongoQueries.C_GAMES,Document.class).getMappedResults();

    }
}
