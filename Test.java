import java.util.*;
import java.io.File;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {

    static ArrayList<double[][]> pesos = new ArrayList<double[][]>();
    static ArrayList<double[]> test = new ArrayList<double[]>();
    static ArrayList<double[]> salida = new ArrayList<double[]>(); 
    static ArrayList<double[][]> testeo = new ArrayList<double[][]>();       
    public static double[][] evaluar;
    public static void main(String[] args) {
      
      evaluar = new double[200][105]; //200 imagenes de 105(array lenght)

      ImagenProces img = new ImagenProces();

      String ruta_test_gato = "img_test//gato//cat.";
      String ruta_test_perro ="img_test//perro//dog.";
      
      for(int i=0;i<100;i++){ // se ingresa elementos de dos en dos (100*2)
        evaluar[2*i] = img.elemento(ruta_test_gato+(4900+i+1)+".jpg");
        System.out.println("Imagen "+(2*i));
      
        evaluar[2*i+1] = img.elemento(ruta_test_perro+(4900+i+1)+".jpg");
        System.out.println("Imagen "+(2*i +1));
      }

      int num_pesos = 3; // castidad de W 
      
      for(int i=0;i<num_pesos;i++){
          File fiche = new File(System.getProperty("user.dir")+"//w"+(i+1)+".dat");
          pesos.add(matrizPeso(fiche));
      }

      prueba(evaluar,num_pesos);
    }

    static double[][] matrizPeso(File peso){
    
        FileInputStream fis = null;
        DataInputStream entrada = null;
        double [][] matrizError = {{0,0,0},{0,0,0}};
        try {
            fis = new FileInputStream(peso);
            entrada = new DataInputStream(fis);
            int filas = entrada.readInt();            //se lee el primer entero del fichero                           
            int columnas = entrada.readInt();
            System.out.println(filas+" "+columnas);
            double [][] matriz = new double[filas][columnas];         //se lee el segundo entero del fichero
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {  // se leen los double y se guarda en una matriz                
                    matriz[i][j] = entrada.readDouble();
                }
            }
            return matriz;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (EOFException e) {
            System.out.println("Fin de fichero");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (entrada != null) {
                    entrada.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());                                                               
            }
        }
        return matrizError;
    }
    static public void mostrar(double[][] m){
        for(int i=0;i<m.length;i++){
          for(int j = 0; j<m[0].length;j++){
            System.out.print(m[i][j]+" ");
          }
          System.out.println();
        }
    }

    static public double[][] dotArMa(double[] a, double[][] b){
    
        int fil_a = 1;
        int col_a = a.length;
            
        int fil_b = b.length;
        int col_b = b[0].length;
        
        if (col_a != fil_b){
          System.out.println(fil_a+" "+col_a);
          System.out.println(fil_b+" "+col_b);
          throw new RuntimeException("No se pueden multiplicar dotArMa las matrices");
        }
    
        double[][] multiplicacion = new double[fil_a][col_b]; // c
    
        for (int i = 0; i < fil_a; i++) {
          for (int j = 0; j < col_b; j++) {
            for (int k = 0; k < col_a; k++) {
              multiplicacion[i][j] += a[k] * b[k][j];
            }
          }
        }
        return multiplicacion;
    }

    static public double sigmoidea(double x){
        return 1/(1+Math.exp(-x));
    }

    public static double[] generaVector(double[][] m){
        double[] array = new double[625];
    
        for(int i=0;i<m.length;i++){
          for(int j = 0; j<m[0].length;j++){
            if(i*25 +j < 625)
              array[i*25 +j] = m[i][j];
          }
        }
        if(m.length*m[0].length < 625 ){
          for (int i = m.length*m[0].length -1 ; i<625;i++)
            array[i] = 0;
        }
    
        return array;
      }
    
    static double[][] matrizSigmoidea(double[][] m){
        double[][] m2 = new double[m.length][m[0].length];
        for(int i=0;i<m.length;i++){
          for(int j = 0; j<m[0].length;j++){
            m2[i][j] = sigmoidea(m[i][j]);
          }
        }
        return m2;
    }

    static void prueba(double [][] entrada, int num_pesos){
        salida.clear();
        System.out.println("-----****Inicio Test****-------");
        
        for(int i = 0; i<entrada.length;i++){
          test.clear();
          test.add(entrada[i]);
          //System.out.println(entrada.length);
          for(int ii=1;ii<num_pesos+1;ii++){
            test.add(
              matrizSigmoidea(
                dotArMa(
                  test.get(ii-1),
                  pesos.get(ii-1)
                )
              )[0]);
          }
          salida.add(test.get(test.size()-1));
        }
        for(int j=0;j<salida.size();j++){
          System.out.println("Salida : ");
          for(int i=0;i<salida.get(j).length;i++){
            System.out.println("["+ salida.get(j)[i] +"] ");
          }
          if(mayor(salida.get(j))==0){
            System.out.println("Gato");
          }else{
            System.out.println("Perro");
          }
          System.out.println();
        }
    }
    static int  mayor(double [] numeros){
        double mayor = -10000;
        int index=0;
        for(int i =0;i<numeros.length;i++){
            if(numeros[i] > mayor){
                mayor=numeros[i];
                index = i;
            }
        }
        return index;
    }     
}