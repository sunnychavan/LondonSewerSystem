package diver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import graph.Node;
import graph.NodeStatus;
import graph.ScramState;
import graph.SeekState;
import graph.SewerDiver;

public class McDiver extends SewerDiver {

	/** Get to the ring in as few steps as possible. Once there, <br>
	 * McDiver must return from this function in order to pick<br>
	 * it up. If McDiver continues to move after finding the ring rather <br>
	 * than returning, it will not count.<br>
	 * If McDiver returns from this function while not standing on top of the ring, <br>
	 * it will count as a failure.
	 *
	 * There is no limit to how many steps McDiver can take, but you will receive<br>
	 * a score bonus multiplier for finding the ring in fewer steps.
	 *
	 * At every step, McDiver knows only the current tile's ID and the ID of all<br>
	 * open neighbor tiles, as well as the distance to the ring at each of <br>
	 * these tiles (ignoring walls and obstacles).
	 *
	 * In order to get information about the current state, use functions<br>
	 * currentLocation(), neighbors(), and distanceToRing() in state.<br>
	 * You know McDiver is standing on the ring when distanceToRing() is 0.
	 *
	 * Use function moveTo(long id) in state to move McDiver to a neighboring<br>
	 * tile by its ID. Doing this will change state to reflect your new position.
	 *
	 * A suggested first implementation that will always find the ring, but <br>
	 * likely won't receive a large bonus multiplier, is a depth-first walk. <br>
	 * Some modification is necessary to make the search better, in general. */
	@Override
	public void seek(SeekState state) {
		// TODO : Find the ring and return.
		// DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
		// Instead, write your method elsewhere, with a good specification,
		// and call it from this one.
		dfswalk_optimized(state);
	}
	
	
	/** Unoptimized portion. Carry out the DFS algorithm for the Seek phase. */
	public void dfswalk(SeekState state) {
		Stack<NodeStatus> s = new Stack<NodeStatus>(); 
		HashMap<Long, Long> visited_map = new HashMap<Long, Long>();
		SeekState u = state; 
		NodeStatus q;
		Long q_prev;
		
		if(u.distanceToRing()==0) return;
		visited_map.put(u.currentLocation(), u.currentLocation());
		for(NodeStatus i:u.neighbors()) {
			s.push(i);
		}
		
		while(u.distanceToRing()!=0) {			
			q = s.pop();
			if(!visited_map.containsKey(q.getId())) {
				q_prev = u.currentLocation();
				try{u.moveTo(q.getId());}
				catch(IllegalArgumentException i) {
					while(!u.neighbors().contains(q)) {
						u.moveTo(visited_map.get(u.currentLocation()));
					}
					q_prev = u.currentLocation();
					u.moveTo(q.getId());
				}
				visited_map.put(u.currentLocation(), q_prev);
				for(NodeStatus i: u.neighbors()) {
						s.push(i);
					}
		}
	}
	}
	
	/** Optimized portion. Carry out the DFS algorithm for the seek phase. 
	 * Push neighbors of current node onto stack in descending order 
	 * of distance to ring. */
	public void dfswalk_optimized(SeekState state) {
		Stack<NodeStatus> s = new Stack<NodeStatus>(); 
		HashMap<Long, Long> visited_map = new HashMap<Long, Long>();
		SeekState u = state; 
		NodeStatus q;
		Long q_prev;
		
		if(u.distanceToRing()==0) return;
		visited_map.put(u.currentLocation(), u.currentLocation());
		NodeStatus[] b = working_sort_stack(u.neighbors());				
		for(int i = 0; i < b.length; i++) { 
				s.push(b[i]);
		}
		
		while(u.distanceToRing()!=0) {			
			q = s.pop();
			if(!visited_map.containsKey(q.getId())) {
				q_prev = u.currentLocation();
				try{u.moveTo(q.getId());}
				catch(IllegalArgumentException i) {
					while(!u.neighbors().contains(q)) {
						u.moveTo(visited_map.get(u.currentLocation()));
					}
					q_prev = u.currentLocation();
					u.moveTo(q.getId());
				}
				visited_map.put(u.currentLocation(), q_prev);
				
				NodeStatus[] r = working_sort_stack(u.neighbors());				
				for(int i = 0; i < r.length; i++) {
						s.push(r[i]);
					}					
					}				
		}
	}
	
