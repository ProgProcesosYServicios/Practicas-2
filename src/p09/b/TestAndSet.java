package p09.b;

import java.util.concurrent.atomic.AtomicInteger;

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
 * Para evitar la condición de carrera, se utiliza la instrucción
 * "Test and Set" proporcionada por Java en la clase
 * java.util.concurrent.atomic.AtomicInteger.
 * 
 * @author Pedro Pablo Gómez Martín
 */
public class TestAndSet implements Runnable {

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
		//Thread.yield(); // Aumentar la probabilidad de condición de carrera
		return total;
		
	} // sumaN
	
	//-----------------------------------------------------

	/**
	 * Método a ser ejecutado a través de una hebra. Llama
	 * NUM_VECES a sumaN para sumar NUMERO_SUMADO al atributo _suma.
	 */
	public void run() {
		
		for (int i = 1; i <= NUM_VECES; ++i) {
			//System.out.println("[" + numHebra + "]: intento entrar");
			entradaSeccionCritica();
				//System.out.println("[" + numHebra + "]: \t entro");
				_suma = sumaN(_suma, NUMERO_SUMADO);
				//System.out.println("[" + numHebra + "]: \t salgo (" + _suma + ")");
			salidaSeccionCritica();
		}

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
	// Métodos y atributos de gestión de la sección crítica
	//-----------------------------------------------------

	/**
	 * "Preprotocolo" para entrar en la sección crítica de modo
	 * que se use _suma en exclusión mútua. Se pretende que el
	 * método vuelva únicamente cuando se garantice que sólo la
	 * hebra actual estará dentro de la sección crítica.
	 */
	protected void entradaSeccionCritica() {

		while(!testAndSet())
			;
		
	} // entradaSeccionCritica

	/**
	 * "Postprotocolo" para indicar que la hebra ha terminado
	 * el uso del recurso compartido que debe ser usado en
	 * exclusión mútua y que abandona por tanto la sección crítica.
	 */
	protected void salidaSeccionCritica() {
		
		_cerrojo.set(0);
		
	} // salidaSeccionCritica

	/**
	 * Realiza la instrucción "test and set" sobre el atributo
	 * _cerrojo. Se usa en entradaSeccionCritica().
	 * @return
	 */
	protected boolean testAndSet() {
		
		// Java garantiza atomicidad en esta operación.
		return _cerrojo.compareAndSet(0,1);

	}

	protected AtomicInteger _cerrojo = new AtomicInteger(0);

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
		
		TestAndSet racer = new TestAndSet();
		Thread t1, t2;
		
		t1 = new Thread(racer, "Hebra0"); // Los nombres ya no son
		t2 = new Thread(racer, "Hebra1"); // imprescindibles...

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
	 */
	private volatile long _suma = 0;
	
} // TestAndSet
