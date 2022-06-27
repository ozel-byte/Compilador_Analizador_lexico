package com.example.analizador_sintactico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Pattern;

public class AnalizadorSintactico {
    private boolean status = true;
    private String mensaje = "";
    private String mensaje2 = "Todo correcto";
    private ArrayList<ArrayList<String>> entrada = new ArrayList<ArrayList<String>>();
    private ArrayList<String> terminales = new ArrayList<>(Arrays.asList("Columna", "Fila", "Contenedor", "Lista", "[", "]", "a..z", "A..Z", "\"", "Button", "(", ")", ",", "$"));
    private ArrayList<String> noTerminales = new ArrayList<>(Arrays.asList("S", "Widget", "CI", "CC", "Cuerpo", "LE", "Texto", "RT", "Boton", "PI", "PC", "RC", "RC2"));
    private Pattern letras = Pattern.compile("^[a-zA-Z][a-zA-Z]*");
    private Pattern letrasMa = Pattern.compile("^[A-Z][A-Z]*");

    public AnalizadorSintactico(ArrayList<ArrayList<String>> entrada){
        this.entrada = entrada;
    }

    public void analizarEntrada(){
        System.out.println("\nSINTACTO\n");
        Stack<String> pila = new Stack<>();
        String [][] tabla = tablaPredictiva();
        pila.push("$");
        pila.push("S");
        String x = "";
        int apuntador = 0;
        int ciAux = 0;
        int ccAux = 0;
        String a = entrada.get(apuntador).get(0);
        System.out.println("\n" + a);

        mostrar_pila(pila);

        do {
            x = pila.peek();

            if (apuntador == entrada.size()){
                a = "$";
            }

            System.out.println("Valor x: " + x);
            System.out.println("Valor a: " + a);
            if (terminales.contains(x)){
                if(x.equals(a) || letras.matcher(a).find()){
                    pila.pop();
                    apuntador++;

                    if (apuntador < entrada.size()){
                        System.out.println("Valor a if: " + entrada.get(apuntador).get(0));
                        a = entrada.get(apuntador).get(0);
                    } else if (!pila.isEmpty() && apuntador == entrada.size()+1){ //(ccAux != ciAux)
                        System.out.println("Error: cadena incompleta");
                        mensaje = "Entrada incorrecta: Entrada incompleta";
                        mensaje2 = "Hace falta un ]";
                        status = false;
                        System.out.println("apuntador: " + apuntador + " | Entrada: " + entrada.size());
                    }
                } else {
                    mensaje = "Error";
                    System.out.println("Error");
                }
            } else {
                int posicion = 0;

                if (terminales.contains(a)){
                    System.out.println("Contiene");
                    posicion = terminales.indexOf(a);
                } else if (letras.matcher(a).find()){
                    if (letrasMa.matcher(a.substring(0,1)).find()){
                        posicion = terminales.indexOf("A..Z");
                        System.out.println("Mayus");
                    } else {
                        posicion = terminales.indexOf("a..z");
                        System.out.println("Minus");
                    }
                }

                if (tabla[noTerminales.indexOf(x)][posicion] != null){
                    System.out.println("Inter: " + tabla[noTerminales.indexOf(x)][posicion]);
                    pila.pop();
                    String [] produccion = tabla[noTerminales.indexOf(x)][posicion].split(" ");
                    for (int i = produccion.length-1; i>=0; i--){
                        if (produccion[i].equals("]")){
                            pila.pop();
                        }
                        pila.push(produccion[i]);
                    }
                } else {
                    mensaje = "Entrada incorrecta: Error Sintactico";
                    mensajeError(a, x);
                    System.out.println("Mensaje2: " + mensaje2);
                    System.out.println("Error Sintactico: " + a);
                    System.out.println("Error Sintactico: " + tabla[noTerminales.indexOf(x)][posicion] + " Posicionfila: " + noTerminales.indexOf(x));
                    status = false;
                }
            }
            if(pila.isEmpty()){
                mensaje2 = "Good :)";
            }
            mostrar_pila(pila);
            System.out.println("\n");
        }while (!x.equals("$") && status);

    }

