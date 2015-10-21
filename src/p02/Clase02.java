package p02;

public class Clase02 implements Runnable { 

	static  volatile int x = 0, y = 0;
	
	private static void duerme() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			
		}
	}
	
	static void cambia(){
		//duerme();
		x = 3;
		y = 4;
	}
	static int calcula(){
		int result = y;
		result += x;
		return result;
	}
	
	@Override
	public void run() {
		int numHebra;
		if (Thread.currentThread().getName().equals("Hebra1"))
			numHebra = 0;
		else
			numHebra = 1;

		if (numHebra == 0)
			cambia();
		else
			System.out.println(calcula());

	}
	
	public static void main(String[] args) throws InterruptedException {

		Clase02 racer = new Clase02();
		Thread t1, t2;

		t1 = new Thread(racer, "Hebra1");
		t2 = new Thread(racer, "Hebra2");
		
		t2.start();
		t1.start();

		t1.join();
		t2.join();

	} // main

} // class Clase

