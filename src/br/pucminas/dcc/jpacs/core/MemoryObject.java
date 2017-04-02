package br.pucminas.dcc.jpacs.core;

/**
 * 
 *  Essa classe contém as informações da memória e também a sua paridade.
 *  As operações que eram feitas diretamente entre o Processador e a Memória agora 
 *  são realizadas recebendo uma instância dessa classe.
 * 
 * @author RodrigoSoldi
 */
public class MemoryObject {
    
    // ------- Atributes
    
    private int element;
    private int parity;

    // ------- Constructors
    
    public MemoryObject(int elemento, int paridade) {
            super();
            
            this.element = elemento;
            this.parity = paridade;
    }

    
    // ------- Getters e Setters
    
    public int getElement() {
            return element;
    }

    public void setElement(int elemento) {
            this.element = elemento;
    }

    public int getParity() {
            return parity;
    }

    public void setParity(int parity) {
            this.parity = parity;
    }
}
