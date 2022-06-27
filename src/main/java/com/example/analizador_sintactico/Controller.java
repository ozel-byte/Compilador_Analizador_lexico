package com.example.analizador_sintactico;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
    @FXML
    private ImageView idImagenView;
    @FXML
    private Label labelStatus;
    @FXML
    private Label labelStatus2;
    @FXML
    private TextField idEntrada;
    @FXML
    private Pane paneLista;
    @FXML
    private Button btnLimpiar;
    @FXML
    private ListView listView;


    @FXML
    private ListView<String> terminal;
    @FXML
    private Pane ventanaNombreArchivo;
    @FXML
    private ListView<HboxCell> listaArchivos;

    @FXML
    private TextField campoNombreFile;
    @FXML
    private Text msjNombreExistente;
    AnalizadorLexico analizador;

    @FXML
    private TextArea inputcode;

    @FXML
    private VBox columnaText;

    FileWriter archivoAbierto;

    String textoaux = "";

    boolean aux = false;
    double tamñanoTexto = 18;
    @FXML
    private Text nombreArchivoAbiertVista;

    String nameArchivoAbrir = "";

    ArrayList<File> listaFiles = new ArrayList();
    ObservableList<String> f = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws IOException {
        System.out.println("second");
        analizador = new AnalizadorLexico();
        leerArchivosExistentes();
        abrirArchivoTxt();
//        archivoAbierto = new FileWriter("src/main/java/com/example/analizador_sintactico/Archivos/main");
//        archivoAbierto.write("hola mundo");
//        archivoAbierto.close();
        guardarCode();
        inputcode.textProperty().addListener((observableValue, s, t1) -> {
            if(!t1.equals("")){
                textoaux = t1;
                validarEntrada(t1);
            }else if(t1.equals("\r")){
                System.out.println("entro en enter");
                columnaText.getChildren().add(new Text("1"));
            }
            else{
                System.out.println("entra aqui kkllklklk "+t1);
                labelStatus2.setText("");
                labelStatus.setText("");
                terminal.getItems().clear();
            }
        });
    }

    private void guardarCode(){
        inputcode.setOnKeyPressed(keyEvent -> {
           if( keyEvent.getCode() == KeyCode.CONTROL){
               System.out.println("preciono la letra s");
               try {
                   archivoAbierto.write(textoaux);
                   archivoAbierto.close();

               } catch (IOException e) {
                   throw new RuntimeException(e);
               }

            }else if(keyEvent.getCode() == KeyCode.ALT){
               System.out.println("preciono alt");
               tamñanoTexto+=4.0;
               inputcode.setFont(Font.font(tamñanoTexto));
           }else if(keyEvent.getCode() == KeyCode.ENTER){
               columnaText.getChildren().add(new Text("1"));
           }
        });
    }
    @FXML
    private void validarEntrada(String entrada){
        //String entrada = idEntrada.getText().replace(" ",""); // Se eliminan los espacios de la entrada
//        String entrada =idEntrada.getText();
        if (entrada.length() > 0){
            labelStatus.setText("");
//            paneLista.setVisible(true);
//            btnLimpiar.setVisible(true);
            ArrayList<ArrayList<String>> listaValidados = analizador.analizarEntrada(entrada);

            System.out.println("Tamaño: " +  listaValidados.size());

            for (ArrayList item : listaValidados){
                //System.out.println("Token: " + item.get(2) + " | Dato: " + item.get(0) + " - Correcto");
                String estado;
                if (item.get(1).equals("1")){
                    estado = "Correcto";
                } else {
                    estado = "Incorrecto";
                }
                terminal.getItems().add("Token: " + item.get(2) + " | Dato: " + item.get(0) + " - " + estado);

            }

            if (analizador.getStatus() && listaValidados.size() > 0){
                AnalizadorSintactico analizadorSintactico = new AnalizadorSintactico(listaValidados);
                analizadorSintactico.analizarEntrada();
                if (analizadorSintactico.getStatus()){
                    labelStatus.setText("Entrada correcta");
                    labelStatus2.setText("");
                    labelStatus.setStyle("-fx-text-fill: GREEN");
                } else {
                    String mjs = analizadorSintactico.getMensaje();
                    String mjs2 = analizadorSintactico.getMensaje2();
                    labelStatus.setText(mjs);
                    labelStatus.setStyle("-fx-text-fill: RED");
                    labelStatus2.setText(mjs2);
                    labelStatus2.setStyle("-fx-text-fill: ORANGE");
                }

            } else {
                labelStatus.setText("Entrada incorrecta: Error de lexico");
                labelStatus.setStyle("-fx-text-fill: RED");
                if (listaValidados.isEmpty()){
                    System.out.println("No hay");
                    terminal.getItems().add("Entrada incorrecta : " + entrada);
                }
            }
        } else {
            System.out.println("Entrada vacía");
            labelStatus.setText("Entrada Vacía");
            labelStatus2.setText("");
            labelStatus.setStyle("-fx-text-fill: RED");
        }
    }

    @FXML
    private void limpiar(){
        idEntrada.clear();
        paneLista.setVisible(false);
        btnLimpiar.setVisible(false);
        labelStatus.setText("");
        labelStatus2.setText("");
        listView.getItems().clear();
    }

    @FXML
    private void abrirVentanaParaCrearArchivo(){
        ventanaNombreArchivo.setVisible(true);
        System.out.println("entro aqui");
    }
    @FXML
    private void crearArchivoTxt() throws IOException {
        if (!campoNombreFile.getText().isEmpty()){
            String nombre = campoNombreFile.getText();
            String ruta = "src/main/java/com/example/analizador_sintactico/Archivos/"+nombre;
            File file = new File(ruta);

            if(!file.exists()){
                file.createNewFile();
                listaFiles.add(file);

                listaArchivos.getItems().add(new HboxCell(file.getName()));

                ventanaNombreArchivo.setVisible(false);
                campoNombreFile.setText("");
            }else {
                msjNombreExistente.setVisible(true);
            }


        }

    }



    private void leerArchivosExistentes() throws FileNotFoundException {
        listaArchivos.getItems().clear();
        String ruta = "src/main/java/com/example/analizador_sintactico/Archivos/";
        File file = new File(ruta);
        List<HboxCell>lis = new ArrayList<>();
        for(File f: file.listFiles()){
            lis.add(new HboxCell(f.getName()));
            listaFiles.add(f);
        }
        ObservableList<HboxCell> listaBbj = FXCollections.observableList(lis);
        listaArchivos.setItems(listaBbj);
    }

    private void abrirArchivoTxt(){
        String ruta = "src/main/java/com/example/analizador_sintactico/Archivos/";

        listaArchivos.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            System.out.println("entro selecion : "+textoaux);
            Text change = (Text) t1.getChildren().get(0);
            inputcode.setText("");
            String code = "";
            File f = new File(ruta+change.getText());
            textoaux = "";
            nameArchivoAbrir =change.getText();
            nombreArchivoAbiertVista.setText("Nombre archivo: "+change.getText());
            nombreArchivoAbiertVista.setStyle("-fx-fill: green");

            try {
                Scanner obj = new Scanner(f);
                System.out.println("Nombre del archivo que se va abrir y leer: "+f.getName());
                while(obj.hasNextLine()){
                    code = obj.nextLine();
                    System.out.println(code+" entro a leer");
                    inputcode.setText(code);
                }
                obj.close();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        });


    }

    private void funcion2(){

    }

    public static class HboxCell extends HBox{
        Text label = new Text();
        InputStream img = new FileInputStream("src/main/java/com/example/analizador_sintactico/linux.png");
        ImageView imgV = new ImageView(new Image(img));


        HboxCell(String labelText) throws FileNotFoundException {
            super();
            label.setText(labelText);
            System.out.println(imgV.getImage().getWidth());
            imgV.setFitHeight(17);
            imgV.setFitWidth(17);
            HBox.setHgrow(label, Priority.ALWAYS);

            this.setSpacing(10);
            this.getChildren().addAll(label,imgV);
        }
    }

    @FXML
    private void abrirEscrituraCampo(){
        System.out.println("entro aqui en clcieke");
        String ruta = "src/main/java/com/example/analizador_sintactico/Archivos/";
        try {
            archivoAbierto = new FileWriter(ruta+nameArchivoAbrir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}