	/** Optimized portion. Reorder the neighboring nodes of current node in descending order 
	 * based on distance to the ring. Save this order of NodeStatus neighboring nodes 
	 * in an array*/
	public NodeStatus[] working_sort_stack(Collection<NodeStatus> neighbors) {
		List<NodeStatus> node_status = new ArrayList<NodeStatus>(neighbors.size());
		NodeStatus[] nodes = new NodeStatus[neighbors.size()];
		
		int accum = 0;
		for(NodeStatus i:neighbors) {
			node_status.add(accum, i);
			accum = accum + 1;
		}
		
		Collections.sort(node_status, new Comparator<NodeStatus>(){
			public int compare(NodeStatus o1, NodeStatus o2)
			  {
			    Integer dist_o1 = (Integer) o1.getDistanceToRing(); 
			    Integer dist_o2 = (Integer) o2.getDistanceToRing();			    		
			    		return dist_o1.compareTo(dist_o2);
			  }
			});
		
		int ac1 = 0;
		for(int i = neighbors.size()-1; i >= 0; i--) {
			nodes[ac1] = node_status.get(i);
			ac1 = ac1 + 1;
		}
		return nodes;
	}
		
	/** Scram --get out of the sewer system before the steps are all used, trying to <br>
	 * collect as many coins as possible along the way. McDiver must ALWAYS <br>
	 * get out before the steps are all used, and this should be prioritized above<br>
	 * collecting coins.
	 *
	 * You now have access to the entire underlying graph, which can be accessed<br>
	 * through ScramState. currentNode() and getExit() will return Node objects<br>
	 * of interest, and getNodes() will return a collection of all nodes on the graph.
	 *
	 * You have to get out of the sewer system in the number of steps given by<br>
	 * stepsToGo(); for each move along an edge, this number is <br>
	 * decremented by the weight of the edge taken.
	 *
	 * Use moveTo(n) to move to a node n that is adjacent to the current node.<br>
	 * When n is moved-to, coins on node n are automatically picked up.
	 *
	 * McDiver must return from this function while standing at the exit. Failing <br>
	 * to do so before steps run out or returning from the wrong node will be<br>
	 * considered a failed run.
	 *
	 * Initially, there are enough steps to get from the starting point to the<br>
	 * exit using the shortest path, although this will not collect many coins.<br>
	 * For this reason, a good starting solution is to use the shortest path to<br>
	 * the exit. */
	@Override
	public void scram(ScramState state) {
		// TODO: Get out of the sewer system before the steps are used up.
		// DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
		// with a good specification, and call it from this one.
		// We say this because it makes it easier for you to try different
		// possibilities, always keeping at least one method that always scrams
		// in the prescribed number of steps.
		
		HashMap<Node, Integer> allnodes = format_map_optimized(state);
		SP_optimized(state, allnodes);
		
	}
	/** Part of the unoptimized portion of scram state.
	 * Find the shortest path from the current node to exit node*/ 
	public List<Node> compute_SP(ScramState state){
		return A6.shortestPath(state.currentNode(), state.exit());		
	}
	
	/** Part of the unoptimized portion of Scram state.
	 * Move state from current node to last node in shortest path list 
	 * returned from the function compute_SP */
	public void move_to_exit(List<Node> nodes, ScramState state) {
		for(int i=1;i< nodes.size();i++) {
			state.moveTo(nodes.get(i));
		}
	}
	
	/** Part of the optimized portion for the Scram phase.
	 * Adds all nodes from the allNodes collection into a HashMap.
	 *  Remove the current node and exit node from the HashMap*/
	public HashMap<Node, Integer> format_map_optimized(ScramState state) {
		Collection<Node> u = state.allNodes();
		HashMap<Node, Integer> Nodes = new HashMap<Node, Integer>();
		for(Node i: u) {
			Nodes.put(i, 1);
		}
		Nodes.remove(state.currentNode());
		Nodes.remove(state.exit());
		return Nodes;
	}

	/** Part of the optimized portion for the Scram State. Iterate through 
	 * all nodes in the HashMap (which indicates which notes have not been visited). If 
	 * the shortest path from the current node to unvisited node to exit node is less 
	 * than the Steps to go, move state to unvisited node. */
	public void SP_optimized(ScramState state, HashMap<Node, Integer> Nodes) {
		HashMap<Node, Integer> u = Nodes;
		int tpl; 
		
		for(Node i: u.keySet()) {
			tpl = 0;
			if(i.getTile().coins() > 500) {
			List<Node> first = A6.shortestPath(state.currentNode(), i);
			for(int q = 0; q < first.size()-1; q++) {
				tpl = tpl + first.get(q).getEdge(first.get(q+1)).length();
			}
			
			List<Node> second = A6.shortestPath(i, state.exit());
			for(int y = 0; y < second.size()-1; y++) {
				tpl = tpl + second.get(y).getEdge(second.get(y+1)).length();
			}
			 
			if(tpl < state.stepsToGo()) {
				for(int q=1; q<first.size(); q++) {
					state.moveTo(first.get(q));
					u.remove(first.get(q));
				}
			SP_optimized(state, u);	
			return;}
			}
		}
		
		List<Node> end = A6.shortestPath(state.currentNode(), state.exit());
			for(int r=1; r<end.size(); r++) {
				state.moveTo(end.get(r));
		}
		return;
	}
	}
			





