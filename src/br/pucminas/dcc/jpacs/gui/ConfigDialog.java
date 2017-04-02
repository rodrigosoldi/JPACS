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
import javax.swing.border.*;
import java.io.*;
import java.util.Vector;
import java.math.BigInteger;

/**
 * JDialog com a interface gráfica da tela de configurações do JPACS
 */
public class ConfigDialog extends JDialog {

    JTextField T2, T3, T4;
    JComboBox ComboMemTam, ComboBlocTam;
    JRadioButton Lru, Fifo, Sequencial, Paralelo, CB, WT;
    JButton Aplicar, Cancelar, Salvar, Abrir;
    protected static JFileChooser FileChooser;
    public static final String ConfigFile = "JPACS.xml";

    /**
     * Construtor padrão
     * @param f JFrame 'dono' do Dialog
     */
    public ConfigDialog(JFrame f) {
        super(f, Internacionalizacao.get("Config.Title"), true);//construtor da superclasse
        JPanel Panel1 = new JPanel(new BorderLayout());
        JPanel Panel2 = new JPanel();

        JPanel Memoria = new JPanel(new GridLayout(4, 1));

        JPanel Cache = new JPanel(new GridLayout(4, 1));

        JPanel PrincTam = new JPanel(new GridLayout(1, 2));
        JPanel PrincTemp = new JPanel(new GridLayout(1, 2));
        JPanel CacheTam = new JPanel(new GridLayout(1, 2));
        JPanel CacheTemp = new JPanel(new GridLayout(1, 2));
        JPanel CacheBloc = new JPanel(new GridLayout(1, 2));
        JPanel Subs = new JPanel(new GridLayout(1, 2));
        JPanel Acesso = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JPanel Sincronia = new JPanel(new FlowLayout(FlowLayout.LEADING));

        JLabel L1, L2, L3;

        JLabel SubLabel, SinLabel, AceLabel;

        setBounds(50, 50, 300, 400);//posicao e tamanho do frame

        JPACSSaxParser Config = new JPACSSaxParser();
        try {
            Config.parse(ConfigFile);
        } catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("Error.Parser") + ": " + e, Internacionalizacao.get("Error.Title"), JOptionPane.ERROR_MESSAGE);
        }

        File Diretorio = new File("./config/");
        if (FileChooser == null) {
            FileChooser = new JFileChooser(Diretorio);
        }
        FileChooser.addChoosableFileFilter(new XmlFilter());

        Memoria.setBorder(new TitledBorder(Internacionalizacao.get("Config.Memory")));
        L1 = new JLabel(Internacionalizacao.get("Config.Memory.Size"));

        Vector MemTamData = new Vector();
        Vector BlockTamData = new Vector();
        BigInteger Aux;
        for (int i = 0; i < 20; i++) {
            Aux = new BigInteger("2");
            Aux = Aux.pow(i);
            BlockTamData.add(Aux);
            if (i >= 7) {
                MemTamData.add(Aux);
            }
        }
        ComboMemTam = new JComboBox(MemTamData);
        ComboMemTam.setRenderer(new ComboBoxRenderer());

        ComboMemTam.setSelectedItem(new BigInteger(Config.RamTamanho));
        PrincTam.add(L1);
        PrincTam.add(ComboMemTam);
        L2 = new JLabel(Internacionalizacao.get("Config.Memory.Time"));
        T2 = new JTextField(10);
        T2.setText(Config.RamTempoAcesso);
        T2.setHorizontalAlignment(JTextField.RIGHT);
        PrincTemp.add(L2);
        PrincTemp.add(T2);

        Cache.setBorder(new TitledBorder(Internacionalizacao.get("Config.Cache")));
        L1 = new JLabel(Internacionalizacao.get("Config.Cache.Blocks"));
        T3 = new JTextField(10);
        T3.setText(Config.CacheTamanho);
        T3.setHorizontalAlignment(JTextField.RIGHT);
        CacheTam.add(L1);
        CacheTam.add(T3);
        L2 = new JLabel(Internacionalizacao.get("Config.Cache.Time"));
        T4 = new JTextField(10);
        T4.setText(Config.CacheTempoAcesso);
        T4.setHorizontalAlignment(JTextField.RIGHT);
        CacheTemp.add(L2);
        CacheTemp.add(T4);
        L3 = new JLabel(Internacionalizacao.get("Config.Cache.Block.Size"));
        ComboBlocTam = new JComboBox(BlockTamData);
        ComboBlocTam.setSelectedItem(new BigInteger(Config.CacheTamanhoBloco));
        ComboBlocTam.setRenderer(new ComboBoxRenderer());
        CacheBloc.add(L3);
        CacheBloc.add(ComboBlocTam);
        ButtonGroup Grupo1 = new ButtonGroup();//Necessário para q não se possa ativar dois RadioButton ao mesmo tempo
        SubLabel = new JLabel(Internacionalizacao.get("Config.Cache.Subs"));
        Lru = new JRadioButton(Internacionalizacao.get("Config.Cache.Lru"));
        Fifo = new JRadioButton(Internacionalizacao.get("Config.Cache.Fifo"));
        Grupo1.add(Lru);
        Grupo1.add(Fifo);

        JPanel RadioSub = new JPanel(new FlowLayout());
        RadioSub.add(Lru);
        RadioSub.add(Fifo);

        Subs.add(SubLabel);
        Subs.add(RadioSub);

        if (Config.CacheLru) {
            Lru.setSelected(true);
        } else {
            Fifo.setSelected(true);
        }
        ButtonGroup Grupo2 = new ButtonGroup();//Necessário para q não se possa ativar dois RadioButton ao mesmo tempo
        AceLabel = new JLabel(Internacionalizacao.get("Config.Memory.Access"));
        Sequencial = new JRadioButton(Internacionalizacao.get("Config.Memory.Seq"));
        Paralelo = new JRadioButton(Internacionalizacao.get("Config.Memory.Par"));
        Grupo2.add(Sequencial);
        Grupo2.add(Paralelo);

        Acesso.add(AceLabel);
        Acesso.add(Sequencial);
        Acesso.add(Paralelo);

        if (Config.CacheSequencial) {
            Sequencial.setSelected(true);
        } else {
            Paralelo.setSelected(true);
        }
        ButtonGroup Grupo3 = new ButtonGroup();
        CB = new JRadioButton(Internacionalizacao.get("Config.Memory.CB"));
        WT = new JRadioButton(Internacionalizacao.get("Config.Memory.WT"));
        SinLabel = new JLabel(Internacionalizacao.get("Config.Memory.Synchrony"));
        Grupo3.add(CB);
        Grupo3.add(WT);

        Sincronia.add(SinLabel);
        Sincronia.add(CB);
        Sincronia.add(WT);

        if (Config.CacheCopyBack) {
            CB.setSelected(true);
        } else {
            WT.setSelected(true);
        }
        Aplicar = new JButton(Internacionalizacao.get("Config.Button.Apply"));
        Cancelar = new JButton(Internacionalizacao.get("Config.Button.Cancel"));
        Salvar = new JButton(Internacionalizacao.get("Config.Button.Save"));
        Abrir = new JButton(Internacionalizacao.get("Config.Button.Open"));

        Aplicar.setMnemonic(Internacionalizacao.get("Config.Button.Apply.Mnemonic").charAt(0));
        Cancelar.setMnemonic(Internacionalizacao.get("Config.Button.Cancel.Mnemonic").charAt(0));
        Salvar.setMnemonic(Internacionalizacao.get("Config.Button.Save.Mnemonic").charAt(0));
        Abrir.setMnemonic(Internacionalizacao.get("Config.Button.Open.Mnemonic").charAt(0));

        Aplicar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (Condicoes()) {
                    GravaConfig(ConfigFile);
                    Fechar();
                }
            }
        });

        Cancelar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Fechar();
            }
        });

        Salvar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Salvar();
            }
        });

        Abrir.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Abrir();
            }
        });

        Memoria.add(PrincTam);
        Memoria.add(PrincTemp);
        Memoria.add(Acesso);
        Memoria.add(Sincronia);

        Cache.add(CacheTam);
        Cache.add(CacheBloc);
        Cache.add(CacheTemp);
        Cache.add(Subs);

        Panel1.add(Memoria, BorderLayout.CENTER);
        Panel1.add(Cache, BorderLayout.EAST);

        Panel2.add(Cancelar);
        Panel2.add(Abrir);
        Panel2.add(Salvar);
        Panel2.add(Aplicar);
        this.getRootPane().setDefaultButton(Aplicar);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(Panel1, BorderLayout.NORTH);
        this.getContentPane().add(Panel2, BorderLayout.SOUTH);

        pack();
    }

    /**
     * Grava as configurações atuais da janela de configuração em um arquivo XML 
     * @param FileName O nome do arquivo a ser gravado
     */
    public void GravaConfig(String FileName) {
        JPACSSaxParser Config = new JPACSSaxParser();

        Config.RamTamanho = ComboMemTam.getSelectedItem().toString();
        Config.RamTempoAcesso = T2.getText().trim();
        Config.CacheTamanho = T3.getText().trim();
        Config.CacheTempoAcesso = T4.getText().trim();
        Config.CacheTamanhoBloco = ComboBlocTam.getSelectedItem().toString();
        if (Lru.isSelected()) {
            Config.CacheLru = true;
        } else {
            Config.CacheLru = false;
        }
        if (Sequencial.isSelected()) {
            Config.CacheSequencial = true;
        } else {
            Config.CacheSequencial = false;
        }
        if (CB.isSelected()) {
            Config.CacheCopyBack = true;
        } else {
            Config.CacheCopyBack = false;
        }
        Config.salvarParaArquivo(FileName);
    }

    /**
     * Abre um JFileChooser para o usuário selecionar o nome do arquivo e diretório
     * para salvar o arquivo de configurações.
     */
    public void Salvar() {
        int Op;
        String Arquivo;

        FileChooser.setDialogTitle(Internacionalizacao.get("Config.Save"));//titulo do Dialog
        Op = FileChooser.showSaveDialog(this);//executa o dialog
        if (Op == JFileChooser.APPROVE_OPTION)//clicou em ok
        {
            Arquivo = FileChooser.getSelectedFile().toString();//pega arquivo escolhido
            if (FileChooser.getSelectedFile().getName().lastIndexOf(".") < 0) //sem extensao
            {
                Arquivo += ".xml"; //adiciona extensao
            }
            GravaConfig(Arquivo);
        }
    }

    /**
     * Abre um JFileChooser para o usuário selecionar o nome do arquivo e diretório
     * para abrir um arquivo de configurações.
     */
    public void Abrir() {
        int Op;
        String Arquivo;

        FileChooser.setDialogTitle(Internacionalizacao.get("Config.Open"));//titulo do Dialog
        Op = FileChooser.showOpenDialog(this);//executa o dialog
        if (Op == JFileChooser.APPROVE_OPTION)//clicou em ok
        {
            Arquivo = FileChooser.getSelectedFile().toString();//pega arquivo escolhido

            JPACSSaxParser Config = new JPACSSaxParser();
            try {
                Config.parse(Arquivo);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("Error.Parser") + ": " + e, Internacionalizacao.get("Error.Title"), JOptionPane.ERROR_MESSAGE);
            }

            ComboMemTam.setSelectedItem(new BigInteger(Config.RamTamanho));
            T2.setText(Config.RamTempoAcesso);

            T3.setText(Config.CacheTamanho);
            T4.setText(Config.CacheTempoAcesso);
            ComboBlocTam.setSelectedItem(new BigInteger(Config.CacheTamanhoBloco));

            if (Config.CacheLru) {
                Lru.setSelected(true);
            } else {
                Fifo.setSelected(true);
            }
            if (Config.CacheSequencial) {
                Sequencial.setSelected(true);
            } else {
                Paralelo.setSelected(true);
            }
            if (Config.CacheCopyBack) {
                CB.setSelected(true);
            } else {
                WT.setSelected(true);
            }
        }
    }

    /**
     * Verifica se existe inconsistências nas configurações selecionadas pelo usuário
     * @return true se não houve problemas, false caso contrário
     */
    public boolean Condicoes() {
        String ErroMsg = null;
        boolean Ret = true;

        int RamTamanho = Integer.parseInt(ComboMemTam.getSelectedItem().toString());
        int RamTempoAcesso = Integer.parseInt(T2.getText().trim());
        int CacheTamanho = Integer.parseInt(T3.getText().trim());
        int CacheTempoAcesso = Integer.parseInt(T4.getText().trim());
        int CacheTamanhoBloco = Integer.parseInt(ComboBlocTam.getSelectedItem().toString());

        if (RamTempoAcesso < CacheTempoAcesso) {
            ErroMsg = Internacionalizacao.get("Error.Cache.Time");
        } else if (RamTamanho < 0 || RamTempoAcesso < 0 || CacheTamanho < 0 || CacheTempoAcesso < 0 || CacheTamanhoBloco < 0) {
            ErroMsg = Internacionalizacao.get("Error.Negative.Number");
        } else if (RamTamanho / CacheTamanho < CacheTamanhoBloco) {
            ErroMsg = Internacionalizacao.get("Error.Block.Size") + " " + RamTamanho / CacheTamanho;
        }


        if (ErroMsg != null) {
            Ret = false;
            JOptionPane.showMessageDialog(this, ErroMsg, Internacionalizacao.get("Error.Title"), JOptionPane.ERROR_MESSAGE);
        }
        return Ret;
    }

    /**
     * Fecha a tela de configurações
     */
    public void Fechar() {
        dispose();
    }
};

