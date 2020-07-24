import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.util.*;

public class ImagenProces  extends Component { 

  public double[][] input;
  public double[][] output;

  public void printPixelARGB(int pixel) {
    int alpha = (pixel >> 24) & 0xff;
    int red = (pixel >> 16) & 0xff;
    int green = (pixel >> 8) & 0xff;
    int blue = (pixel) & 0xff;
    System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
  }
  
  private double[][] generalMaxPooling(double[][] image, int poolDim) throws IOException {
            
      int width_img = image.length;
      int height_img = image[0].length;
      System.out.println("new width, new height: " + width_img + ", " +height_img);
      int out_width = (int)Math.ceil((float)width_img/poolDim);
      int out_height = (int)Math.ceil((float)height_img/poolDim);
      
      double output[][] = new double[out_width][out_height];
      
      int outX = 0, outY = 0 ;
      
      
      for(int x=0; x<width_img ; x += poolDim){
        for(int y=0 ;y<height_img ; y += poolDim){

                int maxr = -1, maxg = -1, maxb = -1;

                for(int maxPoolX=x; maxPoolX< Math.min(x+poolDim,width_img); maxPoolX++){
                    for(int maxPoolY=y; maxPoolY<Math.min(y+poolDim,height_img); maxPoolY++)
                    {
                            int imageX = maxPoolX;
                            int imageY = maxPoolY;

                            int rgb = (int)image[imageX][imageY];
                            int r = (rgb >> 16) & 0xff; 
                            int g = (rgb >> 8) & 0xff;
                            int b = (rgb & 0xff);
                            
                            if( r > maxr) maxr = r;
                            if( g > maxg) maxg = g;
                            if( b > maxb) maxb = b;

                    }
                }
 
                int rr = (int) maxr;
                int gg = (int) maxg;
                int bb = (int) maxb;
 
                int pixel = (rr << 16) + (gg << 8) + bb;
                output[outX][outY] = pixel;
                
                outY++;
        }
        outY = 0;
        outX++;
      }

      return output;
  }
  
  private void maxPooling(BufferedImage image, String image_name) throws IOException {
            
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Ingresa dim maxPooling: ");
      int poolDim = Integer.parseInt(reader.readLine());
      System.out.println(poolDim);
      
      int width_img = image.getWidth();
      int height_img = image.getHeight();
      System.out.println("new width, new height: " + width_img + ", " +height_img);
      int out_width = (int)Math.ceil((float)width_img/poolDim);
      int out_height = (int)Math.ceil((float)height_img/poolDim);
      
      BufferedImage image_out = new BufferedImage(out_width, out_height, image.getType());
      
      int outX = 0, outY = 0 ;
      
      for(int x=0; x<width_img ; x += poolDim){
        for(int y=0 ;y<height_img ; y += poolDim){

                int maxr = -1, maxg = -1, maxb = -1;

                for(int maxPoolX=x; maxPoolX< Math.min(x+poolDim,width_img); maxPoolX++){
                    for(int maxPoolY=y; maxPoolY<Math.min(y+poolDim,height_img); maxPoolY++)
                    {
                            int imageX = maxPoolX;
                            int imageY = maxPoolY;

                            int rgb = image.getRGB(imageX,imageY);
                            int r = (rgb >> 16) & 0xff; 
                            int g = (rgb >> 8) & 0xff;
                            int b = (rgb & 0xff);
                            
                            if( r > maxr) maxr = r;
                            if( g > maxg) maxg = g;
                            if( b > maxb) maxb = b;

                    }
                }
 
                int rr = (int) maxr;
                int gg = (int) maxg;
                int bb = (int) maxb;
 
                int pixel = (rr << 16) + (gg << 8) + bb;
                image_out.setRGB(outX,outY,pixel);
                
                outY++;
        }
        outY = 0;
        outX++;
      }
      
      System.out.print("Nombre de archivo modificado: ");
      String outputfname = reader.readLine();
      File myNewJPegFile = new File(System.getProperty("user.dir")+"\\src\\leerjpg\\"+outputfname+".jpg");
      ImageIO.write(image_out, "jpg", myNewJPegFile);
  }
  
