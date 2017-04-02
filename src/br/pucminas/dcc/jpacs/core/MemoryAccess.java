/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.pucminas.dcc.jpacs.core;

import br.pucminas.dcc.jpacs.Exceptions.JPACSParityException;

/**
 *
 * Essa classe é responsável por todas as operações que serão executadas na memória
  (Gravação, leitura, calculo de parity, etc)
 * 
 * @author RodrigoSoldi
 */
class MemoryAccess {
	
    protected static ClasseMemoria memory;

    public MemoryAccess(ClasseMemoria memoria) {
            memory = memoria;
    }

    private int oddParity(int i) {
         
        i = i - ( ( i >>> 1 ) & 0x55555555 );
        i = ( i & 0x33333333 ) + ( (i >>> 2 ) & 0x33333333 );
         
        int numberBits = ( ( ( i + (i >>> 4 ) )  & 0x0F0F0F0F ) * 0x01010101 ) >>> 24;

        /* if not odd */
        if (numberBits % 2 == 0) {
            return 1;
        }
         
        return 0;
    }

    public void directSave (int position, int element) throws JPACSParityException {
        
            int parity = this.oddParity(element);

            System.out.println("Paridade direto: " + parity);

            MemoryObject objeto = new MemoryObject(element,parity);

            if (memory.GravarDireto(position, objeto) == -1) {
                // Erro de parity, tentar novamente
                parity = this.oddParity(element);
                objeto.setParity(parity);
                if (memory.GravarDireto(position, objeto) == -1) {
                        // Na segunda falha, lançar exceção
                        String message = "Occoreu uma falha de paridade ao tentar gravar diretamente";
                        throw new JPACSParityException(message);
                }
            }
    }

    public int access (int position) throws JPACSParityException {
        MemoryObject object = memory.Acessar(position);
        int parity = oddParity(object.getElement());
        if (parity != object.getParity()) {
            // Erro na leitura, tentar novamente
            object = memory.Acessar(position);
            parity = oddParity(object.getElement());
            if (parity != object.getParity()) {
                //Segundo erro, lancamento de excecao
                String message = "Falha na leitura da paridade";
                throw new JPACSParityException(message);
            }
        }
        return object.getElement();
    }

    public void save (int position, int element) throws JPACSParityException {
        int parity = this.oddParity(element);
        System.out.println("Paridade: " + parity);
        MemoryObject object = new MemoryObject(element,parity);

        if (memory.Gravar(position, object) == -1) {
            // Recalcular
            parity = this.oddParity(element);
            object.setParity(parity);
            // Lança excecao
            if (memory.Gravar(position, object) == -1) {                    
                    String message = "Falha ao gravar a paridade";
                    throw new JPACSParityException(message);
            }
        }
    }
};

