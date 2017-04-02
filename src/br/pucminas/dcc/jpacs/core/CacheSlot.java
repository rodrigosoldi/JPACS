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
 * Casse que lida com 1 slot da memória cache
 * 
 */
public class CacheSlot {

    /**
     * Indica o número do bloco
     */
    public int NumBloco;
    
    /**
     * Bloco do slot da cache
     */
    public int Bloco[];
    
    /**
     * indica se o bloco está desatualizado em relação a mem (usado somente em Copy Back)
     */
    public boolean Sujo;

    CacheSlot() {
        NumBloco = 0;
        Sujo = false;
    }

    CacheSlot(int N) {
        Bloco = new int[N];
        Sujo = false;
    }

    @Override
    public String toString() {
        String Ret;
        Ret = "Num: " + NumBloco;

        return Ret;
    }

    /**
     * Compara somente se os números dos blocos de dois slots de cache são iguais
     * 
     * @param Obj O slot da cache a ser comparado
     * @return true se tiverem o mesmo número de bloco, false caso contrário
     */
    public boolean Compara(CacheSlot Obj) {
        return (NumBloco == Obj.NumBloco);
    }

    /**
     * Compara se o número e o conteúdo dos slots da cache são iguais
     * 
     * @param Obj O slot da cache a ser comparado
     * @param Tam Tamanho do slot
     * @return true se forem iguais, false caso contrário
     */
    public boolean ComparaTudo(CacheSlot Obj, int Tam) {
        int i = 0;
        boolean T = true;
        if (Compara(Obj)){ //Se o Num. dos Blocos forem iguais
            while (i < Tam && Bloco[i] == Obj.Bloco[i]) {
                i++;
            }
        }
        if (i < Tam) {
            T = false;
        }
        return T;
    }
};