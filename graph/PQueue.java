package graph;

/** An implementation implements a priority queue whose elements are of type E. <br>
 * Below, N is used as the number of elements currently in the priority queue. <br>
 * Duplicate elements are not allowed. <br>
 * The priorities are double values. */
interface PQueue<E> {

	/** Return a string that represents this priority queue, in the format:<br>
	 * [item0:priority0, item1:priority1, ..., item(N-1):priority(N-1)]<br>
	 * Thus, the list is delimited by '[' and ']' and ", " (i.e. a<br>
	 * comma and a space char) separate adjacent items. */
	@Override
	String toString();

	/** Return the number of elements in the priority queue. */
	int size();

	/** Return true iff the priority queue is empty. */
	boolean isEmpty();

	/** Add e with priority p to the priority queue.<br>
	 * Throw an illegalArgumentException if e is already in the queue. */
	void add(E e, double priority) throws IllegalArgumentException;

	/** Return the element of the priority queue with lowest priority, <br>
	 * without changing the priority queue.<br>
	 * Precondition: the priority queue is not empty. */
	E peek();

	/** Remove and return the element of the priority queue with lowest priority.<br>
	 * Precondition: the priority queue is not empty. */
	E poll();

	/** Change the priority of element e to p.<br>
	 * Precondition: e is in the priority queue */
	void changePriority(E e, double p);
}