/**
 * Filtro de JFileChooser para arquivos XML (Configurações do JPACS)
 */
class XmlFilter extends javax.swing.filechooser.FileFilter { 

    /**
     * Indica se o filtro exibe um arquivo ou não
     * 
     * @param f Um arquivo encontrado no diretório
     * @return true se é para exibir o arquivo, false caso contrário
     */
    public boolean accept(File f) {
        String Extensao;
        int Pos;

        if (f.isDirectory()) {
            return true; //mostrar se for um diretorio
        //não é diretorio, verificar a extensão
        }
        Pos = f.toString().lastIndexOf(".");
        if (Pos >= 0) //possue extensão
        {
            Extensao = f.toString().substring(Pos);
            if (Extensao.compareToIgnoreCase(".XML") == 0) {
                return true; //extensão eh xml, aceitar
            }
        }

        //não possue extensão
        return false;
    }

    /**
     * Retorna a descrição do filtro de arquivos
     * @return A descrição do filtro de arquivos
     */
    public String getDescription() {
        return Internacionalizacao.get("File.Filter.Xml.Description");
    }
};

/**
 * Classe Redenderer customizada para ComboBox
 */
class ComboBoxRenderer extends JLabel
        implements ListCellRenderer {

    public ComboBoxRenderer() {
        setOpaque(true);
        setHorizontalAlignment(RIGHT);
        setVerticalAlignment(CENTER);
    }

    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setText(value.toString());
        return this;
    }
};

