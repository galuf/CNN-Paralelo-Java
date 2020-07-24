// entrenamieto simple (no paralelo)
public class Principal2 {
  
  public static double[][] input;
  public static double[][] output;

  public static void main(String[] args) {
    
    ImagenProces img = new ImagenProces();
    input = new double[1800][105];
    output = new double[1800][2];

    String ruta_input_gato = "img_train//gato//cat.";
    String ruta_input_perro = "img_train//perro//dog."; 
    //img.elemento(ruta_input_gato+"1.jpg");
    double[] salida_G = {1,0};
    double[] salida_D = {0,1};
    
    for(int i=0;i<900;i++){
      input[2*i] = img.elemento(ruta_input_gato+(4000+i+1)+".jpg");
      System.out.println("Imagen "+(2*i));
      output[2*i] = salida_G;
      
      input[2*i+1] = img.elemento(ruta_input_perro+(4000+i+1)+".jpg");
      System.out.println("Imagen "+(2*i +1));
      output[2*i+1] = salida_D;
    }
    
    prueba2 rn = new prueba2(null,105,50,20,2);
    rn.entrenamiento(input, output,100);
    rn.guardaPesos();
  }
}