  private double[][] kernelEntreLayers(double[][] image, double kernel[][]) throws IOException {
  
    int width_img = image.length;
    int height_img = image[0].length;
    
    int kernel_col = kernel[0].length;
    int kernel_row = kernel.length;
    int offset_x = kernel_col/2;
    int offset_y = kernel_row/2;
    
    double[][] output = new double[width_img-kernel_col+1][height_img-kernel_row+1];
    int out_x = 0;
    int out_y = 0;
    
    for(int x=0; x<width_img - kernel_col + 1 ;x++)
    {
        for(int y = 0; y < height_img - kernel_row + 1;y++)
        {
                float out=0f;
                for(int kr=0; kr<kernel_row; kr++)
                    for(int kc=0; kc<kernel_col; kc++)
                    {
                            int imageX = x + kr;
                            int imageY = y + kc;

                            int punto = (int)image[imageX][imageY];
                            out += (punto*kernel[kr][kc]);

                    }
                /*
                int rr = Math.min(Math.max((int)(red*1),0),255);
                int gg = Math.min(Math.max((int)(green*1),0),255);
                int bb = Math.min(Math.max((int)(blue*1),0),255);
                */
                int o = (int) out;
                
                output[x][y] = o;
                out_y++;
               
        }
        out_y = 0;
        out_x++;
    }
    
    return output;
  }
  
  private double[][] applyGeneralKernel(BufferedImage image, double kernel[][]) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
 
    /* 1. Crear numero random (pero se probara con 3 y con estos numeros)
        2. For de N en base a la dimension del kernel 
        3. se obtiene N resultados. (mtariz de N x (dimxdimx1)
        4. For de M maxpooling
        5. Se obtiene matriz de M x (dimxdimx1)
        6. Aplanar esta salida  
    double kernel[][] = {{1 / 9, 1 / 9, 1 / 9},
                         {1 / 9, 1 / 9, 1 / 9},
                         {1 / 9, 1 / 9, 1 / 9}};*/
    
    int width_img = image.getWidth();
    int height_img = image.getHeight();
    BufferedImage image_out = image;
   // System.out.println("kernel col, kernel row: " + kernel[0].length + ", " + kernel.length);
   // System.out.println("Width:, Heigth: " + width_img + ", " + height_img);
    
    int kernel_col = kernel[0].length;
    int kernel_row = kernel.length;
    int offset_x = kernel_col/2;
    int offset_y = kernel_row/2;
    
    double[][] output = new double[width_img-kernel_col+1][height_img-kernel_row+1];
    int out_x = 0;
    int out_y = 0;
    
    for(int x=0; x<width_img - kernel_col + 1 ;x++)
    {
        for(int y = 0; y < height_img - kernel_row + 1;y++)
        {
                float red=0f,green=0f,blue=0f;
                for(int kr=0; kr<kernel_row; kr++)
                    for(int kc=0; kc<kernel_col; kc++)
                    {
                            int imageX = x + kr;
                            int imageY = y + kc;

                            int rgb = image_out.getRGB(imageX,imageY);
                            int r = (rgb >> 16) & 0xff; 
                            int g = (rgb >> 8) & 0xff;
                            int b = (rgb & 0xff);

                            red += (r*kernel[kr][kc]);
                            green += (g*kernel[kr][kc]);
                            blue += (b*kernel[kr][kc]);
                    }
                /*
                int rr = Math.min(Math.max((int)(red*1),0),255);
                int gg = Math.min(Math.max((int)(green*1),0),255);
                int bb = Math.min(Math.max((int)(blue*1),0),255);
                */
                int rr = (int) red;
                int gg = (int) green;
                int bb = (int) blue;
 
                int pixel = (rr << 16) + (gg << 8) + bb;
                
                output[x][y] = pixel;
                out_y++;
               
        }
        out_y = 0;
        out_x++;
    }
    // bordes
    /*
    for(int y = 0 ; y < height_img ; y++){
        int rgb = image_out.getRGB(0,y);
        int r = (rgb >> 16) & 0xff; 
        int g = (rgb >> 8) & 0xff;
        int b = (rgb & 0xff);
        
        int pixel = (r << 16) + (g << 8) + b;
        output[0][y] = pixel;
    }
    
    for(int y = 0 ; y < height_img ; y++){
        int rgb = image_out.getRGB(width_img-1,y);
        int r = (rgb >> 16) & 0xff; 
        int g = (rgb >> 8) & 0xff;
        int b = (rgb & 0xff);
        
        int pixel = (r << 16) + (g << 8) + b;
        output[width_img-1][y] = pixel;
    }
    
    for(int x = 1 ; x < width_img - 1 ; x++){
        int rgb = image_out.getRGB(x,0);
        int r = (rgb >> 16) & 0xff; 
        int g = (rgb >> 8) & 0xff;
        int b = (rgb & 0xff);
        
        int pixel = (r << 16) + (g << 8) + b;
        output[x][0] = pixel;
    }
    
    for(int x = 1 ; x < width_img - 1 ; x++){
        int rgb = image_out.getRGB(x,height_img-1);
        int r = (rgb >> 16) & 0xff; 
        int g = (rgb >> 8) & 0xff;
        int b = (rgb & 0xff);
        
        int pixel = (r << 16) + (g << 8) + b;
        output[x][height_img-1] = pixel;
    }*/
    
