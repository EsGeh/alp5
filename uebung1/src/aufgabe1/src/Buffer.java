
public interface Buffer<E> {
	// pre (q,s) =  length q < s
    // post(q,q',s,s',x) =  q'==q++[x]   && s'==s	
	void send(E x);
	
	// pre (q,s) =  length q > 0
    // post(q,q',s,s',result) = result:q'==q && s'==s	
	E recv();
	int length();
}
