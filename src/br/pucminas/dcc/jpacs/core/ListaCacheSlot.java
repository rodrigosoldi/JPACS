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

/**
 * Classe que irá conter os slots de memória cache
 */
class ListaCacheSlot {

    /**
     * Os items da lista de slots de cache
     */
    protected java.util.Vector<CacheSlot> vctItems = null;
    
    /**
     * Capacidade máxima de slots da memória cache
     */
    protected int Capacidade;

    /**
     * Construtor padrão, cria uma lista com capacidade máxima para 10 slots 
     */
    ListaCacheSlot() {
        this(10);
    }

    /**
     * Construtor, cria uma lista com capacidade máxima de N slots
     * @param N O número máximo de slots da cache
     */
    ListaCacheSlot(int N) {
        Capacidade = N;
        vctItems = new java.util.Vector<CacheSlot>(Capacidade);
    }

    /**
     * Verifica se a lista está vazia
     * 
     * @return True se a lista estiver vazia, false caso contrário
     */
    public boolean Vazia() {
        return vctItems.isEmpty();
    }

    /**
     * Verifica se a lista está cheia
     * 
     * @return True se a lista estiver cheia, false caso contrário
     */
    public boolean Cheia() {
        return vctItems.size() >= Capacidade;
    }

    /**
     * Retorna o tamanho da lista
     * 
     * @return int contendo a quantidade de elementos da lista
     */
    public int Tamanho() {
        return vctItems.size();
    }

    /**
     * Insere elemento na ultima posicao da lista
     * 
     * @param Elemento O Elemento a ser inserido
     */
    public void Inserir(CacheSlot Elemento) {
        vctItems.add(Elemento);
    }

    /**
     * Retira o primeiro elemento
     * @return O elemento removido da lista
     */
    public CacheSlot Retirar() {
        return Retirar(0);
    }

    /**
     * Retira o elemento da posição Pos
     * 
     * @param Pos Posição do elemento a ser removido
     * @return O elemento removido
     */
    public CacheSlot Retirar(int Pos) {
        CacheSlot Ret = null;
        int Ultimo = Tamanho();

        // Corrige posições inválidas
        if (Pos >= Ultimo) {
            Pos = Ultimo - 1;
        } else if (Pos < 0) {
            Pos = 0;
        }

        //Verifica se a lista não está vazia antes de remover algum item
        if (!Vazia()) {
            Ret = vctItems.remove(Pos);
        }

        return Ret;
    }

    /**
     * Retira o elemento 
     * 
     * @param Elemento
     * @return O Elmento removido da lista
     */
    public CacheSlot Retirar(CacheSlot Elemento) {
        int i = PosicaoDe(Elemento);
        CacheSlot Ret = null;

        if (i >= 0) { //existe elemento
            Ret = Retirar(i);
        }
        return Ret;
    }

    /**
     * Encontra a posição do elemento (comparando somente o número do bloco) na lista
     * 
     * @param Elemento O elemento procurado
     * @return A posição do elementos na lista, ou -1 caso não exista
     */
    public int PosicaoDe(CacheSlot Elemento) {
        int i = 0;
        int Ultimo = Tamanho();

        while (i < Ultimo && !Elemento.Compara(vctItems.get(i))) {//procura pelo item

            i++;
        }

        if (i == Ultimo) {
            i = -1;//não encontrou
        }
        return i;
    }

    /**
     * Verifica se existe o bloco na lista e retorna sua posição
     * @param NBloco O número do bloco procurado
     * @return A posição do bloco na lista, ou -1 caso não encontrado
     */
    public int ExisteBloco(int NBloco) {
        int i = 0;
        int Ultimo = Tamanho();

        while (i < Ultimo && vctItems.get(i).NumBloco != NBloco) {//procura pelo bloco

            i++;
        }

        if (i == Ultimo) {
            i = -1;//não encontrou
        }
        return i;
    }

    /**
     * Retorna o elemento da lista que está na posição
     * @param Pos A posição do elemento desejado
     * @return O elemento da posição Pos
     */
    public CacheSlot ElementoEm(int Pos) {

        int Ultimo = Tamanho();

        // Corrige posições inválidas
        if (Pos >= Ultimo) {
            Pos = Ultimo - 1;
        } else if (Pos < 0) {
            Pos = 0;
        }

        return vctItems.get(Pos);
    }

    /**
     * Seta o elemento da lista que está na posição passada
     * @param Elemento O elemento a ser gravado
     * @param Pos Posição na lista onde o elemento será gravado
     */
    public void SetarElementoEm(CacheSlot Elemento, int Pos) {
        int Ultimo = Tamanho();

        // Corrige posições inválidas
        if (Pos >= Ultimo) {
            Pos = Ultimo - 1;
        } else if (Pos < 0) {
            Pos = 0;
        }

        vctItems.setElementAt(Elemento, Pos);
    }

    /**
     * Método que retorna um vetor de slots de cache para 'visualização' na interface
     * @return Vetor contendo todos os elementos da lista
     */
    public CacheSlot[] Visualiza() {
        CacheSlot[] L = new CacheSlot[1];
        return vctItems.toArray(L);
    }
};