package p01;

/**
 * Clase de ejemplo que busca conseguir una condición de carrera
 * de dos hebras. Implementa el interfaz Runnable y tiene un main.
 * En el main se crea una instancia del objeto y se utiliza en
 * dos Thread diferentes.
 * 
 * El método run() ejecutado por cada hebra suma NUM_VECES veces el
 * valor NUMERO_SUMADO al atributo _suma. Pero la suma la realiza
 * incrementando el valor de uno en uno.
 * 
 * Como la operación es sencilla, es fácil saber de antemano el
 * valor final esperado, para compararlo con el conseguido. 
 * 
 * @author Pedro Pablo Gómez Martín
 */
public class CondicionDeCarrera implements Runnable {

	/**
	 * Número que vamos a sumar al atributo _suma en el método
	 * run(). Pero lo haremos de uno en uno.
	 */
	public static final int NUMERO_SUMADO = 10000;

	/**
	 * Número de veces que vamos a sumar NUMERO_SUMADO al atributo
	 * _suma en el método run().
	 */
	public static final long NUM_VECES = 10000;

	/**
	 * Método estático que devuelve acumulador + n. Hace la
	 * suma de uno en uno con un for.
	 * 
	 * @param acumulador Valor inicial.
	 * @param n Valor a sumar
	 * @return acumulador + n
	 */
	private static long sumaN(long acumulador, int n) {

		long total = acumulador;
		for (int i = 0; i < n; ++i)
			total += 1;
		//Thread.yield();
		return total;
		
	} // sumaN
	
	//-----------------------------------------------------

	/**
	 * Método a ser ejecutado a través de una hebra. Llama
	 * NUM_VECES a sumaN para sumar NUMERO_SUMADO al atributo _suma.
	 */
	public void run() {
		
		for (int i = 1; i <= NUM_VECES; ++i)
			_suma = sumaN(_suma, NUMERO_SUMADO);

	} // run
	
	//-----------------------------------------------------

	/**
	 * Devuelve el valor del atributo _suma.
	 * 
	 * @return Valor del atributo _suma.
	 */
	public long getSuma() {
		
		return _suma;

	} // getSuma

	//-----------------------------------------------------
	//                    Métodos estáticos
	//-----------------------------------------------------

	/**
	 * Programa principal. Crea una instancia de esta clase, y la
	 * ejecuta simultáneamente en dos hebras diferentes. Espera a que
	 * ambas terminen y mira el valor sumado final, comprobando si es
	 * el esperado.
	 * 
	 * @param args Parámetros de la aplicación. Se ignoran.
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		
		CondicionDeCarrera racer = new CondicionDeCarrera();
		Thread t1, t2;
		
		t1 = new Thread(racer);
		t2 = new Thread(racer);

		t1.start();
		t2.start();

		long resultadoEsperado;
		resultadoEsperado = NUMERO_SUMADO * NUM_VECES * 2;

		t1.join();
		t2.join();

		System.out.println("El resultado final es " + racer.getSuma());
		System.out.println("Esperábamos " + resultadoEsperado);

		if (racer.getSuma() != resultadoEsperado)
			System.out.println("¡¡¡NO COINCIDEN!!!");
		
	} // main

	//-----------------------------------------------------
	//                    Atributos privados
	//-----------------------------------------------------

	/**
	 * Atributo con el valor acumulado donde se realiza la suma.
	 * Hace las veces de variable compartida entre las dos hebras.
	 * volatile significa que será modificada por varias hebras
	 */
	private volatile long _suma = 0;
	
} // CondicionDeCarrera
