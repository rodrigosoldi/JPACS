//This file is part of JPACS.
//
//    JPACS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    JPACS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with JPACS.  If not, see <http://www.gnu.org/licenses/>.

package br.pucminas.dcc.jpacs;

import java.io.*;
import java.util.*;

public class Internacionalizacao {

    protected static Locale Local;
    protected static ResourceBundle Recursos;
    protected static Internacionalizacao Instancia = null;

    protected Internacionalizacao(String Lingua, String Pais) {
        try {

            Local = new Locale(Lingua, Pais);
            Recursos = ResourceBundle.getBundle("br/pucminas/dcc/jpacs/resources/Language", Local, this.getClass().getClassLoader());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static synchronized void create(String Lingua, String Pais) {
        Instancia = new Internacionalizacao(Lingua, Pais);
    }

    public static String get(String Chave) {
        return Recursos.getString(Chave);
    }

    public static void readFromFile(String f) {

        char Buffer[] = new char[10];
        String Idioma, Pais, Aux;

        try {
            FileReader In = new FileReader(f);
            In.read(Buffer, 0, 6);
            In.close();

            Aux = new String(Buffer);
            Idioma = Aux.substring(0, 2).trim();
            Pais = Aux.substring(3).trim();

            create(Idioma, Pais);

        } catch (Exception e) {
        }
    }

    public static void writeToFile(String NomeArquivo) {
        File Arq = new File(NomeArquivo);

        String Saida;

        Saida = Local.getLanguage() + " " + Local.getCountry();
        try {
            FileWriter Out = new FileWriter(Arq);
            Out.write(Saida);
            Out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
};