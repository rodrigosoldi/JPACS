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
import br.pucminas.dcc.jpacs.core.ItemRelatorio;
import br.pucminas.dcc.jpacs.core.Processor;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.net.URL;

/**
 * Classe contendo o JFrame principal do JPACS
 */
public class MainFrame extends JFrame {

    static final Integer CAMADAAJUDA = new Integer(7);
    static final Integer CAMADACODIGO = new Integer(6);
    static final Integer CAMADAACESSO = new Integer(5);
    static final Integer CAMADAOUTROS = new Integer(4);
    protected JMenuBar MenuPrincipal;
    protected JToolBar BarraFerramentas;
    public JDesktopPane Desk;
    protected JTable Table1;//Tabela que contém a memoria ram
    protected JInternalFrame FrameRam;//Frame q mostra a tabela da ram
    protected JInternalFrame Output;//mostra o output da simulação
    protected JInternalFrame FrameCache;//Frame q mostra a cache
    protected JInternalFrame FramePro;//Frame q mosta os registradores e os flags
    protected HelpInternalFrame FrameAjuda;//Frame q mostra ajuda
    protected JInternalFrame FrameListCom;//frame q mostra lista dos comandas
    protected Vector Vet = new Vector();
    protected Vector Backup;
    protected Vector Backup2;
    protected JTable Relatorio;
    protected JList Resultado = null;
    protected int Pass = 0;//indica em que linha está o passo
    protected Processor Processador = new Processor();
    public JFileChooser EscolheArq;
    public String UltimoArquivo = Internacionalizacao.get("Default.File");
    public String UltimoRelatorio = Internacionalizacao.get("Default.Report.File");
    public JButton Proc;
    public boolean CacheOn = true;
    public static final int SimulaCache = 1;
    public static final int SimulaProcessador = 2;
    public static final int SimulaIntegrado = 3;
    JMenu MenuJanela;
    JCheckBoxMenuItem CheckMenuPrinc;
    JCheckBoxMenuItem CheckMenuCache;
    JCheckBoxMenuItem CheckMenuProc;
    JCheckBoxMenuItem CheckMenuRel;

    public MainFrame() {
        super("JPACS"); //chama construtor JFrame(String)
        getContentPane().setLayout(new BorderLayout());
        CriarMenus();//cria o menu principal
        CriarFerramentas();//cria barra de ferramentas
        Desk = new JDesktopPane();
        Processador.NovaMemoria();
        ExibeRam(Processador.VisualizaRam());//cria o frame contendo a MemoriaRam
        ExibeCache();
        ExibeSimula();
        getContentPane().add(Desk, BorderLayout.CENTER);

        File Diretorio = new File("./files/");
        EscolheArq = new JFileChooser(Diretorio);
        EscolheArq.addChoosableFileFilter(new TextFilter());

        this.setIconImage(new ImageIcon(this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/JPACS.png")).getImage());
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        }); //encerra o programa se a janela for fechada

        //Dimension ScreenSize=Toolkit.getDefaultToolkit().getScreenSize();

        //setBounds(0,0,ScreenSize.width,ScreenSize.height-30); //tamanho da janela
        setExtendedState(MainFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    protected void CriarMenus() {
        MenuPrincipal = new JMenuBar();
        MenuPrincipal.setOpaque(true);
        JMenu Arquivo = CriaMenuArquivo();
        JMenu Simulacao = CriaMenuSimulacao();
        JMenu Config = CriaMenuConfig();
        MenuJanela = CriaMenuJanela();
        JMenu Ajuda = CriaMenuAjuda();

        MenuPrincipal.add(Arquivo);
        MenuPrincipal.add(Simulacao);
        MenuPrincipal.add(Config);
        MenuPrincipal.add(MenuJanela);
        MenuPrincipal.add(Ajuda);
        setJMenuBar(MenuPrincipal);
    }

    protected JMenu CriaMenuArquivo() {
        JMenu Arq = new JMenu(Internacionalizacao.get("Menu.File"));
        JMenu Novo = new JMenu(Internacionalizacao.get("Menu.File.New"));
        JMenuItem Abrir = new JMenuItem(Internacionalizacao.get("Menu.File.Open"));
        JMenuItem Salvar = new JMenuItem(Internacionalizacao.get("Menu.File.Save"));
        JMenuItem Sair = new JMenuItem(Internacionalizacao.get("Menu.File.Exit"));

        JMenuItem Assembly = new JMenuItem(Internacionalizacao.get("Menu.File.New.Assembly"));
        JMenuItem Acesso = new JMenuItem(Internacionalizacao.get("Menu.File.New.Access"));

        Arq.setMnemonic(Internacionalizacao.get("Menu.File.Mnemonic").charAt(0));
        Novo.setMnemonic(Internacionalizacao.get("Menu.File.New.Mnemonic").charAt(0));
        Abrir.setMnemonic(Internacionalizacao.get("Menu.File.Open.Mnemonic").charAt(0));
        Salvar.setMnemonic(Internacionalizacao.get("Menu.File.Save.Mnemonic").charAt(0));
        Sair.setMnemonic(Internacionalizacao.get("Menu.File.Exit.Mnemonic").charAt(0));

        Assembly.setMnemonic(Internacionalizacao.get("Menu.File.New.Assembly.Mnemonic").charAt(0));
        Acesso.setMnemonic(Internacionalizacao.get("Menu.File.New.Access.Mnemonic").charAt(0));

        Abrir.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AbrirJanela();
            }
        });

