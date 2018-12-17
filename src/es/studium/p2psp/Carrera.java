package es.studium.p2psp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Carrera{
	
	static int META = 0;
    private List<Camello> camellos = new ArrayList<Camello>();
    private ExecutorService exec = Executors.newCachedThreadPool();
    private CyclicBarrier barrier;
    private int primerCamello;
    private boolean llegadaMeta;
   
    public Carrera(int numCamellos, final int pause) {
    	primerCamello = 0;
    	llegadaMeta = false;
  
    	
    	// Se usa la clase CyclicBarrier para sincronizar los hilos
    	// Se instancia pasandole los hilos a sincronizar
        barrier = new CyclicBarrier(numCamellos, new Runnable() {
            public void run() {
                
                /*
                 * aquí lo que estamos haciendo es la comprobación de quien es el primer camello
                 * ya que en cualquier momento esto puede cambiar, tenemos que coger los pasos
                 * con el getPasos y comparandolos con los del caballo que es el primero, si 
                 * el valor obtenido es mayor al del que antes era el primero, dentro de la variable
                 * primer camello, se mete el nuevo camello que va en primera posición
                 */
                for (Camello camello : camellos) {
                	// obtenemos el lider
                	if(camello.getPasos() > primerCamello) {
                		primerCamello = camello.getPasos();
                	}
                }
             
                /*
                 * después de meter una bola tenemos que mostrar por pantalla las posiciones, por eso
                 * en el primer if, se comprueba que los pasos del camello sean iguales al del primero, 
                 * en caso de que sea así, eso significa que es el camello que va en cabeza, por lo tanto
                 * al lado de su id, ponemos que es el primero para poder identificarlo. En el caso de que
                 * no sea así, se imprime el id del camello, y se muestra cuantos puntos tiene, y luego resta
                 * los pasos que tiene el primero con los suyos, imprimiendo así a cuántas posiciones está 
                 * del primer camello.
                 * Y luego tenemos otro if, donde se comprueba si los pasos del camello, son iguales o mayor
                 * a los puntos que el usuario a definido como meta, si es así, el atributo boolean llegadaMeta
                 * se cambia a true, estando en false al empezar la carrera
                */
                for (Camello camello : camellos) {
                	if(camello.getPasos() == primerCamello) {
                		System.out.println("El Camello " + (camello.getId()+1) + " " 
                				+ "lleva: "+camello.getPasos()+" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
                				+ " Es el primero");
                	}else {
                		System.out.println("El Camello " + (camello.getId()+1) + " " 
                				+ "lleva: "+camello.getPasos()+" y está"
                				+ " a "+(primerCamello-camello.getPasos())
                				+" posiciones del lider");
                	}
                	if (camello.getPasos() >= META) {
                		llegadaMeta = true;
                	} 
                }
                
                /* Si llegadaMeta es cambiado a true, se termina la carrea y se detiene todo, imprimiendose
                las posiciones finales de forma ordenada con la clase Collections.sort
                
                se realiza otra comprobación, donde se busca el camello que ha llegado primero,
                si no es el primero lo que hace es mirar a cuántas posiciones se ha quedado del primero
                */
                if(llegadaMeta) {
                	System.out.println("\n" +"  POSICIONES: " + "\n");
                	// Ordenar las posiciones e imprimir                 	
                	Collections.sort(camellos);
                	int i=1;
                	for (Camello camello : camellos) {
                		if(i==1) {
                			System.out.println(i+"º: Camello "+ (camello.getId()+1));
                		}else {
                			System.out.println(i+"º: Camello "+ (camello.getId()+1)
                					+ " a "+(META - camello.getPasos())+" posiciones");
                		}                		
                		i++;
                	}
                	exec.shutdownNow();
                    return;
                }                	               
                
                //en este try se ejecutan las pausas metiendo el atributo pause, donde tiene en milisegundos
                //la cantidad de tiempo entre cada ciclo
                
                try {
                    TimeUnit.MILLISECONDS.sleep(pause);
                } catch (InterruptedException e) {
                	System.out.println("barrier-action sleep interrupted");
                }
            }
        });
        // instanciamos el numero de Camellos y creamos sus hilos.
        //el for se repite el número de veces que el usuario haya ingresado para el número
        //de los camellos, creando así los camellos.
        for (int i = 0; i < numCamellos; i++) {
            Camello camello = new Camello(barrier);
            camellos.add(camello);
            exec.execute(camello);         
        }
    }

	public static void main(String[] args) throws InterruptedException{
		//creamos la variable que va a recoger el número de camellos y la ponemos a 0
		//para más abajo darle el valor que nos ingrese el usuario por pantalla
		int numCamellos = 0;
		
		/*
		 * lo que estamos haciendo aquío es preguntar al usuario cuántos camellos van a corer en la carrera
		 * así como que distancia queremos que corran en total.
		 * */
		
    	BufferedReader lectura = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println("Cuantos camellos van a correr?");
    	try {
    		numCamellos= Integer.parseInt(lectura.readLine());
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	System.out.println("Qué distancia quiere que corran?");
    	try {
    		META = Integer.parseInt(lectura.readLine());
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
/*    	
  La variable int pause lo que hace es definir el tiempo que se pausa
   después de cada ciclo, si se 
  quiere que sea más rápido, sólo tenemos que
  hacer el número más pequeño, significando que
  son menos milisegundos entre ciclo y ciclo  	
*/    	
        int pause = 500;
        //se ejecuta con los camellos y con las pausas
        new Carrera(numCamellos, pause);      
    }
}

