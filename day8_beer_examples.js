db.beers.aggregate([
    {
        $lookup:{
            from: 'breweries',
            foreignField:"id",
            localField:"brewery_id",
            as:"brewery",
            pipeline:[
               
             {
               $project:{
                   name:1,
                   _id:0
               }
             },
 
                    
          
            ]
        }
    },
    {
        $unwind: "$brewery"
    },
    {
        $group: {
            _id:"$name",
            count:{$sum:1},
            brewery:{
                $push:"$brewery.name"
            }
        }
    },
    {
        $sort:{count:-1}
    },
    {
        $limit:5
    }
]
)