    return output;
  }
  
  private void applyKernel(BufferedImage image, String image_name) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    double kernel[][] = {{1 , 2, 1},
                         {2, 4, 2},
                         {1, 2, 1}};  
    /* 1. Crear numero random (pero se probara con 3 y con estos numeros)
        2. For de N en base a la dimension del kernel 
        3. se obtiene N resultados. (mtariz de N x (dimxdimx1)
        4. For de M maxpooling
        5. Se obtiene matriz de M x (dimxdimx1)
        6. Aplanar esta salida  
    double kernel[][] = {{1 / 9, 1 / 9, 1 / 9},
                         {1 / 9, 1 / 9, 1 / 9},
                         {1 / 9, 1 / 9, 1 / 9}};*/
    
    int width_img = image.getWidth();
    int height_img = image.getHeight();
    BufferedImage image_out = image;
    System.out.println("kernel col, kernel row: " + kernel[0].length + ", " + kernel.length);
    System.out.println("Width:, Heigth: " + width_img + ", " + height_img);
    
    int kernel_col = kernel[0].length;
    int kernel_row = kernel.length;
    int offset_x = kernel_col/2;
    int offset_y = kernel_row/2;
    
    for(int x=offset_x; x<width_img - offset_x ;x++)
        for(int y=offset_y ;y<height_img - offset_y ;y++)
        {
                float red=0f,green=0f,blue=0f;
                for(int kr=0; kr<kernel_row; kr++)
                    for(int kc=0; kc<kernel_col; kc++)
                    {
                            int imageX = x + kr - offset_x; 
                            int imageY = y + kc - offset_y;

                            int rgb = image_out.getRGB(imageX,imageY);
                            int r = (rgb >> 16) & 0xff; 
                            int g = (rgb >> 8) & 0xff;
                            int b = (rgb & 0xff);

                            red += (r*kernel[kr][kc]);
                            green += (g*kernel[kr][kc]);
                            blue += (b*kernel[kr][kc]);
                    }
                /*
                int rr = Math.min(Math.max((int)(red*1),0),255);
                int gg = Math.min(Math.max((int)(green*1),0),255);
                int bb = Math.min(Math.max((int)(blue*1),0),255);
                */
                int rr = (int) red;
                int gg = (int) green;
                int bb = (int) blue;
 
                int pixel = (rr << 16) + (gg << 8) + bb;

                image_out.setRGB(x,y,pixel);
        }
    
    System.out.print("Nombre de archivo modificado: ");
    String outputfname = reader.readLine();
    File myNewJPegFile = new File(System.getProperty("user.dir")+"\\src\\leerjpg\\"+outputfname+".jpg");
    ImageIO.write(image_out, "jpg", myNewJPegFile);
  }
  
  private void convertToGray(BufferedImage image, String image_name) throws IOException {
    
    int width_img = image.getWidth();
    int height_img = image.getHeight();
    BufferedImage image_out = image;
    System.out.println("width, height: " + width_img + ", " +height_img);
    
    for (int x = 0; x < image_out.getWidth(); ++x)
        for (int y = 0; y < image_out.getHeight(); ++y)
        {
            int rgb = image_out.getRGB(x, y);
            int alpha = (rgb >> 24) & 0Xff;
            int red = (rgb >> 16) & 0xff;
            int green = (rgb >> 8) & 0xff;
            int blue = (rgb & 0xff);
            
            // normalize and gamma correct
            float rr = (float) Math.pow(red   / 255.0 , 2.2);
            float gg = (float) Math.pow(green / 255.0 , 2.2);
            float bb = (float) Math.pow(blue  / 255.0 , 2.2);
            
            float ylinear = (float) (0.2126*rr + 0.7152*gg + 0.0722*bb);
            
            // doc: https://entropymine.com/imageworsener/grayscale/
            int grayPixel = (int) (255.0 * Math.pow(ylinear, 1.0 / 2.2));
            int gray = (alpha << 24) + (grayPixel << 16) + (grayPixel << 8) + grayPixel;
            image_out.setRGB(x, y, gray);
        }

    File myNewJPegFile = new File(System.getProperty("user.dir")+"\\src\\leerjpg\\"+image_name+"-gray.jpg");
    ImageIO.write(image_out, "jpg", myNewJPegFile);
    
  }
  
  private void marchThroughImage(BufferedImage image, String image_name) {
    
    int width_img = image.getWidth();
    int height_img = image.getHeight();
    System.out.println("width, height: " + width_img + ", " +height_img);

    for (int i = 0; i < height_img; i++) {
      for (int j = 0; j < width_img; j++) {
        System.out.println("x,y: " + j + ", " + i);
        int pixel = image.getRGB(j, i);
        printPixelARGB(pixel);
        System.out.println("");
      }
    }
    
  }
  
  public void graficarMatriz(double matriz[][]){
        int x = matriz.length;
        int y = matriz[0].length;
        
        System.out.println("x: "+x+" y: "+y);
  }
  
  public double[][] sumaMatrices(double A[][], double B[][], 
                       int sizeX, int sizeY) 
    { 
        int i, j; 
        double C[][] = new double[sizeX][sizeY]; 
  
        for (i = 0; i < sizeX; i++) 
            for (j = 0; j < sizeY; j++) 
                C[i][j] = A[i][j] + B[i][j]; 
  
        return C; 
    } 

  public ImagenProces() {
    //double[] dato = flater(input("mnist//0//0_2.png")); 
    //for(int i=0;i<dato.length;i++){
    //  System.out.print(dato[i]+" ");
    //}
  }

  public double[] elemento(String imagen){
    double[] dato = flater(input(imagen));
    return dato;
  }
  
  public List<double[][]> input(String imagen){
    try {
      // get the BufferedImage, using the ImageIO class
      String image_name = imagen;
      
      BufferedImage image = ImageIO.read(this.getClass().getResource(image_name));
      image_name = image_name.substring(0,image_name.length()-4);
    //  marchThroughImage(image,image_name);
    //  convertToGray(image, image_name);
     //   marchThroughImage(image, image_name);
     // applyKernel(image, image_name);
    //maxPooling(image, image_name);
    
    /*
     =========== Comenzando CNN ===============
    
    */
    
    //  PRIMERA CAPA CNN
    
    int primeraCapa = 3;
    List<double[][]> outputs_kernel = new ArrayList<>();
    
    double[][] kernel1={{0,0,0},
                        {0,1,0},
                        {0,0,0}};
    double[][] kernel2={{1,0,-1},
                        {0,0,0},
                        {-1,0,1}};
    double[][] kernel3={{0,-1,0},
                        {-1,4,-1},
                        {0,-1,0}};

    List<double[][]> kernel_capa1 = new ArrayList<>();
    kernel_capa1.add(kernel1);
    kernel_capa1.add(kernel2);
    kernel_capa1.add(kernel3);

    for(int i = 0; i < primeraCapa; i++){        
        
        double temp[][] = applyGeneralKernel(image, kernel_capa1.get(i));
        outputs_kernel.add(temp); 
        graficarMatriz(temp);
    }
    
    // MAX POOLING 
    List<double[][]> outputs_pooling = new ArrayList<>();
    for(double[][] actual : outputs_kernel){
        int poolDim = 5;
        outputs_pooling.add(generalMaxPooling(actual, poolDim ));
    }
    System.out.println("primer Pool");
    System.out.println("dimTotal: "+outputs_pooling.size());
    System.out.println("dimOne x: "+outputs_pooling.get(0).length+" y: "+outputs_pooling.get(0)[0].length);
    
    
    // PASO DE LAYER N-1 A LAYER N
    List<double[][]> outputs_primeraCapa = new ArrayList<>();
    int xDim = outputs_pooling.get(0).length;
    int yDim = outputs_pooling.get(0)[0].length;

    int segundaCapa = 5;

    double[][] kernel4={{-1,-1,-1},
                        {-1,8,-1},
                        {-1,-1,-1}};
    double[][] kernel5={{0,-1,0},
                        {-1,5,-1},
                        {0,-1,0}};
    double[][] kernel6={{1/9,1/9,1/9},
                        {1/9,1/9,1/9},
                        {1/9,1/9,1/9}};
    double[][] kernel7={{1/16,1/8,1/16},
                        {1/8,1/4,1/8},
                        {1/16,1/8,1/16}};
    double[][] kernel8={{-1,-1,2},
                        {-1,2,-1},
                        {2,-1,-1}};
    

    List<double[][]> kernel_capa2 = new ArrayList<>();
    kernel_capa2.add(kernel4);
    kernel_capa2.add(kernel5);
    kernel_capa2.add(kernel6);
    kernel_capa2.add(kernel7);
    kernel_capa2.add(kernel8);

    for(int i = 0; i < segundaCapa; i++){

        double temp[][] = new double[xDim-kernel_capa2.get(0).length+1][yDim-kernel_capa2.get(0)[0].length+1];
        
        for(double[][] actual: outputs_pooling){
            double algotemp[][] = kernelEntreLayers(actual,kernel_capa2.get(i));
            temp = sumaMatrices(temp,algotemp,algotemp.length,algotemp[0].length);
        }
        outputs_primeraCapa.add(temp);
        temp = null;  // empty again
    }
    System.out.println("Segundo layer");
    graficarMatriz(outputs_primeraCapa.get(0));
    
    // OTRO POOLING
    List<double[][]> outputs_pooling2 = new ArrayList<>();
    for(double[][] actual : outputs_primeraCapa){
        int poolDim = 3;
        outputs_pooling2.add(generalMaxPooling(actual, poolDim ));
    }
    System.out.println("Segundo Pool");
    System.out.println("dimTotal: "+outputs_pooling2.size());
    System.out.println("dimOne x: "+outputs_pooling2.get(0).length+" y: "+outputs_pooling2.get(0)[0].length);
    
    // PASO DE LAYER N-1 A LAYER N
    List<double[][]> outputs_segundaCapa = new ArrayList<>();
    int xDim2 = outputs_pooling2.get(0).length;
    int yDim2 = outputs_pooling2.get(0)[0].length;

    int terceraCapa = 7;

    double[][] kernel9={{2,-1,-1},
                        {-1,2,-1},
                        {-1,-1,2}};
    double[][] kernel10={{-1,-2,-1},
                        {0,0,0},
                        {1,2,1}};
    double[][] kernel11={{-1,2,-1},
                        {-1,2,-1},
                        {-1,2,-1}};
    double[][] kernel12={{1,0,1},
                        {0,1,0},
                        {1,0,1}};
    double[][] kernel13={{-1/4,0,1/4},
                        {0,0,0},
                        {1/4,0,-1/4}};
    double[][] kernel14={{-1,0,1},
                        {0,-1,0},
                        {1,0,-1}};
    double[][] kernel15={{0,1,0},
                        {0,1,0},
                        {0,1,0}};

    List<double[][]> kernel_capa3 = new ArrayList<>();
    kernel_capa3.add(kernel9);
    kernel_capa3.add(kernel10);
    kernel_capa3.add(kernel11);
    kernel_capa3.add(kernel12);
    kernel_capa3.add(kernel13);
    kernel_capa3.add(kernel14);
    kernel_capa3.add(kernel15);

    for(int i = 0; i < terceraCapa; i++){

        double temp[][] = new double[xDim2-kernel_capa3.get(0).length+1][yDim2-kernel_capa3.get(0)[0].length+1];
        
        for(double[][] actual: outputs_pooling2){
            double algotemp[][] = kernelEntreLayers(actual,kernel_capa3.get(i));
            temp = sumaMatrices(temp,algotemp,algotemp.length,algotemp[0].length);
        }
        outputs_segundaCapa.add(temp);
        temp = null;  // empty again
    }
    System.out.println("Tercer layer");
    graficarMatriz(outputs_segundaCapa.get(0));
    
    // OTRO POOLING
    List<double[][]> outputs_pooling3 = new ArrayList<>();
    for(double[][] actual : outputs_segundaCapa){
        int poolDim = 3;
        outputs_pooling3.add(generalMaxPooling(actual, poolDim ));
    }
    System.out.println("Tercer Pool");
    System.out.println("dimTotal: "+outputs_pooling3.size());
    System.out.println("dimOne x: "+outputs_pooling3.get(0).length+" y: "+outputs_pooling3.get(0)[0].length);

    return outputs_pooling3;
    } catch (IOException e) {
      System.err.println(e.getMessage());
      return null;
    }
  }

  public double[] flater(List<double[][]> imagenes){
    int tamanio = imagenes.size()*imagenes.get(0).length*imagenes.get(0)[0].length;
    double[] entrada = new double[tamanio];
    for(int i=0;i<imagenes.size();i++){
      int index = i*imagenes.get(0).length*imagenes.get(0)[0].length;
      for(int j=0;j<imagenes.get(0).length;j++){
        for(int k=0;k<imagenes.get(0)[0].length;k++ ){
          //System.out.println(index+j*(imagenes.get(0).length > imagenes.get(0)[0].length ? imagenes.get(0)[0].length:imagenes.get(0).length) +k);
          entrada[index+j*(imagenes.get(0).length > imagenes.get(0)[0].length ? imagenes.get(0)[0].length:imagenes.get(0).length) +k] = imagenes.get(i)[j][k];
        }
      }
    }
    return entrada;
  }
}