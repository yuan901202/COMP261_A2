# Auckland Road System v2.0

## A* search algorithm:
Initialise: for all nodes visited <- false, pathFrom <- null

enqueue(?start, null, 0, estimate(start, goal)? , fringe), 

Repeat until Repeat until fringe is empty: 

    ?node, from, costToHere, totalCostToGoal? <- dequeue(fringe)
    
    If not node.visited then
    
	node.visited <- true, node.pathFrom <- from, node.cost <- costToHere
	
	If node = goal then exit then 
	
	for each for each edge to neigh out of node
	
	    if not neigh.visited then
	    
		costToNeigh <- costToHere + edge.weight
		
		estTotal <- costToNeigh + estimate(neighbour, goal )
		
		fringe.enqueue(?neighbour, node, costToNeigh, estTotal?)
		
where fringe = priority queue, ordered by total cost to Goal

