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
import br.pucminas.dcc.jpacs.core.ItemRelatorio;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * Modelo de tabela padrão, onde as células não são editáveis
 */
class ModeloTabela extends DefaultTableModel {
    public ModeloTabela(Object[][] data, Object[] columnNames) {
        super(data,columnNames);
    }
    
    public ModeloTabela(Vector data, Vector columnNames) {
        super(data,columnNames);
    }
    
    @Override
    public boolean isCellEditable(int row, int column){
        //redefinição do metodo para que a tabela não possa ser editada
        return false;
    }
    
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
};

/**
 * Modelo de tabela para relatório
 */
class ModeloTabelaRelatorio extends DefaultTableModel {
    public ModeloTabelaRelatorio(Vector Acessos) {
        super();
        
        Object[][] Dados=new Object[Acessos.size()][7];
        int i, j;
        for(i=0; i<Acessos.size(); i++)
            Dados[i]=((ItemRelatorio)Acessos.get(i)).toArray();
        
        String Titulo[]={Internacionalizacao.get("Report.Number"),
        Internacionalizacao.get("Report.Position"),
        Internacionalizacao.get("Report.Nature"),
        Internacionalizacao.get("Report.Block"),
        Internacionalizacao.get("Report.Time"),
        Internacionalizacao.get("Report.Result"),
        Internacionalizacao.get("Report.Substitution")};
        
        super.setDataVector(Dados,Titulo);
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
};

/**
 * Modelo de tabela para a memória cache
 */
class ModeloTabelaCache extends DefaultTableModel {
    public ModeloTabelaCache(Object[][] data, Object[] columnNames) {
        super(data,columnNames);
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
};
