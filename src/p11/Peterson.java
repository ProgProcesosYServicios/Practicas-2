package  p11;

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
 * Para evitar la condición de carrera, se hace uso del algoritmo
 * de Peterson, pero donde usamos suspend() y resume() para no hacer
 * espera activa. Sin embargo, esto añade una condición de carrera
 * que puede hacer que las dos hebras terminen suspendidas, por lo
 * que la solución no es correcta.
 * 
 * @author Pedro Pablo Gómez Martín
 */
public class Peterson implements Runnable {

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
	 * Constructor. Inicializa los atributos que mantienen el estado
	 * de la sección crítica.
	 */
	public Peterson() {
		
		_enSeccionCritica = new Flag[2];
		_enSeccionCritica[0] = new Flag();
		_enSeccionCritica[1] = new Flag();

	} // constructor

	//-----------------------------------------------------

	public void setThreads(Thread t0, Thread t1) {

		_threads[0] = t0;
		_threads[1] = t1;

	} // setThreads
	
	//-----------------------------------------------------

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

		int numHebra;
		if (Thread.currentThread().getName().equals("Hebra0"))
			numHebra = 0;
		else
			numHebra = 1;
		
		for (int i = 1; i <= NUM_VECES; ++i) {
			//System.out.println("[" + numHebra + "]: intento entrar");
			entradaSeccionCritica(numHebra);
				//System.out.println("[" + numHebra + "]: \t entro");
				_suma = sumaN(_suma, NUMERO_SUMADO);
				//System.out.println("[" + numHebra + "]: \t salgo (" + _suma + ")");
			salidaSeccionCritica(numHebra);
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
	 * 
	 * @param numHebra Número de hebra (0 o 1) que quiere entrar
	 * en la sección crítica. 
	 */
	protected void entradaSeccionCritica(int numHebra) {

		_enSeccionCritica[numHebra].valor = true;

		int otraHebra = numHebra ^ 0x1;
		
		_turno = otraHebra;
		while(_enSeccionCritica[otraHebra].valor &&
		      (_turno == otraHebra))
			Thread.currentThread().suspend();
		
		// ¡Está libre!

	} // entradaSeccionCritica

	/**
	 * "Postprotocolo" para indicar que la hebra ha terminado
	 * el uso del recurso compartido que debe ser usado en
	 * exclusión mútua y que abandona por tanto la sección crítica.
	 * 
	 * @param numHebra Número de hebra (0 o 1) que abandona
	 * la sección crítica.
	 */
	protected void salidaSeccionCritica(int numHebra) {
		
		_enSeccionCritica[numHebra].valor = false;

		int otraHebra = numHebra ^ 0x1;
		// Un resume a una hebra no suspendida es inocuo. La
		// "despertamos" siempre.
		_threads[otraHebra].resume();
				
	} // salidaSeccionCritica

	/**
	 * Clase ("estructura") con un único booleano. Lo importante
	 * del booleano es que es volátil.
	 */
	class Flag {
		public volatile boolean valor = false;
	} // class Flag

	protected Flag[] _enSeccionCritica; // Inicialización en el constructor

	/**
	 * Hebras que están ejecutando la clase y que entran en juego
	 * en la exclusión mutua.
	 */
	protected Thread[] _threads = new Thread[2];

	/**
	 * Hebra que tiene el turno para entrar en la sección crítica (en caso
	 * de "empate").
	 */
	protected volatile int _turno = 0;

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
		
		Peterson racer = new Peterson();
		Thread t1, t2;
		
		t1 = new Thread(racer, "Hebra0");
		t2 = new Thread(racer, "Hebra1");
		racer.setThreads(t1,  t2);

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
	
} // Peterson
