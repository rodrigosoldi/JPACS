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

import br.pucminas.dcc.jpacs.gui.MainFrame;

/**
 * Classe Principal do JPACS, contém o método main
 */
public class JPACS {
    
    public static void main(String args[]) {
        if(args.length>=2)
            Internacionalizacao.create(args[0],args[1]);
        else {
            Internacionalizacao.readFromFile("JPACS.lang");
        }
        
        MainFrame JanelaPrincipal = new MainFrame();
    }
}