        Salvar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SalvarJanela();
            }
        });

        Sair.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        Assembly.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JanelaNovoCodigo();
            }
        });

        Acesso.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JanelaNovoAcesso();
            }
        });

        Novo.add(Assembly);
        Novo.add(Acesso);

        Arq.add(Novo);
        Arq.add(Abrir);
        Arq.add(Salvar);
        Arq.addSeparator();
        Arq.add(Sair);

        return Arq;
    }

    protected JMenu CriaMenuSimulacao() {
        JMenu Simulacao = new JMenu(Internacionalizacao.get("Menu.Simulation"));
        JMenuItem Normal = new JMenuItem(Internacionalizacao.get("Menu.Simulation.Quick"));
        JMenuItem Passo = new JMenuItem(Internacionalizacao.get("Menu.Simulation.Step"));
        JMenuItem Salva = new JMenuItem(Internacionalizacao.get("Menu.Simulation.Report"));
        JMenuItem Config = new JMenuItem(Internacionalizacao.get("Menu.Simulation.Configuration"));
        JMenuItem Limp = new JMenuItem(Internacionalizacao.get("Menu.Simulation.Clean"));

        Simulacao.setMnemonic(Internacionalizacao.get("Menu.Simulation.Mnemonic").charAt(0));
        Normal.setMnemonic(Internacionalizacao.get("Menu.Simulation.Quick.Mnemonic").charAt(0));
        Passo.setMnemonic(Internacionalizacao.get("Menu.Simulation.Step.Mnemonic").charAt(0));
        Salva.setMnemonic(Internacionalizacao.get("Menu.Simulation.Report.Mnemonic").charAt(0));
        Config.setMnemonic(Internacionalizacao.get("Menu.Simulation.Configuration.Mnemonic").charAt(0));
        Limp.setMnemonic(Internacionalizacao.get("Menu.Simulation.Clean.Mnemonic").charAt(0));

        Normal.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JanelaSimula();
            }
        });

        Passo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SimulaPasso();
            }
        });

        Salva.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SalvaSimula();
            }
        });

        Limp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LimpaTudo();
            }
        });

        Simulacao.add(Normal);
        Simulacao.add(Passo);
        Simulacao.addSeparator();
        Simulacao.add(Salva);
        Simulacao.add(Limp);

        return Simulacao;
    }

    protected JMenu CriaMenuConfig() {
        JMenu Config = new JMenu(Internacionalizacao.get("Menu.Config"));
        JMenuItem Mem = new JMenuItem(Internacionalizacao.get("Menu.Config.Mem"));
        JMenuItem Inter = new JMenuItem(Internacionalizacao.get("Menu.Config.International"));

        Config.setMnemonic(Internacionalizacao.get("Menu.Config.Mnemonic").charAt(0));
        Mem.setMnemonic(Internacionalizacao.get("Menu.Config.Mem.Mnemonic").charAt(0));
        Inter.setMnemonic(Internacionalizacao.get("Menu.Config.International.Mnemonic").charAt(0));

        Mem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JanelaConfig();
            }
        });

        Inter.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JanelaInter();
            }
        });

        Config.add(Mem);
        Config.add(Inter);
        return Config;
    }

    protected JMenu CriaMenuJanela() {
        JMenu Janela = new JMenu(Internacionalizacao.get("Menu.Window"));
        CheckMenuPrinc = new JCheckBoxMenuItem(Internacionalizacao.get("Menu.Window.Memory"));
        CheckMenuCache = new JCheckBoxMenuItem(Internacionalizacao.get("Menu.Window.Cache"));
        CheckMenuProc = new JCheckBoxMenuItem(Internacionalizacao.get("Menu.Window.Processor"));
        CheckMenuRel = new JCheckBoxMenuItem(Internacionalizacao.get("Menu.Window.Report"));

        Janela.setMnemonic(Internacionalizacao.get("Menu.Window.Mnemonic").charAt(0));
        CheckMenuPrinc.setMnemonic(Internacionalizacao.get("Menu.Window.Memory.Mnemonic").charAt(0));
        CheckMenuCache.setMnemonic(Internacionalizacao.get("Menu.Window.Cache.Mnemonic").charAt(0));
        CheckMenuProc.setMnemonic(Internacionalizacao.get("Menu.Window.Processor.Mnemonic").charAt(0));
        CheckMenuRel.setMnemonic(Internacionalizacao.get("Menu.Window.Report.Mnemonic").charAt(0));

        CheckMenuPrinc.setState(true);
        CheckMenuCache.setState(true);
        CheckMenuProc.setState(true);
        CheckMenuRel.setState(true);

        CheckMenuPrinc.addActionListener(new CheckMenuListener(CheckMenuPrinc) {

            @Override
            public void actionPerformed(ActionEvent e) {
                FrameRam.setVisible(Owner.getState());
            }
        });

        CheckMenuCache.addActionListener(new CheckMenuListener(CheckMenuCache) {

            @Override
            public void actionPerformed(ActionEvent e) {
                FrameCache.setVisible(Owner.getState());
            }
        });

        CheckMenuProc.addActionListener(new CheckMenuListener(CheckMenuProc) {

            @Override
            public void actionPerformed(ActionEvent e) {
                FramePro.setVisible(Owner.getState());
            }
        });

        CheckMenuRel.addActionListener(new CheckMenuListener(CheckMenuRel) {

            @Override
            public void actionPerformed(ActionEvent e) {
                Output.setVisible(Owner.getState());
            }
        });

        Janela.add(CheckMenuPrinc);
        Janela.add(CheckMenuCache);
        Janela.add(CheckMenuProc);
        Janela.add(CheckMenuRel);
        return Janela;
    }

    protected JMenu CriaMenuAjuda() {
        JMenu Ajuda = new JMenu(Internacionalizacao.get("Menu.Help"));
        JMenuItem Topicos = new JMenuItem(Internacionalizacao.get("Menu.Help.Topics"));
        JMenuItem Comand = new JMenuItem(Internacionalizacao.get("Menu.Help.Assembly"));
        JMenuItem Sobre = new JMenuItem(Internacionalizacao.get("Menu.Help.About"));

        Ajuda.setMnemonic(Internacionalizacao.get("Menu.Help.Mnemonic").charAt(0));
        Topicos.setMnemonic(Internacionalizacao.get("Menu.Help.Topics.Mnemonic").charAt(0));
        Comand.setMnemonic(Internacionalizacao.get("Menu.Help.Assembly.Mnemonic").charAt(0));
        Sobre.setMnemonic(Internacionalizacao.get("Menu.Help.About.Mnemonic").charAt(0));

        Topicos.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AjudaTopicos();
            }
        });

        Comand.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AjudaComandos();
            }
        });

        Sobre.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AjudaSobre();
            }
        });

        Ajuda.add(Topicos);
        Ajuda.add(Comand);
        Ajuda.addSeparator();
        Ajuda.add(Sobre);

        return Ajuda;
    }

    public void CriarFerramentas() {
        BarraFerramentas = new JToolBar();
        URL Path;

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/novo.png");
        JButton Novo = new JButton(new ImageIcon(Path));
        Novo.setToolTipText(Internacionalizacao.get("Toolbar.Button.New.Access"));
        Novo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JanelaNovoAcesso();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/novoA.png");
        JButton NovoA = new JButton(new ImageIcon(Path));
        NovoA.setToolTipText(Internacionalizacao.get("Toolbar.Button.New.Assembly"));
        NovoA.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JanelaNovoCodigo();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/abrir.png");
        JButton Abre = new JButton(new ImageIcon(Path));
        Abre.setToolTipText(Internacionalizacao.get("Toolbar.Button.Open"));
        Abre.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AbrirJanela();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/salvar.png");
        JButton Salva = new JButton(new ImageIcon(Path));
        Salva.setToolTipText(Internacionalizacao.get("Toolbar.Button.Save"));
        Salva.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SalvarJanela();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/config.png");
        JButton Config = new JButton(new ImageIcon(Path));
        Config.setToolTipText(Internacionalizacao.get("Toolbar.Button.Configuration"));
        Config.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JanelaConfig();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/refaz.png");
        JButton Limpa = new JButton(new ImageIcon(Path));
        Limpa.setToolTipText(Internacionalizacao.get("Toolbar.Button.Clean"));
        Limpa.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                LimpaTudo();
            }
        });

        Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/procOff.png");
        Proc = new JButton(new ImageIcon(Path));
        Proc.setToolTipText(Internacionalizacao.get("Editor.Toolbar.Proc.Off"));
        Proc.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ProcClick();
            }
        });

        BarraFerramentas.add(Novo);
        BarraFerramentas.add(NovoA);
        BarraFerramentas.addSeparator();
        BarraFerramentas.add(Abre);
        BarraFerramentas.add(Salva);
        BarraFerramentas.addSeparator();
        BarraFerramentas.add(Config);
        BarraFerramentas.add(Limpa);
        BarraFerramentas.add(Proc);

        getContentPane().add(BarraFerramentas, BorderLayout.NORTH);

    }

    public void ProcClick() {
        CacheOn = !CacheOn;

        try {
            URL Path;

            if (CacheOn) {
                Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/procOff.png");
                Proc.setIcon(new ImageIcon(Path));
                Proc.setToolTipText(Internacionalizacao.get("Editor.Toolbar.Proc.Off"));
                FrameCache.setVisible(true);
                Output.setVisible(true);
            } else {
                Path = this.getClass().getResource("/br/pucminas/dcc/jpacs/resources/procOn.png");
                Proc.setIcon(new ImageIcon(Path));
                Proc.setToolTipText(Internacionalizacao.get("Editor.Toolbar.Proc.On"));
                FrameCache.setVisible(false);
                Output.setVisible(false);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void JanelaInter() {
        InternationalDialog Inter = new InternationalDialog(this);
        Inter.setVisible(true);
        if (Inter.ok()) {
            Internacionalizacao.create(Inter.idioma(), Inter.local());
            Internacionalizacao.writeToFile("JPACS.lang");
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("Config.Inter.Warning"), Internacionalizacao.get("Warning.Title"), JOptionPane.WARNING_MESSAGE);
        }
    }

    public void JanelaConfig() {
        JanelaConfig(false);
    }

    public void JanelaConfig(boolean Rec) {
        ConfigDialog Config = new ConfigDialog(this);
        if (Rec) {
            Config.Cancelar.setEnabled(false);
            Config.Salvar.setEnabled(false);
        }
        Config.setVisible(true);
        try {
            LimpaTudo();
        } catch (OutOfMemoryError e) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("Error.Out.Of.Memory"), Internacionalizacao.get("Error.Title"), JOptionPane.ERROR_MESSAGE);
            System.gc();
            JanelaConfig(true);
        }
    }

    public void LimpaTudo() {
        JInternalFrame Selecionado = Desk.getSelectedFrame();

        Resultado = null;
        Vet = new Vector();
        Processador.LimpaProcessador();
        ExibeRam(Processador.VisualizaRam());
        ExibeCache();
        ExibeSimula();
        Desk.repaint();
        this.setVisible(true);

        try {
            Selecionado.setSelected(true);
        } catch (Exception e) {
        }
    }

    public void JanelaNovoCodigo() {
        EditorInternalFrame N = new EditorInternalFrame(this);//novo frame
        N.SyntaxHighlight();
        Desk.add(N, CAMADACODIGO);//adiciona frame ao frame principal
        try {
            N.setVisible(true);
            N.setSelected(true); //seleciona o frame criado
        } catch (java.beans.PropertyVetoException e2) {
        }

    }

    public void SalvarJanela() {
        JInternalFrame Frames[] = Desk.getAllFramesInLayer(CAMADACODIGO.intValue());//pega InternalFrame atual
        int Resp;
        String Titulo, Texto, Extensao;
        File Saida;//Arquivo a ser salvo

        EscolheArq.resetChoosableFileFilters();
        if (Frames.length == 0 || !Frames[0].isSelected()) // não existe frame selecionado em camada de codigo
        {
            Frames = Desk.getAllFramesInLayer(CAMADAACESSO.intValue());//pega InternalFrame atual
            if (Frames.length == 0 || !Frames[0].isSelected()) // não existe frame selecionado em camada de acesso
            {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("Error.Select.Frame"), Internacionalizacao.get("Error.Title"), JOptionPane.ERROR_MESSAGE);
                return;
            } else //frame é de acesso direto
            {
                EscolheArq.addChoosableFileFilter(new TextFilter());
                EscolheArq.setDialogTitle(Internacionalizacao.get("Save.Access.Title"));//titulo do Dialog
                Titulo = Internacionalizacao.get("Editor.Access.Title") + ": ";
                Extensao = ".txt";
            }
        } else //frame é de codigo assembly
        {
            EscolheArq.addChoosableFileFilter(new AsmFilter());
            EscolheArq.setDialogTitle(Internacionalizacao.get("Save.Assembly.Title"));//titulo do Dialog
            Titulo = Internacionalizacao.get("Editor.Assembly.Title") + ": ";
            Extensao = ".asm";
        }

        Saida = new File(EscolheArq.getCurrentDirectory(), UltimoArquivo);
        EscolheArq.setSelectedFile(Saida);
        Resp = EscolheArq.showSaveDialog(this);//executa o dialog
        if (Resp == JFileChooser.APPROVE_OPTION)//clicou em ok
        {
            Saida = EscolheArq.getSelectedFile();//pega arquivo escolhido
            if (Saida.getName().lastIndexOf(".") < 0) //sem extensao
            {
                Saida = new File(Saida.toString() + Extensao); //adiciona exdtensao
            }
            UltimoArquivo = Saida.getName();
            Titulo = Titulo + Saida.getName();//pega o nome do arquivo
            try {
                Frames[0].setTitle(Titulo);//altera o titulo do frame
                Texto = Frames[0].toString();//pega o texto
                FileWriter F = new FileWriter(Saida);
                F.write(Texto);//grava o texto
                F.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void AbrirJanela() {
        String Titulo;
        File Entrada;//arquivo a ser lido
        int Rep;

        EscolheArq.setDialogTitle(Internacionalizacao.get("Open.Title"));//titulo do Dialog
        EscolheArq.resetChoosableFileFilters();
        EscolheArq.addChoosableFileFilter(new TextFilter());
        EscolheArq.addChoosableFileFilter(new AsmFilter());

        Entrada = new File(EscolheArq.getCurrentDirectory(), UltimoArquivo);
        EscolheArq.setSelectedFile(Entrada);
        Rep = EscolheArq.showOpenDialog(this);//executa o dialog
        if (Rep == JFileChooser.APPROVE_OPTION)//clicou em ok
        {
            Entrada = EscolheArq.getSelectedFile();//pega arquivo escolhido
            Titulo = Entrada.getName();//pega o nome do arquivo
            UltimoArquivo = Entrada.getName();
            EditorInternalFrame Aberto = new EditorInternalFrame(Titulo, this);//cria um frame
            if (Titulo.substring(Titulo.length() - 4).compareToIgnoreCase(".ASM") == 0) //compara extens�o
            {
                Aberto.SyntaxHighlight();
                Desk.add(Aberto, CAMADACODIGO);//adiciona o frame ao frame principal
                Titulo = Internacionalizacao.get("Editor.Assembly.Title") + ": " + Titulo;
            } else {
                Desk.add(Aberto, CAMADAACESSO);//adiciona o frame ao frame principal
                Titulo = Internacionalizacao.get("Editor.Access.Title") + ": " + Titulo;
            }
            try {
                Aberto.setVisible(true);
                Aberto.setSelected(true);
                Aberto.LerDoArquivo(Entrada);//lê do arquivo
                Aberto.setTitle(Titulo);
            } catch (java.beans.PropertyVetoException e2) {
            }
        }

    }

    public void JanelaCompCodigo() {
        JInternalFrame Codigo[];
        int Erros[];
        int i;
        String Texto;

        try {
            Codigo = Desk.getAllFramesInLayer(CAMADACODIGO.intValue());//pega InternalFrame selecionado na camada certa
            Texto = Codigo[0].toString();//pega o programa fonte
            try {
                Erros = Processador.Compilador(Texto);//compila o programa fonte

                for (i = 0; i < Erros.length && Erros[i] > 0; i++) //cria mensagem de erro
                {
                    if (i == 0) {
                        Texto = Internacionalizacao.get("Warning.Invalid.Instruction");
                    }
                    Texto += Integer.toString(Erros[i]) + " ";
                }

                if (i > 0) //houve erros
                {
                    JOptionPane.showMessageDialog(this, Texto, Internacionalizacao.get("Warning.Title"), JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                System.out.println(e);
                JOptionPane.showMessageDialog(this, e, Internacionalizacao.get("Error.Title"), JOptionPane.ERROR_MESSAGE);
            }

            ExibeRam(Processador.VisualizaRam());
            ExibeSimula();


            if (!CacheOn) {
                Output.setVisible(false);
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }

    }

    public void ExibeRam(Vector Ram)//cria ou atualiza o frame contendo a MemoriaRam
    {
        int i;
        Vector Tabela = new Vector();
        Vector Titulo = new Vector();
        Vector Linha;

        Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Titulo.add(Internacionalizacao.get("Memory.Position.Abreviation"));
        Titulo.add(Internacionalizacao.get("Memory.Item.Abreviation"));
        for (i = 0; i < Ram.size(); i++) {
            Linha = new Vector();
            Linha.add(new Integer(i));//posicao da mem
            Linha.add(Ram.get(i));//conteúdo da mem

            Tabela.add(Linha);
        }

        if (FrameRam == null)//caso não exista o frame ram
        {
            FrameRam = new JInternalFrame(Internacionalizacao.get("Memory.Title"), true, false, false, true) {

                @Override
                public void setVisible(boolean b) {
                    super.setVisible(b);
                    try {
                        CheckMenuPrinc.setState(b);
                    } catch (Exception e) {
                    }
                }
            };
            FrameRam.setBounds(0, 10, 170, ScreenSize.height - 170);//posição e tamanho do frame
            Desk.add(FrameRam, CAMADAOUTROS);//adiciona frame ao Frame principal
        }

        ModeloTabela Modelo = new ModeloTabela(Tabela, Titulo);
        Table1 = new JTable(Modelo);//cria a tabela
        JScrollPane T = new JScrollPane(Table1);//cria barra de rolagem

        FrameRam.getContentPane().removeAll(); //remove todos componentes do Frame
        FrameRam.getContentPane().add(T);//adiciona Tabela ao InternalFrame
        try {
            FrameRam.setVisible(true);//torna frame visivel
            FrameRam.setSelected(true);//seleciona o frame
        } catch (java.beans.PropertyVetoException e2) {
            System.out.println(e2);
        }
        Desk.repaint();
        this.setVisible(true);

    }

    public void ExibeCache() {
        if (FrameCache == null) {
            FrameCache = new JInternalFrame(Internacionalizacao.get("Cache.Title"), true, false, false, true) {

                @Override
                public void setVisible(boolean b) {
                    super.setVisible(b);
                    try {
                        CheckMenuCache.setState(b);
                    } catch (Exception e) {
                    }
                }
            };

            FrameCache.setBounds(180, 10, 300, 200);
            Desk.add(FrameCache, CAMADAOUTROS);
        }

        FrameCache.getContentPane().removeAll();
        FrameCache.getContentPane().add(new CachePanel(Processador.VisualizaCache()));

        try {
            FrameCache.setVisible(true);
            FrameCache.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.out.println(e);
        }

        Desk.repaint();
        this.setVisible(true);

    }

    public void ExibeSimula() {
        Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (Output == null) {
            Output = new JInternalFrame(Internacionalizacao.get("Report.Title"), true, false, false, true) {

                @Override
                public void setVisible(boolean b) {
                    super.setVisible(b);
                    try {
                        CheckMenuRel.setState(b);
                    } catch (Exception e) {
                    }
                }
            };
            Output.setBounds(490, 10, ScreenSize.width - 510, ScreenSize.height - 170);
            Desk.add(Output, CAMADAOUTROS);
        }


        ModeloTabelaRelatorio Mtr = new ModeloTabelaRelatorio(Vet);
        Relatorio = new JTable(Mtr);

        TableColumn column = null;
        for (int i = 0; i < 7; i++) {
            column = Relatorio.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(10); //primeira coluna é menor
            } else {
                column.setPreferredWidth(50);
            }

            if (i == 2 || i == 5) {
                column.setCellRenderer(new Renderizador(JLabel.CENTER));
            } else if (i == 6) {
                column.setCellRenderer(new Renderizador(JLabel.RIGHT));
            }
        }

        JScrollPane Scroll = new JScrollPane(Relatorio);
        Output.getContentPane().removeAll();

        if (Resultado == null) {
            Output.getContentPane().add(Scroll);
        } else {
            Resultado.setFont(new Font("Courier New", Font.PLAIN, 12));
            JScrollPane Scroll2 = new JScrollPane(Resultado);
            Scroll2.setBorder(new TitledBorder(Internacionalizacao.get("Report.Results")));

            JSplitPane Split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
            Split.setLeftComponent(Scroll);
            Split.setRightComponent(Scroll2);
            Split.setDividerLocation(ScreenSize.height / 2);
            Split.setOneTouchExpandable(true);

            Output.getContentPane().add(Split);
        }

        if (FramePro == null) {
            FramePro = new JInternalFrame(Internacionalizacao.get("Processor.Title"), true, false, false, true) {

                @Override
                public void setVisible(boolean b) {
                    super.setVisible(b);
                    try {
                        CheckMenuProc.setState(b);
                    } catch (Exception e) {
                    }
                }
            };
            FramePro.setBounds(180, 220, 300, ScreenSize.height - 380);
            Desk.add(FramePro, CAMADAOUTROS);
        }

        FramePro.getContentPane().removeAll();
        FramePro.getContentPane().add(new ProcessorPanel(Processador));

        try {
            FramePro.setVisible(true);
            FramePro.setSelected(true);

            Output.setVisible(true);
            Output.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.out.println(e);
        }

        FramePro.show();

        Desk.repaint();
        this.setVisible(true);
    }

    public void JanelaSimula() {
        String Texto = "";
        String N[] = new String[30];
        String Enderecos[];
        JInternalFrame Ac[];
        Vector Acessos;
        int i;
        int Estado = 0;

        Ac = Desk.getAllFramesInLayer(CAMADAACESSO.intValue());

        try { //se existir janela na camada código selecionada, simule-a

            if (Ac.length != 0 && Ac[0].isSelected())//caso esteja selecionado
            {
                Texto = Ac[0].toString();//pega os endereços a serem acessados
                Estado = SimulaCache; //simula somente a cache desligando o processador

                Processador.ZeraCalculos();
                Enderecos = Processor.SeparaEmLinhas(Texto);//separa as linhas
                for (i = 0; i < Enderecos.length; i++) {
                    Acessos = Processador.Acessando(Enderecos[i]); //Acessa posicão de memória
                    Vet.addAll(Acessos);//adiciona Cache Hit ou Miss
                }
            } else//simular executando programa da Ram
            {
                if (CacheOn) {
                    Estado = SimulaIntegrado;
                } else {
                    Estado = SimulaProcessador;
                }
                Acessos = Processador.Processando();//executa instrução e pega seus resultados
                while (!Processador.Finis() && Acessos != null)//até q o processador para
                {
                    Vet.addAll(Acessos);//inclui
                    Acessos = Processador.Processando();//executa instrução e pega seus resultados
                }

                if (Acessos != null) {
                    Vet.addAll(Acessos);//inclui
                }
            }

        } catch (Exception e) {
            System.out.println("Error. MainFrame.JanelaSimula()->" + e);
        }

        Texto = Processador.Calcula();//calcula HitRatio, Tempo Medio de Acesso e etc...
        N = Processor.SeparaEmLinhas(Texto);//separa em linhas
        Backup2 = new Vector();
        for (i = 0; i < N.length; i++) {
            Backup2.addElement(N[i]);//adiciona
        }
        Resultado = new JList(Backup2);

        ExibeSimula();//refaz a janela com o resultado da simulação
        ExibeCache();//refaz a janela que exibe a cache
        ExibeRam(Processador.VisualizaRam()); //refaz a janela que exibe a ram

        switch (Estado) {
            case SimulaCache:
                FramePro.setVisible(false);
                break;
            case SimulaProcessador:
                FrameCache.setVisible(false);
                Output.setVisible(false);
                break;
            case SimulaIntegrado:
                break;
        }

        Backup = Vet;
        Vet = new Vector();//deixa preparado pro próximo
        Processador.ZeraCalculos();
    }

    public void SimulaPasso() {
        String Texto = "";
        String N[] = new String[30];
        String Enderecos[];
        JInternalFrame Ac[];
        Vector Acessos;
        int i, Estouro = -1;
        int Estado = 0;

        Ac = Desk.getAllFramesInLayer(CAMADAACESSO.intValue());
        Resultado = null;

        try {

            if (Ac.length != 0 && Ac[0].isSelected())//caso esteja selecionado
            {

                Texto = Ac[0].toString();//pega os endereços a serem acessados
                Estado = SimulaCache; //simula somente a cache desligando o processador
                i = Pass++;
                if (Texto == null || Texto.length() == 0) {
                    return;
                }
                Enderecos = Processor.SeparaEmLinhas(Texto);//separa as linhas
                Estouro = Enderecos.length; //indica quando termina a simulação
                Acessos = Processador.Acessando(Enderecos[i]); //Acessa posição de memória
                Vet.addAll(Acessos);//adiciona Cache Hit ou Miss
            } else {
                if (CacheOn) {
                    Estado = SimulaIntegrado;
                } else {
                    Estado = SimulaProcessador;
                }
                if (!Processador.Finis()) {
                    Acessos = Processador.Processando();
                    Vet.addAll(Acessos);
                } else //terminou simulação
                {
                    Estouro = Pass;
                }
            }
        } catch (Exception e) {
            System.out.println("Error. MainFrame.SimulaPasso()->" + e);
        }

        ExibeSimula();
        if (Pass == Estouro) {
            Texto = Processador.Calcula();
            N = Processor.SeparaEmLinhas(Texto);
            Backup2 = new Vector();
            for (i = 0; i < N.length; i++) {
                Backup2.addElement(N[i]);
            }
            Resultado = new JList(Backup2);

            Pass = 0;
            ExibeSimula();
            Processador.ZeraCalculos();
            Backup = Vet;
            Vet = new Vector();
        }

        ExibeCache();
        ExibeRam(Processador.VisualizaRam());

        switch (Estado) {
            case SimulaCache:
                FramePro.setVisible(false);
                break;
            case SimulaProcessador:
                FrameCache.setVisible(false);
                Output.setVisible(false);
                break;
            case SimulaIntegrado:
                break;
        }

    }

    public void AjudaComandos() {
        int i = 0;
        JLabel Comandos[] = new JLabel[26];
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Main"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Prog"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Mov"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Mvi"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Inc"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Dec"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Add"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Sub"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Or"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.And"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Cmp"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Ldi"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Sti"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Ldr"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Str"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Jmp"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Jnz"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Jz"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Call"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Ret"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Push"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Pop"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Nop"));
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Halt"));
        Comandos[i++] = new JLabel(" ");
        Comandos[i++] = new JLabel(Internacionalizacao.get("Help.Commands.Regs"));

        if (FrameListCom != null) {
            Desk.remove(FrameListCom);
        }

        FrameListCom = new JInternalFrame(Internacionalizacao.get("Help.Commands.Title"), true, true, true, true);
        JPanel P = new JPanel(new GridLayout(Comandos.length, 1));
        P.setBorder(new TitledBorder(Internacionalizacao.get("Help.Commands.List")));
        for (i = 0; i < Comandos.length; i++) {
            P.add(Comandos[i]);
        }
        FrameListCom.getContentPane().add(new JScrollPane(P));
        FrameListCom.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        FrameListCom.setBounds(100, 100, 300, 300);
        FrameListCom.pack();

        Desk.add(FrameListCom, CAMADAAJUDA);

        try {
            FrameListCom.setVisible(true);
            FrameListCom.setSelected(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void AjudaSobre() {
        String Texto;
        Texto = "JPACS: Java Processor And Cache Simulator - Version 1.6.1\n\n";
        Texto += "Daniel de Carvalho\n";
        Texto += "Gladyston Batista\n";
        Texto += "Henrique Rocha\n";
        Texto += "Leonan Vasconcelos\n";

        JOptionPane.showMessageDialog(this, Texto, Internacionalizacao.get("Help.About.Title"), 1);
    }

    public void SalvaSimula() {
        File Arq;
        int i;
        int Rep;
        String Titulo;
        EscolheArq.setDialogTitle(Internacionalizacao.get("Save.Report.Title"));//titulo do Dialog

        EscolheArq.resetChoosableFileFilters();
        EscolheArq.addChoosableFileFilter(new TextFilter());
        EscolheArq.addChoosableFileFilter(new XmlFilter());
        Arq = new File(EscolheArq.getCurrentDirectory(), UltimoRelatorio);
        EscolheArq.setSelectedFile(Arq);
        Rep = EscolheArq.showSaveDialog(this);//executa o dialog
        if (Rep == JFileChooser.APPROVE_OPTION)//clicou em ok e existe frame selecionado
        {
            Arq = EscolheArq.getSelectedFile();//pega arquivo escolhido
            Titulo = Arq.getName();
            UltimoRelatorio = Titulo;

            if (EscolheArq.getFileFilter().getDescription().equals(Internacionalizacao.get("File.Filter.Xml.Description"))) {
                if (Titulo.lastIndexOf(".") < 0) {
                    Titulo += ".xml";
                    Arq = new File(Arq.getPath() + ".xml");
                }
            } else if (EscolheArq.getFileFilter().getDescription().equals(Internacionalizacao.get("File.Filter.Text.Description"))) {
                if (Titulo.lastIndexOf(".") < 0) {
                    Titulo += ".txt";
                    Arq = new File(Arq.getPath() + ".txt");
                }
            }

            try {

                FileWriter Saida = new FileWriter(Arq);

                if (Titulo.substring(Titulo.length() - 4).compareToIgnoreCase(".XML") == 0) //compara extensão
                {
                    JPACSSaxParser Config = new JPACSSaxParser();
                    Config.parse(ConfigDialog.ConfigFile);
                    String Rel = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<!DOCTYPE JPACS SYSTEM \"" + JPACSSaxParser.DtdFileName + "\">" + "\n" + "\n<JPACS>" + Config.toString() + "\n\t<relatorio>";
                    for (i = 0; i < Backup.size(); i++) {
                        Rel += "\n\t\t" + ((ItemRelatorio) Backup.elementAt(i)).toXml();
                    }
                    Rel += "\n" + Processador.EstatisticasXml() + "\n\t</relatorio>" + "\n</JPACS>";
                    Saida.write(Rel);
                    Config.salvarArquivoDtd(Arq.getParent());
                } else {
                    Saida.write(Internacionalizacao.get("Report.Access.History") + ": \n\n");
                    for (i = 0; i < Backup.size(); i++) {
                        Saida.write(Backup.elementAt(i).toString());
                        Saida.write("\n");
                    }
                    Saida.write("\n" + Internacionalizacao.get("Report.Results") + ": \n\n");
                    for (i = 0; i < Backup2.size(); i++) {
                        Saida.write(Backup2.elementAt(i).toString());
                        Saida.write("\n");
                    }
                }

                Saida.close();
            } catch (IOException e1) {
                System.out.println(e1);
            } catch (Exception e2) {
                System.out.println(e2);
            }

        }

    }

    public void AjudaTopicos() {
        if (FrameAjuda != null) {
            Desk.remove(FrameAjuda);
        }
        FrameAjuda = new HelpInternalFrame();
        Desk.add(FrameAjuda, CAMADAAJUDA);
        Desk.repaint();
        this.setVisible(true);

    }

    public void JanelaNovoAcesso() {
        EditorInternalFrame N = new EditorInternalFrame(this);//novo frame
        N.setTitle(Internacionalizacao.get("Editor.Access.Title.Default") + " " + N.QuantidadeDeFrames());
        Desk.add(N, CAMADAACESSO);//adiciona frame ao frame principal
        try {
            N.setVisible(true);
            N.setSelected(true); //seleciona o frame criado
        } catch (java.beans.PropertyVetoException e2) {
        }
    }
};

/**
 * Filtro para arquivos Texto para os FileChoosers
 */
class TextFilter extends javax.swing.filechooser.FileFilter { 

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
            if (Extensao.compareToIgnoreCase(".TXT") == 0) {
                return true; //extensão eh txt, aceitar
            }
        }

        //não possue extensão
        return false;
    }

    public String getDescription() {
        return Internacionalizacao.get("File.Filter.Text.Description");
    }
};

/**
 * Filtro para arquivos Assembly para os FileChoosers
 */
class AsmFilter extends javax.swing.filechooser.FileFilter { 

    public boolean accept(File f) {
        String Extensao;
        int Pos;

        if (f.isDirectory()) {
            return true; //mostrar se for um diretorio
        //não é diretorio, verificar a extens�o
        }
        Pos = f.toString().lastIndexOf(".");
        if (Pos >= 0) //possue extensão
        {
            Extensao = f.toString().substring(Pos);
            if (Extensao.compareToIgnoreCase(".ASM") == 0) {
                return true; //extensão eh asm, aceitar
            }
        }

        //não possue extensão
        return false;
    }

    public String getDescription() {
        return Internacionalizacao.get("File.Filter.Asm.Description");
    }
};

/**
 * Renderizador da tabela de Histórico, faz diferenciação cromática entre Erros, Acertos, Leitura e Gravação
 */
class Renderizador extends DefaultTableCellRenderer {

    protected int Align;

    public Renderizador(int Align) {
        this.Align = Align;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component Comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        try {
            String V = (String) value;

            JLabel Lab = (JLabel) Comp;
            Lab.setHorizontalAlignment(Align);
            if (V.equals(Internacionalizacao.get("Report.Miss"))) {
                Lab.setForeground(new Color(128, 0, 0));
            } else if (V.equals(Internacionalizacao.get("Report.Hit"))) {
                Lab.setForeground(new Color(0, 128, 50));
            } else if (V.equals(Internacionalizacao.get("Report.Read"))) {
                Lab.setForeground(new Color(0, 102, 204));
            } else if (V.equals(Internacionalizacao.get("Report.Write"))) {
                Lab.setForeground(new Color(170, 20, 220));
            }

            return Lab;
        } catch (Exception e) {
        }
        return Comp;
    }
};

/**
 * Listener padrão para o menu de janelas
 */
class CheckMenuListener implements ActionListener {

    public JCheckBoxMenuItem Owner;

    public CheckMenuListener(JCheckBoxMenuItem O) {
        Owner = O;
    }

    public void actionPerformed(ActionEvent e) {
    }
};