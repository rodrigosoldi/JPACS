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

package br.pucminas.dcc.jpacs.gui;

import br.pucminas.dcc.jpacs.Internacionalizacao;
import br.pucminas.dcc.jpacs.core.CacheSlot;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Classe responsável por desenhar a cache em um JPanel
 */
class CachePanel extends JPanel {
    
    /**
     * Construtor padão, cria um JPanel que representa a memória cache
     * @param Cache Os slos da memória cache a serem exibidos
     */
    public CachePanel(CacheSlot Cache[]) {
        super(new BorderLayout());
        int i,j;
        Object Tabela[][]=new Object[Cache.length][3];//cria tabela
        String Titulo[]={Internacionalizacao.get("Cache.Alt"),Internacionalizacao.get("Cache.Block.Number"),Internacionalizacao.get("Cache.Block.Items")};
        String Bloco="";
        
        if(Cache[0]!=null) {
            for(i=0; i<Cache.length; i++) {
                Tabela[i][0]=new Boolean(Cache[i].Sujo);
                Tabela[i][1]=new String(""+Cache[i].NumBloco);
                Bloco="";
                for(j=0; j<Cache[i].Bloco.length; j++)
                    Bloco+=Cache[i].Bloco[j]+" ";
                
                Tabela[i][2]=Bloco;
            }
        } else {
            for(i=0; i<Cache.length; i++) {
                Tabela[i][0]=new Boolean(false);
                Tabela[i][1]=" ";
                Tabela[i][2]=" ";
            }
        }
        
        ModeloTabelaCache Modelo=new ModeloTabelaCache(Tabela,Titulo);
        JTable Table1=new JTable(Modelo);
        Table1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        TableColumn column = null;
        for (i=0; i<Tabela[0].length; i++) {
            column=Table1.getColumnModel().getColumn(i);
            if (i==0)
                column.setPreferredWidth(15);
            else if(i==1)
                column.setPreferredWidth(40);
            else
                column.setPreferredWidth(200);
        }
        
        JScrollPane Scroll = new JScrollPane(Table1);
        this.add(Scroll,BorderLayout.CENTER);
    }
    
};