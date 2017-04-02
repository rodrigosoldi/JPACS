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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Dialog para escolha do idioma do JPACS
 */
class InternationalDialog extends JDialog {

    JComboBox ComboLing;
    Vector Idioma = new Vector();
    Vector Abrev = new Vector();
    Vector Local = new Vector();
    boolean Ok;

    public InternationalDialog(JFrame Owner) {
        super(Owner, true);
        Ok = false;

        this.setTitle(Internacionalizacao.get("Menu.Config.International"));

        JPanel ButtonPanel = new JPanel(new FlowLayout());
        JPanel ComboPanel = new JPanel(new GridLayout(1, 2));
        JButton Aplicar = new JButton(Internacionalizacao.get("Config.Button.Apply"));
        JButton Cancel = new JButton(Internacionalizacao.get("Config.Button.Cancel"));

        Aplicar.setMnemonic(Internacionalizacao.get("Config.Button.Apply.Mnemonic").charAt(0));
        Cancel.setMnemonic(Internacionalizacao.get("Config.Button.Cancel.Mnemonic").charAt(0));

        Aplicar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Ok = true;
                dispose();
            }
        });

        Cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Ok = false;
                dispose();
            }
        });

        LerComboDoArquivo();

        JLabel ComboLabel = new JLabel(" " + Internacionalizacao.get("Config.Inter") + " ");
        ComboLing = new JComboBox(Idioma);

        ButtonPanel.add(Aplicar);
        ButtonPanel.add(Cancel);

        ComboPanel.add(ComboLabel);
        ComboPanel.add(ComboLing);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(ButtonPanel, BorderLayout.SOUTH);
        this.getContentPane().add(ComboPanel);
        this.setLocation(100, 100);
        this.pack();
    }

    public boolean ok() {
        return Ok;
    }

    public String idioma() {
        int Index = ComboLing.getSelectedIndex();
        return (String) Abrev.get(Index);
    }

    public String local() {
        int Index = ComboLing.getSelectedIndex();
        return (String) Local.get(Index);
    }

    public void LerComboDoArquivo() {
        char c;
        int i, j;
        String S = "";

        try {
            Reader In = new InputStreamReader(this.getClass().getResourceAsStream("/br/pucminas/dcc/jpacs/resources/languages.cfg"));

            i = In.read();
            while (i != -1) {
                for (j = 0; j < 3; j++) {
                    S = "";
                    while (i != '-' && i != '\n' && i != -1) {
                        c = (char) i;
                        S = S + c;
                        i = In.read();
                    }
                    i = In.read();

                    S = S.trim();
                    if (S.length() > 0) {
                        switch (j) {
                            case 0:
                                Idioma.add(S);
                                break;
                            case 1:
                                Abrev.add(S);
                                break;
                            case 2:
                                Local.add(S);
                                break;
                        }
                    }
                }
            }
            In.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
};