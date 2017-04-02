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

package br.pucminas.dcc.jpacs.core;

import br.pucminas.dcc.jpacs.gui.ConfigDialog;
import br.pucminas.dcc.jpacs.JPACSSaxParser;
import java.util.Vector;

/**
 * Classe responsável por toda hierarquia de memória,
 * possui Memória Principal e uma Memória Cache
 */
public class ClasseMemoria {

    public MemoriaPrincipal Ram;
    public MemoriaCache Cache;
    public Vector Acessos;
    public static int HitRatio;
    public static int TempoGasto;
    public static int TempoMedio;
    public static int TempoAcessoMemPrincipal;
    public static int TempoAcessoCache;
    public static int AcessosMemPrincipal;
    public static int AcessosCache;
    public static int CacheTempoTotal;
    public static int MemPrincipalTempoTotal;
    public static int TempoTotal;

    /**
     * Construtor padrão
     */
    public ClasseMemoria()
    {
        Ram = new MemoriaPrincipal();//cria a Memoria Ram
        Cache = new MemoriaCache();//cria a Memoria Cache
        Acessos = new Vector(); //vector para armazenar historico de acessos
    }
    
    private int ParidadeImpar(int i) {
         i = i - ((i >>> 1) & 0x55555555);
         i = (i & 0x33333333) + ((i >>> 2) & 0x33333333);
         int numberOfSetBits = (((i + (i >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
         if (numberOfSetBits % 2 == 0) return 1;
         else return 0;
    }

    /**
     * Acessa memória para leitura
     * 
     * @param Pos posição de memória a ser acessada
     * @return Conteúdo da posição de memória acessada (em formato int)
     */
    public MemoryObject Acessar(int Pos) {
        int Ret = Cache.AcessarMemoria(Ram, Pos, false, 0);
        Acessos.add(Cache.HistAcesso);
        
        MemoryObject objeto = new MemoryObject(Ret,ParidadeImpar(Ret));
        
        return objeto;
    }

    /***
     * Acessa a memória para gravação
     * 
     * @param Pos Posição de memória a ser acessado
     * @param Elemento Dado a ser gravado na memória
     */
    public int Gravar(int Pos, MemoryObject objeto) {
        int paridade = ParidadeImpar(objeto.getElement());
        if (paridade == objeto.getParity()) {
            Cache.AcessarMemoria(Ram, Pos, true, objeto.getElement());
            Acessos.add(Cache.HistAcesso);
            return 0;
        }
        return -1;
    }

    /**
     * Acesso direto a memória principal, não passa pela cache
     * 
     * @param Pos Posição de memória a ser acessado
     * @param objeto
     * @return 
     */
    public int GravarDireto(int Pos, MemoryObject objeto) {
        int paridade = ParidadeImpar(objeto.getElement());
        if (paridade == objeto.getParity()) {
        	Ram.gravar(Pos, objeto.getElement());
        	return 0;
        }
        return -1;
    }

    /**
     * Refaz a memória principal
     * 
     * @param Tamanho Tamanho total da memória principal
     * @param TempoAcesso Tempo de Acesso a memória principal (ns)
     */
    public void CriarRam(int Tamanho, int TempoAcesso) {
        Ram = null;
        Ram = new MemoriaPrincipal(Tamanho, TempoAcesso);
        TempoAcessoMemPrincipal = TempoAcesso;
    }

    /**
     * Refaz a memória cache
     * 
     * @param Tamanho Tamanho total da memória cache
     * @param TempoAcesso Tempo de acesso a memória cache (ns)
     * @param TamanhoBloco Tamanho de um bloco de memória a ser armazenado na cache
     */
    public void CriarCache(int Tamanho, int TempoAcesso, int TamanhoBloco) {
        Cache = null;
        Cache = new MemoriaCache(Tamanho, TempoAcesso, TamanhoBloco);
        TempoAcessoCache = TempoAcesso;
        Acessos.removeAllElements();
    }

    /**
     * Refaz todos componentes de memória baseado nas configurações
     * 
     */
    public void Refaz() {
        JPACSSaxParser Config = new JPACSSaxParser();
        try {
            Config.parse(ConfigDialog.ConfigFile);

            CriarRam(Integer.parseInt(Config.RamTamanho), Integer.parseInt(Config.RamTempoAcesso));
            CriarCache(Integer.parseInt(Config.CacheTamanho), Integer.parseInt(Config.CacheTempoAcesso), Integer.parseInt(Config.CacheTamanhoBloco));
            if (Config.CacheLru) {
                Cache.MetodoLRU();
            } else {
                Cache.MetodoFIFO();
            }
            if (Config.CacheSequencial) {
                Cache.AcessoSequencial();
            } else {
                Cache.AcessoParalelo();
            }
            if (Config.CacheCopyBack) {
                Cache.CopyBack();
            } else {
                Cache.WriteThrought();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Empilha um elemento na pilha de memória, e incrementa o ponteiro para
     * pilha. O ponteiro para pilha começa na última posição válida para memória.
     * 
     * @param Elemento O elemento a ser empilhada
     */
    public void Empilha(int Elemento) {
        Ram.Empilha(Elemento);
    }

    /**
     * Desempilha um elemento da pilha de memória, e decrementa o ponteiro para
     * pilha. 
    
     * @return O elemento desempilhado
     */
    public int Desempilha() {
        return (Ram.Desempilha());
    }

    /**
     * Retorna cópia da memória principal
     * 
     * @return Vector contendo todo o conteúdo da memória principa
     */
    public Vector VisualizaRam() {
        return (Ram.VisualizaMemoria());
    }

    /**
     * Atualiza toda memória cache na memória principal
     */
    public void CacheParaRam() {
        Cache.AtualizaTudo(Ram);
    }

    /**
     * Calcula relatório estatístico dos índices de Acerto e Erro da memória,
     * Tempo total, Tempo médio, entre outros.
     */
    public void Calcula() {
        if ((Cache.Hits + Cache.Misses) > 0) {
            HitRatio = (Cache.Hits * 100) / (Cache.Hits + Cache.Misses);//porcentagem de acertos
        } else {
            HitRatio = 0;
        }
        TempoGasto = Cache.TempoTotal;
        TempoMedio = Cache.TempoAcesso + ((100 - HitRatio) * Ram.TempoAcesso) / 100;

        AcessosMemPrincipal = Cache.AcessoPrinc;
        AcessosCache = Cache.NAcesso;
        CacheTempoTotal = AcessosCache * TempoAcessoCache;
        MemPrincipalTempoTotal = AcessosMemPrincipal * TempoAcessoMemPrincipal;
        TempoTotal = Cache.TempoTotal;

    }

    /**
     * Retorna um clone dos acessos feitos a memória para exibição na interface
     * @return Vector contendo os acessos a memória
     */
    public Vector PegaOutput() {
        Vector Ret = (Vector) Acessos.clone();
        Acessos.removeAllElements();
        return Ret;
    }
};


/**
 * Classe responsável pela Memória Principal
 */
class MemoriaPrincipal {

    /**
     * Tamanho da memória (em bytes)
     */
    protected int Tamanho; 
    
    /**
     * Tempo de acesso a memória em nano-segundos
     */
    public int TempoAcesso;
    
    /**
     * Conteúdo da memória
     */
    private Vector Memoria;
    
    /**
     * Indica o topo da pilha de memória
     */
    public int Topo;

    /**
     * Construtor padrão, cria uma memória principal com tamanho de 1KB e tempo de acesso de 100ns
     */
    MemoriaPrincipal() {

        this(1024, 100);
    }

    /**
     * Construtor, cria uma memória principal com o tamanho e tempo de acesso especificados
     * @param Tamanho O tamanho da memória principal (em Bytes)
     * @param TempoAcesso O tempo acesso a memória principal (em nano-segundos)
     */
    MemoriaPrincipal(int Tamanho, int TempoAcesso) { 

        this.Tamanho = Tamanho;
        this.TempoAcesso = TempoAcesso;
        Memoria = new Vector();
        for (int i = 0; i < Tamanho; i++) { //inicializa a memória com tudo '0'
            Memoria.add(new Integer(0));
        }
        Topo = Tamanho - 1;
    }

    /**
     * Pega um bloco da memoria
     * 
     * @param PosI Posição inicial do 1o. item do bloco 
     * @param Tam Tamanho do bloco
     * @return Vetor de inteiros com o conteúdo do bloco
     */
    public int[] PegaBloco(int PosI, int Tam)
    {
        int i;
        int Ret[] = new int[Tam];

        for (i = 0; i < Tam; i++) {
            Ret[i] = ((Integer) Memoria.get(PosI + i)).intValue();
        }
        return Ret;
    }

    /**
     * Grava CacheSlot na Memória Principal
     * 
     * @param Elemento Item da Cache a ser gravado na memória principal
     * @param Tam Tamanho máximo do bloco da cache
     */
    public void AtualizaBloco(CacheSlot Elemento, int Tam)
    {
        int i;
        int PosI = Elemento.NumBloco * Tam;

        for (i = 0; i < Tam; i++) {
            Memoria.setElementAt(new Integer(Elemento.Bloco[i]), PosI + i);
        }
    }

    /**
     * Verifica se a pilha de memória está vazia
     * @return
     */
    public boolean PilhaVazia()
    {
        return (Topo == (Tamanho - 1)); //Última posição de memória é o fim da pilha
    }

    /**
     * Empilha elemento na memória
     * @param Elemento O elemento a ser empilhado
     */
    public void Empilha(int Elemento)
    {
        Memoria.setElementAt(new Integer(Elemento), Topo);
        Topo--;//Pilha cresce para baixo
    }

    /**
     * Desempilha elemento da memória
     * @return O elemento desempilhado
     */
    public int Desempilha()
    {
        if (!PilhaVazia())//se a pilha não estiver vazia, pode desempilhar
        {
            Topo++;//topo sempre aponta para posição livre
        }
        return ((Integer) Memoria.get(Topo)).intValue();
    }

    /**
     * Grava elemento na memória principal
     * @param Pos Posição da memória onde será gravado o elemento
     * @param Elemento Elemento a ser gravado na posição de memória
     */
    public void gravar(int Pos, int Elemento)
    {
        if (Pos < Tamanho && Pos >= 0) //posição válida
        {
            Memoria.setElementAt(new Integer(Elemento), Pos);//altera memoria
        }
    }

    /**
     * Retorna uma cópia do conteúdo da memória para vizualização na interface
     * @return Vector contendo a memória
     */
    public Vector VisualizaMemoria()
    {
        return Memoria;
    }

    /**
     * Retorna o tamanho da memória principal
     * @return O tamanho da memória principal
     */
    public int tamanho() {
        return Tamanho;
    }
};

/**
 * Classe pela Memoria Cache
 */
class MemoriaCache {

    /**
     * Tamanho da memória cache em quantidade de slots
     */
    protected int Tamanho; 
    
    /**
     * Tempo de acesso a memória cache em nano-segundos
     */
    protected int TempoAcesso;
    
    /**
     * Tamanho de um bloco de memória armazenado na cache (em Bytes)
     */
    protected int TamanhoBloco;
    
    /**
     * Variável para geração de estasticas contendo o tempo total de acessos
     */
    public int TempoTotal;
    
    /**
     * Quantidade de 'acertos' a cache (Cache Hits)
     */
    public int Hits;
    
    /**
     * Quantidade de 'erros' a cache (Cache Misses)
     */
    public int Misses;
    
    /**
     * Variável de controle de substituição para política Copy Back
     */
    protected int ExtraSubsCB;
    
    /**
     * Os slots da cache
     */
    protected ListaCacheSlot Slots;
    
    /**
     * Politica de Substituíção da Cache, Verdade=LRU, False=FIFO
     */
    public boolean LRU;
    
    /**
     * Política de Acesso a memória principal, Verdade=Sequencial, Falso=Paralelo
     */
    public boolean Sequencial;
    
    /**
     * Politica de sincronia a memória cache, Verdade=Copy Back, Falso=Write Throught
     */
    public boolean CB;
    
    /**
     * Número de acessos feitos a cache
     */
    public int NAcesso; 
    
    /**
     * Número de acessos feitos a memória principal
     */
    public int AcessoPrinc;
    
    /**
     * Item contendo o histórico do última acesso para criação de relatórios
     */
    public ItemRelatorio HistAcesso;

    /**
     * Construtor padrão, cria uma memória cache com o 10 slots de tamanho, 
     * tempo de acesso de 10 nano-segundos, e blocos com 10 bytes de tamanho.
     */
    MemoriaCache() 
    {
        this(10, 10, 10);//chama o outro construtor
    }

    /**
     * Construtor, cria uma memória cahce com os parâmetros especificados
     * 
     * @param Tamanho Tamanho da memória cache (quantidade de slots)
     * @param TempoAcesso Tempo de acesso a memória cache
     * @param TamanhoBloco Tamanho do bloco (em Bytes) da memória cache
     */
    MemoriaCache(int Tamanho, int TempoAcesso, int TamanhoBloco) 
    {
        this.Tamanho = Tamanho;
        this.TempoAcesso = TempoAcesso;
        this.TamanhoBloco = TamanhoBloco;
        Slots = new ListaCacheSlot(Tamanho);
        LRU = true;
        Sequencial = true;
        CB = true;

        TempoTotal = 0;
        Hits = 0;//nenhum cache Hit ou miss
        Misses = 0;
        NAcesso = 0;
        AcessoPrinc = 0;
    }

    /**
     * Insere Slot na Cache, pode causar substituição de outro slot caso esteja cheia
     * @param Elemento O slot a ser inserido na cache
     * @return O slot removido (ou o próprio slot se a cache não estava cheia)
     */
    protected CacheSlot Insere(CacheSlot Elemento) {
        CacheSlot Ret;
        if (LRU)//método LRU
        {
            Ret = InsereLRU(Elemento);
        } else //método FIFO
        {
            Ret = InsereFIFO(Elemento);
        }
        return Ret;
    }

    /**
     * Insere slot na cache usando a politica de substituição de LRU
     * 
     * @param Aux O slot a ser inserido na cache
     * @return O slot removido 
     */
    private CacheSlot InsereLRU(CacheSlot Aux) {
        CacheSlot Ret = Aux;
        if (!Slots.Vazia()) {
            Slots.Retirar(Aux);//retira-se o elemento para coloca-lo no topo da lista
        }
        if (Slots.Cheia()) //slots cheios, haverá substituição
        {
            Ret = Slots.Retirar();//retira o elemento primeiro elemento (foi usado num passado mais distante)
        }
        Slots.Inserir(Aux);//insere no topo (maior prioridade)
        return Ret;
    }

    /**
     * Insere slot na cache usando a politica de substituição de FIFO
     * 
     * @param Aux O slot a ser inserido na cache
     * @return O slot removido 
     */
    private CacheSlot InsereFIFO(CacheSlot Aux) {
        CacheSlot Ret = Aux;
        if (Slots.Cheia())//se os slots estiverem cheios
        {
            Ret = Slots.Retirar();//retira o primeiro slot
        }
        Slots.Inserir(Aux);//insere no topo
        return Ret;
    }

    /**
     * Acessa a memória cache, se não existir a posição de memória dentro da 
     * cache então vai até a memória principal e atualiza a memória cache.
     * 
     * @param Ram A memória principal
     * @param Pos A posição de memória a ser acessada
     * @param Gravacao Indica se o acesso é para gravação ou leitura
     * @param Item O item a ser gravado na memória (somente em caso de gravação)
     * @return O conteúdo da memória acessada (usado em caso de leitura)
     */
    public int AcessarMemoria(MemoriaPrincipal Ram, int Pos, boolean Gravacao, int Item) {
        int NumBloco = Pos / TamanhoBloco;
        int Mem = 0;
        int i, x;

        HistAcesso = new ItemRelatorio();
        try {
            HistAcesso.Numero = ++NAcesso;
            HistAcesso.Posicao = Pos;
            HistAcesso.Bloco = NumBloco;

            if (Slots.ExisteBloco(NumBloco) == -1) //Cache Miss
            {
                Misses++;//mais um cache miss
                HistAcesso.Resultado = 'E';
                AcessoPrinc++; //acessou a ram
                if (Gravacao)//acesso a cache para gravação
                {
                    Ram.gravar(Pos, Item);//grava na ram
                    HistAcesso.Natureza = 'G';
                } else {
                    HistAcesso.Natureza = 'L';
                }

                Mem = CacheMiss(Ram, Pos);//pega o bloco
                if (Sequencial)//se for sequencial
                {
                    TempoTotal += (TempoAcesso + Ram.TempoAcesso + ExtraSubsCB);
                    HistAcesso.Tempo = (TempoAcesso + Ram.TempoAcesso + ExtraSubsCB);
                } else //paralelo
                {
                    TempoTotal += Ram.TempoAcesso + ExtraSubsCB;
                    HistAcesso.Tempo = Ram.TempoAcesso + ExtraSubsCB;
                }

            } else //Cache Hit
            {
                Hits++;//mais um cache hit
                HistAcesso.Resultado = 'A';
                if (Gravacao)//altera na cache
                {
                    Atualiza(Pos, Item);
                    HistAcesso.Natureza = 'G';

                } else //leitura apenas
                {
                    Mem = CacheHit(Pos);
                    HistAcesso.Natureza = 'L';
                }

                if (CB || (!Gravacao))//se for Copy Back ou WriteThrought Leitura
                {
                    TempoTotal += TempoAcesso;
                    HistAcesso.Tempo = TempoAcesso;
                } else //WriteThrought Gravacao
                {
                    Ram.gravar(Pos, Item);//grava na ram
                    AcessoPrinc++; //acessou memoria principal

                    if (Sequencial) {
                        TempoTotal += (TempoAcesso + Ram.TempoAcesso);
                        HistAcesso.Tempo = (TempoAcesso + Ram.TempoAcesso);
                    } else //paralelo
                    {
                        TempoTotal += Ram.TempoAcesso;
                        HistAcesso.Tempo = Ram.TempoAcesso;
                    }
                }

            }

        } catch (Exception e) {
            System.out.println("Erro: MemoriaCache.AcessarMemoria()->" + e);
        }

        return Mem;
    }

    /**
     * Tentativa de acessar uma posição de memória que não está na cache
     * 
     * @param M A memória principal
     * @param Pos A posição de memória acessada
     * @return O conteúdo da memória acessado
     */
    public int CacheMiss(MemoriaPrincipal M, int Pos) {
        CacheSlot Elemento = new CacheSlot(TamanhoBloco);
        CacheSlot Retirado = null;
        Elemento.NumBloco = Pos / TamanhoBloco;

        ExtraSubsCB = 0;

        Elemento.Bloco = M.PegaBloco(Elemento.NumBloco * TamanhoBloco, TamanhoBloco);
        Retirado = Insere(Elemento);

        if (Retirado != null && !Retirado.ComparaTudo(Elemento, TamanhoBloco)) //Elemento saiu do Cache, deve-se atualizar na memoria principal
        {
            M.AtualizaBloco(Retirado, TamanhoBloco);
            HistAcesso.Subs = Retirado.NumBloco;
            if (CB && Retirado.Sujo) {
                AcessoPrinc++;
                ExtraSubsCB = M.TempoAcesso;
            }
        }

        return (Elemento.Bloco[Pos % TamanhoBloco]);
    }

    /**
     * Tentativa de acessar uma posição de memória que está na cache
     * 
     * @param Pos A posição de memória acessada
     * @return O conteúdo da memória acessado
     */
    public int CacheHit(int Pos) {
        int NumBloco = Pos / TamanhoBloco;
        int i = Slots.ExisteBloco(NumBloco);
        CacheSlot Aux;
        if (LRU) {
            Aux = Slots.Retirar(i);//retira-se o elemento
            Slots.Inserir(Aux);//para colocá-lo novamente com maior prioridade
        } else //FIFO
        {
            Aux = Slots.ElementoEm(i);
        }
        return (Aux.Bloco[Pos % TamanhoBloco]);
    }

    /**
     * Muda a politica de substiuição da cache para LRU
     */
    public void MetodoLRU()
    {
        LRU = true;
    }

    /**
     * Muda a politica de substiuição da cache para FIFO
     */
    public void MetodoFIFO()
    {
        LRU = false;
    }

    /**
     * Muda a politica de acesso a memória principal para acesso sequencial
     */
    public void AcessoSequencial()
    {
        Sequencial = true;
    }

    /**
     * Muda a politica de acesso a memória principal para acesso paralelo
     */
    public void AcessoParalelo()
    {
        Sequencial = false;
    }

    /**
     * Muda a politica de sincronia da cache para Write Throught
     */
    public void WriteThrought()
    {
        CB = false;
    }

    /**
     * Muda a politica de sincronia da cache para Copy Back
     */
    public void CopyBack() {
        CB = true;
    }

    /**
     * Atualiza (grava) posição da memória contida na cache com o Item
     * @param Pos Posição de memória a ser acessada
     * @param Item Valor a ser gravado na memória
     */
    public void Atualiza(int Pos, int Item) {
        int NumBloco = Pos / TamanhoBloco;
        int i = Slots.ExisteBloco(NumBloco);
        CacheSlot Aux;
        if (LRU) {
            Aux = Slots.Retirar(i);//retira elemento da cache
            if (CB) {
                Aux.Sujo = true; //slot sujo
            }
            Insere(Aux);//e insere no topo com maior prioridade
        } else //FIFO
        {
            Aux = Slots.ElementoEm(i);//pega o elemento
            if (CB) {
                Aux.Sujo = true; //slot sujo
            }
            Slots.SetarElementoEm(Aux, i);//atualiza elemento na cache
        }

        if (Aux != null) {
            Aux.Bloco[Pos % TamanhoBloco] = Item;
        }
    }

    /**
     * Atualiza (grava) todo conteúdo da memória cache na memoria principal
     * @param Ram A memória principal
     */
    public void AtualizaTudo(MemoriaPrincipal Ram)
    {
        CacheSlot Aux;
        int i = 0;
        Aux = Slots.ElementoEm(i);//pega slot
        while (Aux != null) {
            i++;
            Ram.AtualizaBloco(Aux, TamanhoBloco);
            Aux = Slots.ElementoEm(i);
        }
    }

    /**
     * Retorna uma cópia do conetúdo da memória cache para viazualização na interface
     * @return Vetor contendo os slots da memória cache
     */
    public CacheSlot[] Visualiza() {
        return Slots.Visualiza();
    }
};