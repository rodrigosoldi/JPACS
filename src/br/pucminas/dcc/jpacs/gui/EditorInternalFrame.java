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
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.net.URL;

/**
 * Classe que faz um JInterframe com um editor do JPACS
 */
class EditorInternalFrame extends JInternalFrame {

    private static int ContadorDeFrame = 1;//para o título e posicionamento na tela
    public JTextPane Texto;
    protected JScrollPane TextScroller;
    protected JToolBar BarraDeFerramentas;
    protected JButton Comp,  Proc;
    protected MainFrame OwnerFrame;
    public boolean Acesso = true;
    public JCheckBoxMenuItem MenuMainFrame;

    public EditorInternalFrame(MainFrame aFrame) {
        this(Internacionalizacao.get("Editor.Assembly.Title.Default") + " " + ContadorDeFrame, aFrame);
    }

    public EditorInternalFrame(String Titulo, MainFrame aFrame) //construtor
    {
        super(Titulo, true, true, true, true);//construtor da superclasse -- JInternalFrame
        ContadorDeFrame++;
        this.OwnerFrame = aFrame;
        this.getContentPane().setLayout(new BorderLayout());

        constroiBarraDeFerramentas();

        JPanel Tudo = new JPanel();//Panel para colocar os elementos da interface
        Tudo.setBorder(new EmptyBorder(10, 10, 10, 10));
        Tudo.setLayout(new BorderLayout());

        Texto = new JTextPane();//Area para Texto
        Texto.setPreferredSize(new Dimension(250, 250));
        Texto.setBorder(new EmptyBorder(0, 5, 0, 5));

        buildMenuMainFrame();
        addInternalFrameListener(new InternalFrameAdapter() {

            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                MenuMainFrame.setState(true);
                if (Acesso) {
                    OwnerFrame.FrameCache.setVisible(true);
                    OwnerFrame.Output.setVisible(true);
                    OwnerFrame.FramePro.setVisible(false);
                } else {
                    if (!OwnerFrame.CacheOn) {
                        OwnerFrame.FrameCache.setVisible(false);
                        OwnerFrame.Output.setVisible(false);
                    }
                    OwnerFrame.FramePro.setVisible(true);
                }

                try {
                    setSelected(true);
                    setVisible(true);
                } catch (Exception exc) {
                }
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent e) {
                MenuMainFrame.setState(false);
            }

            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                OwnerFrame.MenuJanela.remove(MenuMainFrame);
            }
        });

        TextScroller = new JScrollPane(Texto,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);//barras de rolagem

        getContentPane().add(TextScroller, BorderLayout.CENTER);//coloca o Panel na Janela
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocation(35 * ContadorDeFrame, 25 * ContadorDeFrame);//posição na tela
    }

    @Override
    public void setTitle(String Title) {
        super.setTitle(Title);
        MenuMainFrame.setText(Title);
    }

    public void buildMenuMainFrame() {
        MenuMainFrame = new JCheckBoxMenuItem(this.getTitle());

        MenuMainFrame.addActionListener(new CheckEditorMenuListener(MenuMainFrame) {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setSelected(true);
                    setVisible(true);
                } catch (Exception exc) {
                }
            }
        });

        if (OwnerFrame.MenuJanela.getItemCount() == 4) {
            OwnerFrame.MenuJanela.addSeparator();
        }
        OwnerFrame.MenuJanela.add(MenuMainFrame);
    }

    public void constroiBarraDeFerramentas() {
        BarraDeFerramentas = new JToolBar();
        URL Path;

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/novo.png");
        JButton Novo = new JButton(new ImageIcon(Path));
        Novo.setToolTipText(Internacionalizacao.get("Editor.Toolbar.New"));
        Novo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Limpa();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/abrir.png");
        JButton Abre = new JButton(new ImageIcon(Path));
        Abre.setToolTipText(Internacionalizacao.get("Editor.Toolbar.Open"));
        Abre.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Abrir();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/salvar.png");
        JButton Salva = new JButton(new ImageIcon(Path));
        Salva.setToolTipText(Internacionalizacao.get("Editor.Toolbar.Save"));
        Salva.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OwnerFrame.SalvarJanela();
            }
        });


        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/compila.png");
        Comp = new JButton(new ImageIcon(Path));
        Comp.setToolTipText(Internacionalizacao.get("Editor.Toolbar.Compile"));
        Comp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OwnerFrame.JanelaCompCodigo();
            }
        });
        Comp.setEnabled(false);

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/passo.png");
        JButton Passo = new JButton(new ImageIcon(Path));
        Passo.setToolTipText(Internacionalizacao.get("Editor.Toolbar.Simulation.Step"));
        Passo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OwnerFrame.SimulaPasso();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/relampago.png");
        JButton Rel = new JButton(new ImageIcon(Path));
        Rel.setToolTipText(Internacionalizacao.get("Editor.Toolbar.Simulation.Quick"));
        Rel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                OwnerFrame.JanelaSimula();
            }
        });

        BarraDeFerramentas.add(Novo);
        BarraDeFerramentas.add(Abre);
        BarraDeFerramentas.add(Salva);
        BarraDeFerramentas.addSeparator();
        BarraDeFerramentas.add(Comp);
        BarraDeFerramentas.addSeparator();
        BarraDeFerramentas.add(Passo);
        BarraDeFerramentas.add(Rel);

        getContentPane().add(BarraDeFerramentas, BorderLayout.NORTH);

    }

    public void Limpa() {
        String Titulo = this.getTitle();
        if (Titulo.charAt(0) == 'C' || Titulo.charAt(1) == 's') {
            this.setTitle(Internacionalizacao.get("Editor.Assembly.Title.Default") + " " + ContadorDeFrame++);
        } else {
            this.setTitle(Internacionalizacao.get("Editor.Access.Title.Default") + " " + ContadorDeFrame++);
        }
        Texto.setText("");
    }

    public void Novo() {
        getContentPane().remove(TextScroller);

        Texto = new JTextPane();//Area para Texto
        Texto.setPreferredSize(new Dimension(250, 250));
        Texto.setBorder(new EmptyBorder(0, 5, 0, 5));

        TextScroller = new JScrollPane(Texto,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);//barras de rolagem

        getContentPane().add(TextScroller, BorderLayout.CENTER);//coloca o Panel na Janela
    }

    public void Abrir() {
        String Titulo;
        File Entrada;//arquivo a ser lido
        int Rep;

        OwnerFrame.EscolheArq.setDialogTitle(Internacionalizacao.get("Open.Title"));//titulo do Dialog
        OwnerFrame.EscolheArq.resetChoosableFileFilters();
        OwnerFrame.EscolheArq.addChoosableFileFilter(new TextFilter());
        OwnerFrame.EscolheArq.addChoosableFileFilter(new AsmFilter());
        Entrada = new File(OwnerFrame.EscolheArq.getCurrentDirectory(), OwnerFrame.UltimoArquivo);
        OwnerFrame.EscolheArq.setSelectedFile(Entrada);

        Rep = OwnerFrame.EscolheArq.showOpenDialog(this);//executa o dialog
        if (Rep == JFileChooser.APPROVE_OPTION)//clicou em ok
        {
            Entrada = OwnerFrame.EscolheArq.getSelectedFile();//pega arquivo escolhido
            Titulo = Entrada.getName();//pega o nome do arquivo
            OwnerFrame.UltimoArquivo = Entrada.getName();
            Novo();
            if (Titulo.substring(Titulo.length() - 4).compareToIgnoreCase(".ASM") == 0) //compara extensão
            {
                this.SyntaxHighlight();
                Titulo = Internacionalizacao.get("Editor.Assembly.Title") + ": " + Titulo;
                OwnerFrame.Desk.remove(this);
                OwnerFrame.Desk.add(this, MainFrame.CAMADACODIGO);
            } else {
                Titulo = Internacionalizacao.get("Editor.Access.Title") + ": " + Titulo;
                Comp.setEnabled(false);
                OwnerFrame.Desk.remove(this);
                OwnerFrame.Desk.add(this, MainFrame.CAMADAACESSO);
            }

            this.LerDoArquivo(Entrada);//le do arquivo
            this.setTitle(Titulo);
            this.setVisible(true);
        }

    }

    public void LerDoArquivo(File Entrada) {
        String T = "";
        try {
            FileReader Ent = new FileReader(Entrada);
            int c;
            char C;
            while ((c = Ent.read()) != -1) {
                C = (char) c;
                T += C;
            }
            Ent.close();
        } catch (IOException e) {
        }
        Texto.setText(T);
    }

    public void SyntaxHighlight() {
        Texto.setDocument(new CommandStyledDocument());
        Texto.setFont(new Font("Courier New", Font.PLAIN, 14));
        Comp.setEnabled(true);
        Acesso = false;
    }

    @Override
    public String toString() {
        return Texto.getText();
    }

    public int QuantidadeDeFrames() {
        return (ContadorDeFrame - 1);
    }
};

/**
 * Listener padrão para o menu de janelas
 */
class CheckEditorMenuListener implements ActionListener {

    JCheckBoxMenuItem OwnerMenu;

    public CheckEditorMenuListener(JCheckBoxMenuItem Own) {
        OwnerMenu = Own;
    }

    public void actionPerformed(ActionEvent e) {
    }
};