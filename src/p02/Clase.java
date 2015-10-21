package p02;

public class Clase implements Runnable {

	static int i = 0;

	static void cambia() {
		i = 3;
		//duerme();
		i = 4;
	}

	private static void duerme() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			
		}
	}

	static int calcula() {
		int result = i;
		result += 2 * i;
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

		Clase racer = new Clase();
		Thread t1, t2;

		t1 = new Thread(racer, "Hebra1");
		t2 = new Thread(racer, "Hebra2");

		t1.start();
		t2.start();

		t1.join();
		t2.join();

	} // main	


} // class Clase