    private void mensajeError(String et, String regla){
        // Fila[Fila[Fila[Contenedor["KevinUno"]]],Button("hola kevin"),Columna[KevinDos"]]
        if (regla.equals("RC2") || regla.equals("Cuerpo")){
            if (et.equals("[")){
                mensaje2 = "Se esperaba Columna, Fila, Contenedor o Lista antes de un [";
            } else {
                if (et.equals("]")){
                    mensaje2 = "Se esperaba un Texto, Button o un widget antes de un ]";
                }
            }
            if (letras.matcher(et).matches()){
                mensaje2 = "Se espearaba una \" antes de " + et;
            }
            if (et.equals("(")){
                mensaje2 = "Se esperaba la palabra Button entes de los parentesis";
            }
        }

        if (regla.equals("RT")){
            mensaje2 = "Se esperaba una \" antes de " + et;
        }

        if (regla.equals("CI")){
            mensaje2 = "Se esperaba un [ y se recibio un " + et;
        }

        if (regla.equals("PI")){
            mensaje2 = "Se esperaba un ( depues de un Button";
        }

        if (regla.equals("PC")){
            mensaje2 = "Se esperaba un ) despues del tetxo en un Button";
        }

        if (regla.equals("RC")){
            mensaje2 = "Se esperaba una , y se recibio " + et;
        }
        if (regla.equals("S")){
            mensaje2 = "Se esperaba una Columna, Fila, Contenedor o lista antes de " + et;
        }

        if (regla.equals("Texto")){
            mensaje2 = "Se esperaba Texto para el Button";
        }

        if (regla.equals("LE")){
            mensaje2 = "Se esperaba un Texto dentro de los parenetsis";
        }

    }

    private void mostrar_pila(Stack<String> pila){

        System.out.println("\n---------------------PILA INICIO-----------------------");

        for (int i = pila.size(); i>0; i--){
            System.out.println(pila.get(i-1));
        }
        System.out.println("---------------------PILA FIN------------------------\n");
    }

    public boolean getStatus() {
        return status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getMensaje2() {
        return mensaje2;
    }

    private String[][] tablaPredictiva() {
        String[][] tabla = new String[13][14];
        tabla[0][0] = "Widget CI Cuerpo CC";
        tabla[0][1] = "Widget CI Cuerpo CC";
        tabla[0][2] = "Widget CI Cuerpo CC";
        tabla[0][3] = "Widget CI Cuerpo CC";
        tabla[1][0] = "Columna";
        tabla[1][1] = "Fila";
        tabla[1][2] = "Contenedor";
        tabla[1][3] = "Lista";
        tabla[2][4] = "[";
        tabla[3][5] = "]";
        tabla[4][0] = "S RC";
        tabla[4][1] = "S RC";
        tabla[4][2] = "S RC";
        tabla[4][3] = "S RC";
        tabla[4][8] = "Texto RC";
        tabla[4][9] = "Boton RC";
        tabla[5][6] = "a..z";
        tabla[5][7] = "A..Z";
        tabla[6][8] = "\" LE RT";
        tabla[7][6] = "LE RT";
        tabla[7][7] = "LE RT";
        tabla[7][8] = "\"";
        tabla[8][9] = "Button PI Texto PC";
        tabla[9][10] = "(";
        tabla[10][11] = ")";
        tabla[11][5] = "]";
        tabla[11][12] = ", RC2";
        tabla[11][13] = "$";
        tabla[12][0] = "Cuerpo";
        tabla[12][1] = "Cuerpo";
        tabla[12][2] = "Cuerpo";
        tabla[12][3] = "Cuerpo";
        tabla[12][8] = "Cuerpo";
        tabla[12][9] = "Cuerpo";

        return tabla;
    }
}
