package es.studium.p2psp;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class Camello implements Runnable, Comparable<Camello>{
	private static int counter = 0;
	private final int id = counter++;
	private int avanza = 0;
	private static Random rand = new Random();
	private static CyclicBarrier barrier;
	private int tirada;        

	public void run() {
		try {
			while (!Thread.interrupted()) {
				// Sincronizacion de todos los hilos
				synchronized (this) {
					try {
						avanza += tirada();
					} catch (IOException e) {
						e.printStackTrace();
					}          
				}
				// El metodo await obliga a esperar a que todos los hilos detenido
				barrier.await();
			}
		} catch (InterruptedException e) {

		} catch (BrokenBarrierException e) {

			throw new RuntimeException(e);
		} 
	}

	/*
     Este método lo que hace es generar un número aleatorio, el cuál es usado 
     para determinar la probabilidad que tiene cada bola de entrar en el agujero
	 * */

	public int tirada() throws IOException{    	
		int resultado=0;
		int tirada=rand.nextInt(10);
		if(tirada < 3){
			resultado = 0;
		}else if(tirada < 7) {
			resultado = 1;
		}else if(tirada < 9) {
			resultado = 1;    		
		}else {
			resultado = 2;
		}

		//aquí lo que hacemos es meter con el setter el resultado de la probabilidad, dentro de la tirada	

		setTirada(resultado);
		return resultado;
	}

	/* Metodo requerido por Collections.sort para ordenar un Objeto, en este caso lo que está
	 * haciendo es ordenar las posiciones que tienen los camellos en el resultado final, el Collections.sort, 
	 * se usa en la clase CarreraMain
	 */
	public int compareTo(Camello comparaCamello) {
		int comparaPasos = ((Camello) comparaCamello).getPasos();
		//orden descendiente
		return comparaPasos - this.avanza;
	}

	public int getTirada() {
		return tirada;
	}

	public void setTirada(int tirada) {
		this.tirada = tirada;
	}

	public int getId() {
		return id;
	}   

	public Camello(CyclicBarrier b) {
		barrier = b;    
	}

	public synchronized int getPasos() {
		return avanza;
	}
}
