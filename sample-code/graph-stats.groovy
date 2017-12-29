;[] // Small Groovy script that can be used from within the Gremlin console after
;[] // the air-routes graph has been loaded that will provide statistics about the 
;[] // graph. The use of ;[] is just to stop the console printing additional return values.

println "\n\nA few statistics about the air-routes graph";[]
println "===========================================";[]

println "\nDistribution of vertices and edges";[]
println "----------------------------------";[]
verts = g.V().groupCount().by(label).next();[]
edges = g.E().groupCount().by(label).next();[]
println "Vertices : ${verts}";[]
println "Edges    : ${edges}";[]

most = g.V().hasLabel('airport').order().by(bothE('route').count(),decr).limit(1).
             project('ap','num','city').by('code').by(bothE('route').count()).by('city').next();[]

println "\nThe airport with the most routes (incoming and outgoing) is ${most['ap']}/${most['city']} with ${most['num']}";[]

println "\nTop 20 airports ordered by number of outgoing routes";[]
println "----------------------------------------------------";[]
most = g.V().hasLabel('airport').order().by(out('route').count(),decr).limit(20).
                     project('ap','num','city').by('code').by(out('route').count()).by('city').toList();[]

most.each {printf("%4s  %15s %5d\n", it.ap, it.city,  it.num)};[]

println "\nTop 20 airports ordered by number of incoming routes";[]
println "----------------------------------------------------";[]
most = g.V().hasLabel('airport').order().by(__.in('route').count(),decr).limit(20).
                     project('ap','num','city').by('code').by(__.in('route').count()).by('city').toList();[]

most.each {printf("%4s  %15s %5d\n", it.ap, it.city,  it.num)};[]


longroute = g.E().hasLabel('route').order().by('dist',decr).limit(1).
                  project('from','to','num').
                  by(inV().values('code')).by(outV().values('code')).by('dist').next();[]

println "\nThe longest route in the graph is ${longroute['num']} miles between ${longroute['from']} and ${longroute['to']}";[]

shortroute = g.E().hasLabel('route').order().by('dist',incr).limit(1).
                   project('from','to','num').
                   by(inV().values('code')).by(outV().values('code')).by('dist').next();[]

println "The shortest route in the graph is ${shortroute['num']} miles between ${shortroute['from']} and ${shortroute['to']}";[]


meanroute = g.E().hasLabel('route').values('dist').mean().next().round(4);[]
println "The average route distance is ${meanroute} miles";[]

println "\nTop 20 routes in the graph by distance";[]
println "--------------------------------------";[]

routes = g.E().hasLabel('route').order().by('dist',decr).limit(40).
               project('a','b','c').
               by(inV().values('code')).by('dist').by(outV().values('code')).
               filter(select('a','c')).where('a',lt('c')).toList();[]
               
routes.each {printf("%4s  %5d %4s\n", it.a, it.b,  it.c)};[]


longest = g.V().hasLabel('airport').order().by('longest',decr).limit(1).
                project('ap','num','city').by('code').by('longest').by('city').next();[]

println "\nThe longest runway in the graph is ${longest['num']} feet at ${longest['ap']}/${longest['city']}";[]

shortest = g.V().hasLabel('airport').order().by('longest',incr).limit(1).
                 project('ap','num','city').by('code').by('longest').by('city').next();[]

println "The shortest runway in the graph is ${shortest['num']} feet at ${shortest['ap']}/${shortest['city']}";[]

;[] // A different way of doing the above type of query using two queries. 
;[] // Just to show a different approach
highest = g.V().hasLabel('airport').values('elev').max().next();[]
aptcity = g.V().has('elev',highest).valueMap('code','city').next();[]
println "\nThe highest airport in the graph is ${aptcity['code'][0]}/${aptcity['city'][0]} which is at ${highest} feet above sea level";[]