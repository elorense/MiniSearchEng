
public class Lock {
	private int numReader, numWriter;

	public Lock(){
		numReader = 0;
		numWriter = 0;
	}
	
	public synchronized void acquireWriteLock(){

		while(numReader > 0 || numWriter > 0){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		numWriter++;

	}
	
	public synchronized void acquireReadLock(){

		while(numWriter > 0){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		numReader++;

	}
	
	public synchronized void releaseReadLock(){
		numReader--;
		notifyAll();
	}
	
	public synchronized void releaseWriteLock(){
		numWriter--;
		notifyAll();

	}
	
}
