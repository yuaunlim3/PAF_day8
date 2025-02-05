/*
db.series.aggregate([
    {
        $match: {
            name:{$regex:"an",$options:"i"},
            status:{$regex:'Ended',$options:'i'}
        }
    },
    {
        $sort: {'rating.average': -1}
    },
    {
        $project:{
            name:1,
            summary:1,
            id:1,
            _id:-1
        }
    }
])

db.series.aggregate([
    {
        $group:{
            _id: "$type",
            shows: {
                $push:{
                    title: "$name",
                    description: "$summary",
                    rating: "$rating.average",
                }
            },
            count:{$sum:1}
        }
    },
    {
        $out:"show_type"
    }
])


db.series.findOne({}),

db.series.aggregate([
    {
       $group:{
           _id:
           {
               network: "$network.name",
               type: "$type"
           },
           count:{"$sum":1},
           shows:{
               $push:{
                   title:"$name"
               }
           }
       }
    },
    {
        $sort:{count:-1}
    }
])




db.series.aggregate([
    {
        $project:{
            id:1,
            title: {
                $concat:[
                    "$name","-",
                    { $toString: "$runtime"},"m"
                ]
            }
        }
    }
])



db.series.aggregate(
    {
        $unwind : "$genres"
    },
    {
        $group:{
            _id : "$genres",
            title_count:{$sum:1}
        }
    },
    {
        $sort:{title_count:-1}
    }
    
)



db.series.aggregate([
    {
        $unwind: "$schedule.days"
    },
    {
      $group:{
          _id:{
              days:"$schedule.days",
              code: "$network.country.code"
          },
          shows_count: {$sum:1},
          shows:{
              $push:{
                  title:"$name"
              }
          }
      }  
    },
    {
        $sort :{count:-1}
    }
])



db.series.aggregate(
    {
        $project:{
            title:"$name",
            num_days:{
                $size:"$schedule.days"
            },
            days: "$schedule.days"
        }
    },
    {
        $match:{
            num_days:{$gt:1}
        }
    },
    {
        $sort: {num_days:1}
    }
)



db.series.aggregate(
    {
        $bucket:{
            groupBy: "$rating.average",
            boundaries: [0,3,5,7,9],
            default: ">=9",
            output:
                {
                    show_count: {$sum:1},
                    tiles:{$push:"$name"}
                }
        },
    }
)

db.series.countDocuments(
    {
        $and:[
        {
            "rating.average":{$gte:5}
        },
        {
            "rating.average":{$lt:7}
        }
        ]
    }
)
*/





