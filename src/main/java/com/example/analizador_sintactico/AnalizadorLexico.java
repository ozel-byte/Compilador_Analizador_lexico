package com.example.analizador_sintactico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class AnalizadorLexico {
    private boolean status;
    private ArrayList<String> palabrasReservadas = new ArrayList<>(Arrays.asList("Columna", "Fila", "Contenedor", "Lista", "Button"));
    private ArrayList<String> caracteresPuntuacion = new ArrayList<>(Arrays.asList("[", "]" ,"(" ,")" ,"\"", "“", "”", ","));
    private Pattern expresionLetra = Pattern.compile("^[a-zA-Z][a-zA-Z]*$");

    public ArrayList<ArrayList<String>> analizarEntrada(String entrada){
        status = true;
        ArrayList<ArrayList<String>> listaTokenValidos = new ArrayList<ArrayList<String>>();

        char[] listaCharCadenda = convertirCadena(entrada);
        System.out.println("Lista: " + listaCharCadenda.length);
        ArrayList<String> listaCorrectos = separarCadena(listaCharCadenda);
        System.out.println(listaCorrectos);

        for (String item : listaCorrectos){
            ArrayList<String> auxiliar = new ArrayList<>();
            auxiliar.add(item);
            if (palabrasReservadas.contains(item)){
                //System.out.println("Si: " + item );
                auxiliar.add("1");
                auxiliar.add("Palabras Reservadas");
            } else if (caracteresPuntuacion.contains(item)) {
                //System.out.println("Si: " + item );
                auxiliar.add("1");
                auxiliar.add("Caracteres de puntuación");
            } else if (expresionLetra.matcher(item).matches()){
                //System.out.println("Si: " + item );
                auxiliar.add("1");
                auxiliar.add("Letras");
            } else {
                //System.out.println("No: " + item );
                auxiliar.add("2");
                auxiliar.add("Sin token");
                status = false;
            }
            listaTokenValidos.add(auxiliar);
        }

        System.out.println("\n" + listaTokenValidos);
        System.out.println("Entrada: " + entrada);

        return listaTokenValidos;
    }

    private ArrayList<String> separarCadena(char[] listaCharCadenda){
        ArrayList<String> correctos = new ArrayList<>();
        String anterior = "";
        String aux = "";
        int posicionSiguiente = 1;
        int contador = 0;
        String puntuacion = "”,“[]()\"";
        for (char caracter : listaCharCadenda){
            String item = String.valueOf(caracter);
            //System.out.println("Item: " + item);

            if (!anterior.equals("")){
                if (!item.equals(" ")){
                    if ((puntuacion.contains(String.valueOf(listaCharCadenda[posicionSiguiente])) && expresionLetra.matcher(item).matches()) || (!expresionLetra.matcher(String.valueOf(listaCharCadenda[posicionSiguiente])).matches() && !puntuacion.contains(String.valueOf(listaCharCadenda[posicionSiguiente])))){
                        correctos.add(aux+item);
                        aux = "";
                    } else if ((puntuacion.contains(anterior) && puntuacion.contains(item)) || (!puntuacion.contains(anterior) && puntuacion.contains(item)) || (!expresionLetra.matcher(item).matches() && !puntuacion.contains(item))) {
                        correctos.add(item);
                        aux = "";
                    } else {
                        if (contador == listaCharCadenda.length-1){
                            correctos.add(aux+item);
                        } else {
                            aux = aux+item;
                        }
                    }
                }
            } else {
                System.out.println("No hay dato antes");
                if( (puntuacion.contains(item) || (expresionLetra.matcher(item).matches()) && contador == listaCharCadenda.length-1)){
                    correctos.add(item);
                } else if (expresionLetra.matcher(item).matches() && puntuacion.contains(String.valueOf(listaCharCadenda[posicionSiguiente]))){ // en caso de que haya un dato siguiente y no sea letra
                    correctos.add(item);
                } else if(!expresionLetra.matcher(item).matches() && !puntuacion.contains(item)){
                    correctos.add(item);
                } else {
                    aux = aux + item;
                }
            }
            if (posicionSiguiente < listaCharCadenda.length-1){
                posicionSiguiente++;
            }
            anterior = item;
            contador++;
        }
        return correctos;
    }

    private char[] convertirCadena(String cadena){
        return cadena.toCharArray();
    }

    public boolean getStatus() {
        return status;
    }
}
