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

import br.pucminas.dcc.jpacs.*;
import br.pucminas.dcc.jpacs.core.Processor;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * JPanel para conter os dados do Processador para exibição na interface
 */
class ProcessorPanel extends JPanel {

    protected JRadioButton F1,  F2,  F3;
    protected JTable Registradores;

    public ProcessorPanel(Processor Processador) {
        super();
        JPanel PainelSuperior;
        PainelSuperior = new JPanel(new GridLayout(2, 4));

        F1 = new JRadioButton("");
        F2 = new JRadioButton("");
        F3 = new JRadioButton("");

        JLabel L1, L2, L3;
        L1 = new JLabel(Internacionalizacao.get("Processor.Flag.Zero"));
        L3 = new JLabel(Internacionalizacao.get("Processor.Flag.Signal"));

        JLabel PC;
        JLabel PCLabel = new JLabel("  " + Internacionalizacao.get("Processor.PC"));
        if (Processador.PegaPC() >= 0) {
            PC = new JLabel("  " + Processador.PegaPC() + " ");
        } else {
            PC = new JLabel("  X");
        }
        JLabel PilhaLabel = new JLabel("  " + Internacionalizacao.get("Processor.Stack"));
        JLabel Pilha = new JLabel("  " + Processador.Pilha());

        final boolean[] Flags = Processador.PegaFlags();
        if (Flags[0] == true) {
            F1.setSelected(true);
        }
        if (Flags[1] == true) {
            F2.setSelected(true);
        }
        if (Flags[2] == true) {
            F3.setSelected(true);
        }
        F1.addActionListener(new ProtectedRadioAdapter(F1, Flags[0]));
        F2.addActionListener(new ProtectedRadioAdapter(F2, Flags[1]));
        F3.addActionListener(new ProtectedRadioAdapter(F3, Flags[2]));

        PainelSuperior.add(PCLabel);
        PainelSuperior.add(L1);
        PainelSuperior.add(L3);
        PainelSuperior.add(PilhaLabel);
        PainelSuperior.add(PC);
        PainelSuperior.add(F1);
        PainelSuperior.add(F3);
        PainelSuperior.add(Pilha);

        Object[][] Tabela;
        String[] Titulo = {Internacionalizacao.get("Processor.Register"), Internacionalizacao.get("Processor.Register.Item")};
        JScrollPane Scroll;
        int i;
        char[] Temp = new char[2];
        Temp[1] = ' ';
        int Regs[] = Processador.PegaRegs();
        Tabela = new Object[Regs.length - 1][2];

        for (i = 1; i < Regs.length; i++) {
            Tabela[i - 1][1] = new Integer(Regs[i]);
            Temp[0] = (char) (i + 64);
            Tabela[i - 1][0] = new String(Temp);
        }

        ModeloTabela Modelo = new ModeloTabela(Tabela, Titulo);
        Registradores = new JTable(Modelo);

        TableColumn column = null;
        for (i = 0; i < Tabela[0].length; i++) {
            column = Registradores.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else {
                column.setPreferredWidth(120);
            }
        }

        Scroll = new JScrollPane(Registradores);

        this.setLayout(new BorderLayout());

        this.add(Scroll, BorderLayout.CENTER);
        this.add(PainelSuperior, BorderLayout.NORTH);
    }

    protected class ProtectedRadioAdapter implements ActionListener {

        protected JRadioButton TheButton;
        protected boolean Value;

        public ProtectedRadioAdapter(JRadioButton aButton, boolean Value) {
            TheButton = aButton;
            this.Value = Value;
        }

        public void actionPerformed(ActionEvent e) {
            TheButton.setSelected(Value);
        }
    };
};

