// Entrenamiento Paralelo

import java.util.ArrayList;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

public class paralel {

  public static ArrayList<ArrayList <double[][]> > pesos = new ArrayList<ArrayList <double[][]> >();
	public static ArrayList<double []> costos = new ArrayList<double []>();
	public static ArrayList<double[][]> peso_promedio = new ArrayList<double[][]>();
	public static ArrayList<ArrayList <double[][]> > pesos_final = new ArrayList<ArrayList <double[][]> >();
	public static double[][] input;
	public static double[][] output;
	
  public static void main(String args[]){

		ImagenProces img = new ImagenProces();
    input = new double[100][105];
    output = new double[100][2];

    String ruta_input_gato = "img_train//gato//cat.";
    String ruta_input_perro = "img_train//perro//dog."; 
    //img.elemento(ruta_input_gato+"1.jpg");
    double[] salida_G = {1,0};
    double[] salida_D = {0,1};
		
		// LLenamos las matrices Input y Output
    for(int i=0;i<50;i++){
      input[2*i] = img.elemento(ruta_input_gato+(4000+i+1)+".jpg");
      System.out.println("Imagen "+(2*i));
      output[2*i] = salida_G;
      
      input[2*i+1] = img.elemento(ruta_input_perro+(4000+i+1)+".jpg");
      System.out.println("Imagen "+(2*i +1));
      output[2*i+1] = salida_D;
		}

		//Entrenamos 5 hilos
		new paralel().inicio();
		
		//Hallamos los pesos mas eficientes
		int efi = eficiente();
		pesos_final.add(pesos.get(efi));
		//Hallamos el promedio de los pesos
		pesosPromedio(); // Funciona correctamente ? SI
		pesos_final.add(peso_promedio);
		pesos_final.add(null);

		System.out.println("Segundo Bloque: ");
		
		//Entrenamos segundo Bloque 3 Bloques
		new paralel().segundo();

		int efi2 = eficiente();

		guardaPesos(pesos.get(efi2));
	}  
	
  void inicio(){
			pesos.clear();
			costos.clear();
      int epocas_hilo = 1000;
			int H = 5;
			for(int i=0;i<H;i++){
				pesos.add(null);
				costos.add(null);
			}
      Thread todos[] = new Thread[40];
      for (int i = 0; i < H; i++) {
          todos[i]= new tarea0101(i,epocas_hilo,null);
          todos[i].start();
      }
      
      for(int i=0;i < H; i++){
          try{
            todos[i].join();
          } catch (InterruptedException ex) {
              System.out.println("error"+ex);
          }
      }
	}
	
	void segundo(){
		pesos.clear();
		costos.clear();
		int epocas_hilo = 1000;
		int H = 3;
		for(int i=0;i<H;i++){
			pesos.add(null);
			costos.add(null);
		}
		Thread todos[] = new Thread[40];
		for (int i = 0; i < H; i++) {
				todos[i]= new tarea0101(i,epocas_hilo,pesos_final.get(i));
				todos[i].start();
		}
		
		for(int i=0;i < H; i++){
				try{
					todos[i].join();
				} catch (InterruptedException ex) {
						System.out.println("error"+ex);
				}
		}
}
  
  public  class tarea0101 extends Thread {
    public int id;
		public int epocas;
		public ArrayList<double[][]> peso;

    tarea0101(int id_,int epocas_,ArrayList<double[][]> peso_) {
        id = id_;
				epocas = epocas_;
				peso = peso_;
    }	
		
		public void run() {
			//prueba2 rn = new prueba2(105,50,20,2);
			prueba2 rn = new prueba2(peso,105,50,20,2);
      rn.entrenamiento(input, output,epocas);
			//rn.guardaPesos();
			//System.out.println(id);
			//rn.showCosto();
			pesos.set(id,rn.damePeso());
			costos.set(id,rn.dameCosto());
    }
  }

	public static int eficiente(){
		int num=0;
		double min = 1000;
		for(int i =0;i<costos.size();i++){
			double prom=0;
			for(int j=0;j<costos.get(i).length;j++){
				prom+=costos.get(i)[j];
			}
			if(min > prom/(costos.get(i).length)){
				min = prom/(costos.get(i).length);
				num = i;
			}
		}
		return num;
	}
	public static void pesosPromedio(){
		for(int i=0;i<pesos.get(0).size();i++){
			double [][] m= null;
			for(int j=0;j<pesos.size();j++){
				if(j==0){
					m = new double[pesos.get(j).get(i).length][pesos.get(j).get(i)[0].length];
					m = zeros(m.length,m[0].length);
				}
				m = sumaMatriz(m,pesos.get(j).get(i));
				//System.out.println(m.length+" "+m[0].length);
				if(j == pesos.size()-1)
					peso_promedio.add(divide(m,5));
			}
		}
	}
	public static double[][] zeros(int f,int c){
		double[][] m = new double[f][c];
		for(int i=0;i<f;i++){
			for(int j=0;j<c;j++){
				m[i][j] = 0;
			}
		}
		return m;
	}

	public static double[][] sumaMatriz(double[][] a, double[][] b){
    if(a.length != b.length  || a[0].length != b[0].length)
      throw new RuntimeException("No se pueden sumar las matrices.");
    double [][] suma = new double[a.length][a[0].length];
    for(int i=0;i<a.length;i++){
      for(int j = 0;j<a[0].length;j++){
        suma[i][j] = a[i][j] + b[i][j];
      }
    }
    return suma;
	}
	public static double[][] divide(double[][] m,double a){
		double [][] producto = new double[m.length][m[0].length];
		for(int i=0;i<m.length;i++){
			for(int j = 0;j<m[0].length;j++){
				producto[i][j] = m[i][j]/a;
			}
		}
		return producto;
	} 
	public static void mostrar(double[][] m){
    for(int i=0;i<m.length;i++){
      for(int j = 0; j<m[0].length;j++){
        System.out.print(m[i][j]+" ");
      }
      System.out.println();
		}
		System.out.println("\n");
  } 

	public static void guardaPesos(ArrayList<double[][]> pesos){
		int num_pesos = pesos.size();
		for(int i=0;i<num_pesos;i++){
      FileOutputStream fos = null;
      DataOutputStream salida = null;
      
      int filas = pesos.get(i).length;
      int columnas = pesos.get(i)[0].length;

      try {
        //crear el fichero de salida
        File fiche = new File(System.getProperty("user.dir")+"//w"+(i+1)+".dat");
        fos = new FileOutputStream(fiche);
        salida = new DataOutputStream(fos);

        //escribir el número de filas y columnas en el fichero                                                
        salida.writeInt(filas);
        salida.writeInt(columnas);
      
      //escribir la matriz en el fichero
        for (int ii = 0; ii < filas; ii++) {
            for (int jj = 0; jj < columnas; jj++) {
                salida.writeDouble(pesos.get(i)[ii][jj]);
            }
        }
      } catch (FileNotFoundException e) {
          System.out.println(e.getMessage());
      } catch (IOException e) {
          System.out.println(e.getMessage());                                                                   
      } finally {
        try {
            if (fos != null) {
                fos.close();
            }
            if (salida != null) {
                salida.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
      }
    }
    System.out.println("Pesos Guardados");
  }
}